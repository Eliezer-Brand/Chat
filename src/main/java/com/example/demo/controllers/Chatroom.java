package com.example.demo.controllers;

import com.example.demo.repo.Message;
import com.example.demo.repo.MessageRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class Chatroom {
    // the application context is injected via ctor
    private ApplicationContext context;

    @Autowired
    public Chatroom(ApplicationContext c) {
        this.context = c;
    }

    private MessageRepository getRepo() {
        return (MessageRepository)this.context.getBean(MessageRepository.class);
    }

    @GetMapping("/chatroom")
    public String chat(Model model, Message message, HttpSession session) {
        String name = (String)session.getAttribute("name");

        if (name == null) {
            return "redirect:/";
        }

        long userid = (long)session.getAttribute("userid");
        model.addAttribute("name", name);
        model.addAttribute("userid", userid);

        return "chatroom";
    }

    @PostMapping("/send")
    public String send(@Valid Message message, BindingResult result, Model model, HttpSession session) {
        String name = (String)session.getAttribute("name");
        long userid = (long)session.getAttribute("userid");
        model.addAttribute("name", name);
        model.addAttribute("userid", userid);

        if (result.hasErrors()) {
            return "chatroom";
        }

        message.setName(name);
        getRepo().save(message);

        return "redirect:/chatroom";
    }

    @GetMapping(value="/getjson")
    public @ResponseBody List<Message> getAll(Model model, HttpSession session) {
        if(session.getAttribute("name") == null)
        {
            List<Message> r = new LinkedList<>();
            r.add(new Message("false"));
            return r;
        }
        return getRepo().findAll();
    }
}
