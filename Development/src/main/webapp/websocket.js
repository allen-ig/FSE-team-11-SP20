var ws;
var senderObj;
var recipientObj;

var sender;
var numRequests = 0;



function connect() {
    var username = document.getElementById("username").value;
    sender = document.getElementById("username").value;

    var host = document.location.host;
    var pathname = document.location.pathname;
    
    ws = new WebSocket("ws://" + host + pathname + "chat/" + username);
    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        var message = JSON.parse(event.data);
        var searchAndFriend = document.getElementById("popUpBody");
        if (message.content != "friendRequest") {
          var newMessage = message.from + " : " + message.content;
          if (message.timestamp){
              newMessage += formatDate(new Date(message.timestamp));
          }
          newMessage += "\n";
          log.innerHTML += newMessage
        }
        else {
            numRequests += 1;
            document.getElementById("frButton").classList.remove("dontShow");
            searchAndFriend.innerHTML +=
                `<div id="friendRequest"><span>${message.from} just send you a friend request!</span>
                <button id="approveFriendRequest" onclick="handleFriendRequest('${message.from}', '${message.to}', 'approve');">Approve</button>
                <button id="denyFriendRequest" onclick="handleFriendRequest('${message.from}', '${message.to}', 'deny');">Deny</button> </div>`;
        }
    };
}

function formatDate(d){
    let mins = d.getMinutes();
    if (mins < 10) {
        mins = "0" + mins;
    }
    return "\n---" + (d.getMonth()+1) + "." + d.getDate() + "."+ d.getFullYear() + " at " +
    d.getHours() + ":" + mins + "\n";
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
            recipientObj = response;
            if (response.name) {
                console.log(`you can add ${response.name} as a friend!`);
                addFriendBtn.classList.remove("dontShow");
            } else console.log("the user you searched does not exist!");
        }, err => {
            console.log(err);
        })
        .then(() => fetch(`http://${document.location.host}${document.location.pathname}rest/user/${ws.url.split('/').pop()}`)
            .then(response => response.json())
            .then(response => {
                senderObj = response;
            }));
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
        .then(() => console.log("friend request sent!"))
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
    numRequests -= 1;
    if (numRequests == 0) {
        document.getElementById("frButton").classList.add("dontShow");
    }
}


function getFriendList() {
    let friendListNode = document.getElementById("friendList")
    if (friendListNode){
        friendListNode.remove();
    } else {
        fetch(`http://${document.location.host}${document.location.pathname}rest/friend/${ws.url.split('/').pop()}/friends`,)
            .then(response => response.json())
            .then(response => {
                let connectField = document.getElementById("connect");
                let friendList = "<tr><ul id='friendList'>";
                response.map(user => {
                    friendList += `<li>${user.name} &nbsp; <span>${user.status}</span></li>`;
                });
                friendList += "</ul></tr>";
                connectField.innerHTML += friendList;
            })
    }
}

function printFriendList () {
        fetch(`http://${document.location.host}${document.location.pathname}rest/friend/${ws.url.split('/').pop()}/friends`,)
            .then(response => response.json())
            .then(response => {
                let friendList = document.getElementById("friendsLog");
                friendList.innerHTML = "";
                response.forEach(user => {
                    friendList.innerHTML += "[" + user.name + "]  " + user.status + "\n";
                });
            })
}
