$( document ).ready(function() {
    getMessages();

    setInterval(function() {
        getMessages();
    }, 30000); // 30 seconds

    $('#send').on('submit', function(e){
        e.preventDefault();
        $('#content').val('');
        getMessages();
    });
});

function getMessages() {
    $.ajax({
        url: "/getjson", success: function (response) {
            res = "";
            $.each(response, function (index, message) {
                if(message.name == null) {
                    window.location.replace("/");
                }
                res = res + '<tr><th scope="col">' + message.name + '</th><th scope="col">' + message.content + '</th></tr>';
            });
            $("#messages").html(res);
        }, error: function () {
            console.log("error!");
            $("#messages").html("Some error occured!");
        }
    });
}
