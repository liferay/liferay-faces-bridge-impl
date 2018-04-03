var scripts = document.getElementsByTagName('script');
var srcOfCurrentScript;

for (var i = 0; i < scripts.length; i++) {

	if (scripts[i].src.indexOf('FACES-2958') > -1 &&
		/resourceViaAjax[.]js/.test(scripts[i].src)) {
		srcOfCurrentScript = scripts[i].src;
	}
}

if (!srcOfCurrentScript) {
	throw new Error('Could not obtain FACES-2958 resource url.');
}

testURLEncoded(srcOfCurrentScript, function(){
	displayResult('SUCCESS', 'All special URL characters were correctly encoded.');
});
