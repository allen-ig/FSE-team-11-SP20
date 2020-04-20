var startWindow = document.getElementById("startWindow");
var connectWindow = document.getElementById("connectWindow");
var newUserWindow = document.getElementById("newUserWindow");


async function createUser(newUser, newPass) {
  let addUserUrl = `http://${document.location.host}${document.location.pathname}rest/user/create`;
  let newUserObj = {
    "name": newUser,
    "password": newPass
  };

  let jsonUser = JSON.stringify(newUserObj);

  let allGood = await fetch(addUserUrl, {
    method: 'post',
    headers: {
      "Content-Type": "application/json; charset=UTF-8"
    },
    body: jsonUser
  })
  .then(function (response) {
    if (response.status === 200) {
      console.log("User" + newUser + "Created");
      return true;
    } else {
      console.log("Error in creating user", response);
      return false;
    }
  })
  .catch(function (error) {
    console.log("Service: ", error);
    return false;
  });
  return allGood;
}

async function authenticate(username, password) {
  let slog = document.getElementById("serviceLog");

  let allgood = await fetch(
      `http://${document.location.host}${document.location.pathname}rest/user/${username}`)
  .then(response => response.json())
  .then(response => {
    if ("undefined" === typeof(response.password) || response.password === null) {
      console.log("relic user account found");
      return true;
    } else {
      return response.password === password;
    }
  })
  .catch(function (error) {
    console.log("Authentication error: ", error);
    return false;
  });
  return allgood;
}

function goToLogin() {
  document.getElementById('connectWindow').style.display='none';
  document.getElementById('startWindow').style.display='none';
  document.getElementById('newUserWindow').style.display='none';
  document.getElementById('connectWindow').style.display='block';
}

function goToCreate() {
  document.getElementById('startWindow').style.display='none';
  document.getElementById('newUserWindow').style.display='none';
  document.getElementById('connectWindow').style.display='none';
  document.getElementById('newUserWindow').style.display='block';
}

async function startCreateUser() {
  document.getElementById("newPassConf").style.backgroundColor = "white";
  document.getElementById("newUser").placeholder = 'Username';

  let newUserName = document.getElementById("newUser").value;
  let newPass = document.getElementById("newPass").value;
  let newPassConf = document.getElementById("newPassConf").value;

  if (newPass === newPassConf) {
    if (newPass === null) {
      newPass = "";
    }
    let success = await createUser(newUserName, newPass);
    if(success === true) {
      goToLogin();
    } else {
      document.getElementById("newUser").value = "";
      document.getElementById("newUser").placeholder = 'Name taken, try another';
    }
  } else {
    document.getElementById("newPassConf").style.backgroundColor = "#db524d";
  }
}