var popupAlias = document.getElementById("aliasPopUp");
var btnAlias = document.getElementById("aliasButton");
var spanAlias = document.getElementById("closeAlias");
btnAlias.onclick = function() {
    popupAlias.style.display = "block";
};

spanAlias.onclick = function() {
    popupAlias.style.display = "none";
};