var ws;
var senderObj;
var recipientObj;

function connect() {
    var username = document.getElementById("username").value;
    
    var host = document.location.host;
    var pathname = document.location.pathname;
    
    ws = new WebSocket("ws://" +host  + pathname + "chat/" + username);

    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        var searchAndFriend = document.getElementById("searchAndFriend");
        if (message.content !== "friendRequest") {
          var newMessage = message.from + " : " + message.content;
          if (message.timestamp){
              newMessage += formatDate(new Date(message.timestamp));
          }
          newMessage += "\n";
          log.innerHTML += newMessage
        }
        else {
            // friendId = message.friendId;
            searchAndFriend.innerHTML +=
                `<div id="friendRequest"><span>${message.from} just send you a friend request!</span>
                <button id="approveFriendRequest" onclick="handleFriendRequest(senderObj.name, recipientObj.name, 'approve');">Approve</button>
                <button id="denyFriendRequest" onclick="handleFriendRequest(senderObj.name, recipientObj.name, 'deny');">Deny</button> </div>`;
        }
        
    };
}

function formatDate(d){
    return " on " + (d.getMonth()+1) + "-" + d.getDate() + "-"+ d.getFullYear() + " at " +
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

function search() {
    let addFriendBtn = document.getElementById("addFriend");
    let userToSearch = document.getElementById("search").value;
    fetch(`http://${document.location.host}${document.location.pathname}rest/user/${userToSearch}`)
        .then(response => response.json())
        .then(response => {
            console.log(response)
            recipientObj = response;
            if (response.name) {
                alert(`you can add ${response.name} as a friend!`);
                addFriendBtn.classList.remove("dontShow");
            } else alert("the user you searched does not exist!");
        }, err => {
            console.log(err);
        })
        .then(() => fetch(`http://${document.location.host}${document.location.pathname}rest/user/${ws.url.split('/').pop()}`)
            .then(response => response.json())
            .then(response => {
                senderObj = response;
            }))
}

function addFriend(){
    let addFriendBtn = document.getElementById("addFriend");
    addFriendBtn.classList.add("dontShow");
    let searchField = document.getElementById("search");
    let recipient = searchField.value;
    searchField.value = "";
    let json = JSON.stringify({
        "content": "friendRequest",
        "to": recipient
    })
    ws.send(json);
    let postBody = {
        "sender": senderObj,
        "recipient": recipientObj
    }
    fetch(`http://${document.location.host}${document.location.pathname}rest/friend/create`,
        {method: "POST",
            body: JSON.stringify(postBody),
            headers: {
                'Content-Type': 'application/json; charset=utf-8'
            }})
        .then(() => alert("friend request sent!"))
}

function handleFriendRequest(sender, recipient, response) {
    fetch(`http://${document.location.host}${document.location.pathname}rest/friend/${sender}/${recipient}/${response}`,
        {method: "PATCH",
                body: JSON.stringify({
                    response: response
                }),
                headers: {
                    'Content-Type': 'application/json; charset=utf-8'
                }
        })
        .then(() => {
            let friendRequest = document.getElementById("friendRequest");
            friendRequest.parentNode.removeChild(friendRequest);
        })
}
