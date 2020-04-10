function addStatus() {
  let serviceLog = document.getElementById("serviceLog");
  let setStatusUrl = `http://${document.location.host}${document.location.pathname}rest/user/status`;

  let status = document.getElementById("msg").value;
  let json = {
    "name": sender,
    "status": status
  };

  let JSONstatus = JSON.stringify(json);
  fetch(setStatusUrl, {
    method: 'post',
    headers: {
      "Content-Type": "application/json; charset=UTF-8"
    },
    body: JSONstatus
  })
  .then(function (response) {
    return response.text();
  })
  .then(function (text) {
    console.log("Service: ", text);
    serviceLog.innerHTML += "Service: " + text + "\n";
  })
  .catch(function (error) {
    console.log("Service: Error - ", error);
  });
}