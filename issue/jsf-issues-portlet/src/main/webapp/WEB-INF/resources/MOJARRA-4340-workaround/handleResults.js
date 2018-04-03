jsf.ajax.addOnEvent(function(data) {

	if (data.status === 'success') {
		var stylesheetParamsUnnecessarilyEscaped = true;
		var stylesheets = document.getElementsByTagName('link');

		for (var i = 0; i < stylesheets.length; i++) {

			if (stylesheets[i].href.indexOf('test.css') > -1 &&
				stylesheets[i].href.indexOf('firstParam=1&secondParam=2') > -1) {
				stylesheetParamsUnnecessarilyEscaped = false;
			}
		}

		var scripts = document.getElementsByTagName('script');

		if (!stylesheetParamsUnnecessarilyEscaped) {

			for (var i = 0; i < scripts.length; i++) {

				if (scripts[i].src.indexOf('test.js') > -1 &&
					scripts[i].src.indexOf('firstParam=1&secondParam=2') > -1) {

					var portletBody = document.getElementsByClassName('liferay-faces-bridge-body')[0];
					portletBody.innerHTML +=
						'<div id="mojarra4345WorkaroundResult">' +
						'<p>Test: <span id="MOJARRA-4345-WORKAROUND-test-name">' +
						'MOJARRA-4340-workaround</span></p>' +
						'<p>Status: <span id="MOJARRA-4345-WORKAROUND-result-status">' +
						'SUCCESS</span></p>' +
						'<p>Detail: <span id="MOJARRA-4345-WORKAROUND-result-detail">' +
						'Resource URLs loaded via Ajax were not double escaped.</span></p>' +
						'</div>';
				}
			}
		}
	}
});
