var ws;
var senderObj;
var recipientObj;

var sender;



function connect() {
    var username = document.getElementById("username").value;
    sender = document.getElementById("username").value;
    
    var host = document.location.host;
    var pathname = document.location.pathname;
    
    ws = new WebSocket("ws://" + host + pathname + "chat/" + username);

    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        var searchAndFriend = document.getElementById("searchAndFriend");
        if (message.content != "friendRequest") {
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
}

function sendGroup() {
    var content = document.getElementById("msg").value;
    var recipient = document.getElementById("usr").value;
    var group = "group ";
    var withGroup = group.concat(recipient);
    var json = JSON.stringify({
        "to":withGroup,
        "content":content
    });

    ws.send(json);
}

function addMember() {

    var host = document.location.host;
    var pathname = document.location.pathname;
    var addMemUrl = "ws://" + host + pathname + "group/" + "addUser";

    var user = document.getElementById("usr").value;
    var group = document.getElementById("msg").value;
    var json = JSON.stringify({
        "sender":sender,
        "user":user,
        "group":group
    });

    fetch(addMemUrl, {
        method: 'post',
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: json
        })
        .then(function (response) {
            return response.text();
        })
        .then(function (text) {
            console.log("Service: ", text);
        })
        .catch(function (error) {
            console.log("Service: Error: ", error);
        });

    /*
    addMemberRequest = new XMLHttpRequest();

    var host = document.location.host;
    var pathname = document.location.pathname;

    addMemberRequest.open("PUT", "ws://" + host + pathname + "group/" + "addUser", true);
    addMemberRequest.setRequestHeader("Content-Type", "application/json; charset=utf-8");


    var user = document.getElementById("usr").value;
    var group = document.getElementById("msg").value;
    var json = JSON.stringify({
            "sender":sender,
            "user":user,
            "group":group
        });

    addMemberRequest.onreadystatechange = function() {
        if (this.readyState == 4) {
            var log = document.getElementById("log");
            var String = addMemberRequest.responseText;
            console.log(addMemberRequest.responseText);
            log.innerHTML += addMemberRequest.responseText;
        } else {
            log.innerHTML += addMemberRequest.responseText;
        }
    };

    addMemberRequest.send(json);
     */
}

function addMod() {

    addModeratorRequest = new XMLHttpRequest();

    var host = document.location.host;
    var pathname = document.location.pathname;

    addModeratorRequest.open("PUT", "ws://" + host + pathname + "group/" + "addModerator", true);

    addModeratorRequest.setRequestHeader('Content-Type', 'application/json');

    var user = document.getElementById("usr").value;
    var group = document.getElementById("msg").value;
    var json = JSON.stringify({
            "sender":sender,
            "user":user,
            "group":group
        });

    addModeratorRequest.onreadystatechange = function() {
      var log = document.getElementById("log");
      console.log(addModeratorRequest.responseText);
      log.innerHTML += addModeratorRequest.responseText;
    };

    addModeratorRequest.send(json);
}

function removeMember() {

    removeMemberRequest = new XMLHttpRequest();

    var host = document.location.host;
    var pathname = document.location.pathname;

    removeMemberRequest.open("PUT", "ws://" + host + pathname + "group/" + "removeUser", true);

    removeMemberRequest.setRequestHeader('Content-Type', 'application/json');
    var user = document.getElementById("usr").value;
    var group = document.getElementById("msg").value;
    var json = JSON.stringify({
            "sender":sender,
            "user":user,
            "group":group
        });

    removeMemberRequest.onreadystatechange = function() {
      var log = document.getElementById("log");
      log.innerHTML += removeMemberRequest.responseText;
    };

    removeMemberRequest.send(json);
}

function removeMod() {

    removeModeratorRequest = new XMLHttpRequest();

    var host = document.location.host;
    var pathname = document.location.pathname;
    removeModeratorRequest.open("PUT", "ws://" + host + pathname + "group/" + "removeModerator", true);

    removeModeratorRequest.setRequestHeader('Content-Type', 'application/json');
    var user = document.getElementById("usr").value;
    var group = document.getElementById("msg").value;
    var json = JSON.stringify({
            "sender":sender,
            "user":user,
            "group":group
        });

    removeModeratorRequest.onreadystatechange = function() {
        var log = document.getElementById("log");
        log.innerHTML += removeModeratorRequest.responseText;
      };

    removeModeratorRequest.send(json);
}

function deleteGroup() {

    deleteGroupRequest = new XMLHttpRequest();

    deleteGroupRequest.open("DELETE", "ws://" + host + pathname + "group/" + "delete", true);

    var host = document.location.host;
    var pathname = document.location.pathname;

    deleteGroupRequest.setRequestHeader('Content-Type', 'application/json');
    var group = document.getElementById("msg").value;
    var json = JSON.stringify({
            "sender":sender,
            "user":sender,
            "group":group
        });

    deleteGroupRequest.onreadystatechange = function() {
        var log = document.getElementById("log");
        log.innerHTML += deleteGroupRequest.responseText;
     };

    deleteGroupRequest.send(json);

}
