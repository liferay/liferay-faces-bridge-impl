function testURLEncoded(url, onSuccess, onFailure) {

	var resultDetail = '';
	var indexOfFriendlyURLSeparator = url.indexOf('/-/');
	var friendlyURL = indexOfFriendlyURLSeparator > -1;
	var indexOfQuesryString = url.indexOf('?');

	if (friendlyURL) {
		indexOfQuesryString = indexOfFriendlyURLSeparator + '/-/'.length;
	}

	var queryString = url.substring(indexOfQuesryString + 1);
	var parameterSeparator = '&';

	if (friendlyURL) {
		parameterSeparator = '/';
	}

	var parameters = queryString.split(parameterSeparator);
	var testParamFound = false;

	for (var i = 0; i < parameters.length; i++) {

		var nameValueSeparator = '=';

		if (friendlyURL) {
			nameValueSeparator = '_';
		}

		var parameter = parameters[i].split(nameValueSeparator);
		var name = parameter[0];
		var indexOfParamPrefix = name.indexOf('param');

		if (parameter.length > 1 && indexOfParamPrefix > -1) {

			var value = parameter[1];
			var nameSuffix = name.substring(indexOfParamPrefix + 'param'.length);

			if (nameSuffix.length < 1) {
				continue;
			}

			testParamFound = true;

			if (value !== 'encoded%' + nameSuffix + 'param%' + nameSuffix + 'value' &&
				!(nameSuffix === '20' && value === "encoded+param+value")) {

				if (resultDetail.length > 0) {
					resultDetail += ', ';
				}

				resultDetail += 'ASCII character with hex code <code>0x' + nameSuffix +
					'</code> (should be encoded as &quot;<code>%' + nameSuffix + '</code>&quot;)';
			}
		}
	}

	var encodedURLParamFound = url.indexOf('urlParam=http%3A%2F%2Fliferay.com%3Fname1%3Dvalue1%26name2%3Dvalue2')
			> -1;

	if (!encodedURLParamFound || !testParamFound || resultDetail.length > 0) {

		if (!encodedURLParamFound) {
			resultDetail =
				'The url &quot;http://liferay.com?name1=value1&amp;name2=value2&quot; was not found encoded on the URL.';
		}
		else if (!testParamFound) {
			resultDetail = 'No test params found on URL.';
		}
		else {
			resultDetail = 'The following characters were not encoded: ' + resultDetail + '.';
		}

		displayResult('FAILED', resultDetail);

		if (onFailure) {
			onFailure();
		}
	}
	else if (onSuccess) {
		onSuccess();
	}
}

function displayResult(resultStatus, resultDetail) {

	var portletBody = document.getElementsByClassName('liferay-faces-bridge-body')[0];
	portletBody.innerHTML +=
		'<div id="FACES-2958-result">' +
		'<p>Test: <span id="FACES-2958-test-name">FACES-2958</span></p>' +
		'<p>Status: <span id="FACES-2958-result-status">' + resultStatus + '</span></p>' +
		'<p>Detail: <span id="FACES-2958-result-detail">' + resultDetail + '</span></p>' +
		'</div>';
}
