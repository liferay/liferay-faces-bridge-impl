var portletBody = document.getElementsByClassName('liferay-faces-bridge-body')[0];
portletBody.innerHTML +=
	'<div id="mojarra4340WorkaroundResult">' +
	'<p>Test: <span id="MOJARRA-4340-WORKAROUND-test-name">MOJARRA-4340-workaround</span></p>' +
	'<p>Status: <span id="MOJARRA-4340-WORKAROUND-result-status">SUCCESS</span></p>' +
	'<p>Detail: <span id="MOJARRA-4340-WORKAROUND-result-detail">' +
	'Successfully loaded body script in Ajax request.</span></p>' +
	'</div>';
