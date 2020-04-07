function sendAlias() {
    var content = document.getElementById("msg").value;
    var recipient = document.getElementById("usr").value;
    var alias = document.getElementById("alias").value;
    var wombocombo = "alias " + recipient + " " + alias.trim();
    var json = JSON.stringify({
        "to":wombocombo,
        "content":content
    });
    console.log(json);

    ws.send(json);
}