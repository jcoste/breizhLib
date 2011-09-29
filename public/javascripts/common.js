function displayHide(id, tab) {
        var div = document.getElementById(id);
        var li = document.getElementById('li' + id);
        li.className = "active";
        if (div.style.visibility == 'hidden') {
            div.style.visibility = 'visible';
            div.style.display = "block";
            for (var i = 0; i < tab.length; ++i)
                hide(tab[i]);
        }
}



function hide(id) {
       var div = document.getElementById(id);
       var li = document.getElementById('li' + id);
       li.className = "";
       div.style.visibility = 'hidden';
       div.style.display = "none";
}
