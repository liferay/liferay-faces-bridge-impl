var scripts = document.getElementsByTagName('script');
var srcOfCurrentScript = scripts[scripts.length - 1].src;

if (srcOfCurrentScript.indexOf('FACES-2958') === -1 ||
	!/resource[.]js/.test(srcOfCurrentScript)) {
	throw new Error('Could not obtain FACES-2958 resource url.');
}

testURLEncoded(srcOfCurrentScript, function(){
	displayResult('SUCCESS', 'All special URL characters were correctly encoded.');
});