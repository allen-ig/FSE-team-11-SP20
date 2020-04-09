/**
 * Sends a message with an alias to a user. Only one user.
 */
function sendAlias() {
    let content = document.getElementById("msg").value;
    let recipient = document.getElementById("usr").value;
    let alias = document.getElementById("alias").value;
    let wombocombo = "alias " + recipient + " " + alias.trim();
    let json = JSON.stringify({
        "to":wombocombo,
        "content":content
    });
    console.log(json);

    ws.send(json);
}

/**
 * Sends a group alias message to chat end point. does not preserve multi-group functionality.
 */
function groupAlias() {
    let content = document.getElementById("msg").value;
    let recipient = document.getElementById("usr").value;
    recipient = recipient.split(" ");
    let alias = document.getElementById("alias").value;
    let wombocombo = "groupalias" + " " + recipient[0] + " " + alias.trim();
    let json = JSON.stringify({
        "to":wombocombo,
        "content":content
    });
    console.log(json);

    ws.send(json);
}