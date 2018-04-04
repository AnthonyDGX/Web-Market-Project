function langueAleatoire(){
	let tLangue=["Bienvenue","Welcome","Benvenuti","Willkommen","Bienvenidos","欢迎"];
	var langue = tLangue[Math.floor(Math.random()*tLangue.length)];
	return langue;
}

function choisiTexte(){
	let elt = document.getElementById("j2");
	elt.style.transition = "all 2s";
	elt.innerHTML = langueAleatoire();
}

setInterval(choisiTexte,1500);


