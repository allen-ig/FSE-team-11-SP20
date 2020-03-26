var ws;

function connect() {
    var username = document.getElementById("username").value;
    
    var host = document.location.host;
    var pathname = document.location.pathname;
    
    ws = new WebSocket("ws://" +host  + pathname + "chat/" + username);

    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        var newMessage = message.from + " : " + message.content;
        if (message.timestamp){
            newMessage += formatDate(new Date(message.timestamp));
        }
        newMessage += "\n";
        log.innerHTML += newMessage
    };
}

function formatDate(d){
    return " on " + (d.getMonth()+1) + "-" d.getDate() + "-"+ d.getFullYear() + " at" +
    d.getHours() + ":" + d.getMinutes();
}

function send() {
    var content = document.getElementById("msg").value;
    var recipient = document.getElementById("usr").value;
    var json = JSON.stringify({
        "to":recipient,
        "content":content
    });

    ws.send(json);
}