if (window.FACES_3175reproducer) {

	var portletBody = document.getElementsByClassName('liferay-faces-bridge-body')[0];
	portletBody.innerHTML =
		'<p>Test: <span id="FACES-3175-test-name">FACES-3175</span></p>' +
		'<p>Status: <span id="FACES-3175-result-status">FAILED</span></p>' +
		'<p>Detail: <span id="FACES-3175-result-detail">' +
		'Reloaded FACES-3175:reproducer.js resource unnecessarily.</span></p>';
}

window.FACES_3175reproducer = true;
window.onload = function() {

	var portletBody = document.getElementsByClassName('liferay-faces-bridge-body')[0];
	portletBody.style.display = null;
};
