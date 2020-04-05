var sender;

function sendGroup() {
    var content = document.getElementById("msg").value;
    var recipient = document.getElementById("usr").value;
    var group = "group ";
    var withGroup = group.concat(recipient);
    var json = JSON.stringify({
        "to": withGroup,
        "content": content
    });

    ws.send(json);
}

function addGroup() {
    var log = document.getElementById("log");
    var addGroupUrl = `http://${document.location.host}${document.location.pathname}rest/group/create`;

    var users = document.getElementById("gfUser").value.split(" ");
    var group = document.getElementById("gfGroup").value;
    var mods = document.getElementById("gfModerator").value.split(" ");
    var newGroup = {
        "name": group,
        "members": [{"name": sender}],
        "moderators": []
    };


    for (i = 0; i < users.length; i++) {
        if (users[i] != "") {
            newGroup["members"].push({"name": users[i]});
        }
    }
    for (i = 0; i < mods.length; i++) {
        if (mods[i] != "") {
            newGroup["moderators"].push({"name": mods[i]});
        }
    }

    var JSONgroup = JSON.stringify(newGroup);
    console.log(JSONgroup);

    var ok = false;

    fetch(addGroupUrl, {
        method: 'post',
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: JSONgroup
    })
        .then(function (response) {
            if (response.ok) {
                ok = true;
            }
            return response.text();
        })
        .then(function (text) {
            console.log("Service: ", text);
            log.innerHTML += "Service: " + text + "\n";
        })
        .then(function (text) {
            if (ok === true) {
                notifygroup(group, "you have been added to this group");
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
}

function addMember() {
    var log = document.getElementById("log");
    var addMemUrl = `http://${document.location.host}${document.location.pathname}rest/group/addUser`;

    var user = document.getElementById("gfUser").value;
    var group = document.getElementById("gfGroup").value;
    if(user !== "" && group !== "") {
        var json = JSON.stringify({
            "sender": sender,
            "user": user,
            "group": group
        });
        console.log(json);
    } else {
        log.innerHTML += "Service: no User or Group specified\n";
        return;
    }

    var ok = false;

    fetch(addMemUrl, {
        method: 'put',
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: json
    })
        .then(function (response) {
            if (response.ok) {
                ok = true;
            }
            return response.text();
        })
        .then(function (text) {
            console.log("Service: ", text);
            log.innerHTML += "Service: " + text + "\n";
        })
        .then(function (text) {
            if (ok === true) {
                notifygroup(group, "added " + user);
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
}

function addMod() {
    var log = document.getElementById("log");
    var addModUrl = `http://${document.location.host}${document.location.pathname}rest/group/addModerator`;

    var user = document.getElementById("gfModerator").value;
    var group = document.getElementById("gfGroup").value;
    if(user !== "" && group !== "") {
        var json = JSON.stringify({
            "sender": sender,
            "user": user,
            "group": group
        });
        console.log(json);
    } else {
        log.innerHTML += "Service: no Moderator or Group specified\n";
        return;
    }

    var ok = false;

    fetch(addModUrl, {
        method: 'put',
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: json
    })
        .then(function (response) {
            if (response.ok) {
                ok = true;
            }
            return response.text();
        })
        .then(function (text) {
            console.log("Service: ", text);
            log.innerHTML += "Service: " + text + "\n";
        })
        .then(function (text) {
            if (ok === true) {
                notifygroup(group, user + " is now a moderator");
            }
        })
        .catch(function (error) {
            console.log("Service: Error - ", error);
        });
}

function removeMember() {
    var log = document.getElementById("log");
    var remMemUrl = `http://${document.location.host}${document.location.pathname}rest/group/removeUser`;

    var user = document.getElementById("gfUser").value;
    var group = document.getElementById("gfGroup").value;
    if(user !== "" && group !== "") {
        var json = JSON.stringify({
            "sender": sender,
            "user": user,
            "group": group
        });
        console.log(json);
    } else {
        log.innerHTML += "Service: no User or Group specified\n";
        return;
    }

    var ok = false;
    notifygroup(group, "attempting to remove " + user + " as member")
        .then(function () {
            fetch(remMemUrl, {
                method: 'put',
                headers: {
                    "Content-Type": "application/json; charset=UTF-8"
                },
                body: json
            })
                .then(function (response) {
                    if (response.ok) {
                        ok = true;
                    }
                    return response.text();
                })
                .then(function (text) {
                    console.log("Service: ", text);
                    log.innerHTML += "Service: " + text + "\n";
                })
                .then(function (text) {
                    if (ok === true) {
                        notifygroup(group, "removed " + user + " from members");
                    } else {
                        notifygroup(group, "attempt to remove " + user + " from members failed");
                    }
                })
                .catch(function (error) {
                    console.log("Service: Error - ", error);
                });
        });
}

function removeMod() {
    var log = document.getElementById("log");
    var remModUrl = `http://${document.location.host}${document.location.pathname}rest/group/removeModerator`;

    var user = document.getElementById("gfModerator").value;
    var group = document.getElementById("gfGroup").value;
    if(user !== "" && group !== "") {
        var json = JSON.stringify({
            "sender": sender,
            "user": user,
            "group": group
        });
        console.log(json);
    } else {
        log.innerHTML += "Service: no User or Moderator specified\n";
        return;
    }

    var ok = false;
    var notified = notifygroup(group, "attempting to remove " + user + " as moderator")
        .then(function () {
            fetch(remModUrl, {
                method: 'put',
                headers: {
                    "Content-Type": "application/json; charset=UTF-8"
                },
                body: json
            })
                .then(function (response) {
                    if (response.ok) {
                        ok = true;
                    }
                    return response.text();
                })
                .then(function (text) {
                    console.log("Service: ", text);
                    log.innerHTML += "Service: " + text + "\n";
                })
                .then(function (text) {
                    if (ok === true) {
                        notifygroup(group, user + " no longer a moderator");
                    } else {
                        notifygroup(group, "attempt to remove " + user + "from moderators failed");
                    }
                })
                .catch(function (error) {
                    console.log("Service: Error - ", error);
                });
        });
}

function deleteGroup() {

    var log = document.getElementById("log");
    var delGroupUrl = `http://${document.location.host}${document.location.pathname}rest/group/delete`;

    var group = document.getElementById("gfGroup").value;
    if(group !== "") {
        var json = JSON.stringify({
            "sender": sender,
            "user": sender,
            "group": group
        });
        console.log(json);
    } else {
        log.innerHTML += "Service: no Group specified\n";
        return;
    }

    notifygroup(group, "attempting to delete group")
        .then(function () {
            fetch(delGroupUrl, {
                method: 'delete',
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
                    log.innerHTML += "Service: " + text + "\n";
                })
                .catch(function (error) {
                    console.log("Service: Error - ", error);
                });
        }).catch(function (err) {
        console.log(err);
    });
}

function notifygroup(groupName, groupMessage) {
    var g = "group ";
    var to = g.concat(groupName);
    var noteGroupMessage = JSON.stringify({
        "to": to,
        "content": groupMessage
    });
    ws.send(noteGroupMessage);
    return new Promise(function (resolve, reject) {
        setTimeout(() => resolve(1), 1000);
    });
}