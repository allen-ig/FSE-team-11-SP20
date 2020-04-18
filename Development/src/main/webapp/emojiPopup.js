var popupEmoji = document.getElementById("emojiPopUp");
var btnEmoji = document.getElementById("emojiButton");
var spanEmoji = document.getElementById("closeEmoji");
btnEmoji.onclick = function() {
  popupEmoji.style.display = "block";
};

spanEmoji.onclick = function() {
  popupEmoji.style.display = "none";
};

window.onclick = function(event) {
  if (event.target == popupEmoji) {
    popupEmoji.style.display = "none";
  }
};

function addEmoji(e) {
  document.getElementById("msg").value += e.innerText;
}