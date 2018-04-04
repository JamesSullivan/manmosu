function setHints(currentElement) {
  clearHints()
  document.getElementById(currentElement.getAttribute('datahintid')).setAttribute('style', 'display: block;')
}
function clearHints() {
	  var hints = document.getElementsByClassName('hint')
	  var i = hints.length;
	  while (i--) {
	    hints[i].setAttribute('style', '');
	  }
}