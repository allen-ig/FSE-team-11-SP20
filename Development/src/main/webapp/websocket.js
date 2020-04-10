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
  ws.onmessage = function (event) {
    var log = document.getElementById("log");
    var message = JSON.parse(event.data);
    var searchAndFriend = document.getElementById("popUpBody");
    if (message.content != "friendRequest") {
      let newMessage = formatMessage(message);
      log.innerHTML += newMessage;
      log.scrollTop = log.scrollHeight;
      if (document.getElementsByClassName("messageTabContentAll").item(
          0).style.display === "none" && newMessage.indexOf("[Me]") !== 0) {
        document.getElementById("linkAll").style.backgroundColor = "#3bbff3";
      }
      updateDirectConvo(message, newMessage);
      updateGroupConvo(message, newMessage);
    } else {
      numRequests += 1;
      document.getElementById("frButton").classList.remove("dontShow");
      searchAndFriend.innerHTML +=
          `<div id="friendRequest"><span>${message.from} just send you a friend request!</span>
                <button id="approveFriendRequest" onclick="handleFriendRequest('${message.from}', '${message.to}', 'approve');">Approve</button>
                <button id="denyFriendRequest" onclick="handleFriendRequest('${message.from}', '${message.to}', 'deny');">Deny</button> </div>`;
    }
  };
}

function formatDate(d) {
  let mins = d.getMinutes();
  if (mins < 10) {
    mins = "0" + mins;
  }

  return "\n---" + (d.getMonth() + 1) + "." + d.getDate() + "."
      + d.getFullYear() + " at " +
      d.getHours() + ":" + mins + "\n";
}

function formatMessage(message) {
  let newMessage = "";
  //check if me and label appropriately
  let indexOfMe = message.from.indexOf(sender);
  while (true) {
    let ind = message.from.indexOf(sender, indexOfMe + 1);
    if (ind !== -1) {
      indexOfMe = ind;
    } else {
      break;
    }
  }
  if (indexOfMe !== -1 && indexOfMe + sender.length === message.from.length) {
    newMessage += "[Me] ";
  }
  newMessage += message.from + " : " + message.content;
  if (message.timestamp) {
    newMessage += formatDate(new Date(message.timestamp));
  }
  newMessage += "\n";
  return newMessage
}

function updateDirectConvo(message, newMessage) {
  let highlightedUser = document.getElementById("linkUser").innerText;
  console.log(highlightedUser);
  if (highlightedUser != "User") {
    let indexOfUser = message.from.toLowerCase().indexOf(
        highlightedUser.toLowerCase());
    /*
    while (true) {
      let ind = message.from.toLowerCase().indexOf(
          highlightedUser.toLowerCase(), indexOfUser + 1);
      if (ind !== -1) {
        indexOfUser = ind;
      } else {
        break;
      }
    }
    indexOfUser !== -1 && indexOfUser + highlightedUser.length
        === message.from.length
     */
    if ((indexOfUser === 0 && highlightedUser.length === message.from.length)
        || (message.to.toLowerCase() === highlightedUser.toLowerCase())) {
      let userLog = document.getElementById("userLog");
      userLog.innerHTML += newMessage;
      userLog.scrollTop = userLog.scrollHeight;
      if (document.getElementsByClassName("messageTabContentUser").item(
          0).style.display === "none" && (message.to.toLowerCase()
          !== highlightedUser.toLowerCase())) {
        document.getElementById("linkUser").style.backgroundColor = "#3bbff3";
      }
    }
  }
}

function updateGroupConvo(message, newMessage) {
  let highlightedGroup = document.getElementById("linkGroup").innerText;
  if (highlightedGroup != "Group") {
    let indexOfGroup = message.from.toLowerCase().indexOf(
        highlightedGroup.toLowerCase());
    if (indexOfGroup == 0) {
      let groupLog = document.getElementById("groupLog");
      groupLog.innerHTML += newMessage;
      groupLog.scrollTop = groupLog.scrollHeight;
      if (document.getElementsByClassName("messageTabContentGroup").item(
          0).style.display === "none") {
        document.getElementById("linkGroup").style.backgroundColor = "#3bbff3";
      }
    }
  }
}

function send() {
  var content = document.getElementById("msg").value;
  var recipient = document.getElementById("usr").value;
  var json = JSON.stringify({
    "to": recipient,
    "content": content
  });

  ws.send(json);
}

function search() {
  let addFriendBtn = document.getElementById("addFriend");
  let userToSearch = document.getElementById("search").value;
  fetch(
      `http://${document.location.host}${document.location.pathname}rest/user/${userToSearch}`)
  .then(response => response.json())
  .then(response => {
    recipientObj = response;
    if (response.name) {
      console.log(`you can add ${response.name} as a friend!`);
      addFriendBtn.classList.remove("dontShow");
    } else {
      console.log("the user you searched does not exist!");
    }
  }, err => {
    console.log(err);
  })
  .then(() => fetch(
      `http://${document.location.host}${document.location.pathname}rest/user/${ws.url.split(
          '/').pop()}`)
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
        .then(response => {
            let slog = document.getElementById("serviceLog");
            if (response.status === 409){
                slog.innerHTML += "Service: friend relationship already exist!\n";
            }else if (response.status === 405) {
                slog.innerHTML += "Service: you can't add yourself as a friend!\n";
            }
            else{
                ws.send(json)
                console.log("friend request sent!")
            }
        })
        .catch(err => console.log(err))
}

function handleFriendRequest(_sender, recipient, response) {
    fetch(`http://${document.location.host}${document.location.pathname}rest/friend/${_sender}/${recipient}/${response}`,
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
  if (friendListNode) {
    friendListNode.remove();
  } else {
    fetch(
        `http://${document.location.host}${document.location.pathname}rest/friend/${ws.url.split(
            '/').pop()}/friends`,)
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
                    friendList.innerHTML += "[" + user.name + "] - " + user.isOnline + "\n  Status: " + user.status + "\n";
                });
            })
}

function removeFriend() {
    let friendToRemove = document.getElementById("removeFriend").value;
    let slog;
    fetch(
        `http://${document.location.host}${document.location.pathname}rest/friend/${ws.url.split('/').pop()}/${friendToRemove}/remove`,
        {
            method: 'DELETE'
        }
    ).then(response => {
        slog = document.getElementById("serviceLog");
        if (response.status === 404) {
            slog.innerHTML += "Service: Friend does not exist!\n";
        }else {
            printFriendList();
        }
    })
}