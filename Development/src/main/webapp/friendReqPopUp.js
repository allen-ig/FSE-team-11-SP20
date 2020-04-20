var friendsPopup = document.getElementById("friendsPopUp");
var btnFriends = document.getElementById("frButton");
var spanFriends = document.getElementById("closePopup");
btnFriends.onclick = function() {
    friendsPopup.style.display = "block";
};

spanFriends.onclick = function() {
    friendsPopup.style.display = "none";
};