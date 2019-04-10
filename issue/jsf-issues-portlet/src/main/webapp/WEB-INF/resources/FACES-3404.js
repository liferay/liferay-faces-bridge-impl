function renderResults(testRenderURL, testActionURL, testResourceURL, portletNamespace,
	parameterValueWithCharactersThatMustBeEncoded) {

	var RENDER_URL_INDEX = 0,
		ACTION_URL_INDEX = 1,
		RESOURCE_URL_INDEX = 2,
		testURLs = [
			new URL(testRenderURL),
			new URL(testActionURL),
			new URL(testResourceURL)
		];

	var resultStatus = 'SUCCESS';
	var resultDetail = '';

	for (var i = 0; i < testURLs.length; i++) {

		var doAsUserId = testURLs[i].searchParams.get('doAsUserId');

		if (!doAsUserId || doAsUserId === '') {
			doAsUserId = testURLs[i].searchParams.get(portletNamespace + 'doAsUserId');
		}

		if (!doAsUserId || doAsUserId === '') {
			doAsUserId = testURLs[i].pathname.match(/[^\/]+\/?$/)[0];
		}

		if (!doAsUserId || doAsUserId !== parameterValueWithCharactersThatMustBeEncoded) {
			resultStatus = 'FAILED';
			resultDetail += '<br />Liferay parameter was not properly encoded in ';

			if (RENDER_URL_INDEX === i) {
				resultDetail += 'Render url.<br />';
			}
			else if (ACTION_URL_INDEX === i) {
				resultDetail += 'Action url.<br />';
			}
			else if (RESOURCE_URL_INDEX === i) {
				resultDetail += 'Resource url.<br />';
			}

			resultDetail += 'Parameter <code>doAsUserId</code> is <code>' +	doAsUserId +
				'</code> instead of <code>' + parameterValueWithCharactersThatMustBeEncoded + '</code>.';
		}
	}

	if (resultStatus === 'SUCCESS') {
		resultDetail = 'Liferay parameter was properly encoded in Render, Action, and Resource URLs.';
	}

	var portletBody = document.getElementById('FACES_3404_result_wrapper');
	portletBody.innerHTML +=
		'<div id="FACES-3404-result">' +
		'<p>Test: <span id="FACES-3404-test-name">FACES-3404</span></p>' +
		'<p>Status: <span id="FACES-3404-result-status">' + resultStatus + '</span></p>' +
		'<p>Detail: <span id="FACES-3404-result-detail">' + resultDetail + '</span></p>' +
		'</div>';
}
