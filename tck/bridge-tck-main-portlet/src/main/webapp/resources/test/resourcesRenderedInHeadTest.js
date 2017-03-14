(function() {

	function getTestHeadElementId(element) {

		var testHeadElementId = null;
		var testHeadElementIds = window.getTestHeadElementIds();

		for (var i = 0; i < testHeadElementIds.length; i++) {

			if (element.id.indexOf(testHeadElementIds[i]) > -1) {

				testHeadElementId = testHeadElementIds[i];
				break;
			}
		}

		return testHeadElementId;
	}

	window.onload = function () {

		var actualTestHeadElementIds = [];
		var head = document.getElementsByTagName('head')[0];
		var headResources = Array.prototype.slice.call(head.getElementsByTagName('link'), 0);
		headResources = headResources.concat(Array.prototype.slice.call(head.getElementsByTagName('style'), 0));
		headResources = headResources.concat(Array.prototype.slice.call(head.getElementsByTagName('script'), 0));

		for (var i = 0; i < headResources.length; i++) {

			var testHeadElementId = getTestHeadElementId(headResources[i]);

			if (testHeadElementId) {
				actualTestHeadElementIds.push(testHeadElementId);
			}
		}

		var resultsElement = document.getElementById('resourcesRenderedInHeadTestResults');

		if (resultsElement) {

			var testHeadElementIds = window.getTestHeadElementIds();
			var resourcesNotRendered = '';

			for (var i = 0; i < testHeadElementIds.length; i++) {

				if (actualTestHeadElementIds.indexOf(testHeadElementIds[i]) === -1) {

					if (resourcesNotRendered.length > 0) {
						resourcesNotRendered += ', ';
					}

					resourcesNotRendered += testHeadElementIds[i];
				}
			}

			var testResultStatus = 'SUCCESS';
			var testResultDetail = 'Resources correctly rendered in the &lt;head&gt; section.';

			if (resourcesNotRendered.length > 0) {

				testResultStatus = 'FAILURE';
				testResultDetail = 'The following resources were not rendered in the &lt;head&gt; section: ' +
						resourcesNotRendered;
			}

			resultsElement.innerHTML =
					'<p>Test: <span id="resourcesRenderedInHeadTest-test-name">resourcesRenderedInHeadTest</span></p>' +
					'<p>Status: <span id="resourcesRenderedInHeadTest-result-status">' + testResultStatus +'</span></p>' +
					'<p>Detail: <span id="resourcesRenderedInHeadTest-result-detail">' + testResultDetail + '</span></p>';
		}
	};
})();
