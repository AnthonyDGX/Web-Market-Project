function langueAleatoire(){
	let tLangue=["Bonjour","Hello","Bongiorno","Hallo","Hola","	你好"];
	var langue = tLangue[Math.floor(Math.random()*tLangue.length)];
	return langue;
}

function choisiTexte(){
	let elt = document.getElementById("j2");
	elt.style.transition = "all 2s";
	elt.innerHTML = langueAleatoire();
}

setInterval(choisiTexte,1500);


