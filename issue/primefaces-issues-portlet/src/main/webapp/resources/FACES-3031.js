// Unfortunately, FACES-3031 causes a client-side ParseException before onerror or oncomplete events fire.
// To work around this, listen for onsuccess and parse the partial response for errors.
function renderResults(data) {

	var testResultStatus = 'SUCCESS';
	var testResultDetail = 'State successfully saved and restored.';
	var errorNames = data.getElementsByTagName('error-name');

	if (errorNames.length > 0) {

		testResultStatus = 'FAILURE';
		testResultDetail = 'Failed to restore state due to the following error:\n<code>' +
			errorNames[0].innerHTML + ': ' +
			data.getElementsByTagName('error-message')[0].firstChild.nodeValue +
			'</code>. See server log for full stack trace.';
	}

	var faces3031Results = document.getElementById('FACES-3031-results');
	faces3031Results.innerHTML =
		'<p>Test: <span id="FACES-3031-test-name">FACES-3031</span></p>' +
		'<p>Status: <span id="FACES-3031-result-status">' + testResultStatus + '</span></p>' +
		'<p>Detail: <span id="FACES-3031-result-detail">' + testResultDetail + '</span></p>';
}
