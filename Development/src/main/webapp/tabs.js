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
                    userLog.innerHTML += message.from + ": " + message.content;
                    userLog.innerHTML += formatDate(new Date(message.timestamp)) + "\n";
                });
            } else {
                userLog.innerHTML += messages + "\n";
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
    openTabUser(document.getElementById("linkUser"));
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
                    groupLog.innerHTML += message.from + ": " + message.content;
                    groupLog.innerHTML += formatDate(new Date(message.timestamp)) + "\n";
                });
            } else {
                groupLog.innerHTML += messages + "\n";
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
    openTabGroup(document.getElementById("linkGroup"));
}

function openTabService(e) {
    let stLinks = document.getElementsByClassName("serviceTabLink");
    for (i = 0; i < stLinks.length; i++) {
        stLinks[i].style.backgroundColor = "";
    }

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

    document.getElementsByClassName("contentService").item(0).style.display = "none";
    document.getElementsByClassName("contentFriends").item(0).style.display = "none";
    document.getElementsByClassName("contentGroups").item(0).style.display = "block";
    e.style.backgroundColor = "#ccc";
}