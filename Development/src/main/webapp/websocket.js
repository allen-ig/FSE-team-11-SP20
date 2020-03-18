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
        log.innerHTML += message.from + " : " + message.content + "\n";
    };
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

function search(){
    let addFriendBtn = document.getElementById("addFriend");
    let userToSearch = document.getElementById("search").value;
    fetch(`http://${document.location.host}${document.location.pathname}rest/user/${userToSearch}`)
        .then(response => response.json())
        .then(response => {
            if (response.name) {
                alert(`you can add ${response.name} as a friend!`);
                addFriendBtn.classList.remove("dontShow");
            }
            else alert("the user you searched does not exist!");
        }, err => {
            console.log(err);
        })
}

function addFriend(){
    let addFriendBtn = document.getElementById("addFriend");
    addFriendBtn.classList.add("dontShow");
    let searchField = document.getElementById("search");
    searchField.value = "";
    alert("friend request sent!");
}