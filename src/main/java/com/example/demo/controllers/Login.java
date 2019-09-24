package com.example.demo.controllers;

import com.example.demo.repo.User;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class Login {
    // the application context is injected via ctor
    private ApplicationContext context;

    @Autowired
    public Login(ApplicationContext c) {
        this.context = c;
    }

    private UserRepository getRepo() {
        return (UserRepository)this.context.getBean(UserRepository.class);
    }

    @GetMapping("/")
    public String main(Model model, User user, HttpSession session) {
        String name = (String)session.getAttribute("name");

        if (name != null) {
            return "redirect:/chatroom";
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid User user, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "login";
        }

        getRepo().save(user);
        model.addAttribute("users", getRepo().findAll());

        HttpSession session = request.getSession();
        session.setAttribute("name", user.getName());
        session.setAttribute("userid", user.getId());
        return "redirect:/chatroom";
    }

    @GetMapping("/logout/{id}")
    public String destroySession(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        request.getSession().invalidate();
        User user = getRepo().findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        getRepo().delete(user);
        return "redirect:/";
    }

    private void checkLogin (HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("name") == null) {
            response.sendRedirect("/login");
        }
    }
}
