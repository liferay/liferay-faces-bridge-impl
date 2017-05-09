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

import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.ActionRequest;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.filter.ActionRequestWrapper;
import javax.portlet.filter.RenderRequestWrapper;
import javax.xml.namespace.QName;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.common.util.HTTPUtils;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	public static final String EVENT_QNAME = "http://liferay.com/faces/event_ns";
	public static final String EVENT_NAME = "faces.liferay.com.tck.testEvent";

	// Test is SingleRequest -- Render only
	// Test #6.3
	@BridgeTest(test = "dispatchUsesForwardTest")
	public String dispatchUsesForwardTest(TestBean testBean) {
		testBean.setTestComplete(true);

		// Some servlet containers wrap their request/response before invoking the dispatched servlet +
		// handle the servlet defined special (include/forward) attributes in a separate list which is
		// managed in the wrapped request -- Pluto (2.0) does this.  This means that inside of a JSF
		// expression running in a JSP we can't merely get the externalContext and check if the
		// forward attribute is set.  Rather we must do an EL evaluation on the request object to
		// do the check.  Unfortunately ther is no easy way to get the JSP elcontext -- so instead
		// do the check in the JSP itself and set a request attribute we will have access to.
		Boolean value = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(
				"com.liferay.faces.bridge.tck.dispatchForward");

		if (value == null) {
			testBean.setTestResult(false,
				"Unable to acquire the request attribute containing the result that should have been set in the JSP page.");

			return Constants.TEST_FAILED;
		}
		else if (value.booleanValue()) {
			testBean.setTestResult(true,
				"Successfully accessed javax.servlet.forward.servletPath attribute indicating we are inside a dispatch.forward");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"javax.servlet.forward.servletPath not set, but it would be if we are inside a dispatch.forward");

			return Constants.TEST_FAILED;
		}
	}

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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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

		if (isStrictXhtmlEncoded(portletEncoded)) {
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

		if (isStrictXhtmlEncoded(portletEncoded)) {
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

		if (isStrictXhtmlEncoded(portletEncoded)) {
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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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

		if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {
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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

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

	// Test is SingleRequest -- Render only
	// Test #6.32
	@BridgeTest(test = "encodeResourceURLBackLinkTest")
	public String encodeResourceURLBackLinkTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String URL_BACKLINK_TEST_STRING = "/resources/myImage.jpg?javax.portlet.faces.BackLink=myBackLinkParam";
		final String URL_BACKLINK_VERIFY_STRING = "/resources/myImage.jpg?myBackLinkParam=";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

		// compute what the backLink should be
		String actionURL = externalContext.encodeActionURL(viewHandler.getActionURL(facesContext,
					facesContext.getViewRoot().getViewId()));
		String testString = viewHandler.getResourceURL(facesContext, URL_BACKLINK_TEST_STRING);
		String verifyString = null;

		try {
			verifyString = viewHandler.getResourceURL(facesContext, URL_BACKLINK_VERIFY_STRING) +
				HTTPUtils.encode(actionURL, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			testBean.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// According to bridge rules since string passed in isn't xml strict encoded the result won't be as well
		// So ensure compares match by stripping from the one generated by the portlet container (if it exists)
		if (externalContext.encodeResourceURL(testString).equals(
					((PortletResponse) externalContext.getResponse()).encodeURL(verifyString).replace("&amp;", "&"))) {
			testBean.setTestResult(true, "encodeResourceURL correctly encoded an URL with a backLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL didn't correctly encoded an URL with a backLink.  Expected: " +
				((PortletResponse) externalContext.getResponse()).encodeURL(verifyString) +
				" but encodeResourceURL returned: " + externalContext.encodeResourceURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.27
	@BridgeTest(test = "encodeResourceURLForeignExternalURLBackLinkTest")
	public String encodeResourceURLForeignExternalURLBackLinkTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String FOREIGNEXTERNALURL_BACKLINK_TEST_STRING =
			"http://www.apache.org?javax.portlet.faces.BackLink=myBackLinkParam";
		final String FOREIGNEXTERNALURL_BACKLINK_VERIFY_STRING = "http://www.apache.org?myBackLinkParam=";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// compute what the backLink should be
		String actionURL = externalContext.encodeActionURL(facesContext.getApplication().getViewHandler().getActionURL(
					facesContext, facesContext.getViewRoot().getViewId()));
		String verifyString = null;

		try {
			verifyString = FOREIGNEXTERNALURL_BACKLINK_VERIFY_STRING + HTTPUtils.encode(actionURL, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			testBean.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// According to bridge rules since string passed in isn't xml strict encoded the result won't be as well
		// So ensure compares match by stripping from the one generated by the portlet container (if it exists)
		if (externalContext.encodeResourceURL(FOREIGNEXTERNALURL_BACKLINK_TEST_STRING).equals(
					((PortletResponse) externalContext.getResponse()).encodeURL(verifyString).replace("&amp;", "&"))) {
			testBean.setTestResult(true, "encodeResourceURL correctly encoded a foreign external URL with a backLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL didn't correctly encoded a foreign external URL with a backLink.  Expected: " +
				((PortletResponse) externalContext.getResponse()).encodeURL(verifyString) +
				" but encodeResourceURL returned: " +
				externalContext.encodeResourceURL(FOREIGNEXTERNALURL_BACKLINK_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.28
	@BridgeTest(test = "encodeResourceURLForeignExternalURLTest")
	public String encodeResourceURLForeignExternalURLTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String FOREIGNEXTERNALURL_TEST_STRING = "http://www.apache.org";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (externalContext.encodeResourceURL(FOREIGNEXTERNALURL_TEST_STRING).equals(
					((PortletResponse) externalContext.getResponse()).encodeURL(FOREIGNEXTERNALURL_TEST_STRING).replace(
						"&amp;", "&"))) {
			testBean.setTestResult(true, "encodeResourceURL correctly encoded a foreign external URL.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL didn't correctly encoded a foreign external URL.  Expected: " +
				((PortletResponse) externalContext.getResponse()).encodeURL(FOREIGNEXTERNALURL_TEST_STRING) +
				" and encodeResourceURL returned: " +
				externalContext.encodeResourceURL(FOREIGNEXTERNALURL_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.26
	/**
	 * encodeResourceURLTests
	 */
	@BridgeTest(test = "encodeResourceURLOpaqueTest")
	public String encodeResourceURLOpaqueTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String OPAQUE_TEST_STRING = "mailto:jsr-301-comments@jcp.org";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (externalContext.encodeResourceURL(OPAQUE_TEST_STRING).equals(OPAQUE_TEST_STRING)) {
			testBean.setTestResult(true,
				"encodeResourceURL correctly returned an unchanged string when the input was an opaque URL.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL didn't return an unchanged string when the input was an opaque URL.  Test parameter: " +
				OPAQUE_TEST_STRING + " and encodeResourceURL returned: " +
				externalContext.encodeResourceURL(OPAQUE_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.30
	@BridgeTest(test = "encodeResourceURLRelativeURLBackLinkTest")
	public String encodeResourceURLRelativeURLBackLinkTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String RELATIVEURL_BACKLINK_TEST_STRING =
			"../resources/myImage.jpg?javax.portlet.faces.BackLink=myBackLinkParam";
		final String RELATIVEURL_BACKLINK_VERIFY_STRING = "/resources/myImage.jpg?myBackLinkParam=";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// compute what the backLink should be
		String actionURL = externalContext.encodeActionURL(facesContext.getApplication().getViewHandler().getActionURL(
					facesContext, facesContext.getViewRoot().getViewId()));
		String verifyString = null;

		try {
			verifyString = externalContext.getRequestContextPath() + RELATIVEURL_BACKLINK_VERIFY_STRING +
				HTTPUtils.encode(actionURL, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			testBean.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// According to bridge rules since string passed in isn't xml strict encoded the result won't be as well
		// So ensure compares match by stripping from the one generated by the portlet container (if it exists)
		if (externalContext.encodeResourceURL(RELATIVEURL_BACKLINK_TEST_STRING).equals(
					((PortletResponse) externalContext.getResponse()).encodeURL(verifyString).replace("&amp;", "&"))) {
			testBean.setTestResult(true, "encodeResourceURL correctly encoded a relative URL with a backLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL didn't correctly encoded a relative URL with a backLink.  Expected: " +
				verifyString + " but encodeResourceURL returned: " +
				externalContext.encodeResourceURL(RELATIVEURL_BACKLINK_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.29
	@BridgeTest(test = "encodeResourceURLRelativeURLTest")
	public String encodeResourceURLRelativeURLTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String RELATIVEURL_TEST_STRING = "../resources/myImage.jpg";
		final String RELATIVEURL_VERIFY_STRING = "/resources/myImage.jpg";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (externalContext.encodeResourceURL(RELATIVEURL_TEST_STRING).equals(
					((PortletResponse) externalContext.getResponse()).encodeURL(
						externalContext.getRequestContextPath() + RELATIVEURL_VERIFY_STRING).replace("&amp;", "&"))) {
			testBean.setTestResult(true,
				"encodeResourceURL correctly encoded a resource referenced by a relative path.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL incorrectly encoded a resource referenced by a relative path.  Expected: " +
				((PortletResponse) externalContext.getResponse()).encodeURL(
					externalContext.getRequestContextPath() + RELATIVEURL_VERIFY_STRING) +
				" and encodeResourceURL returned: " + externalContext.encodeResourceURL(RELATIVEURL_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.31
	@BridgeTest(test = "encodeResourceURLTest")
	public String encodeResourceURLTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String URL_TEST_STRING = "/myportal/resources/myImage.jpg";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String testURL = externalContext.encodeResourceURL(URL_TEST_STRING);
		String verifyURL = ((PortletResponse) externalContext.getResponse()).encodeURL(URL_TEST_STRING).replace("&amp;",
				"&");

		if (testURL.equals(verifyURL)) {
			testBean.setTestResult(true,
				"encodeResourceURL correctly encoded the resource as an external (App) resource.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL incorrectly encoded a resource as if it were a reference to a resource within this application.  Generated: " +
				testURL + " but expected: " + verifyURL);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.33
	@BridgeTest(test = "encodeResourceURLViewLinkTest")
	public String encodeResourceURLViewLinkTest(TestBean testBean) {
		testBean.setTestComplete(true);

		// assume web.xml does Faces suffix mapping of .jsf to .jsp
		final String URL_VIEWLINK_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.ViewLink=true&param1=testValue";
		final String URL_VIEWLINK_VERIFY_STRING = "/tests/viewLink.jsf?param1=testValue";

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

		String testString = viewHandler.getResourceURL(facesContext, URL_VIEWLINK_TEST_STRING);
		String verifyString = viewHandler.getResourceURL(facesContext, URL_VIEWLINK_VERIFY_STRING);

		if (externalContext.encodeResourceURL(testString).equals(externalContext.encodeActionURL(verifyString))) {
			testBean.setTestResult(true, "encodeResourceURL correctly encoded a viewLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL incorrectly encoded a viewLink.  Expected: " +
				externalContext.encodeActionURL(verifyString) + " but encodeResourceURL with the viewLink returned: " +
				externalContext.encodeResourceURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.34
	@BridgeTest(test = "encodeResourceURLViewLinkWithBackLinkTest")
	public String encodeResourceURLViewLinkWithBackLinkTest(TestBean testBean) {
		testBean.setTestComplete(true);

		final String URL_VIEWLINK_BACKLINK_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.ViewLink=true&param1=testValue&javax.portlet.faces.BackLink=myBackLinkParam";
		final String URL_VIEWLINK_BACKLINK_VERIFY_STRING = "/tests/viewLink.jsf?param1=testValue&myBackLinkParam=";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

		// compute what the backLink should be
		String actionURL = externalContext.encodeActionURL(facesContext.getApplication().getViewHandler().getActionURL(
					facesContext, facesContext.getViewRoot().getViewId()));
		String testString = viewHandler.getResourceURL(facesContext, URL_VIEWLINK_BACKLINK_TEST_STRING);
		String verifyString = null;

		try {
			verifyString = viewHandler.getResourceURL(facesContext,
					URL_VIEWLINK_BACKLINK_VERIFY_STRING + HTTPUtils.encode(actionURL, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			testBean.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// Note:  as we are adding a URL as a parameter make sure its properly URLEncoded -- this causes it to be
		// doubly encoded
		if (externalContext.encodeResourceURL(testString).equals(externalContext.encodeActionURL(verifyString))) {
			testBean.setTestResult(true, "encodeResourceURL correctly encoded a viewLink with a BackLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"encodeResourceURL incorrectly encoded a viewLink.  Expected: " +
				externalContext.encodeActionURL(verifyString) + " but encodeResourceURL with the viewLink returned: " +
				externalContext.encodeResourceURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.136
	@BridgeTest(test = "encodeResourceURLWithModeTest")
	public String encodeResourceURLWithModeTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode portletMode = portletRequest.getPortletMode();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((portletMode == null) || !portletMode.toString().equalsIgnoreCase("view")) {
				testBean.setTestResult(false,
					"encodeResourceURL incorrectly encoded the portlet mode.  The resulting request should have ignored the mode and remained in 'view' mode.");
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeResourceURL incorrectly encoded the mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeResourceURL incorrectly encoded the mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
			else {
				testBean.setTestResult(true,
					"encodeResourceURL correctly encoded a URL by ignoring the mode and properly encoding the parameter.");
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.136 (b)
	@BridgeTest(test = "encodeResourceURLWithWindowStateTest")
	public String encodeResourceURLWithWindowStateTest(TestBean testBean) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState windowState = portletRequest.getWindowState();

			// Check that the parameter came along too
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get("param1");

			if ((windowState == null) || !windowState.toString().equalsIgnoreCase("normal")) {
				testBean.setTestResult(false,
					"encodeResourceURL incorrectly encoded the portlet window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");
			}
			else if (paramValue == null) {
				testBean.setTestResult(false,
					"encodeResourceURL incorrectly encoded the window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!paramValue.equals("testValue")) {
				testBean.setTestResult(false,
					"encodeResourceURL incorrectly encoded the window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					paramValue);
			}
			else {
				testBean.setTestResult(true,
					"encodeResourceURL correctly encoded a URL by ignoring the window state and properly encoding the parameter.");
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.99
	@BridgeTest(test = "encodeURLEscapingTest")
	public String encodeURLEscapingTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// test encodeActionURL preserves the xml escape encoding in the url it returns
		String nonEscapedURL = externalContext.getRequestContextPath() +
			"/tests/SingleRequestTest.jsf?parm1=a&param2=b";
		String encodedNonEscapedURL = externalContext.encodeActionURL(nonEscapedURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedURL)) {
			testBean.setTestResult(false,
				"EncodeActionURL incorrectly returned an url including xml escaping when the input url wasn't escaped.");

			return Constants.TEST_FAILED;
		}

		String escapedURL = externalContext.getRequestContextPath() +
			"/tests/SingleRequestTest.jsf?parm1=a&amp;param2=b";
		String encodedEscapedURL = externalContext.encodeActionURL(escapedURL);

		if (!isStrictXhtmlEncoded(encodedEscapedURL) && encodedEscapedURL.contains("&")) {
			testBean.setTestResult(false,
				"EncodeActionURL incorrectly returned an url without xml escaping when the input url was escaped.");

			return Constants.TEST_FAILED;
		}

		nonEscapedURL = externalContext.getRequestContextPath() + "/tests/SingleRequestTest.jsf";
		encodedNonEscapedURL = externalContext.encodeActionURL(nonEscapedURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedURL)) {
			testBean.setTestResult(false,
				"EncodeActionURL incorrectly returned an url including xml escaping when the input url contained no indication (no query string).");

			return Constants.TEST_FAILED;
		}

		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

		String nonEscapedPath = "/tests/SingleRequestTest.jsf?parm1=a&param2=b";
		String nonEscapedResourceURL = viewHandler.getResourceURL(facesContext, nonEscapedPath);
		String encodedNonEscapedResourceURL = externalContext.encodeResourceURL(nonEscapedResourceURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedResourceURL)) {
			testBean.setTestResult(false,
				"EncodeResourceURL incorrectly returned an url including xml escaping when the input url wasn't escaped.");

			return Constants.TEST_FAILED;
		}

		String escapedPath = "/tests/SingleRequestTest.jsf?parm1=a&amp;param2=b";
		String escapedResourceURL = viewHandler.getResourceURL(facesContext, escapedPath);
		String encodedEscapedResourceURL = externalContext.encodeResourceURL(escapedResourceURL);

		if (!isStrictXhtmlEncoded(encodedEscapedResourceURL) && encodedEscapedURL.contains("&")) {
			testBean.setTestResult(false,
				"EncodeResourceURL incorrectly returned an url without xml escaping when the input url was escaped.");

			return Constants.TEST_FAILED;
		}

		nonEscapedPath = "/tests/SingleRequestTest.jsf";
		nonEscapedResourceURL = viewHandler.getResourceURL(facesContext, nonEscapedPath);
		encodedNonEscapedResourceURL = externalContext.encodeResourceURL(nonEscapedResourceURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedResourceURL)) {
			testBean.setTestResult(false,
				"EncodeResourceURL incorrectly returned an url including xml escaping when the input url contained no indication (no query string).");

			return Constants.TEST_FAILED;
		}

		// Otherwise all was good.

		testBean.setTestResult(true,
			"encodeActionURL and encodeResourceURL both correctly xml escaped urls it should and didn't xml escape urls it shouldn't");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.57
	@BridgeTest(test = "getRequestCharacterEncodingActionTest")
	public String getRequestCharacterEncodingActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			ActionRequest actionRequest = (ActionRequest) externalContext.getRequest();
			String charEncoding = externalContext.getRequestCharacterEncoding();
			String actionCharEncoding = actionRequest.getCharacterEncoding();

			if (((charEncoding == null) && (actionCharEncoding == null)) || charEncoding.equals(actionCharEncoding)) {
				testBean.setTestResult(true,
					"externalContext.getRequestCharacterEncoding() correctly returned the same value as actionRequest.getCharacterEncoding()");

			}
			else {
				testBean.setTestResult(false,
					"externalContext.getRequestCharacterEncoding() incorrectly returned the different value than actionRequest.getCharacterEncoding(). " +
					"Expected: " + actionCharEncoding + " but received: " + charEncoding);
			}

			return "getRequestCharacterEncodingActionTest";
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

	// Test is MultiRequest
	// Test #6.124
	@BridgeTest(test = "getRequestCharacterEncodingEventTest")
	public String getRequestCharacterEncodingEventTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {

			testBean.setTestComplete(true);

			String eventMsg = (String) externalContext.getRequestMap().get(
					"com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testBean.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testBean.setTestResult(true,
					"externalContext.getRequestCharacterEncoding() correctly returned null when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.56
	@BridgeTest(test = "getRequestCharacterEncodingRenderTest")
	public String getRequestCharacterEncodingRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String charEncoding = externalContext.getRequestCharacterEncoding();

		if (charEncoding == null) {
			testBean.setTestResult(true,
				"externalContext.getRequestCharacterEncoding() correctly returned null when called during the render phase.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getRequestCharacterEncoding() incorrectly returned non-null value when called during the render phase: " +
				charEncoding);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.123
	@BridgeTest(test = "getRequestCharacterEncodingResourceTest")
	public String getRequestCharacterEncodingResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceRequest resourceRequest = (ResourceRequest) externalContext.getRequest();
			String charEncoding = externalContext.getRequestCharacterEncoding();
			String resourceCharEncoding = resourceRequest.getCharacterEncoding();

			if (((charEncoding == null) && (resourceCharEncoding == null)) ||
					charEncoding.equals(resourceCharEncoding)) {
				testBean.setTestResult(true,
					"externalContext.getRequestCharacterEncoding() correctly returned the same value as resourceRequest.getCharacterEncoding()");

			}
			else {
				testBean.setTestResult(false,
					"externalContext.getRequestCharacterEncoding() incorrectly returned the different value than resourceRequest.getCharacterEncoding(). " +
					"Expected: " + resourceCharEncoding + " but received: " + charEncoding);
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
		else {
			return "";
		}
	}

	// Test #6.59
	@BridgeTest(test = "getRequestContentTypeActionTest")
	public String getRequestContentTypeActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			ActionRequest actionRequest = (ActionRequest) externalContext.getRequest();
			String contentType = externalContext.getRequestContentType();
			String actionContentType = actionRequest.getContentType();

			if (((contentType == null) && (actionContentType == null)) || contentType.equals(actionContentType)) {
				testBean.setTestResult(true,
					"externalContext.getRequestContentType() correctly returned the same value as actionRequest.getContentType()");

			}
			else {
				testBean.setTestResult(false,
					"externalContext.getRequestContentType() incorrectly returned the different value than actionRequest.getContentType(). " +
					"Expected: " + actionContentType + " but received: " + contentType);
			}

			return "getRequestContentTypeActionTest";
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

	// Test is MultiRequest
	// Test #6.126
	@BridgeTest(test = "getRequestContentTypeEventTest")
	public String getRequestContentTypeEventTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {

			testBean.setTestComplete(true);

			String eventMsg = (String) externalContext.getRequestMap().get(
					"com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testBean.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testBean.setTestResult(true,
					"externalContext.getRequestContentType() correctly returned null when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test #6.58
	@BridgeTest(test = "getRequestContentTypeRenderTest")
	public String getRequestContentTypeRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String contentType = externalContext.getRequestContentType();

		if (contentType == null) {
			testBean.setTestResult(true,
				"externalContext.getRequestContentType() correctly returned null when called during the render phase.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getRequestContentType() incorrectly returned non-null value when called during the render phase: " +
				contentType);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.125
	@BridgeTest(test = "getRequestContentTypeResourceTest")
	public String getRequestContentTypeResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceRequest resourceRequest = (ResourceRequest) externalContext.getRequest();
			String contentType = externalContext.getRequestContentType();
			String resourceContentType = resourceRequest.getContentType();

			if (((contentType == null) && (resourceContentType == null)) || contentType.equals(resourceContentType)) {
				testBean.setTestResult(true,
					"externalContext.getRequestContentType() correctly returned the same value as resourceRequest.getContentType()");

			}
			else {
				testBean.setTestResult(false,
					"externalContext.getRequestContentType() incorrectly returned the different value than resourceRequest.getContentType(). " +
					"Expected: " + resourceContentType + " but received: " + contentType);
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
		else {
			return "";
		}
	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.39
	@BridgeTest(test = "getRequestHeaderMapActionTest")
	public String getRequestHeaderMapActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map headerMap = externalContext.getRequestHeaderMap();
			Enumeration propertyNames = ((PortletRequest) externalContext.getRequest()).getPropertyNames();

			// Test for immutability
			try {
				String s = (String) headerMap.put("TestKey", "TestValue");
				testBean.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

				return "getRequestHeaderMapActionTest";
			}
			catch (Exception e) {
				// we expect to get an exception
			}

			Set<Map.Entry<String, String>> set = headerMap.entrySet();
			int propsFound = 0;

			for (Iterator<Map.Entry<String, String>> headers = set.iterator(); headers.hasNext();) {
				Map.Entry<String, String> e = headers.next();
				String key = e.getKey();

				if (key.equalsIgnoreCase("accept")) {
					boolean found = false;

					// parse the accept header into its parts
					String[] accepts = trim(e.getValue().split(","));

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getResponseContentTypes();

					// Now ensure the two match
					while (em.hasMoreElements()) {
						String rct = ((String) em.nextElement());
						found = false;

						for (int i = 0; i < accepts.length; i++) {

							if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
								found = true;

								break;
							}
						}

						if (!found) {
							testBean.setTestResult(false,
								"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
								rct);

							return "getRequestHeaderMapActionTest";
						}
					}
				}
				else if (key.equalsIgnoreCase("accept-language")) {

					// parse the accept header into its parts
					String[] accepts = trim(e.getValue().split(","));

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getLocales();

					// Now ensure the two match
					int found = 0;

					while (em.hasMoreElements()) {
						String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

						for (int i = 0; i < accepts.length; i++) {

							if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
								found += 1;

								break;
							}
						}
					}

					if (found != accepts.length) {
						testBean.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

						return "getRequestHeaderMapActionTest";
					}
				}
			}

			// Now enumerate the requests property Enumeration and make sure all are in this Map
			while (propertyNames.hasMoreElements()) {
				String prop = (String) propertyNames.nextElement();

				if (!headerMap.containsKey(prop)) {

					// Note: By spec Content-Length is only included if getContentLength() isn't -1
					if (prop.equalsIgnoreCase("content-length")) {

						if (((ActionRequest) externalContext.getRequest()).getContentLength() == -1) {
							continue;
						}
					}

					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return "getRequestHeaderMapActionTest";

				}
			}

			// Otherwise all out tests passed:

			testBean.setTestResult(true,
				"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

			return "getRequestHeaderMapActionTest";
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

	// Test is MultiRequest
	// Test #6.119
	@BridgeTest(test = "getRequestHeaderMapEventTest")
	public String getRequestHeaderMapEventTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {

			testBean.setTestComplete(true);

			String eventMsg = (String) externalContext.getRequestMap().get(
					"com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testBean.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {
				// All out tests passed:

				testBean.setTestResult(true,
					"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
				testBean.appendTestDetail(
					"The getRequestHeaderMap Map correctly didn't contain the content-type property.");
				testBean.appendTestDetail(
					"The getRequestHeaderMap Map correctly didn't contain the content-length property.");
				testBean.appendTestDetail(
					"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
				testBean.appendTestDetail(
					"The getRequestHeaderMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
				testBean.appendTestDetail(
					"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.38
	/**
	 * getRequestHeaderMap Tests
	 */
	@BridgeTest(test = "getRequestHeaderMapRenderTest")
	public String getRequestHeaderMapRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains properties from the portlet request (that it should)
		// 2. Doesn't contain the Content-Type property
		// 3. Does include the Accept and Accept-Language

		Map headerMap = externalContext.getRequestHeaderMap();
		Enumeration propertyNames = ((PortletRequest) externalContext.getRequest()).getPropertyNames();

		// Test for immutability
		try {
			String s = (String) headerMap.put("TestKey", "TestValue");
			testBean.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

			return Constants.TEST_FAILED;
		}
		catch (Exception e) {
			// we expect to get an exception
		}

		Set<Map.Entry<String, String>> set = headerMap.entrySet();
		int propsFound = 0;

		for (Iterator<Map.Entry<String, String>> headers = set.iterator(); headers.hasNext();) {
			Map.Entry<String, String> e = headers.next();
			String key = e.getKey();

			if (key.equalsIgnoreCase("content-type")) {
				testBean.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderMap contains a content-type header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("content-length")) {
				testBean.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderMap contains a content-length header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("accept")) {
				boolean found = false;

				// parse the accept header into its parts
				String[] accepts = trim(e.getValue().split(","));

				Enumeration em = ((PortletRequest) externalContext.getRequest()).getResponseContentTypes();

				// Now ensure that all entries in the getResponseContentTypes enum are in the property
				while (em.hasMoreElements()) {
					String rct = ((String) em.nextElement());
					found = false;

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found = true;

							break;
						}
					}

					if (!found) {
						testBean.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
							rct);

						return Constants.TEST_FAILED;
					}
				}
			}
			else if (key.equalsIgnoreCase("accept-language")) {

				// parse the accept header into its parts
				String[] accepts = trim(e.getValue().split(","));

				Enumeration em = ((PortletRequest) externalContext.getRequest()).getLocales();

				// Now ensure the two match
				int found = 0;

				while (em.hasMoreElements()) {
					String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found += 1;

							break;
						}
					}
				}

				if (found != accepts.length) {
					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

					return Constants.TEST_FAILED;
				}
			}
		}

		// Now enumerate the requests property Enumeration and make sure all are in this Map (except Content-Type)
		while (propertyNames.hasMoreElements()) {
			String prop = (String) propertyNames.nextElement();

			if (!prop.equalsIgnoreCase("content-type") && !prop.equalsIgnoreCase("content-length")) {

				if (!headerMap.containsKey(prop)) {
					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;
				}
			}
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true,
			"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
		testBean.appendTestDetail("The getRequestHeaderMap Map correctly didn't contain the content-type property.");
		testBean.appendTestDetail("The getRequestHeaderMap Map correctly didn't contain the content-length property.");
		testBean.appendTestDetail(
			"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
		testBean.appendTestDetail(
			"The getRequestHeaderMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
		testBean.appendTestDetail(
			"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

		return Constants.TEST_SUCCESS;

	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.120
	@BridgeTest(test = "getRequestHeaderMapResourceTest")
	public String getRequestHeaderMapResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map headerMap = externalContext.getRequestHeaderMap();
			Enumeration propertyNames = ((PortletRequest) externalContext.getRequest()).getPropertyNames();

			testBean.setTestComplete(true);

			// Test for immutability
			try {
				String s = (String) headerMap.put("TestKey", "TestValue");
				testBean.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

				return Constants.TEST_FAILED;
			}
			catch (Exception e) {
				// we expect to get an exception
			}

			Set<Map.Entry<String, String>> set = headerMap.entrySet();
			int propsFound = 0;

			for (Iterator<Map.Entry<String, String>> headers = set.iterator(); headers.hasNext();) {
				Map.Entry<String, String> e = headers.next();
				String key = e.getKey();

				if (key.equalsIgnoreCase("accept")) {
					boolean found = false;

					// parse the accept header into its parts
					String accept = e.getValue();
					String[] accepts = trim(accept.split(","));

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getResponseContentTypes();

					// Now ensure the two match -- issue here is that the Portlet spec is unclear whether
					// getResponseContentTypes() returns the same as servlet.getHeaders("accept") or merely an
					// enumeration where each entry in the enum is a distinct content type. Pluto does the former while
					// WC does the later.  So test for both by first assuming they are split and then that they are not.
					while (em.hasMoreElements()) {
						String rct = ((String) em.nextElement());
						found = false;

						for (int i = 0; i < accepts.length; i++) {

							if (rct.indexOf(',') == -1) {

								// only one entry
								if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
									found = true;
								}
							}
							else {

								// multiple entries
								String[] compareAccepts = trim(rct.split(","));

								for (int j = 0; j < compareAccepts.length; j++) {

									if (compareAccepts[j].regionMatches(true, 0, accepts[i], 0,
												compareAccepts[j].length())) {
										found = true;
									}
								}
							}

							if (found)
								break;
						}

						if (!found) {
							testBean.setTestResult(false,
								"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
								rct);

							return Constants.TEST_FAILED;
						}
					}
				}
				else if (key.equalsIgnoreCase("accept-language")) {

					// parse the accept header into its parts
					String[] accepts = trim(e.getValue().split(","));

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getLocales();

					// Now ensure the two match
					int found = 0;

					while (em.hasMoreElements()) {
						String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

						for (int i = 0; i < accepts.length; i++) {

							if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
								found += 1;

								break;
							}
						}
					}

					if (found != accepts.length) {
						testBean.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

						return Constants.TEST_FAILED;
					}
				}
			}

			// Now enumerate the requests property Enumeration and make sure all are in this Map
			while (propertyNames.hasMoreElements()) {
				String prop = (String) propertyNames.nextElement();

				if (!headerMap.containsKey(prop)) {

					// Note: By spec Content-Length is only included if getContentLength() isn't -1
					if (prop.equalsIgnoreCase("content-length")) {

						if (((ResourceRequest) externalContext.getRequest()).getContentLength() == -1) {
							continue;
						}
					}

					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;

				}
			}

			// Otherwise all out tests passed:

			testBean.setTestResult(true,
				"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

			return Constants.TEST_SUCCESS;

		}
		else {
			return "";
		}
	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.41
	@BridgeTest(test = "getRequestHeaderValuesMapActionTest")
	public String getRequestHeaderValuesMapActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map<String, String[]> headerMap = externalContext.getRequestHeaderValuesMap();
			Enumeration propertyNames = ((PortletRequest) externalContext.getRequest()).getPropertyNames();

			// Test for immutability
			try {
				String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });
				headerMap.put("TestKey", s);
				testBean.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

				return "getRequestHeaderValuesMapActionTest";
			}
			catch (Exception e) {
				// we expect to get an exception
			}

			Set<Map.Entry<String, String[]>> set = headerMap.entrySet();
			int propsFound = 0;

			for (Iterator<Map.Entry<String, String[]>> headers = set.iterator(); headers.hasNext();) {
				Map.Entry<String, String[]> e = headers.next();
				String key = e.getKey();

				if (key.equalsIgnoreCase("accept")) {
					boolean found = false;

					// parse the accept header into its parts
					String[] acceptsValues = e.getValue();
					String[] accepts = null;
					int count = 0;

					for (int i = 0; i < acceptsValues.length; i++) {
						String[] temp = trim(acceptsValues[i].split(","));

						if (accepts == null) {
							accepts = new String[temp.length];
						}
						else {
							String[] acceptsTemp = new String[accepts.length + temp.length];
							System.arraycopy(accepts, 0, acceptsTemp, 0, count);
							accepts = acceptsTemp;
						}

						System.arraycopy(temp, 0, accepts, count, temp.length);
					}

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getResponseContentTypes();

					// Now ensure the two match
					while (em.hasMoreElements()) {
						String rct = ((String) em.nextElement());
						found = false;

						for (int i = 0; i < accepts.length; i++) {

							if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
								found = true;

								break;
							}
						}

						if (!found) {
							testBean.setTestResult(false,
								"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
								rct);

							return "getRequestHeaderValuesMapActionTest";
						}
					}
				}
				else if (key.equalsIgnoreCase("accept-language")) {

					// parse the accept header into its parts
					String[] acceptLangValues = e.getValue();
					String[] accepts = null;
					int count = 0;

					for (int i = 0; i < acceptLangValues.length; i++) {
						String[] temp = trim(acceptLangValues[i].split(","));

						if (accepts == null) {
							accepts = new String[temp.length];
						}
						else {
							String[] acceptsTemp = new String[accepts.length + temp.length];
							System.arraycopy(accepts, 0, acceptsTemp, 0, count);
							accepts = acceptsTemp;
						}

						System.arraycopy(temp, 0, accepts, count, temp.length);
						count += temp.length;
					}

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getLocales();

					// Now ensure the two match
					int found = 0;

					while (em.hasMoreElements()) {
						String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

						for (int i = 0; i < accepts.length; i++) {

							if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
								found += 1;

								break;
							}
						}
					}

					if (found != accepts.length) {
						testBean.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

						return "getRequestHeaderValuesMapActionTest";
					}
				}
			}

			// Now enumerate the requests property Enumeration and make sure all are in this Map (except Content-Type)
			while (propertyNames.hasMoreElements()) {
				String prop = (String) propertyNames.nextElement();

				if (!headerMap.containsKey(prop)) {
					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return "getRequestHeaderValuesMapActionTest";

				}
			}

			// Otherwise all out tests passed:

			testBean.setTestResult(true,
				"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

			return "getRequestHeaderValuesMapActionTest";
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

	// Test is MultiRequest
	// Test #6.119
	@BridgeTest(test = "getRequestHeaderValuesMapEventTest")
	public String getRequestHeaderValuesMapEventTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {

			testBean.setTestComplete(true);

			String eventMsg = (String) externalContext.getRequestMap().get(
					"com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testBean.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				// All out tests passed:
				testBean.setTestResult(true,
					"The immutable Map returned from getRequestHeaderValuesMap correctly threw an exception when written to.");
				testBean.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly didn't contain the content-type property.");
				testBean.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly didn't contain the content-length property.");
				testBean.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
				testBean.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
				testBean.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly contained all other properties returned by request.getProperties.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.40
	/**
	 * getRequestHeaderValuesMap Tests
	 */
	@BridgeTest(test = "getRequestHeaderValuesMapRenderTest")
	public String getRequestHeaderValuesMapRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains properties from the portlet request (that it should)
		// 2. Doesn't contain the Content-Type property
		// 3. Does include the Accept and Accept-Language

		Map<String, String[]> headerMap = externalContext.getRequestHeaderValuesMap();
		Enumeration propertyNames = ((PortletRequest) externalContext.getRequest()).getPropertyNames();

		// Test for immutability
		try {
			String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });
			testBean.setTestResult(false, "Failed: The Map returned from getRequestHeaderValuesMap isn't immutable.");

			return Constants.TEST_FAILED;
		}
		catch (Exception e) {
			// we expect to get an exception
		}

		Set<Map.Entry<String, String[]>> set = headerMap.entrySet();
		int propsFound = 0;

		for (Iterator<Map.Entry<String, String[]>> headers = set.iterator(); headers.hasNext();) {
			Map.Entry<String, String[]> e = headers.next();
			String key = e.getKey();

			if (key.equalsIgnoreCase("content-type")) {
				testBean.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderValuesMap contains a content-type header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("content-length")) {
				testBean.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderValuesMap contains a content-length header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("accept")) {
				boolean found = false;

				// parse the accept header into its parts
				String[] acceptsValues = e.getValue();
				String[] accepts = null;
				int count = 0;

				for (int i = 0; i < acceptsValues.length; i++) {
					String[] temp = trim(acceptsValues[i].split(","));

					if (accepts == null) {
						accepts = new String[temp.length];
					}
					else {
						String[] acceptsTemp = new String[accepts.length + temp.length];
						System.arraycopy(accepts, 0, acceptsTemp, 0, count);
						accepts = acceptsTemp;
					}

					System.arraycopy(temp, 0, accepts, count, temp.length);
					count += temp.length;
				}

				Enumeration em = ((PortletRequest) externalContext.getRequest()).getResponseContentTypes();

				// Now ensure that all entries in the getResponseContentTypes enum are in the property
				while (em.hasMoreElements()) {
					String rct = ((String) em.nextElement());
					found = false;

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found = true;

							break;
						}
					}

					if (!found) {
						testBean.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderValuesMap is missing a key returned in request.getResponseContentTypes: " +
							rct);

						return Constants.TEST_FAILED;
					}
				}
			}
			else if (key.equalsIgnoreCase("accept-language")) {

				// parse the accept header into its parts
				String[] acceptLangValues = e.getValue();
				String[] accepts = null;
				int count = 0;

				for (int i = 0; i < acceptLangValues.length; i++) {
					String[] temp = trim(acceptLangValues[i].split(","));

					if (accepts == null) {
						accepts = new String[temp.length];
					}
					else {
						String[] acceptsTemp = new String[accepts.length + temp.length];
						System.arraycopy(accepts, 0, acceptsTemp, 0, count);
						accepts = acceptsTemp;
					}

					System.arraycopy(temp, 0, accepts, count, temp.length);
				}

				Enumeration em = ((PortletRequest) externalContext.getRequest()).getLocales();

				// Now ensure the two match
				int found = 0;

				while (em.hasMoreElements()) {
					String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found += 1;

							break;
						}
					}
				}

				if (found != accepts.length) {
					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderValuesMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

					return Constants.TEST_FAILED;
				}
			}
		}

		// Now enumerate the requests property Enumeration and make sure all are in this Map (except Content-Type)
		while (propertyNames.hasMoreElements()) {
			String prop = (String) propertyNames.nextElement();

			if (!prop.equalsIgnoreCase("content-type") && !prop.equalsIgnoreCase("content-length")) {

				if (!headerMap.containsKey(prop)) {
					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderValuesMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;
				}
			}
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true,
			"The immutable Map returned from getRequestHeaderValuesMap correctly threw an exception when written to.");
		testBean.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly didn't contain the content-type property.");
		testBean.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly didn't contain the content-length property.");
		testBean.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
		testBean.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
		testBean.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly contained all other properties returned by request.getProperties.");

		return Constants.TEST_SUCCESS;

	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.122
	@BridgeTest(test = "getRequestHeaderValuesMapResourceTest")
	public String getRequestHeaderValuesMapResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map<String, String[]> headerMap = externalContext.getRequestHeaderValuesMap();
			Enumeration propertyNames = ((PortletRequest) externalContext.getRequest()).getPropertyNames();

			testBean.setTestComplete(true);

			// Test for immutability
			try {
				String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });
				headerMap.put("TestKey", s);
				testBean.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderValuesMap isn't immutable.");

				return Constants.TEST_FAILED;
			}
			catch (Exception e) {
				// we expect to get an exception
			}

			Set<Map.Entry<String, String[]>> set = headerMap.entrySet();
			int propsFound = 0;

			for (Iterator<Map.Entry<String, String[]>> headers = set.iterator(); headers.hasNext();) {
				Map.Entry<String, String[]> e = headers.next();
				String key = e.getKey();

				if (key.equalsIgnoreCase("accept")) {
					boolean found = false;

					// parse the accept header into its parts
					String[] acceptsValues = e.getValue();
					String[] accepts = null;
					int count = 0;

					for (int i = 0; i < acceptsValues.length; i++) {
						String[] temp = trim(acceptsValues[i].split(","));

						if (accepts == null) {
							accepts = new String[temp.length];
						}
						else {
							String[] acceptsTemp = new String[accepts.length + temp.length];
							System.arraycopy(accepts, 0, acceptsTemp, 0, count);
							accepts = acceptsTemp;
						}

						System.arraycopy(temp, 0, accepts, count, temp.length);
					}

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getResponseContentTypes();

					// Now ensure the two match -- issue here is that the Portlet spec is unclear whether
					// getResponseContentTypes() returns the same as servlet.getHeaders("accept") or merely an
					// enumeration where each entry in the enum is a distinct content type. Pluto does the former while
					// WC does the later.  So test for both by first assuming they are split and then that they are not.
					while (em.hasMoreElements()) {
						String rct = ((String) em.nextElement());
						found = false;

						for (int i = 0; i < accepts.length; i++) {

							if (rct.indexOf(',') == -1) {

								// only one entry
								if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
									found = true;
								}
							}
							else {

								// multiple entries
								String[] compareAccepts = trim(rct.split(","));

								for (int j = 0; j < compareAccepts.length; j++) {

									if (compareAccepts[j].regionMatches(true, 0, accepts[i], 0,
												compareAccepts[j].length())) {
										found = true;
									}
								}
							}

							if (found)
								break;
						}

						if (!found) {
							testBean.setTestResult(false,
								"Failed: The Map returned from getRequestHeaderValuesMap is missing a key returned in request.getResponseContentTypes: " +
								rct);

							return Constants.TEST_FAILED;
						}
					}
				}
				else if (key.equalsIgnoreCase("accept-language")) {

					// parse the accept header into its parts
					String[] acceptLangValues = e.getValue();
					String[] accepts = null;
					int count = 0;

					for (int i = 0; i < acceptLangValues.length; i++) {
						String[] temp = trim(acceptLangValues[i].split(","));

						if (accepts == null) {
							accepts = new String[temp.length];
						}
						else {
							String[] acceptsTemp = new String[accepts.length + temp.length];
							System.arraycopy(accepts, 0, acceptsTemp, 0, count);
							accepts = acceptsTemp;
						}

						System.arraycopy(temp, 0, accepts, count, temp.length);
						count += temp.length;
					}

					Enumeration em = ((PortletRequest) externalContext.getRequest()).getLocales();

					// Now ensure the two match
					int found = 0;

					while (em.hasMoreElements()) {
						String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

						for (int i = 0; i < accepts.length; i++) {

							if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
								found += 1;

								break;
							}
						}
					}

					if (found != accepts.length) {
						testBean.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderValuesMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

						return Constants.TEST_FAILED;
					}
				}
			}

			// Now enumerate the requests property Enumeration and make sure all are in this Map (except Content-Type)
			while (propertyNames.hasMoreElements()) {
				String prop = (String) propertyNames.nextElement();

				if (!headerMap.containsKey(prop)) {
					testBean.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderValuesMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;

				}
			}

			// Otherwise all out tests passed:

			testBean.setTestResult(true,
				"The immutable Map returned from getRequestHeaderValuesMap correctly threw an exception when written to.");
			testBean.appendTestDetail(
				"The getRequestHeaderValuesMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderValuesMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testBean.appendTestDetail(
				"The getRequestHeaderValuesMap Map correctly contained all other properties returned by request.getProperties.");

			return Constants.TEST_SUCCESS;
		}
		else {
			return "";
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.47
	@BridgeTest(test = "getRequestParameterDefaultViewParamsTest")
	public String getRequestParameterDefaultViewParamsTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String> paramMap = externalContext.getRequestParameterMap();

		// loop through the Map and verify that 'value1' and 'value2' aren't there.
		boolean foundField1 = false;
		boolean foundField2 = false;
		Set<Map.Entry<String, String>> set = paramMap.entrySet();

		for (Iterator<Map.Entry<String, String>> params = set.iterator(); params.hasNext();) {
			Map.Entry<String, String> e = params.next();
			String key = e.getKey();

			if (key.equals("field1"))
				foundField1 = true;

			if (key.equals("field2"))
				foundField2 = true;
		}

		if (!foundField1) {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestParameterMap() didn't properly expose the 'field1' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else if (!foundField2) {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestParameterMap() didn't properly expose the 'field2' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else {
			testBean.setTestResult(true,
				"Passed RenderPhase: The getRequestParameterMap Map correctly exposed the query string fields in defaultViewId.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.45
	/**
	 * getRequestParameterMap Tests
	 */
	@BridgeTest(test = "getRequestParameterMapCoreTest")
	public String getRequestParameterMapCoreTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map (during action) contains the parameters in the underlying
			// request 2. Map (during render) doesn't contain any params other than the VIEW_STATE_PARAM

			Map<String, String> paramMap = externalContext.getRequestParameterMap();
			Map<String, String[]> requestParamMap = ((PortletRequest) externalContext.getRequest()).getParameterMap();

			// Test for immutability
			try {
				paramMap.put("TestKey", "TestValue");
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from getRequestParameterMap isn't immutable.");

				return "getRequestParameterMapCoreTest";
			}
			catch (Exception e) {
				// we expect to get an exception
			}

			boolean foundField1 = false;
			boolean foundField2 = false;
			Set<Map.Entry<String, String>> set = paramMap.entrySet();

			for (Iterator<Map.Entry<String, String>> params = set.iterator(); params.hasNext();) {
				Map.Entry<String, String> e = params.next();
				String key = e.getKey();
				String value = e.getValue();

				if (key.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (key.indexOf("formDataField2") > -1)
					foundField2 = true;

				String[] requestVals = requestParamMap.get(key);
				String requestVal = (requestVals != null) ? requestVals[0] : null;

				if (requestVal == null) {
					testBean.setTestResult(false,
						"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is missing a key returned in request.getParameterMap: " +
						key);

					return "getRequestParameterMapCoreTest";
				}
				else if (!value.equals(requestVal)) {
					testBean.setTestResult(false,
						"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap contains a different value for an entry vs. request.getParameterMap.  key: " +
						key + " ExtCtx.value: " + value + " RequestValue: " + requestVal);

					return "getRequestParameterMapCoreTest";
				}
			}

			// Otherwise all out tests passed:
			// Now make sure the two form fields are there:
			if (!foundField1) {
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is either missing the form's field1 parameter or its value wasn't 'value1'.");

				return "getRequestParameterMapCoreTest";
			}
			else if (!foundField2) {
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is either missing the form's field2 parameter or its value wasn't 'value2'.");

				return "getRequestParameterMapCoreTest";
			}

			testBean.setTestResult(true,
				"Passed Action Phase: The immutable Map returned from getRequestParameterMap correctly threw an exception when written to.");
			testBean.appendTestDetail(
				"Passed Action Phase: The getRequestParameterMap Map correctly contained all parameters in the underlying request.getParameterMap.");

			return "getRequestParameterMapCoreTest";
		}
		else {
			testBean.setTestComplete(true);

			// Now verify we only have the VIEW_STATE_PARAM
			Map<String, String> paramMap = externalContext.getRequestParameterMap();

			if (paramMap.get(ResponseStateManager.VIEW_STATE_PARAM) == null) {
				testBean.setTestResult(false,
					"Render Phase externalContext.getRequestParameterMap() doesn't contain the ResponseStateManager.VIEW_STATE parameter. Test Result from the prior action phase was: " +
					testBean.getTestResult());

				return Constants.TEST_FAILED;
			}

			// loop through the Map and verify that 'value1' and 'value2' aren't there.
			boolean foundField1 = false;
			boolean foundField2 = false;
			Set<Map.Entry<String, String>> set = paramMap.entrySet();

			for (Iterator<Map.Entry<String, String>> params = set.iterator(); params.hasNext();) {
				Map.Entry<String, String> e = params.next();
				String key = e.getKey();

				// Can't look up by key because JSF munges this id
				if (key.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (key.indexOf("formDataField2") > -1)
					foundField2 = true;
			}

			if (foundField1) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterMap() incorrectly contains the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (foundField2) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterMap() incorrectly contains the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.appendTestDetail(
					"Passed RenderPhase: The getRequestParameterMap Map correctly excluded the submitted form fields from the render Phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	/**
	 * getRequestParameterNames Tests
	 */
	// Test #6.48
	@BridgeTest(test = "getRequestParameterNamesCoreTest")
	public String getRequestParameterNamesCoreTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 2. Map (during action) contains the parameters in the underlying request 2. Map
			// (during render) doesn't contain any params other than the VIEW_STATE_PARAM

			Iterator<String> namesIterator = externalContext.getRequestParameterNames();

			boolean foundField1 = false;
			boolean foundField2 = false;

			while (namesIterator.hasNext()) {
				String name = namesIterator.next();

				// Can't exact match by key because JSF munges this id
				if (name.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (name.indexOf("formDataField2") > -1)
					foundField2 = true;

				Enumeration<String> requestNamesEnum = ((PortletRequest) externalContext.getRequest())
					.getParameterNames();
				boolean foundName = false;

				// verify its in the underlying request Enumeration
				while (requestNamesEnum.hasMoreElements()) {

					if (name.contains(requestNamesEnum.nextElement())) {
						foundName = true;

						break;
					}
				}

				if (!foundName) {
					testBean.setTestResult(false,
						"Failed Action Phase: The Iterator returned from ExternalContext.getRequestParameterNames is missing a name returned in request.getParameterNames: " +
						name);

					return "getRequestParameterNamesCoreTest";
				}
			}

			// Otherwise all out tests passed:
			// Now make sure the two form fields are there:
			if (!foundField1) {
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is missing the form's field1 parameter.");

				return "getRequestParameterNamesCoreTest";
			}
			else if (!foundField2) {
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is missing the form's field2.");

				return "getRequestParameterNamesCoreTest";
			}

			testBean.setTestResult(true,
				"Passed Action Phase: The getRequestParameterNames iterator correctly contained all parameters in the underlying request.getParameterNames.");

			return "getRequestParameterNamesCoreTest";
		}
		else {
			testBean.setTestComplete(true);

			// Now verify we only have the VIEW_STATE_PARAM
			Iterator<String> namesIterator = externalContext.getRequestParameterNames();
			boolean foundViewState = false;
			boolean foundField1 = false;
			boolean foundField2 = false;

			while (namesIterator.hasNext()) {
				String s = namesIterator.next();

				if (s.contains(ResponseStateManager.VIEW_STATE_PARAM)) {
					foundViewState = true;
				}
				else if (s.indexOf("formDataField1") > -1) {
					foundField1 = true;
				}
				else if (s.indexOf("formDataField2") > -1) {
					foundField2 = true;
				}
			}

			if (!foundViewState) {
				testBean.setTestResult(false,
					"Render phase externalContext.getRequestParameterNames() doesn't contain the ResponseStateManager.VIEW_STATE parameter. Test Result from the prior action Phase was: " +
					testBean.getTestResult());

				return Constants.TEST_FAILED;
			}
			else if (foundField1) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterName() incorrectly contains the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (foundField2) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterName() incorrectly contains the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.appendTestDetail(
					"Passed RenderPhase: The getRequestParameterName() correctly excluded the submitted form fields from the render phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.50
	@BridgeTest(test = "getRequestParameterNamesDefaultViewParamsTest")
	public String getRequestParameterNamesDefaultViewParamsTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Iterator<String> namesIterator = externalContext.getRequestParameterNames();

		// loop through the Map and verify that 'value1' and 'value2' aren't there.
		boolean foundField1 = false;
		boolean foundField2 = false;

		while (namesIterator.hasNext()) {
			String key = namesIterator.next();

			if (key.equals("field1"))
				foundField1 = true;

			if (key.equals("field2"))
				foundField2 = true;
		}

		if (!foundField1) {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestParameterNames() didn't properly expose the 'field1' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else if (!foundField2) {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestParameterNames() didn't properly expose the 'field2' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else {
			testBean.setTestResult(true,
				"Passed RenderPhase: The getRequestParameterNames Iterator correctly exposed the query string fields in defaultViewId.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Test whether actionParameters are preserved into
	// the Render if the config value is set.
	// Test #6.49
	@BridgeTest(test = "getRequestParameterNamesPreserveParamsTest")
	public String getRequestParameterNamesPreserveParamsTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Iterator<String> namesIterator = externalContext.getRequestParameterNames();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "getRequestParameterNamesPreserveParamsTest";
		}
		else {
			testBean.setTestComplete(true);

			// loop through the Map and verify that 'field1' and 'field2' are there.
			boolean foundField1 = false;
			boolean foundField2 = false;

			while (namesIterator.hasNext()) {
				String name = namesIterator.next();

				if (name.indexOf("formDataField1") > -1) {
					foundField1 = true;
				}
				else if (name.indexOf("formDataField2") > -1) {
					foundField2 = true;
				}
			}

			if (!foundField1) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterNames() didn't properly preserve the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterNames() didn't properly preserve the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"Passed RenderPhase: The getRequestParameterNames Iterator correctly preserved the submitted form fields into the render phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- Test whether actionParameters are preserved into
	// the Render if the config value is set.
	// Test #6.46
	@BridgeTest(test = "getRequestParameterPreserveParamsTest")
	public String getRequestParameterPreserveParamsTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String> paramMap = externalContext.getRequestParameterMap();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "getRequestParameterPreserveParamsTest";
		}
		else {
			testBean.setTestComplete(true);

			// loop through the Map and verify that 'value1' and 'value2' aren't there.
			boolean foundField1 = false;
			boolean foundField2 = false;
			Set<Map.Entry<String, String>> set = paramMap.entrySet();

			for (Iterator<Map.Entry<String, String>> params = set.iterator(); params.hasNext();) {
				Map.Entry<String, String> e = params.next();
				String key = e.getKey();

				// Can't look up by key because JSF munges this id
				if (key.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (key.indexOf("formDataField2") > -1)
					foundField2 = true;
			}

			if (!foundField1) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterMap() didn't properly preserve the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterMap() didn't properly preserve the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"Passed RenderPhase: The getRequestParameterMap Map correctly preserved the submitted form fields into the render Phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.51
	/**
	 * getRequestParameterValuesMap Tests
	 */
	@BridgeTest(test = "getRequestParameterValuesMapCoreTest")
	public String getRequestParameterValuesMapCoreTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map (during action) contains the parameters in the underlying
			// request 2. Map (during render) doesn't contain any params other than the VIEW_STATE_PARAM

			Map<String, String[]> paramMap = externalContext.getRequestParameterValuesMap();
			Map<String, String[]> requestParamMap = ((PortletRequest) externalContext.getRequest()).getParameterMap();

			// Test for immutability
			try {
				paramMap.put("TestKey", new String[] { "TestValue" });
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from getRequestParameterValuesMap isn't immutable.");

				return "getRequestParameterValuesMapCoreTest";
			}
			catch (Exception e) {
				// we expect to get an exception
			}

			boolean foundField1 = false;
			boolean foundField2 = false;
			Set<Map.Entry<String, String[]>> set = paramMap.entrySet();

			for (Iterator<Map.Entry<String, String[]>> params = set.iterator(); params.hasNext();) {
				Map.Entry<String, String[]> e = params.next();
				String key = e.getKey();
				String[] values = e.getValue();

				if (key.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (key.indexOf("formDataField2") > -1)
					foundField2 = true;

				String[] requestVals = requestParamMap.get(key);

				if (requestVals == null) {
					testBean.setTestResult(false,
						"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap is missing a key returned in request.getParameterMap: " +
						key);

					return "getRequestParameterValuesMapCoreTest";
				}

				for (int i = 0; i < values.length; i++) {

					if (!values[i].equals(requestVals[i])) {
						testBean.setTestResult(false,
							"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap contains a different value for an entry vs. request.getParameterMap.  key: " +
							key + " ExtCtx.value: " + values[i] + " RequestValue: " + requestVals[i]);

						return "getRequestParameterValuesMapCoreTest";
					}
				}
			}

			// Otherwise all out tests passed:
			// Now make sure the two form fields are there:
			if (!foundField1) {
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap is either missing the form's field1 parameter or its value wasn't 'value1'.");

				return "getRequestParameterValuesMapCoreTest";
			}
			else if (!foundField2) {
				testBean.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap is either missing the form's field2 parameter or its value wasn't 'value2'.");

				return "getRequestParameterValuesMapCoreTest";
			}

			testBean.setTestResult(true,
				"Passed Action Phase: The immutable Map returned from getRequestParameterValuesMap correctly threw an exception when written to.");
			testBean.appendTestDetail(
				"Passed Action Phase: The getRequestParameterValuesMap Map correctly contained all parameters in the underlying request.getParameterMap.");

			return "getRequestParameterValuesMapCoreTest";
		}
		else {
			testBean.setTestComplete(true);

			// Now verify we only have the VIEW_STATE_PARAM
			Map<String, String[]> paramMap = externalContext.getRequestParameterValuesMap();

			if (paramMap.get(ResponseStateManager.VIEW_STATE_PARAM) == null) {
				testBean.setTestResult(false,
					"Render Phase externalContext.getRequestParameterValuesMap() doesn't contain the ResponseStateManager.VIEW_STATE parameter. Test Result from the prior action phase was: " +
					testBean.getTestResult());

				return Constants.TEST_FAILED;
			}

			// loop through the Map and verify that 'value1' and 'value2' aren't there.
			boolean foundField1 = false;
			boolean foundField2 = false;
			Set<Map.Entry<String, String[]>> set = paramMap.entrySet();

			for (Iterator<Map.Entry<String, String[]>> params = set.iterator(); params.hasNext();) {
				Map.Entry<String, String[]> e = params.next();
				String key = e.getKey();

				if (key.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (key.indexOf("formDataField2") > -1)
					foundField2 = true;
			}

			if (foundField1) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterValuesMap() incorrectly contains the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (foundField2) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterValuesMap() incorrectly contains the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.appendTestDetail(
					"Passed RenderPhase: The getRequestParameterValuesMap Map correctly excluded the submitted form fields from the render Phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.53
	@BridgeTest(test = "getRequestParameterValuesMapDefaultViewParamsTest")
	public String getRequestParameterValuesMapDefaultViewParamsTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String[]> paramMap = externalContext.getRequestParameterValuesMap();

		// loop through the Map and verify that 'value1' and 'value2' aren't there.
		boolean foundField1 = false;
		boolean foundField2 = false;
		Set<Map.Entry<String, String[]>> set = paramMap.entrySet();

		for (Iterator<Map.Entry<String, String[]>> params = set.iterator(); params.hasNext();) {
			Map.Entry<String, String[]> e = params.next();
			String key = e.getKey();

			if (key.equals("field1"))
				foundField1 = true;

			if (key.equals("field2"))
				foundField2 = true;
		}

		if (!foundField1) {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestParameterValuesMap() didn't properly expose the 'field1' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else if (!foundField2) {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestParameterValuesMap() didn't properly expose the 'field2' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else {
			testBean.setTestResult(true,
				"Passed RenderPhase: The getRequestParameterValuesMap Map correctly exposed the query string fields in defaultViewId.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Test whether actionParameters are preserved into
	// the Render if the config value is set.
	// Test #6.52
	@BridgeTest(test = "getRequestParameterValuesMapPreserveParamsTest")
	public String getRequestParameterValuesMapPreserveParamsTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String[]> paramMap = externalContext.getRequestParameterValuesMap();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			return "getRequestParameterValuesMapPreserveParamsTest";
		}
		else {
			testBean.setTestComplete(true);

			// loop through the Map and verify that 'value1' and 'value2' aren't there.
			boolean foundField1 = false;
			boolean foundField2 = false;
			Set<Map.Entry<String, String[]>> set = paramMap.entrySet();

			for (Iterator<Map.Entry<String, String[]>> params = set.iterator(); params.hasNext();) {
				Map.Entry<String, String[]> e = params.next();
				String key = e.getKey();

				if (key.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (key.indexOf("formDataField2") > -1)
					foundField2 = true;
			}

			if (!foundField1) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterValuesMap() didn't properly preserve the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testBean.setTestResult(false,
					"Failed Render Phase: externalContext.getRequestParameterValuesMap() didn't properly preserve the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true,
					"Passed RenderPhase: The getRequestParameterValuesMap Map correctly preserved the submitted form fields into the render Phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.54
	@BridgeTest(test = "getRequestPathInfoTest")
	public String getRequestPathInfoTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String pathInfo = externalContext.getRequestPathInfo();

		// As this web.xml maps the faces servlet using extension mapping
		// the value should be null
		if (pathInfo == null) {
			testBean.setTestResult(true,
				"Passed RenderPhase: externalContext.getRequestPathInfo() correctly returned a null value as the Faces servlet is extension mapped.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestPathInfo() returned a non-null value though null was expected as the Faces servlet is extension mapped.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.55
	@BridgeTest(test = "getRequestServletPathTest")
	public String getRequestServletPathTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String servletPath = externalContext.getRequestServletPath();

		// As this web.xml maps the faces servlet using extension mapping
		// the value should be viewId.mappingext i.e. viewid.jsf
		String viewId = facesContext.getViewRoot().getViewId();
		int dot = viewId.indexOf('.');
		viewId = viewId.substring(0, (dot >= 0) ? dot : viewId.length()) + ".jsf";

		if (servletPath.equalsIgnoreCase(viewId)) {
			testBean.setTestResult(true,
				"Passed RenderPhase: externalContext.getRequestServletPath() correctly returned the unmapped (extension mapped) viewId: " +
				servletPath);

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"Failed Render Phase: externalContext.getRequestServletPath() returned something other than the the unmapped (extension mapped) viewId.  Expected: " +
				viewId + " getRequestServletPath returned: " + servletPath);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.61
	@BridgeTest(test = "getResponseCharacterEncodingActionTest")
	public String getResponseCharacterEncodingActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			try {
				String encoding = externalContext.getResponseCharacterEncoding();
				testBean.setTestResult(false,
					"externalContext.getResponseCharacterEncoding() didn't throw an IllegalStateException when called during an Action.");
			}
			catch (IllegalStateException e) {
				testBean.setTestResult(true,
					"externalContext.getResponseCharacterEncoding() correctly threw an IllegalStateException when called during an Action.");
			}

			return "getResponseCharacterEncodingActionTest";
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

	// Test is MultiRequest
	// Test #6.126
	@BridgeTest(test = "getResponseCharacterEncodingEventTest")
	public String getResponseCharacterEncodingEventTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {

			testBean.setTestComplete(true);

			String eventMsg = (String) externalContext.getRequestMap().get(
					"com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testBean.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testBean.setTestResult(true,
					"externalContext.getResponseCharacterEncoding() correctly threw an IllegalStateException when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.60
	@BridgeTest(test = "getResponseCharacterEncodingRenderTest")
	public String getResponseCharacterEncodingRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();

		String charEncoding = externalContext.getResponseCharacterEncoding();
		String renderCharEncoding = mimeResponse.getCharacterEncoding();

		if (((charEncoding == null) && (renderCharEncoding == null)) || charEncoding.equals(renderCharEncoding)) {
			testBean.setTestResult(true,
				"externalContext.getResponseCharacterEncoding() correctly returned the same value as renderResponse.getCharacterEncoding()");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getResponseCharacterEncoding() incorrectly returned a different value than renderResponse.getCharacterEncoding(). " +
				"Expected: " + renderCharEncoding + " but received: " + charEncoding);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.127
	@BridgeTest(test = "getResponseCharacterEncodingResourceTest")
	public String getResponseCharacterEncodingResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceResponse resourceResponse = (ResourceResponse) externalContext.getResponse();

			String charEncoding = externalContext.getResponseCharacterEncoding();
			String resourceCharEncoding = resourceResponse.getCharacterEncoding();

			if (((charEncoding == null) && (resourceCharEncoding == null)) ||
					charEncoding.equals(resourceCharEncoding)) {
				testBean.setTestResult(true,
					"externalContext.getResponseCharacterEncoding() correctly returned the same value as resourceResponse.getCharacterEncoding()");
			}
			else {
				testBean.setTestResult(false,
					"externalContext.getResponseCharacterEncoding() incorrectly returned a different value than resourceResponse.getCharacterEncoding(). " +
					"Expected: " + resourceCharEncoding + " but received: " + charEncoding);
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
		else {
			return "";
		}
	}

	// Test #6.63
	@BridgeTest(test = "getResponseContentTypeActionTest")
	public String getResponseContentTypeActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			try {
				String contentType = externalContext.getResponseContentType();
				testBean.setTestResult(false,
					"externalContext.getResponseContentType() didn't throw an IllegalStateException when called during an Action.");
			}
			catch (IllegalStateException e) {
				testBean.setTestResult(true,
					"externalContext.getResponseContentType() correctly threw an IllegalStateException when called during an Action.");
			}

			return "getResponseContentTypeActionTest";
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

	// Test is MultiRequest
	// Test #6.130
	@BridgeTest(test = "getResponseContentTypeEventTest")
	public String getResponseContentTypeEventTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (Bridge.PortletPhase.RENDER_PHASE.equals(BridgeUtil.getPortletRequestPhase(facesContext))) {

			testBean.setTestComplete(true);

			String eventMsg = (String) externalContext.getRequestMap().get(
					"com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testBean.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testBean.setTestResult(true,
					"externalContext.getResponseContentType() correctly returned null when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test #6.62
	@BridgeTest(test = "getResponseContentTypeRenderTest")
	public String getResponseContentTypeRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
		String contentType = externalContext.getResponseContentType();
		String renderContentType = mimeResponse.getContentType();

		if (((contentType == null) && (renderContentType == null)) || contentType.equals(renderContentType)) {
			testBean.setTestResult(true,
				"externalContext.getResponseContentType() correctly returned the same value as renderResponse.getContentType().");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getResponseContentType() incorrectly returned a different value than renderResponse.getContentType(): expected: " +
				renderContentType + " but received: " + contentType);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.129
	@BridgeTest(test = "getResponseContentTypeResourceTest")
	public String getResponseContentTypeResourceTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceResponse resourceResponse = (ResourceResponse) externalContext.getResponse();
			String contentType = externalContext.getResponseContentType();
			String resourceContentType = resourceResponse.getContentType();

			if (((contentType == null) && (resourceContentType == null)) || contentType.equals(resourceContentType)) {
				testBean.setTestResult(true,
					"externalContext.getResponseContentType() correctly returned the same value as resourceRequest.getContentType()");

			}
			else {
				testBean.setTestResult(false,
					"externalContext.getResponseContentType() incorrectly returned the different value than resourceRequest.getContentType(). " +
					"Expected: " + resourceContentType + " but received: " + contentType);
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
		else {
			return "";
		}
	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Test #6.35
	@BridgeTest(test = "getSetRequestObjectTest")
	public String getSetRequestObjectTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			ActionRequest aRequest = new ActionRequestWrapper((ActionRequest) externalContext.getRequest());

			externalContext.setRequest(aRequest);

			if (externalContext.getRequest() == aRequest) {
				testBean.setTestResult(true,
					"Successfully set/retrieved a new ActionRequest using ExternalContext.set/getRequest.");

				return "getSetRequestObjectTest"; // action Navigation result
			}
			else {
				testBean.setTestResult(false,
					"ExternalContext.set/getRequest a new ActionRequest failed as the retrieved object isn't the same as the one set.");

				return "getSetRequestObjectTest";
			}
		}
		else {
			testBean.setTestComplete(true);

			// Now do the same thing for render
			RenderRequest rRequest = new RenderRequestWrapper((RenderRequest) externalContext.getRequest());

			externalContext.setRequest(rRequest);

			if (externalContext.getRequest() == rRequest) {
				testBean.appendTestDetail(
					"Successfully set/retrieved a new RenderRequest using ExternalContext.set/getRequest.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"ExternalContext.set/getRequest a new RenderRequest failed as the retrieved object isn't the same as the one set.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #6.66
	@BridgeTest(test = "illegalRedirectRenderTest")
	public String illegalRedirectRenderTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		testBean.setTestComplete(true);

		String viewId = facesContext.getViewRoot().getViewId();

		if (viewId.equals("/tests/singleRequestTest.xhtml")) {

			try {
				externalContext.redirect(facesContext.getApplication().getViewHandler().getResourceURL(facesContext,
						"/tests/NonJSFView.portlet"));
			}
			catch (IllegalStateException i) {
				testBean.setTestResult(true,
					"externalContext.redirect() during render correctly threw the IllegalStateException when redirecting to a nonFaces view.");

				return Constants.TEST_SUCCESS;
			}
			catch (Exception e) {
				testBean.setTestResult(false,
					"Calling externalContext.redirect() threw an unexpected exception: " + e.getMessage());

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(false,
				"externalContext.redirect() during render failed: it didn't throw an illegalStateException when redirecting to a nonfaces view.");

			return Constants.TEST_FAILED;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.redirect() during render failed: we started out in an unexpected view: " + viewId);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.64
	@BridgeTest(test = "redirectActionTest")
	public String redirectActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    redirect to a new view   set attribute on request scope   call redirect to the renderview   when
			 * called during render -- check we are at the new view and attribute is gone   i.e. request scope isn't
			 * preserved in case of redirect.
			 */
			externalContext.getRequestMap().put("myRedirectRequestObject", Boolean.TRUE);

			String target = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
					"/tests/redirectTestResultRenderCheck.xhtml");

			try {
				externalContext.redirect(target);
			}
			catch (Exception e) {
				testBean.setTestResult(false,
					"Calling externalContext.redirect() threw an exception: " + e.getMessage());

				return "redirectActionTest";
			}

			return
				""; // return something that won't be mapped in faces-config.xml to confirm  redirect is what is causing
					// the page transition
		}
		else {
			testBean.setTestComplete(true);

			String viewId = facesContext.getViewRoot().getViewId();

			if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				// the redirect worked

				// now verify that the scope wasn't saved.
				if (externalContext.getRequestMap().get("myRedirectRequestObject") == null) {
					testBean.setTestResult(true,
						"externalContext.redirect() during action worked correctly as we were redirected to the new view without retaining request scoped beans.");
				}
				else {
					testBean.setTestResult(false,
						"externalContext.redirect() during action failed as though we were redirected to the new view the request scope was retained.");
				}
			}
			else {
				testBean.setTestResult(false,
					"externalContext.redirect() during action failed as we weren't redirected to the new view. The viewId should be /tests/redirectTestResultRenderCheck.xhtml but is: " +
					viewId);
			}

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
	}

	// Test #6.131
	@BridgeTest(test = "redirectEventTest")
	public String redirectEventTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    set attribute on request scope   raise event which will invoke redirect   when called during render --
			 * check we are at the new view and attribute is gone   i.e. request scope isn't preserved in case of
			 * redirect.
			 */
			externalContext.getRequestMap().put("myRedirectRequestObject", Boolean.TRUE);

			// Create and raise the event
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testBean.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			String viewId = facesContext.getViewRoot().getViewId();

			if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				// the redirect worked

				// now verify that the scope wasn't saved.
				if (externalContext.getRequestMap().get("myRedirectRequestObject") == null) {
					testBean.setTestResult(true,
						"externalContext.redirect() during event worked correctly as we were redirected to the new view without retaining request scoped beans.");
				}
				else {
					testBean.setTestResult(false,
						"externalContext.redirect() during event failed as though we were redirected to the new view the request scope was retained.");
				}
			}
			else {
				testBean.setTestResult(false,
					"externalContext.redirect() during event failed as we weren't redirected to the new view. The viewId should be /tests/redirectTestResultRenderCheck.xhtml but is: " +
					viewId);
			}

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
	}

	// Test #6.65
	@BridgeTest(test = "redirectRenderPRP1Test")
	public String redirectRenderPRP1Test(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    set a new value for the PRP by updating the model -- this will cause the PRP to come in the render in
			 * the subsequent render:  set an attribute indicating we are redirecting; redirect; check to see if the PRP
			 * is in the request.
			 *
			 * Key difference between PRP1Test and PRP2Test is that PRP1 already calls encodeActionURL on url prior to
			 * redirect while   PRP2 does not.
			 */
			externalContext.getRequestMap().put("modelPRP", testBean.getTestName());

			return "redirectRenderPRP1Test";
		}
		else {
			String viewId = facesContext.getViewRoot().getViewId();

			if (viewId.equals("/tests/multiRequestTestResultRenderCheck.xhtml")) {
				String target = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
						"/tests/redirectTestResultRenderCheck.xhtml");

				try {
					externalContext.redirect(externalContext.encodeActionURL(target));
				}
				catch (Exception e) {
					testBean.setTestComplete(true);
					testBean.setTestResult(false,
						"Calling externalContext.redirect() threw an exception: " + e.getMessage());

					return Constants.TEST_FAILED;
				}

				return "";
			}
			else if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				testBean.setTestComplete(true);

				// ensure that both the public render paramter and the model are there and have the same value
				RenderRequest request = (RenderRequest) externalContext.getRequest();
				String[] prpArray = request.getPublicParameterMap().get("testPRP");
				String modelPRP = (String) externalContext.getRequestMap().get("modelPRP");

				if (prpArray == null) {
					testBean.setTestResult(false,
						"redirected request didn't carry forward the public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (modelPRP == null) {
					testBean.setTestResult(false,
						"redirected request didn't update the model from the passed public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(prpArray[0])) {
					testBean.setTestResult(false,
						"redirected request:  passed public render parameter value doesn't match underlying one.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(testBean.getTestName())) {
					testBean.setTestResult(false,
						"redirected request:  public render parameter didn't contain expected value.  PRP value: " +
						modelPRP + " but expected: " + testBean.getTestName());

					return Constants.TEST_FAILED;
				}
				else {
					testBean.setTestResult(true,
						"externalContext.redirect() during render worked correctly as we were redirected to the new view.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {
				testBean.setTestResult(false,
					"externalContext.redirect() during render failed as we ended up in an unexpected view: " + viewId);

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #6.65
	@BridgeTest(test = "redirectRenderPRP2Test")
	public String redirectRenderPRP2Test(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    set a new value for the PRP by updating the model -- this will cause the PRP to come in the render in
			 * the subsequent render:  set an attribute indicating we are redirecting; redirect; check to see if the PRP
			 * is in the request.
			 *
			 * Key difference between PRP1Test and PRP2Test is that PRP1 already calls encodeActionURL on url prior to
			 * redirect while   PRP2 does not.
			 */
			externalContext.getRequestMap().put("modelPRP", testBean.getTestName());

			return "redirectRenderPRP2Test";
		}
		else {
			String viewId = facesContext.getViewRoot().getViewId();

			if (viewId.equals("/tests/multiRequestTestResultRenderCheck.xhtml")) {
				String target = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
						"/tests/redirectTestResultRenderCheck.xhtml");

				try {
					externalContext.redirect(target);
				}
				catch (Exception e) {
					testBean.setTestComplete(true);
					testBean.setTestResult(false,
						"Calling externalContext.redirect() threw an exception: " + e.getMessage());

					return Constants.TEST_FAILED;
				}

				return "";
			}
			else if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				testBean.setTestComplete(true);

				// ensure that both the public render paramter and the model are there and have the same value
				RenderRequest request = (RenderRequest) externalContext.getRequest();
				String[] prpArray = request.getPublicParameterMap().get("testPRP");
				String modelPRP = (String) externalContext.getRequestMap().get("modelPRP");

				if (prpArray == null) {
					testBean.setTestResult(false,
						"redirected request didn't carry forward the public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (modelPRP == null) {
					testBean.setTestResult(false,
						"redirected request didn't update the model from the passed public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(prpArray[0])) {
					testBean.setTestResult(false,
						"redirected request:  passed public render parameter value doesn't match underlying one.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(testBean.getTestName())) {
					testBean.setTestResult(false,
						"redirected request:  public render parameter didn't contain expected value.  PRP value: " +
						modelPRP + " but expected: " + testBean.getTestName());

					return Constants.TEST_FAILED;
				}
				else {
					testBean.setTestResult(true,
						"externalContext.redirect() during render worked correctly as we were redirected to the new view.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {
				testBean.setTestResult(false,
					"externalContext.redirect() during render failed as we ended up in an unexpected view: " + viewId);

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #6.65
	@BridgeTest(test = "redirectRenderTest")
	public String redirectRenderTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String viewId = facesContext.getViewRoot().getViewId();

		if (viewId.equals("/tests/singleRequestTest.xhtml")) {
			String target = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
					"/tests/redirectTestResultRenderCheck.xhtml");

			try {
				externalContext.redirect(target);
			}
			catch (Exception e) {
				testBean.setTestComplete(true);
				testBean.setTestResult(false,
					"Calling externalContext.redirect() threw an exception: " + e.getMessage());

				return Constants.TEST_FAILED;
			}

			return "";
		}
		else if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
			testBean.setTestComplete(true);
			testBean.setTestResult(true,
				"externalContext.redirect() during render worked correctly as we were redirected to the new view.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.redirect() during render failed as we ended up in an unexpected view: " + viewId);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.42
	/**
	 * getRequestMap Tests
	 */
	@BridgeTest(test = "requestMapCoreTest")
	public String requestMapCoreTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is mutable
		// 2. Map contains attributes in the underlying request
		// a) set on portlet request -- get via map
		// b) set on map -- get via portlet request
		// 3. Remove in request -- gone from Map
		// 4. Remove from Map -- gone in request

		PortletRequest request = (PortletRequest) externalContext.getRequest();
		Map<String, Object> externalContextRequestMap = externalContext.getRequestMap();

		// ensure they start out identical
		if (
			!containsIdenticalEntries(externalContextRequestMap, (Enumeration<String>) request.getAttributeNames(),
					request)) {
			testBean.setTestResult(false,
				"Failed: Portlet request attributes and the externalContext requestMap entries aren't identical.");

			return Constants.TEST_FAILED;
		}

		// Test for mutability
		try {
			externalContextRequestMap.put("Test0Key", "Test0Value");
			request.setAttribute("Test1Key", "Test1Value");
		}
		catch (Exception e) {
			testBean.setTestResult(false,
				"Failed: Putting an attribute on the ExternalContext's requestmap threw and exception: " +
				e.toString());

			return Constants.TEST_FAILED;
		}

		// test that we can read an attribute set on the portlet request via this Map
		// and vice-versa -- as we have just written an attribute on the externalContext and
		// the test portlet wrote one on the portlet request -- the act of verifying
		// the Maps contain the same keys/values should do the trick.
		if (
			!containsIdenticalEntries(externalContextRequestMap, (Enumeration<String>) request.getAttributeNames(),
					request)) {
			testBean.setTestResult(false,
				"Failed: After setting an attribute on the portlet request and the externalContext requestMap they no longer contain identical entries.");

			return Constants.TEST_FAILED;
		}

		// Now remove the attribute we put in the  -- do the remove on the opposite object
		externalContextRequestMap.remove("Test1Key");
		request.removeAttribute("Test0Key");

		if (
			!containsIdenticalEntries(externalContextRequestMap, (Enumeration<String>) request.getAttributeNames(),
					request)) {
			testBean.setTestResult(false,
				"Failed: After removing an attribute on the portlet request and the externalContext requestMap they no longer contain identical entries.");

			return Constants.TEST_FAILED;
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true, "The Map returned from getRequestMap is mutable.");
		testBean.appendTestDetail(
			"The getRequestMap Map correctly expresses attributes in the underlying request that have been added there.");
		testBean.appendTestDetail(
			"The getRequestMap Map correctly reflects attrbiutes into the underlying request that have been added to it.");
		testBean.appendTestDetail(
			"The getRequestMap Map correctly doesn't express attrbiutes that have been removed from the underlying request");
		testBean.appendTestDetail(
			"The getRequestMap Map correctly cause the underlying request to remove any attributes removed from it");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.44
	@BridgeTest(test = "requestMapPreDestroyRemoveWithinActionTest")
	public String requestMapPreDestroyRemoveWithinActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Application app = facesContext.getApplication();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// ensure the managed beans come into existence
			Boolean isIn = (Boolean) app.evaluateExpressionGet(facesContext, "#{predestroyBean1.inBridgeRequestScope}",
					Object.class);
			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.remove("predestroyBean1");

			// Now verify that things worked correctly We expect that the beans were not added to the bridge scope (yet)
			// and hence only the Predestroy was called
			Boolean notifiedAddedToBridgeScope = (Boolean) requestMap.get("PreDestroyBean1.attributeAdded");
			Boolean notifiedPreDestroy = (Boolean) requestMap.get("PreDestroyBean1.servletPreDestroy");
			Boolean notifiedBridgePreDestroy = (Boolean) requestMap.get("PreDestroyBean1.bridgePreDestroy");

			if ((notifiedAddedToBridgeScope == null) && (notifiedBridgePreDestroy == null) &&
					(notifiedPreDestroy != null) && notifiedPreDestroy.equals(Boolean.TRUE)) {

				// Only the regular PreDestroy was called and so it would have cleaned itself up
				testBean.setTestResult(true,
					"The bridge request scope behaved correctly in handling preDestroy of a removed attribute prior to it being added to the bridge's scope in that:");
				testBean.appendTestDetail(
					"     a) the bean wasn't notified it had been added to the bridge request scope.");
				testBean.appendTestDetail("     b) the bean didn't have its BridgePreDestroy called.");
				testBean.appendTestDetail("     c) the bean did have its Predestroy called.");
			}
			else {
				testBean.setTestResult(false,
					"The bridge request scope didn't behaved correctly in handling preDestroy of a removed attribute prior to it being added to the bridge's scope in that:");

				if (notifiedAddedToBridgeScope != null)
					testBean.appendTestDetail("::::: it notified the bean it was added to the bridge request scope.");

				if (notifiedBridgePreDestroy != null)
					testBean.appendTestDetail(
						"::::: it notified the bean it was removed from the bridge request scope.");

				if (notifiedPreDestroy == null)
					testBean.appendTestDetail("::::: it didn't notify the bean's PreDestroy.");

				if ((notifiedPreDestroy != null) && notifiedPreDestroy.equals(Boolean.FALSE))
					testBean.appendTestDetail(
						"::::: the bean's Predestroy was called but it thought it had been added to the bridge request scope.");
			}

			// Now remove them manually to see if they are cleaned up correctly

			return "requestMapPreDestroyRemoveWithinActionTest";
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

	// Test is MultiRequest --
	// Test #6.43
	@BridgeTest(test = "requestMapRequestScopeTest")
	public String requestMapRequestScopeTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// Test a bunch of things:
		// a) attrs added in action are there in render request
		// b) attrs added in action that are supposed to be excluded aren't there in render request
		// -- attrs added before bridge called (in TestPortlet)
		// -- attrs with one of the excluded values
		// -- attrs with excluded key prefixes
		// -- attrs defined excluded by this portlet (in its portlet.xml)
		// -- test single attr
		// -- test attr prefix
		// -- test attr prefix isn't recursive

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			requestMap.put("myRequestObject", externalContext.getRequest()); // should be excluded because of value type
			requestMap.put("myFacesContext", facesContext); // should be excluded because of value type
			requestMap.put("javax.faces.myKey1", Boolean.TRUE); // should be excluded because its in exlcuded namespace
			requestMap.put("javax.faces.myNamespace.myKey1", Boolean.TRUE); // should be retained because excluded
                                                                            // namespaces don't recurse
			requestMap.put("myKey1", Boolean.TRUE); // should be retained
			requestMap.put("myExcludedNamespace.myKey1", Boolean.TRUE); // should be excluded as defined in portlet.xml
			requestMap.put("myExcludedKey", Boolean.TRUE); // defined as excluded in the portlet.xml
			requestMap.put("myExcludedNamespace.myIncludedNamespace.myKey1", Boolean.TRUE); // should be retained as
                                                                                            // excluded namespaces don't
                                                                                            // recurse
			requestMap.put("myFacesConfigExcludedNamespace.myKey1", Boolean.TRUE); // should be excluded as defined in

			// faces-config.xml
			requestMap.put("myFacesConfigExcludedKey", Boolean.TRUE); // defined as excluded in the faces-config.xml
			requestMap.put("myFacesConfigExcludedNamespace.myIncludedNamespace.myKey1", Boolean.TRUE); // should be retained as

			// excluded namespaces
			// don't recurse

			return "requestMapRequestScopeTest";
		}
		else {
			testBean.setTestComplete(true);

			// make sure that the attrbiute added before ExternalContext acquired is missing (set in the TestPortlet's
			// action handler)
			if (requestMap.get("verifyPreBridgeExclusion") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute that existed prior to FacesContext being acquired.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myRequestObject") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose value is the PortletRequest object.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myFacesContext") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose value is the FacesContext object.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("javax.faces.myKey1") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute in the predefined exlcuded namespace javax.faces.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("javax.faces.myNamespace.myKey1") == null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly exlcuded an attribute that is in a subnamespace of the javax.faces namespace.  Exclusion rules aren't recursive.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myKey1") == null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly excluded an attribute that wasn't defined as excluded.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myExcludedNamespace.myKey1") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key is in a portlet defined excluded namespace.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myExcludedKey") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key matches a portlet defined excluded attribute");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myExcludedNamespace.myIncludedNamespace.myKey1") == null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly exlcuded an attribute that is in a subnamespace of a portlet excluded namespace.  Exclusion rules aren't recursive.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myFacesConfigExcludedNamespace.myKey1") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key is in a faces-config.xml defined excluded namespace.");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myFacesConfigExcludedKey") != null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key matches a faces-config.xml defined excluded attribute");

				return Constants.TEST_FAILED;
			}

			if (requestMap.get("myFacesConfigExcludedNamespace.myIncludedNamespace.myKey1") == null) {
				testBean.setTestResult(false,
					"The bridge request scope incorrectly exlcuded an attribute that is in a subnamespace of a faces-config.xml excluded namespace.  Exclusion rules aren't recursive.");

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true, "The bridge request scope behaved correctly for each of the following tests:");
			testBean.appendTestDetail(
				"     a) it excluded attributes whose values were of the type PortletRequest and FacesContext.");
			testBean.appendTestDetail(
				"     b) it excluded attributes from both a predefined namespace and one the portlet defined.");
			testBean.appendTestDetail(
				"     c) it included attributes from both a predefined namespace and one the portlet defined when the attribute name contained a further namespace qualification.");
			testBean.appendTestDetail(
				"     d) it excluded attributes that were added before the bridge was invoked (FacesContext acquired).");
			testBean.appendTestDetail(
				"     b) it excluded attributes from each of a predefined namespace, a portlet defined namespace, and a faces-config.xml defiend namespace.");
			testBean.appendTestDetail("     b) it included attributes it was supposed to.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- tests both setting request in action and render
	// Note:  this tests that you can't get an encoding after reading the parameter.
	// we can't test for the positive -- that the setEncoding actually works
	// as portlet containers are supposed to process the form parameters before
	// the portlet reads them  -- some containers (WebSphere) chooses to do this
	// before calling the portlet.
	// Test #6.37
	@BridgeTest(test = "setRequestCharacterEncodingActionTest")
	public String setRequestCharacterEncodingActionTest(TestBean testBean) {
		final String utf8 = "UTF-8";
		final String utf16 = "UTF-16";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			String s = externalContext.getRequestCharacterEncoding();
			String testEncoding = null;

			// A number of container have trouble with this test --
			// All one should have to do is get the parameters to cause the test state to be set
			// but some containers require the reader be called -- issue is -- the spec also
			// says you can't get a reader if its a form postback.
			// Anyway -- first try to get a reader -- then to read the parameters to
			// be most accomodative
			try {
				BufferedReader b = ((ClientDataRequest) externalContext.getRequest()).getReader();
				int i = 0;
			}
			catch (Exception e) {

				// container likely did the right thing -- but make sure by reading the parameters
				Map<String, String[]> requestParameterMap = ((PortletRequest) externalContext.getRequest())
					.getParameterMap();
			}

			if ((s == null) || ((s != null) && !s.equalsIgnoreCase(utf8))) {
				testEncoding = utf8;
			}
			else {
				testEncoding = utf16;
			}

			try {
				externalContext.setRequestCharacterEncoding(testEncoding);

				String v = externalContext.getRequestCharacterEncoding();

				if (((v == null) && (s == null)) || ((v != null) && (s != null) && v.equalsIgnoreCase(s))) {
					testBean.setTestResult(true,
						"setRequestCharacterEncoding was correctly ignored after reading a parameter in an action request.");
				}
				else {
					testBean.setTestResult(false,
						"setRequestCharacterEncoding incorrectly set a new encoding after reading a parameter in an action request.");
				}
			}
			catch (Exception e) {
				testBean.setTestResult(false,
					"setRequestCharacterEncoding was correctly ignored after reading a parameter in an action request.");
			}

			return "setRequestCharacterEncodingActionTest";
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

	// Test is SingleRequest -- Render only
	// Test #6.36
	/**
	 * setRequestCharacterEncoding Tests
	 */
	@BridgeTest(test = "setRequestCharacterEncodingRenderTest")
	public String setRequestCharacterEncodingRenderTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Call setRequestCharacterEncoding -- fail if an exception is thrown
		try {
			externalContext.setRequestCharacterEncoding("UTF-8");

			// In portlet 1.0 there is no supprt for this -- so spec says ignore
			testBean.setTestResult(true,
				"setRequestCharacterEncoding correctly didn't throw an exception when called during the render Phase.");

			return Constants.TEST_SUCCESS;
		}
		catch (Exception e) {
			testBean.setTestResult(false,
				"setRequestCharacterEncoding correctly didn't throw an exception when called during the render Phase.");

			return Constants.TEST_FAILED;
		}
	}

	private boolean containsIdenticalEntries(Map<String, Object> m, Enumeration<String> eNames, PortletRequest r) {

		// For each entry in m ensure there is an idenitcal one in the request
		for (Iterator<Map.Entry<String, Object>> entries = m.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<String, Object> e = entries.next();
			Object requestObj = r.getAttribute(e.getKey());
			Object mapObj = e.getValue();

			if ((mapObj == null) && (requestObj == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (requestObj == null) || !mapObj.equals(requestObj)) {
				return false;
			}
		}

		// For each entry in the request -- ensure there is an identical one in the map
		while (eNames.hasMoreElements()) {
			String key = eNames.nextElement();
			Object requestObj = r.getAttribute(key);
			Object mapObj = m.get(key);

			if ((mapObj == null) && (requestObj == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (requestObj == null) || !mapObj.equals(requestObj)) {
				return false;
			}
		}

		return true;
	}

	private boolean isStrictXhtmlEncoded(String url) {

		// check for use of &amp; in query string
		int currentPos = 0;
		boolean isStrict = false;

		while (true) {
			int ampPos = url.indexOf('&', currentPos);
			int xhtmlAmpPos = url.indexOf("&amp;", currentPos);

			// no more & to process -- so return current value of isStrict
			if (ampPos == -1) {
				return isStrict;
			}

			// if the amp we found doesn't start an &amp; then its not strict
			if (ampPos != xhtmlAmpPos) {
				return false;
			}

			isStrict = true;
			currentPos = ampPos + 1;
		}
	}

	private String[] trim(String[] toTrim) {

		for (int i = 0; i < toTrim.length; i++) {
			toTrim[i] = toTrim[i].trim();
		}

		return toTrim;
	}
}
