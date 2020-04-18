function openTabAll(e) {

    let mtLinks = document.getElementsByClassName("messageTabLink");
    for (i = 0; i < mtLinks.length; i++) {
        mtLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("messageTabContentUser").item(0).style.display = "none";
    document.getElementsByClassName("messageTabContentGroup").item(0).style.display = "none";
    document.getElementsByClassName("messageTabContentAll").item(0).style.display = "block";
    e.style.backgroundColor = "#ccc";
}

function openTabGroup(e) {
    let mtLinks = document.getElementsByClassName("messageTabLink");
    for (i = 0; i < mtLinks.length; i++) {
        mtLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("messageTabContentUser").item(0).style.display = "none";
    document.getElementsByClassName("messageTabContentAll").item(0).style.display = "none";
    document.getElementsByClassName("messageTabContentGroup").item(0).style.display = "block";
    e.style.backgroundColor = "#ccc";
}

function openTabUser(e) {
    let mtLinks = document.getElementsByClassName("messageTabLink");
    for (i = 0; i < mtLinks.length; i++) {
        mtLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("messageTabContentGroup").item(0).style.display = "none";
    document.getElementsByClassName("messageTabContentAll").item(0).style.display = "none";
    document.getElementsByClassName("messageTabContentUser").item(0).style.display = "block";

    e.style.backgroundColor = "#ccc";
}

function switchTabUser() {
    let userLog = document.getElementById("userLog");
    userLog.innerHTML = "";

    var user = document.getElementById("search").value;

    var switchTabUrl = `http://${document.location.host}${document.location.pathname}rest/user/getDirectMessages/${sender}/${user}`;
    var ok = false;
    console.log(user, sender);
    fetch(switchTabUrl, {
        method: "get",
    })
        .then(function (response) {
            if (response.status === 200) {
                ok = true;
                return response.json();
            }
            console.log(response);
            return response.text();
        })
        .then(function (messages) {
            console.log(messages);
            if (ok) {
                messages.forEach(message => {
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
                        userLog.innerHTML += "[Me] ";
                    }
                    userLog.innerHTML += message.from + " : " + message.content;
                    userLog.innerHTML += formatDate(new Date(message.timestamp)) + "\n";
                    userLog.scrollTop = userLog.scrollHeight;
                });
            } else {
                userLog.innerHTML += messages + "\n";
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
    document.getElementById("linkUser").innerText = user;
    document.getElementById("usr").value = user;
    openTabUser(document.getElementById("linkUser"));
    openInputSend(document.getElementById("linkSend"));
    document.getElementById("msg").focus();
    document.getElementById("msg").select();
}

function switchTabGroup() {
    let groupLog = document.getElementById("groupLog");
    groupLog.innerHTML = "";

    var group = document.getElementById("search").value;

    var switchTabUrl = `http://${document.location.host}${document.location.pathname}rest/user/getGroupMessages/${sender}/${group}`;
    var ok = false;
    console.log(group, sender);
    fetch(switchTabUrl, {
        method: "get",
    })
        .then(function (response) {
            if (response.status === 200) {
                ok = true;
                return response.json();
            }
            console.log(response);
            return response.text();
        })
        .then(function (messages) {
            console.log(messages);
            if (ok) {
                messages.forEach(message => {
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
                        groupLog.innerHTML += "[Me] ";
                    }
                    groupLog.innerHTML += message.from + ": " + message.content;
                    groupLog.innerHTML += formatDate(new Date(message.timestamp)) + "\n";
                    groupLog.scrollTop = groupLog.scrollHeight;
                });
            } else {
                groupLog.innerHTML += messages + "\n";
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
    document.getElementById("linkGroup").innerText = group;
    document.getElementById("usr").value = group;
    openTabGroup(document.getElementById("linkGroup"));
    openInputSend(document.getElementById("linkSend"));
    document.getElementById("msg").focus();
    document.getElementById("msg").select();
}

function openTabService(e) {
    let stLinks = document.getElementsByClassName("serviceTabLink");
    for (i = 0; i < stLinks.length; i++) {
        stLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("contentOnline").item(0).style.display = "none";
    document.getElementsByClassName("contentService").item(0).style.display = "block";
    document.getElementsByClassName("contentFriends").item(0).style.display = "none";
    document.getElementsByClassName("contentGroups").item(0).style.display = "none";
    e.style.backgroundColor = "#ccc";
}

function openTabFriends(e) {
    printFriendList();
    let stLinks = document.getElementsByClassName("serviceTabLink");
    for (i = 0; i < stLinks.length; i++) {
        stLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("contentOnline").item(0).style.display = "none";
    document.getElementsByClassName("contentService").item(0).style.display = "none";
    document.getElementsByClassName("contentFriends").item(0).style.display = "block";
    document.getElementsByClassName("contentGroups").item(0).style.display = "none";
    e.style.backgroundColor = "#ccc";
}

function openTabMyGroups(e) {
    printGroupsJSON();
    let stLinks = document.getElementsByClassName("serviceTabLink");
    for (i = 0; i < stLinks.length; i++) {
        stLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("contentOnline").item(0).style.display = "none";
    document.getElementsByClassName("contentService").item(0).style.display = "none";
    document.getElementsByClassName("contentFriends").item(0).style.display = "none";
    document.getElementsByClassName("contentGroups").item(0).style.display = "block";
    e.style.backgroundColor = "#ccc";
}

function openOnline(e) {
    let onlineLog = document.getElementById("onlineLog");
    onlineLog.innerHTML = "";

    var getOnlineUrl = `http://${document.location.host}${document.location.pathname}rest/user/getAllUsersOnline/500`;
    var ok = false;

    fetch(getOnlineUrl, {
        method: "get",
    })
    .then(function (response) {
        if (response.status === 200) {
            ok = true;
            return response.json();
        }
        console.log(response);
        return response.text();
    })
    .then(function (online) {
        console.log(online);
        if (ok) {
            online.forEach(user => {
                onlineLog.innerHTML += user.name + "\n";
            });
        } else {
            online.innerHTML += online + "\n";
        }
    })
    .catch(function (error) {
        console.log("Service: Error - ", error);
    });

    let stLinks = document.getElementsByClassName("serviceTabLink");
    for (i = 0; i < stLinks.length; i++) {
        stLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("contentService").item(0).style.display = "none";
    document.getElementsByClassName("contentFriends").item(0).style.display = "none";
    document.getElementsByClassName("contentGroups").item(0).style.display = "none";
    document.getElementsByClassName("contentOnline").item(0).style.display = "block";
    e.style.backgroundColor = "#ccc";

}

function openInputSearch(e) {

    let btLinks = document.getElementsByClassName("bottomTabLink");
    for (i = 0; i < btLinks.length; i++) {
        btLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("searchInputContainer").item(0).style.display = "none";
    document.getElementsByClassName("sendInputContainter").item(0).style.display = "none";
    document.getElementsByClassName("groupInputContainer").item(0).style.display = "none";
    document.getElementsByClassName("searchInputContainer").item(0).style.display = "block";

    e.style.backgroundColor = "#ccc";
}

function openInputSend(e) {

    let btLinks = document.getElementsByClassName("bottomTabLink");
    for (i = 0; i < btLinks.length; i++) {
        btLinks[i].style.backgroundColor = "";
    }

    document.getElementsByClassName("searchInputContainer").item(0).style.display = "none";
    document.getElementsByClassName("sendInputContainter").item(0).style.display = "none";
    document.getElementsByClassName("groupInputContainer").item(0).style.display = "none";
    document.getElementsByClassName("sendInputContainter").item(0).style.display = "block";

    e.style.backgroundColor = "#ccc";
}

function openInputGroups(e) {

    let btLinks = document.getElementsByClassName("bottomTabLink");
    for (i = 0; i < btLinks.length; i++) {
        btLinks[i].style.backgroundColor = "";
    }
    
    document.getElementsByClassName("searchInputContainer").item(0).style.display = "none";
    document.getElementsByClassName("sendInputContainter").item(0).style.display = "none";
    document.getElementsByClassName("groupInputContainer").item(0).style.display = "block";

    e.style.backgroundColor = "#ccc";
}