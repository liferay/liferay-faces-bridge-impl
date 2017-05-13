/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import java.io.StringWriter;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.xml.namespace.QName;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class EncodeActionURLTests {

	// Test is SingleRequest -- Render only
	// Test #6.5
	@BridgeTest(test = "encodeActionURLAbsoluteURLTest")
	public String encodeActionURLAbsoluteURLTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String ABSOLUTEURL_TEST_STRING = "http://www.apache.org";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (externalContext.encodeActionURL(ABSOLUTEURL_TEST_STRING).equals(ABSOLUTEURL_TEST_STRING)) {
			testBean.setTestResult(true,
				"encodeActionURL correctly returned an unchanged string when passed an absolute URL.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL didn't return an unchanged string when passed an absolute URL.  Test parameter: " +
				ABSOLUTEURL_TEST_STRING + " and encodeActionURL returned: " +
				externalContext.encodeActionURL(ABSOLUTEURL_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.7
	@BridgeTest(test = "encodeActionURLDirectLinkFalseTest")
	public String encodeActionURLDirectLinkFalseTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String DIRECTLINK_FALSE_TEST_STRING =
			"/test.jsp?firstParam=value&javax.portlet.faces.DirectLink=false&anotherParam=value";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				DIRECTLINK_FALSE_TEST_STRING).toString();

		if (!externalContext.encodeActionURL(testString).contains("javax.portlet.faces.DirectLink")) {
			testBean.setTestResult(true,
				"encodeActionURL correctly returned an url string without the javax.portlet.faces.DirectLink parameter when its value was false.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL didn't return an url string without the javax.portlet.faces.DirectLink parameter when its value was false.  Test parameter: " +
				testString + " and encodeActionURL returned: " + externalContext.encodeActionURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.6
	@BridgeTest(test = "encodeActionURLDirectLinkTrueTest")
	public String encodeActionURLDirectLinkTrueTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String DIRECTLINK_TRUE_TEST_STRING =
			"/test.jsp?firstParam=value&javax.portlet.faces.DirectLink=true&anotherParam=value";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest r = (PortletRequest) externalContext.getRequest();

		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				DIRECTLINK_TRUE_TEST_STRING).toString();

		String s = new StringBuffer(testString.length() + 20).append(r.getScheme()).append("://").append(
				r.getServerName()).append(":").append(r.getServerPort()).append(testString).toString();

		if (externalContext.encodeActionURL(testString).equalsIgnoreCase(s)) {
			testBean.setTestResult(true,
				"encodeActionURL correctly returned an absolute URL representing the DirectLink url and it correctly contains the javax.portlet.faces.DirectLink parameter with a value of true.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL didn't return an absolute URL representing the DirectLink url or it didn't contain the javax.portlet.faces.DirectLink parameter with a value of true.  Expected: " +
				s + " and encodeActionURL returned: " + externalContext.encodeActionURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.10
	@BridgeTest(test = "encodeActionURLJSFViewActionTest")
	public String encodeActionURLJSFViewActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLJSFViewActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);
			testBean.setTestResult(true, "encodeActionURL correctly encoded a portlet action URL.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.103
	@BridgeTest(test = "encodeActionURLJSFViewEventTest")
	public String encodeActionURLJSFViewEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);
			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL encoded during the event phase.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.18
	@BridgeTest(test = "encodeActionURLJSFViewRenderTest")
	public String encodeActionURLJSFViewRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			testBean.setTestResult(true, "encodeActionURL correctly encoded a portlet action URL.");

			return "encodeActionURLJSFViewRenderTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			return Constants.TEST_SUCCESS;
		}
	}

	// Test Single Render followed by resource request
	// Test #6.111
	@BridgeTest(test = "encodeActionURLJSFViewResourceTest")
	public String encodeActionURLJSFViewResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING = "/tests/viewLink.jsf";

		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with params as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with params as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}

	// Test is SingleRequest -- Render only
	// Test #6.9
	@BridgeTest(test = "encodeActionURLPortletActionTest")
	public String encodeActionURLPortletActionTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String PORTLET_ACTION_TEST_STRING = "portlet:action?param1=value1&param2=value2";
		final String PORTLET_ACTION_TEST_STRING_XMLENCODED = "portlet:action?param1=value1&amp;param2=value2";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		MimeResponse response = (MimeResponse) externalContext.getResponse();
		PortletURL portletURL = response.createActionURL();
		portletURL.setParameter("param1", "value1");
		portletURL.setParameter("param2", "value2");

		StringWriter sw = new StringWriter(50);
		String portletEncoded = null;

		try {
			portletURL.write(sw, true);
			portletEncoded = sw.toString();
		}
		catch (Exception e) {
			portletEncoded = portletURL.toString();
		}

		// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
		// encoding depends on what is past in to it -- make sure we send in a string
		// with the same encoding as compare string.
		String bridgeEncoded = null;

		if (EncodeURLTestUtil.isStrictXhtmlEncoded(portletEncoded)) {
			bridgeEncoded = externalContext.encodeActionURL(PORTLET_ACTION_TEST_STRING_XMLENCODED);
		}
		else {
			bridgeEncoded = externalContext.encodeActionURL(PORTLET_ACTION_TEST_STRING);
		}

		if (bridgeEncoded.equals(portletEncoded)) {
			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL as an actionURL using the portlet: syntax.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL incorrectly encoded a portlet action URL as an actionURL using the portlet: syntax.  In encoding: " +
				PORTLET_ACTION_TEST_STRING + " the bridge returned: " + bridgeEncoded +
				" but the corresponding portletURL encoding returned: " + portletEncoded);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.8
	@BridgeTest(test = "encodeActionURLPortletRenderTest")
	public String encodeActionURLPortletRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String PORTLET_RENDER_TEST_STRING = "portlet:render?param1=value1&param2=value2";
		final String PORTLET_RENDER_TEST_STRING_XMLENCODED = "portlet:render?param1=value1&amp;param2=value2";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		MimeResponse response = (MimeResponse) externalContext.getResponse();
		PortletURL portletURL = response.createRenderURL();
		portletURL.setParameter("param1", "value1");
		portletURL.setParameter("param2", "value2");

		StringWriter sw = new StringWriter(50);
		String portletEncoded = null;

		try {
			portletURL.write(sw, true);
			portletEncoded = sw.toString();
		}
		catch (Exception e) {
			portletEncoded = portletURL.toString();
		}

		// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
		// encoding depends on what is past in to it -- make sure we send in a string
		// with the same encoding as compare string.
		String bridgeEncoded = null;

		if (EncodeURLTestUtil.isStrictXhtmlEncoded(portletEncoded)) {
			bridgeEncoded = externalContext.encodeActionURL(PORTLET_RENDER_TEST_STRING_XMLENCODED);
		}
		else {
			bridgeEncoded = externalContext.encodeActionURL(PORTLET_RENDER_TEST_STRING);
		}

		if (bridgeEncoded.equals(portletEncoded)) {
			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet render URL as an actionURL using the portlet: syntax.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL incorrectly encoded a portlet render URL as an actionURL using the portlet: syntax.  In encoding: " +
				PORTLET_RENDER_TEST_STRING + " the bridge returned: " + bridgeEncoded +
				" but the corresponding portletURL encoding returned: " + portletEncoded);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.102
	@BridgeTest(test = "encodeActionURLPortletResourceTest")
	public String encodeActionURLPortletResourceTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String PORTLET_RENDER_TEST_STRING =
			"portlet:resource?_jsfBridgeViewId=/tests/singleRequestTest.xhtml&param1=value1&param2=value2";
		final String PORTLET_RENDER_TEST_STRING_XMLENCODED =
			"portlet:resource?_jsfBridgeViewId=/tests/singleRequestTest.xhtml&amp;param1=value1&amp;param2=value2";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		MimeResponse response = (MimeResponse) externalContext.getResponse();
		ResourceURL portletURL = response.createResourceURL();
		portletURL.setParameter("_jsfBridgeViewId", "/tests/singleRequestTest.xhtml");
		portletURL.setParameter("param1", "value1");
		portletURL.setParameter("param2", "value2");

		StringWriter sw = new StringWriter(50);
		String portletEncoded = null;

		try {
			portletURL.write(sw, true);
			portletEncoded = sw.toString();
		}
		catch (Exception e) {
			portletEncoded = portletURL.toString();
		}

		// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
		// encoding depends on what is past in to it -- make sure we send in a string
		// with the same encoding as compare string.
		String bridgeEncoded = null;

		if (EncodeURLTestUtil.isStrictXhtmlEncoded(portletEncoded)) {
			bridgeEncoded = externalContext.encodeActionURL(PORTLET_RENDER_TEST_STRING_XMLENCODED);
		}
		else {
			bridgeEncoded = externalContext.encodeActionURL(PORTLET_RENDER_TEST_STRING);
		}

		if (bridgeEncoded.equals(portletEncoded)) {
			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet resource URL as an actionURL using the portlet: syntax.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL incorrectly encoded a portlet resource URL as an actionURL using the portlet: syntax.  In encoding: " +
				PORTLET_RENDER_TEST_STRING + " the bridge returned: " + bridgeEncoded +
				" but the corresponding portletURL encoding returned: " + portletEncoded);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.4
	@BridgeTest(test = "encodeActionURLPoundCharTest")
	public String encodeActionURLPoundCharTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String POUNDCHAR_TEST_STRING = "#AnchorReference";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (externalContext.encodeActionURL(POUNDCHAR_TEST_STRING).equals(POUNDCHAR_TEST_STRING)) {
			testBean.setTestResult(true,
				"encodeActionURL correctly returned an unchanged string when the string was prefixed with #.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeActionURL didn't return an unchanged string when the string was prefixed with #.  Test parameter: " +
				POUNDCHAR_TEST_STRING + " and encodeActionURL returned: " +
				externalContext.encodeActionURL(POUNDCHAR_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.12
	@BridgeTest(test = "encodeActionURLWithInvalidModeActionTest")
	public String encodeActionURLWithInvalidModeActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithInvalidModeActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode portletMode = portletRequest.getPortletMode();

			if ((portletMode == null) || !portletMode.toString().equalsIgnoreCase("view")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid mode and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.105
	@BridgeTest(test = "encodeActionURLWithInvalidModeEventTest")
	public String encodeActionURLWithInvalidModeEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode portletMode = portletRequest.getPortletMode();

			if ((portletMode == null) || !portletMode.toString().equalsIgnoreCase("view")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid mode and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.20
	@BridgeTest(test = "encodeActionURLWithInvalidModeRenderTest")
	public String encodeActionURLWithInvalidModeRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode portletMode = portletRequest.getPortletMode();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((portletMode == null) || !portletMode.toString().equalsIgnoreCase("view")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
			else {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid mode and properly encoding the parameter.");
			}

			return "encodeActionURLWithInvalidModeActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.113
	@BridgeTest(test = "encodeActionURLWithInvalidModeResourceTest")
	public String encodeActionURLWithInvalidModeResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with invalid mode as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with invalid mode as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.16
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityActionTest")
	public String encodeActionURLWithInvalidSecurityActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithInvalidSecurityActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			if (portletRequest.isSecure()) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded an invalid portlet security state.  The resulting request wasn't in the expected 'non-secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL ontaining an invalid security state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.109
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityEventTest")
	public String encodeActionURLWithInvalidSecurityEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			if (portletRequest.isSecure()) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded an invalid portlet security state.  The resulting request wasn't in the expected 'non-secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL ontaining an invalid security state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.24
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityRenderTest")
	public String encodeActionURLWithInvalidSecurityRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (portletRequest.isSecure()) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded an invalid portlet security state.  The resulting request wasn't in the expected 'non-secure' mode.");

				return Constants.TEST_FAILED;
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL ontaining an invalid security state and parameter.");
			}

			return "encodeActionURLWithInvalidSecurityRenderTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.117
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityResourceTest")
	public String encodeActionURLWithInvalidSecurityResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with invalid security as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with invalid security as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.14
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateActionTest")
	public String encodeActionURLWithInvalidWindowStateActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithInvalidWindowStateActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("normal")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid window state and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.107
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateEventTest")
	public String encodeActionURLWithInvalidWindowStateEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("normal")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid window state and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.22
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateRenderTest")
	public String encodeActionURLWithInvalidWindowStateRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("normal")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
			else {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid window state and properly encoding the parameter.");
			}

			return "encodeActionURLWithInvalidWindowStateRenderTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.115
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateResourceTest")
	public String encodeActionURLWithInvalidWindowStateResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with invalid window state as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with invalid window state as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.11
	@BridgeTest(test = "encodeActionURLWithModeActionTest")
	public String encodeActionURLWithModeActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithModeActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode portletMode = portletRequest.getPortletMode();

			if ((portletMode == null) || !portletMode.toString().equalsIgnoreCase("edit")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet mode.  The resulting request wasn't in the expected 'edit' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new mode and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.104
	@BridgeTest(test = "encodeActionURLWithModeEventTest")
	public String encodeActionURLWithModeEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode portletMode = portletRequest.getPortletMode();

			if ((portletMode == null) || !portletMode.toString().equalsIgnoreCase("edit")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet mode.  The resulting request wasn't in the expected 'edit' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter during the event phase.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter during the event phase.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new mode and parameter during the event phase.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.19
	@BridgeTest(test = "encodeActionURLWithModeRenderTest")
	public String encodeActionURLWithModeRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (Bridge.PortletPhase.HEADER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {
			Map<String, String> requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();

			String testName = testBean.getTestName();

			// Parameter/Mode encoded in the faces-config.xml target
			String portletMode = requestParameterMap.get(testName + "-portletMode");

			// Check that the parameter came along too
			String param1 = requestParameterMap.get(testName + "-param1");

			if (!"edit".equals(portletMode)) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet mode.  The resulting request wasn't in the expected 'edit' mode.");
			}
			else if (param1 == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!param1.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					param1);
			}
			else {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a new mode and parameter.");
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
		else if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithModeRenderTest"; // action Navigation result
		}
		else {
			return "";
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.112
	@BridgeTest(test = "encodeActionURLWithModeResourceTest")
	public String encodeActionURLWithModeResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=edit&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with mode as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with mode as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.17
	@BridgeTest(test = "encodeActionURLWithParamActionTest")
	public String encodeActionURLWithParamActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithParamActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();

			// Parameter encoded in the faces-config.xml target
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((paramValue != null) && paramValue.equals("testValue")) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a parameter.");

				return Constants.TEST_SUCCESS;
			}
			else {

				if (paramValue == null) {
					testBean.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request didn't contain the expected 'param1' parameter.");
				}
				else {
					testBean.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
						paramValue);
				}
			}

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.110
	@BridgeTest(test = "encodeActionURLWithParamEventTest")
	public String encodeActionURLWithParamEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Parameter encoded in the faces-config.xml target
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((paramValue != null) && paramValue.equals("testValue")) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a parameter during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {

				if (paramValue == null) {
					testBean.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter during the event phase.  The resulting request didn't contain the expected 'param1' parameter.");
				}
				else {
					testBean.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter during the event phase.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
						paramValue);
				}
			}

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.25
	@BridgeTest(test = "encodeActionURLWithParamRenderTest")
	public String encodeActionURLWithParamRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			// Parameter encoded in the faces-config.xml target
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((paramValue != null) && paramValue.equals("testValue")) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a parameter.");
			}
			else {

				if (paramValue == null) {
					testBean.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request didn't contain the expected 'param1' parameter.");
				}
				else {
					testBean.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
						paramValue);
				}
			}

			return "encodeActionURLWithParamRenderTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.118
	@BridgeTest(test = "encodeActionURLWithParamResourceTest")
	public String encodeActionURLWithParamResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING = "/tests/viewLink.jsf?param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with params as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with params as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";

	}

	// Test is MultiRequest -- Render/Action
	// Test #6.15
	@BridgeTest(test = "encodeActionURLWithSecurityActionTest")
	public String encodeActionURLWithSecurityActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithSecurityActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Secure encoded in the faces-config.xml target
			if (!portletRequest.isSecure()) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the portlet security state.  The resulting request wasn't in the expected 'secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL accessed in a secure state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.108
	@BridgeTest(test = "encodeActionURLWithSecurityEventTest")
	public String encodeActionURLWithSecurityEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Secure encoded in the faces-config.xml target
			if (!portletRequest.isSecure()) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the portlet security state.  The resulting request wasn't in the expected 'secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL accessed in a secure state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.23
	@BridgeTest(test = "encodeActionURLWithSecurityRenderTest")
	public String encodeActionURLWithSecurityRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (!portletRequest.isSecure()) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the portlet security state.  The resulting request wasn't in the expected 'secure' mode.");
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
			else {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL accessed in a secure state and parameter.");
			}

			return "encodeActionURLWithSecurityRenderTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.116
	@BridgeTest(test = "encodeActionURLWithSecurityResourceTest")
	public String encodeActionURLWithSecurityResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with security as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with security as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.13
	@BridgeTest(test = "encodeActionURLWithWindowStateActionTest")
	public String encodeActionURLWithWindowStateActionTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithWindowStateActionTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("maximized")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new window state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.106
	@BridgeTest(test = "encodeActionURLWithWindowStateEventTest")
	public String encodeActionURLWithWindowStateEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(Constants.EVENT_QNAME, Constants.EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("maximized")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new window state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.21
	@BridgeTest(test = "encodeActionURLWithWindowStateRenderTest")
	public String encodeActionURLWithWindowStateRenderTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("maximized")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
			else {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a new window state and parameter.");
			}

			return "encodeActionURLWithWindowStateRenderTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.114
	@BridgeTest(test = "encodeActionURLWithWindowStateResourceTest")
	public String encodeActionURLWithWindowStateResourceTest(TestBean testBean) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(externalContext.getRequestContextPath()).append(
				ENCODE_ACTIONURL_TEST_STRING).toString();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testBean.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = externalContext.encodeActionURL(testString);
			String otherPhaseEncoded = (String) sessionMap.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			sessionMap.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testBean.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testBean.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url as an actionURL with window state during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with window state as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (sessionMap.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			sessionMap.put("com.liferay.faces.bridge.tck.encodedURL", externalContext.encodeActionURL(testString));
		}

		return "";
	}
}
