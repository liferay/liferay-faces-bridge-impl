/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
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
	public String dispatchUsesForwardTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

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
			testRunner.setTestResult(false,
				"Unable to acquire the request attribute containing the result that should have been set in the JSP page.");

			return Constants.TEST_FAILED;
		}
		else if (value.booleanValue()) {
			testRunner.setTestResult(true,
				"Successfully accessed javax.servlet.forward.servletPath attribute indicating we are inside a dispatch.forward");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"javax.servlet.forward.servletPath not set, but it would be if we are inside a dispatch.forward");

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.5
	@BridgeTest(test = "encodeActionURLAbsoluteURLTest")
	public String encodeActionURLAbsoluteURLTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String ABSOLUTEURL_TEST_STRING = "http://www.apache.org";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (extCtx.encodeActionURL(ABSOLUTEURL_TEST_STRING).equals(ABSOLUTEURL_TEST_STRING)) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly returned an unchanged string when passed an absolute URL.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL didn't return an unchanged string when passed an absolute URL.  Test parameter: " +
				ABSOLUTEURL_TEST_STRING + " and encodeActionURL returned: " +
				extCtx.encodeActionURL(ABSOLUTEURL_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.7
	@BridgeTest(test = "encodeActionURLDirectLinkFalseTest")
	public String encodeActionURLDirectLinkFalseTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String DIRECTLINK_FALSE_TEST_STRING =
			"/test.jsp?firstParam=value&javax.portlet.faces.DirectLink=false&anotherParam=value";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		ViewHandler vh = ctx.getApplication().getViewHandler();

		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(DIRECTLINK_FALSE_TEST_STRING)
			.toString();

		if (!extCtx.encodeActionURL(testString).contains("javax.portlet.faces.DirectLink")) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly returned an url string without the javax.portlet.faces.DirectLink parameter when its value was false.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL didn't return an url string without the javax.portlet.faces.DirectLink parameter when its value was false.  Test parameter: " +
				testString + " and encodeActionURL returned: " + extCtx.encodeActionURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.6
	@BridgeTest(test = "encodeActionURLDirectLinkTrueTest")
	public String encodeActionURLDirectLinkTrueTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String DIRECTLINK_TRUE_TEST_STRING =
			"/test.jsp?firstParam=value&javax.portlet.faces.DirectLink=true&anotherParam=value";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		ViewHandler vh = ctx.getApplication().getViewHandler();
		PortletRequest r = (PortletRequest) extCtx.getRequest();

		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(DIRECTLINK_TRUE_TEST_STRING)
			.toString();

		String s = new StringBuffer(testString.length() + 20).append(r.getScheme()).append("://").append(
				r.getServerName()).append(":").append(r.getServerPort()).append(testString).toString();

		if (extCtx.encodeActionURL(testString).equalsIgnoreCase(s)) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly returned an absolute URL representing the DirectLink url and it correctly contains the javax.portlet.faces.DirectLink parameter with a value of true.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL didn't return an absolute URL representing the DirectLink url or it didn't contain the javax.portlet.faces.DirectLink parameter with a value of true.  Expected: " +
				s + " and encodeActionURL returned: " + extCtx.encodeActionURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.10
	@BridgeTest(test = "encodeActionURLJSFViewActionTest")
	public String encodeActionURLJSFViewActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLJSFViewActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(true, "encodeActionURL correctly encoded a portlet action URL.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.103
	@BridgeTest(test = "encodeActionURLJSFViewEventTest")
	public String encodeActionURLJSFViewEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL encoded during the event phase.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.18
	@BridgeTest(test = "encodeActionURLJSFViewRenderTest")
	public String encodeActionURLJSFViewRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			testRunner.setTestResult(true, "encodeActionURL correctly encoded a portlet action URL.");

			return "encodeActionURLJSFViewRenderTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			return Constants.TEST_SUCCESS;
		}
	}

	// Test Single Render followed by resource request
	// Test #6.111
	@BridgeTest(test = "encodeActionURLJSFViewResourceTest")
	public String encodeActionURLJSFViewResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING = "/tests/viewLink.jsf";

		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with params as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with params as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is SingleRequest -- Render only
	// Test #6.9
	@BridgeTest(test = "encodeActionURLPortletActionTest")
	public String encodeActionURLPortletActionTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String PORTLET_ACTION_TEST_STRING = "portlet:action?param1=value1&param2=value2";
		final String PORTLET_ACTION_TEST_STRING_XMLENCODED = "portlet:action?param1=value1&amp;param2=value2";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		MimeResponse response = (MimeResponse) extCtx.getResponse();
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
			bridgeEncoded = extCtx.encodeActionURL(PORTLET_ACTION_TEST_STRING_XMLENCODED);
		}
		else {
			bridgeEncoded = extCtx.encodeActionURL(PORTLET_ACTION_TEST_STRING);
		}

		if (bridgeEncoded.equals(portletEncoded)) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL as an actionURL using the portlet: syntax.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL incorrectly encoded a portlet action URL as an actionURL using the portlet: syntax.  In encoding: " +
				PORTLET_ACTION_TEST_STRING + " the bridge returned: " + bridgeEncoded +
				" but the corresponding portletURL encoding returned: " + portletEncoded);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.8
	@BridgeTest(test = "encodeActionURLPortletRenderTest")
	public String encodeActionURLPortletRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String PORTLET_RENDER_TEST_STRING = "portlet:render?param1=value1&param2=value2";
		final String PORTLET_RENDER_TEST_STRING_XMLENCODED = "portlet:render?param1=value1&amp;param2=value2";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		MimeResponse response = (MimeResponse) extCtx.getResponse();
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
			bridgeEncoded = extCtx.encodeActionURL(PORTLET_RENDER_TEST_STRING_XMLENCODED);
		}
		else {
			bridgeEncoded = extCtx.encodeActionURL(PORTLET_RENDER_TEST_STRING);
		}

		if (bridgeEncoded.equals(portletEncoded)) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet render URL as an actionURL using the portlet: syntax.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL incorrectly encoded a portlet render URL as an actionURL using the portlet: syntax.  In encoding: " +
				PORTLET_RENDER_TEST_STRING + " the bridge returned: " + bridgeEncoded +
				" but the corresponding portletURL encoding returned: " + portletEncoded);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.102
	@BridgeTest(test = "encodeActionURLPortletResourceTest")
	public String encodeActionURLPortletResourceTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String PORTLET_RENDER_TEST_STRING =
			"portlet:resource?_jsfBridgeViewId=/tests/singleRequestTest.xhtml&param1=value1&param2=value2";
		final String PORTLET_RENDER_TEST_STRING_XMLENCODED =
			"portlet:resource?_jsfBridgeViewId=/tests/singleRequestTest.xhtml&amp;param1=value1&amp;param2=value2";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		MimeResponse response = (MimeResponse) extCtx.getResponse();
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
			bridgeEncoded = extCtx.encodeActionURL(PORTLET_RENDER_TEST_STRING_XMLENCODED);
		}
		else {
			bridgeEncoded = extCtx.encodeActionURL(PORTLET_RENDER_TEST_STRING);
		}

		if (bridgeEncoded.equals(portletEncoded)) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet resource URL as an actionURL using the portlet: syntax.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL incorrectly encoded a portlet resource URL as an actionURL using the portlet: syntax.  In encoding: " +
				PORTLET_RENDER_TEST_STRING + " the bridge returned: " + bridgeEncoded +
				" but the corresponding portletURL encoding returned: " + portletEncoded);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.4
	@BridgeTest(test = "encodeActionURLPoundCharTest")
	public String encodeActionURLPoundCharTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String POUNDCHAR_TEST_STRING = "#AnchorReference";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (extCtx.encodeActionURL(POUNDCHAR_TEST_STRING).equals(POUNDCHAR_TEST_STRING)) {
			testRunner.setTestResult(true,
				"encodeActionURL correctly returned an unchanged string when the string was prefixed with #.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeActionURL didn't return an unchanged string when the string was prefixed with #.  Test parameter: " +
				POUNDCHAR_TEST_STRING + " and encodeActionURL returned: " +
				extCtx.encodeActionURL(POUNDCHAR_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.12
	@BridgeTest(test = "encodeActionURLWithInvalidModeActionTest")
	public String encodeActionURLWithInvalidModeActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithInvalidModeActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = pReq.getPortletMode();

			if ((mode == null) || !mode.toString().equalsIgnoreCase("view")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid mode and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.105
	@BridgeTest(test = "encodeActionURLWithInvalidModeEventTest")
	public String encodeActionURLWithInvalidModeEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = pReq.getPortletMode();

			if ((mode == null) || !mode.toString().equalsIgnoreCase("view")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid mode and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.20
	@BridgeTest(test = "encodeActionURLWithInvalidModeRenderTest")
	public String encodeActionURLWithInvalidModeRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = pReq.getPortletMode();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((mode == null) || !mode.toString().equalsIgnoreCase("view")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);
			}
			else {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid mode and properly encoding the parameter.");
			}

			return "encodeActionURLWithInvalidModeActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithInvalidModeResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with invalid mode as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with invalid mode as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.16
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityActionTest")
	public String encodeActionURLWithInvalidSecurityActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithInvalidSecurityActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			if (pReq.isSecure()) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded an invalid portlet security state.  The resulting request wasn't in the expected 'non-secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL ontaining an invalid security state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.109
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityEventTest")
	public String encodeActionURLWithInvalidSecurityEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			if (pReq.isSecure()) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded an invalid portlet security state.  The resulting request wasn't in the expected 'non-secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL ontaining an invalid security state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.24
	@BridgeTest(test = "encodeActionURLWithInvalidSecurityRenderTest")
	public String encodeActionURLWithInvalidSecurityRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pReq.isSecure()) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded an invalid portlet security state.  The resulting request wasn't in the expected 'non-secure' mode.");

				return Constants.TEST_FAILED;
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL ontaining an invalid security state and parameter.");
			}

			return "encodeActionURLWithInvalidSecurityRenderTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithInvalidSecurityResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with invalid security as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with invalid security as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.14
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateActionTest")
	public String encodeActionURLWithInvalidWindowStateActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithInvalidWindowStateActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			if ((ws == null) || !ws.toString().equalsIgnoreCase("normal")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid window state and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.107
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateEventTest")
	public String encodeActionURLWithInvalidWindowStateEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			if ((ws == null) || !ws.toString().equalsIgnoreCase("normal")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid window state and properly encoding the parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.22
	@BridgeTest(test = "encodeActionURLWithInvalidWindowStateRenderTest")
	public String encodeActionURLWithInvalidWindowStateRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((ws == null) || !ws.toString().equalsIgnoreCase("normal")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing an invalid window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);
			}
			else {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL by ignoring the invalid window state and properly encoding the parameter.");
			}

			return "encodeActionURLWithInvalidWindowStateRenderTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithInvalidWindowStateResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with invalid window state as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with invalid window state as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.11
	@BridgeTest(test = "encodeActionURLWithModeActionTest")
	public String encodeActionURLWithModeActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithModeActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = pReq.getPortletMode();

			if ((mode == null) || !mode.toString().equalsIgnoreCase("edit")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet mode.  The resulting request wasn't in the expected 'edit' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new mode and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.104
	@BridgeTest(test = "encodeActionURLWithModeEventTest")
	public String encodeActionURLWithModeEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = pReq.getPortletMode();

			if ((mode == null) || !mode.toString().equalsIgnoreCase("edit")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet mode.  The resulting request wasn't in the expected 'edit' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter during the event phase.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter during the event phase.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new mode and parameter during the event phase.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.19
	@BridgeTest(test = "encodeActionURLWithModeRenderTest")
	public String encodeActionURLWithModeRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.HEADER_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			Map<String, String> requestParameterMap = ctx.getExternalContext().getRequestParameterMap();

			String testName = testRunner.getTestName();

			// Parameter/Mode encoded in the faces-config.xml target
			String portletMode = requestParameterMap.get(testName + "-portletMode");

			// Check that the parameter came along too
			String param1 = requestParameterMap.get(testName + "-param1");

			if (!"edit".equals(portletMode)) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet mode. The resulting request wasn't in the expected 'edit' mode.");
			}
			else if (param1 == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!param1.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					param1);
			}
			else {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a new mode and parameter.");
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithModeResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=edit&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with mode as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with mode as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.17
	@BridgeTest(test = "encodeActionURLWithParamActionTest")
	public String encodeActionURLWithParamActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithParamActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();

			// Parameter encoded in the faces-config.xml target
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((pVal != null) && pVal.equals("testValue")) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a parameter.");

				return Constants.TEST_SUCCESS;
			}
			else {

				if (pVal == null) {
					testRunner.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request didn't contain the expected 'param1' parameter.");
				}
				else {
					testRunner.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
						pVal);
				}
			}

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.110
	@BridgeTest(test = "encodeActionURLWithParamEventTest")
	public String encodeActionURLWithParamEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			// Parameter encoded in the faces-config.xml target
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((pVal != null) && pVal.equals("testValue")) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a parameter during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {

				if (pVal == null) {
					testRunner.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter during the event phase.  The resulting request didn't contain the expected 'param1' parameter.");
				}
				else {
					testRunner.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter during the event phase.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
						pVal);
				}
			}

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.25
	@BridgeTest(test = "encodeActionURLWithParamRenderTest")
	public String encodeActionURLWithParamRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();

			// Parameter encoded in the faces-config.xml target
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((pVal != null) && pVal.equals("testValue")) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a parameter.");
			}
			else {

				if (pVal == null) {
					testRunner.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request didn't contain the expected 'param1' parameter.");
				}
				else {
					testRunner.setTestResult(false,
						"encodeActionURL incorrectly encoded a portlet action URL containing a parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
						pVal);
				}
			}

			return "encodeActionURLWithParamRenderTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithParamResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING = "/tests/viewLink.jsf?param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with params as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with params as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";

	}

	// Test is MultiRequest -- Render/Action
	// Test #6.15
	@BridgeTest(test = "encodeActionURLWithSecurityActionTest")
	public String encodeActionURLWithSecurityActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithSecurityActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Secure encoded in the faces-config.xml target
			if (!pReq.isSecure()) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the portlet security state.  The resulting request wasn't in the expected 'secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL accessed in a secure state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.108
	@BridgeTest(test = "encodeActionURLWithSecurityEventTest")
	public String encodeActionURLWithSecurityEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Secure encoded in the faces-config.xml target
			if (!pReq.isSecure()) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the portlet security state.  The resulting request wasn't in the expected 'secure' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL accessed in a secure state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.23
	@BridgeTest(test = "encodeActionURLWithSecurityRenderTest")
	public String encodeActionURLWithSecurityRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (!pReq.isSecure()) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the portlet security state.  The resulting request wasn't in the expected 'secure' mode.");
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new security state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);
			}
			else {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL accessed in a secure state and parameter.");
			}

			return "encodeActionURLWithSecurityRenderTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithSecurityResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url with security as an actionURL during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with security as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.13
	@BridgeTest(test = "encodeActionURLWithWindowStateActionTest")
	public String encodeActionURLWithWindowStateActionTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "encodeActionURLWithWindowStateActionTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			if ((ws == null) || !ws.toString().equalsIgnoreCase("maximized")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new window state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.106
	@BridgeTest(test = "encodeActionURLWithWindowStateEventTest")
	public String encodeActionURLWithWindowStateEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			if ((ws == null) || !ws.toString().equalsIgnoreCase("maximized")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");

				return Constants.TEST_FAILED;
			}

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");

				return Constants.TEST_FAILED;
			}

			if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"encodeActionURL correctly encoded a portlet action URL containing a new window state and parameter.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.21
	@BridgeTest(test = "encodeActionURLWithWindowStateRenderTest")
	public String encodeActionURLWithWindowStateRenderTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((ws == null) || !ws.toString().equalsIgnoreCase("maximized")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a portlet action URL containing a new window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);
			}
			else {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a portlet action URL containing a new window state and parameter.");
			}

			return "encodeActionURLWithWindowStateRenderTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeActionURLWithWindowStateResourceTest(TestRunnerBean testRunner) {

		// Somewhat of a weird test -- we can't test this directly in that there isn't a way to encode an JSF view
		// actionURL into the response of the resource and have it clicked on.  We also can't generate such an url and
		// compare against the one generated by the underlying response because encodeActionURL will encode the JSF view
		// into the returned string in a manner that is bridge impl specific (so we can't account for this encoding). So
		// instead, perform the same encoding in both other phased request and the resource request.  If they are the
		// same then the test passes.  This works (indirectly) because we have an existing test that verifies this
		// encoding during a render/action phase.  So the presumption is if it works for render/action and you get the
		// same url during from the encoding during a resource then all is good.
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map m = extCtx.getRequestMap();
		Map s = extCtx.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = new StringBuffer(extCtx.getRequestContextPath()).append(ENCODE_ACTIONURL_TEST_STRING)
			.toString();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {

			// mark the resource request as having occurred so the button changes names appropriately
			m.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);

			// Now run the test
			testRunner.setTestComplete(true);

			// PortletContainers can return "URLs" with strict XML encoding -- as the bridge
			// encoding depends on what is past in to it -- make sure we send in a string
			// with the same encoding as compare string.
			String resourceEncoded = extCtx.encodeActionURL(testString);
			String otherPhaseEncoded = (String) s.get("com.liferay.faces.bridge.tck.encodedURL");

			// remove it for future/other tests
			s.remove("com.liferay.faces.bridge.tck.encodedURL");

			if (otherPhaseEncoded == null) {
				testRunner.setTestResult(false,
					"test failed because it relies on comparing the encoded result with one generated during a prior phase, however the one generated from the prior phase isn't there.");

				return Constants.TEST_FAILED;
			}
			else if (resourceEncoded.equals(otherPhaseEncoded)) {
				testRunner.setTestResult(true,
					"encodeActionURL correctly encoded a JSF View url as an actionURL with window state during a resource request.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"encodeActionURL incorrectly encoded a JSF View url with window state as an actionURL during a resource request.  In encoding: " +
					testString + " the bridge returned: " + resourceEncoded +
					" but the corresponding portletURL encoding returned: " + otherPhaseEncoded);

				return Constants.TEST_FAILED;
			}
		}

		// Otherwise -- no output
		if (s.get("com.liferay.faces.bridge.tck.encodedURL") == null) {
			s.put("com.liferay.faces.bridge.tck.encodedURL", extCtx.encodeActionURL(testString));
		}

		return "";
	}

	// Test is SingleRequest -- Render only
	// Test #6.32
	@BridgeTest(test = "encodeResourceURLBackLinkTest")
	public String encodeResourceURLBackLinkTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String URL_BACKLINK_TEST_STRING = "/resources/myImage.jpg?javax.portlet.faces.BackLink=myBackLinkParam";
		final String URL_BACKLINK_VERIFY_STRING = "/resources/myImage.jpg?myBackLinkParam=";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		ViewHandler vh = ctx.getApplication().getViewHandler();

		// compute what the backLink should be
		String actionURL = extCtx.encodeActionURL(vh.getActionURL(ctx, ctx.getViewRoot().getViewId()));
		String testString = vh.getResourceURL(ctx, URL_BACKLINK_TEST_STRING);
		String verifyString = null;

		try {
			verifyString = vh.getResourceURL(ctx, URL_BACKLINK_VERIFY_STRING) + HTTPUtils.encode(actionURL, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			testRunner.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// According to bridge rules since string passed in isn't xml strict encoded the result won't be as well
		// So ensure compares match by stripping from the one generated by the portlet container (if it exists)
		if (extCtx.encodeResourceURL(testString).equals(
					((PortletResponse) extCtx.getResponse()).encodeURL(verifyString).replace("&amp;", "&"))) {
			testRunner.setTestResult(true, "encodeResourceURL correctly encoded an URL with a backLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL didn't correctly encoded an URL with a backLink.  Expected: " +
				((PortletResponse) extCtx.getResponse()).encodeURL(verifyString) + " but encodeResourceURL returned: " +
				extCtx.encodeResourceURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.27
	@BridgeTest(test = "encodeResourceURLForeignExternalURLBackLinkTest")
	public String encodeResourceURLForeignExternalURLBackLinkTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String FOREIGNEXTERNALURL_BACKLINK_TEST_STRING =
			"http://www.apache.org?javax.portlet.faces.BackLink=myBackLinkParam";
		final String FOREIGNEXTERNALURL_BACKLINK_VERIFY_STRING = "http://www.apache.org?myBackLinkParam=";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// compute what the backLink should be
		String actionURL = extCtx.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx,
					ctx.getViewRoot().getViewId()));
		String verifyString = null;

		try {
			verifyString = FOREIGNEXTERNALURL_BACKLINK_VERIFY_STRING + HTTPUtils.encode(actionURL, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			testRunner.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// According to bridge rules since string passed in isn't xml strict encoded the result won't be as well
		// So ensure compares match by stripping from the one generated by the portlet container (if it exists)
		if (extCtx.encodeResourceURL(FOREIGNEXTERNALURL_BACKLINK_TEST_STRING).equals(
					((PortletResponse) extCtx.getResponse()).encodeURL(verifyString).replace("&amp;", "&"))) {
			testRunner.setTestResult(true,
				"encodeResourceURL correctly encoded a foreign external URL with a backLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL didn't correctly encoded a foreign external URL with a backLink.  Expected: " +
				((PortletResponse) extCtx.getResponse()).encodeURL(verifyString) + " but encodeResourceURL returned: " +
				extCtx.encodeResourceURL(FOREIGNEXTERNALURL_BACKLINK_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.28
	@BridgeTest(test = "encodeResourceURLForeignExternalURLTest")
	public String encodeResourceURLForeignExternalURLTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String FOREIGNEXTERNALURL_TEST_STRING = "http://www.apache.org";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (extCtx.encodeResourceURL(FOREIGNEXTERNALURL_TEST_STRING).equals(
					((PortletResponse) extCtx.getResponse()).encodeURL(FOREIGNEXTERNALURL_TEST_STRING).replace("&amp;",
						"&"))) {
			testRunner.setTestResult(true, "encodeResourceURL correctly encoded a foreign external URL.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL didn't correctly encoded a foreign external URL.  Expected: " +
				((PortletResponse) extCtx.getResponse()).encodeURL(FOREIGNEXTERNALURL_TEST_STRING) +
				" and encodeResourceURL returned: " + extCtx.encodeResourceURL(FOREIGNEXTERNALURL_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.26
	/**
	 * encodeResourceURLTests
	 */
	@BridgeTest(test = "encodeResourceURLOpaqueTest")
	public String encodeResourceURLOpaqueTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String OPAQUE_TEST_STRING = "mailto:jsr-301-comments@jcp.org";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (extCtx.encodeResourceURL(OPAQUE_TEST_STRING).equals(OPAQUE_TEST_STRING)) {
			testRunner.setTestResult(true,
				"encodeResourceURL correctly returned an unchanged string when the input was an opaque URL.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL didn't return an unchanged string when the input was an opaque URL.  Test parameter: " +
				OPAQUE_TEST_STRING + " and encodeResourceURL returned: " +
				extCtx.encodeResourceURL(OPAQUE_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.30
	@BridgeTest(test = "encodeResourceURLRelativeURLBackLinkTest")
	public String encodeResourceURLRelativeURLBackLinkTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String RELATIVEURL_BACKLINK_TEST_STRING =
			"../resources/myImage.jpg?javax.portlet.faces.BackLink=myBackLinkParam";
		final String RELATIVEURL_BACKLINK_VERIFY_STRING = "/resources/myImage.jpg?myBackLinkParam=";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// compute what the backLink should be
		String actionURL = extCtx.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx,
					ctx.getViewRoot().getViewId()));
		String verifyString = null;

		try {
			verifyString = extCtx.getRequestContextPath() + RELATIVEURL_BACKLINK_VERIFY_STRING +
				HTTPUtils.encode(actionURL, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			testRunner.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// According to bridge rules since string passed in isn't xml strict encoded the result won't be as well
		// So ensure compares match by stripping from the one generated by the portlet container (if it exists)
		if (extCtx.encodeResourceURL(RELATIVEURL_BACKLINK_TEST_STRING).equals(
					((PortletResponse) extCtx.getResponse()).encodeURL(verifyString).replace("&amp;", "&"))) {
			testRunner.setTestResult(true, "encodeResourceURL correctly encoded a relative URL with a backLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL didn't correctly encoded a relative URL with a backLink.  Expected: " +
				verifyString + " but encodeResourceURL returned: " +
				extCtx.encodeResourceURL(RELATIVEURL_BACKLINK_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.29
	@BridgeTest(test = "encodeResourceURLRelativeURLTest")
	public String encodeResourceURLRelativeURLTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String RELATIVEURL_TEST_STRING = "../resources/myImage.jpg";
		final String RELATIVEURL_VERIFY_STRING = "/resources/myImage.jpg";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (extCtx.encodeResourceURL(RELATIVEURL_TEST_STRING).equals(
					((PortletResponse) extCtx.getResponse()).encodeURL(
						extCtx.getRequestContextPath() + RELATIVEURL_VERIFY_STRING).replace("&amp;", "&"))) {
			testRunner.setTestResult(true,
				"encodeResourceURL correctly encoded a resource referenced by a relative path.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL incorrectly encoded a resource referenced by a relative path.  Expected: " +
				((PortletResponse) extCtx.getResponse()).encodeURL(
					extCtx.getRequestContextPath() + RELATIVEURL_VERIFY_STRING) + " and encodeResourceURL returned: " +
				extCtx.encodeResourceURL(RELATIVEURL_TEST_STRING));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.31
	@BridgeTest(test = "encodeResourceURLTest")
	public String encodeResourceURLTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String URL_TEST_STRING = "/myportal/resources/myImage.jpg";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		String testURL = extCtx.encodeResourceURL(URL_TEST_STRING);
		String verifyURL = ((PortletResponse) extCtx.getResponse()).encodeURL(URL_TEST_STRING).replace("&amp;", "&");

		if (testURL.equals(verifyURL)) {
			testRunner.setTestResult(true,
				"encodeResourceURL correctly encoded the resource as an external (App) resource.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL incorrectly encoded a resource as if it were a reference to a resource within this application.  Generated: " +
				testURL + " but expected: " + verifyURL);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.33
	@BridgeTest(test = "encodeResourceURLViewLinkTest")
	public String encodeResourceURLViewLinkTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		// assume web.xml does Faces suffix mapping of .jsf to .jsp
		final String URL_VIEWLINK_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.ViewLink=true&param1=testValue";
		final String URL_VIEWLINK_VERIFY_STRING = "/tests/viewLink.jsf?param1=testValue";

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		ViewHandler vh = ctx.getApplication().getViewHandler();

		String testString = vh.getResourceURL(ctx, URL_VIEWLINK_TEST_STRING);
		String verifyString = vh.getResourceURL(ctx, URL_VIEWLINK_VERIFY_STRING);

		if (extCtx.encodeResourceURL(testString).equals(extCtx.encodeActionURL(verifyString))) {
			testRunner.setTestResult(true, "encodeResourceURL correctly encoded a viewLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL incorrectly encoded a viewLink.  Expected: " + extCtx.encodeActionURL(verifyString) +
				" but encodeResourceURL with the viewLink returned: " + extCtx.encodeResourceURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.34
	@BridgeTest(test = "encodeResourceURLViewLinkWithBackLinkTest")
	public String encodeResourceURLViewLinkWithBackLinkTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		final String URL_VIEWLINK_BACKLINK_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.ViewLink=true&param1=testValue&javax.portlet.faces.BackLink=myBackLinkParam";
		final String URL_VIEWLINK_BACKLINK_VERIFY_STRING = "/tests/viewLink.jsf?param1=testValue&myBackLinkParam=";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		ViewHandler vh = ctx.getApplication().getViewHandler();

		// compute what the backLink should be
		String actionURL = extCtx.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx,
					ctx.getViewRoot().getViewId()));
		String testString = vh.getResourceURL(ctx, URL_VIEWLINK_BACKLINK_TEST_STRING);
		String verifyString = null;

		try {
			verifyString = vh.getResourceURL(ctx,
					URL_VIEWLINK_BACKLINK_VERIFY_STRING + HTTPUtils.encode(actionURL, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			testRunner.setTestResult(false, "Failed because couldn't UTF-8 encode backLink parameter.");

			return Constants.TEST_FAILED;
		}

		// Note:  as we are adding a URL as a parameter make sure its properly URLEncoded -- this causes it to be
		// doubly encoded
		if (extCtx.encodeResourceURL(testString).equals(extCtx.encodeActionURL(verifyString))) {
			testRunner.setTestResult(true, "encodeResourceURL correctly encoded a viewLink with a BackLink.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"encodeResourceURL incorrectly encoded a viewLink.  Expected: " + extCtx.encodeActionURL(verifyString) +
				" but encodeResourceURL with the viewLink returned: " + extCtx.encodeResourceURL(testString));

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #6.136
	@BridgeTest(test = "encodeResourceURLWithModeTest")
	public String encodeResourceURLWithModeTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = pReq.getPortletMode();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((mode == null) || !mode.toString().equalsIgnoreCase("view")) {
				testRunner.setTestResult(false,
					"encodeResourceURL incorrectly encoded the portlet mode.  The resulting request should have ignored the mode and remained in 'view' mode.");
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeResourceURL incorrectly encoded the mode and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeResourceURL incorrectly encoded the mode and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);
			}
			else {
				testRunner.setTestResult(true,
					"encodeResourceURL correctly encoded a URL by ignoring the mode and properly encoding the parameter.");
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeResourceURLWithWindowStateTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.RESOURCE_PHASE) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest pReq = (PortletRequest) ctx.getExternalContext().getRequest();

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = pReq.getWindowState();

			// Check that the parameter came along too
			String pVal = ctx.getExternalContext().getRequestParameterMap().get("param1");

			if ((ws == null) || !ws.toString().equalsIgnoreCase("normal")) {
				testRunner.setTestResult(false,
					"encodeResourceURL incorrectly encoded the portlet window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");
			}
			else if (pVal == null) {
				testRunner.setTestResult(false,
					"encodeResourceURL incorrectly encoded the window state and parameter.  The resulting request didn't contain the expected 'param1' parameter.");
			}
			else if (!pVal.equals("testValue")) {
				testRunner.setTestResult(false,
					"encodeResourceURL incorrectly encoded the window state and parameter.  The resulting request contained the wrong parameter value. Expected: testValue  Received: " +
					pVal);
			}
			else {
				testRunner.setTestResult(true,
					"encodeResourceURL correctly encoded a URL by ignoring the window state and properly encoding the parameter.");
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String encodeURLEscapingTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// test encodeActionURL preserves the xml escape encoding in the url it returns
		String nonEscapedURL = extCtx.getRequestContextPath() + "/tests/SingleRequestTest.jsf?parm1=a&param2=b";
		String encodedNonEscapedURL = extCtx.encodeActionURL(nonEscapedURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedURL)) {
			testRunner.setTestResult(false,
				"EncodeActionURL incorrectly returned an url including xml escaping when the input url wasn't escaped.");

			return Constants.TEST_FAILED;
		}

		String escapedURL = extCtx.getRequestContextPath() + "/tests/SingleRequestTest.jsf?parm1=a&amp;param2=b";
		String encodedEscapedURL = extCtx.encodeActionURL(escapedURL);

		if (!isStrictXhtmlEncoded(encodedEscapedURL) && encodedEscapedURL.contains("&")) {
			testRunner.setTestResult(false,
				"EncodeActionURL incorrectly returned an url without xml escaping when the input url was escaped.");

			return Constants.TEST_FAILED;
		}

		nonEscapedURL = extCtx.getRequestContextPath() + "/tests/SingleRequestTest.jsf";
		encodedNonEscapedURL = extCtx.encodeActionURL(nonEscapedURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedURL)) {
			testRunner.setTestResult(false,
				"EncodeActionURL incorrectly returned an url including xml escaping when the input url contained no indication (no query string).");

			return Constants.TEST_FAILED;
		}

		ViewHandler vh = ctx.getApplication().getViewHandler();

		String nonEscapedPath = "/tests/SingleRequestTest.jsf?parm1=a&param2=b";
		String nonEscapedResourceURL = vh.getResourceURL(ctx, nonEscapedPath);
		String encodedNonEscapedResourceURL = extCtx.encodeResourceURL(nonEscapedResourceURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedResourceURL)) {
			testRunner.setTestResult(false,
				"EncodeResourceURL incorrectly returned an url including xml escaping when the input url wasn't escaped.");

			return Constants.TEST_FAILED;
		}

		String escapedPath = "/tests/SingleRequestTest.jsf?parm1=a&amp;param2=b";
		String escapedResourceURL = vh.getResourceURL(ctx, escapedPath);
		String encodedEscapedResourceURL = extCtx.encodeResourceURL(escapedResourceURL);

		if (!isStrictXhtmlEncoded(encodedEscapedResourceURL) && encodedEscapedURL.contains("&")) {
			testRunner.setTestResult(false,
				"EncodeResourceURL incorrectly returned an url without xml escaping when the input url was escaped.");

			return Constants.TEST_FAILED;
		}

		nonEscapedPath = "/tests/SingleRequestTest.jsf";
		nonEscapedResourceURL = vh.getResourceURL(ctx, nonEscapedPath);
		encodedNonEscapedResourceURL = extCtx.encodeResourceURL(nonEscapedResourceURL);

		if (isStrictXhtmlEncoded(encodedNonEscapedResourceURL)) {
			testRunner.setTestResult(false,
				"EncodeResourceURL incorrectly returned an url including xml escaping when the input url contained no indication (no query string).");

			return Constants.TEST_FAILED;
		}

		// Otherwise all was good.

		testRunner.setTestResult(true,
			"encodeActionURL and encodeResourceURL both correctly xml escaped urls it should and didn't xml escape urls it shouldn't");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.57
	@BridgeTest(test = "getRequestCharacterEncodingActionTest")
	public String getRequestCharacterEncodingActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			ActionRequest actionRequest = (ActionRequest) extCtx.getRequest();
			String charEncoding = extCtx.getRequestCharacterEncoding();
			String actionCharEncoding = actionRequest.getCharacterEncoding();

			if (((charEncoding == null) && (actionCharEncoding == null)) || charEncoding.equals(actionCharEncoding)) {
				testRunner.setTestResult(true,
					"extCtx.getRequestCharacterEncoding() correctly returned the same value as actionRequest.getCharacterEncoding()");

			}
			else {
				testRunner.setTestResult(false,
					"extCtx.getRequestCharacterEncoding() incorrectly returned the different value than actionRequest.getCharacterEncoding(). " +
					"Expected: " + actionCharEncoding + " but received: " + charEncoding);
			}

			return "getRequestCharacterEncodingActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getRequestCharacterEncodingEventTest(TestRunnerBean testRunner) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

			testRunner.setTestComplete(true);

			String eventMsg = (String) extCtx.getRequestMap().get("com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testRunner.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testRunner.setTestResult(true,
					"extCtx.getRequestCharacterEncoding() correctly returned null when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, eventMsg);

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
	public String getRequestCharacterEncodingRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		String charEncoding = extCtx.getRequestCharacterEncoding();

		if (charEncoding == null) {
			testRunner.setTestResult(true,
				"extCtx.getRequestCharacterEncoding() correctly returned null when called during the render phase.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"extCtx.getRequestCharacterEncoding() incorrectly returned non-null value when called during the render phase: " +
				charEncoding);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.123
	@BridgeTest(test = "getRequestCharacterEncodingResourceTest")
	public String getRequestCharacterEncodingResourceTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceRequest resourceRequest = (ResourceRequest) extCtx.getRequest();
			String charEncoding = extCtx.getRequestCharacterEncoding();
			String resourceCharEncoding = resourceRequest.getCharacterEncoding();

			if (((charEncoding == null) && (resourceCharEncoding == null)) ||
					charEncoding.equals(resourceCharEncoding)) {
				testRunner.setTestResult(true,
					"extCtx.getRequestCharacterEncoding() correctly returned the same value as resourceRequest.getCharacterEncoding()");

			}
			else {
				testRunner.setTestResult(false,
					"extCtx.getRequestCharacterEncoding() incorrectly returned the different value than resourceRequest.getCharacterEncoding(). " +
					"Expected: " + resourceCharEncoding + " but received: " + charEncoding);
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getRequestContentTypeActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			ActionRequest actionRequest = (ActionRequest) extCtx.getRequest();
			String contentType = extCtx.getRequestContentType();
			String actionContentType = actionRequest.getContentType();

			if (((contentType == null) && (actionContentType == null)) || contentType.equals(actionContentType)) {
				testRunner.setTestResult(true,
					"extCtx.getRequestContentType() correctly returned the same value as actionRequest.getContentType()");

			}
			else {
				testRunner.setTestResult(false,
					"extCtx.getRequestContentType() incorrectly returned the different value than actionRequest.getContentType(). " +
					"Expected: " + actionContentType + " but received: " + contentType);
			}

			return "getRequestContentTypeActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getRequestContentTypeEventTest(TestRunnerBean testRunner) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

			testRunner.setTestComplete(true);

			String eventMsg = (String) extCtx.getRequestMap().get("com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testRunner.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testRunner.setTestResult(true,
					"extCtx.getRequestContentType() correctly returned null when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test #6.58
	@BridgeTest(test = "getRequestContentTypeRenderTest")
	public String getRequestContentTypeRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		String contentType = extCtx.getRequestContentType();

		if (contentType == null) {
			testRunner.setTestResult(true,
				"extCtx.getRequestContentType() correctly returned null when called during the render phase.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"extCtx.getRequestContentType() incorrectly returned non-null value when called during the render phase: " +
				contentType);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.125
	@BridgeTest(test = "getRequestContentTypeResourceTest")
	public String getRequestContentTypeResourceTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceRequest resourceRequest = (ResourceRequest) extCtx.getRequest();
			String contentType = extCtx.getRequestContentType();
			String resourceContentType = resourceRequest.getContentType();

			if (((contentType == null) && (resourceContentType == null)) || contentType.equals(resourceContentType)) {
				testRunner.setTestResult(true,
					"extCtx.getRequestContentType() correctly returned the same value as resourceRequest.getContentType()");

			}
			else {
				testRunner.setTestResult(false,
					"extCtx.getRequestContentType() incorrectly returned the different value than resourceRequest.getContentType(). " +
					"Expected: " + resourceContentType + " but received: " + contentType);
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getRequestHeaderMapActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map headerMap = extCtx.getRequestHeaderMap();
			Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

			// Test for immutability
			try {
				String s = (String) headerMap.put("TestKey", "TestValue");
				testRunner.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

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

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

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
							testRunner.setTestResult(false,
								"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
								rct);

							return "getRequestHeaderMapActionTest";
						}
					}
				}
				else if (key.equalsIgnoreCase("accept-language")) {

					// parse the accept header into its parts
					String[] accepts = trim(e.getValue().split(","));

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

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
						testRunner.setTestResult(false,
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

						if (((ActionRequest) extCtx.getRequest()).getContentLength() == -1) {
							continue;
						}
					}

					testRunner.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return "getRequestHeaderMapActionTest";

				}
			}

			// Otherwise all out tests passed:

			testRunner.setTestResult(true,
				"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

			return "getRequestHeaderMapActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getRequestHeaderMapEventTest(TestRunnerBean testRunner) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

			testRunner.setTestComplete(true);

			String eventMsg = (String) extCtx.getRequestMap().get("com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testRunner.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {
				// All out tests passed:

				testRunner.setTestResult(true,
					"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
				testRunner.appendTestDetail(
					"The getRequestHeaderMap Map correctly didn't contain the content-type property.");
				testRunner.appendTestDetail(
					"The getRequestHeaderMap Map correctly didn't contain the content-length property.");
				testRunner.appendTestDetail(
					"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
				testRunner.appendTestDetail(
					"The getRequestHeaderMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
				testRunner.appendTestDetail(
					"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, eventMsg);

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
	public String getRequestHeaderMapRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains properties from the portlet request (that it should)
		// 2. Doesn't contain the Content-Type property
		// 3. Does include the Accept and Accept-Language

		Map headerMap = extCtx.getRequestHeaderMap();
		Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

		// Test for immutability
		try {
			String s = (String) headerMap.put("TestKey", "TestValue");
			testRunner.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

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
				testRunner.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderMap contains a content-type header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("content-length")) {
				testRunner.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderMap contains a content-length header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("accept")) {
				boolean found = false;

				// parse the accept header into its parts
				String[] accepts = trim(e.getValue().split(","));

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

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
						testRunner.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
							rct);

						return Constants.TEST_FAILED;
					}
				}
			}
			else if (key.equalsIgnoreCase("accept-language")) {

				// parse the accept header into its parts
				String[] accepts = trim(e.getValue().split(","));

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

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
					testRunner.setTestResult(false,
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
					testRunner.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;
				}
			}
		}

		// Otherwise all out tests passed:

		testRunner.setTestResult(true,
			"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
		testRunner.appendTestDetail("The getRequestHeaderMap Map correctly didn't contain the content-type property.");
		testRunner.appendTestDetail(
			"The getRequestHeaderMap Map correctly didn't contain the content-length property.");
		testRunner.appendTestDetail(
			"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
		testRunner.appendTestDetail(
			"The getRequestHeaderMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
		testRunner.appendTestDetail(
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
	public String getRequestHeaderMapResourceTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map headerMap = extCtx.getRequestHeaderMap();
			Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

			testRunner.setTestComplete(true);

			// Test for immutability
			try {
				String s = (String) headerMap.put("TestKey", "TestValue");
				testRunner.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

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

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

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
							testRunner.setTestResult(false,
								"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
								rct);

							return Constants.TEST_FAILED;
						}
					}
				}
				else if (key.equalsIgnoreCase("accept-language")) {

					// parse the accept header into its parts
					String[] accepts = trim(e.getValue().split(","));

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

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
						testRunner.setTestResult(false,
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

						if (((ResourceRequest) extCtx.getRequest()).getContentLength() == -1) {
							continue;
						}
					}

					testRunner.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;

				}
			}

			// Otherwise all out tests passed:

			testRunner.setTestResult(true,
				"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testRunner.appendTestDetail(
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
	public String getRequestHeaderValuesMapActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map<String, String[]> headerMap = extCtx.getRequestHeaderValuesMap();
			Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

			// Test for immutability
			try {
				String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });
				headerMap.put("TestKey", s);
				testRunner.setTestResult(false, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

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

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

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
							testRunner.setTestResult(false,
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

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

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
						testRunner.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

						return "getRequestHeaderValuesMapActionTest";
					}
				}
			}

			// Now enumerate the requests property Enumeration and make sure all are in this Map (except Content-Type)
			while (propertyNames.hasMoreElements()) {
				String prop = (String) propertyNames.nextElement();

				if (!headerMap.containsKey(prop)) {
					testRunner.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return "getRequestHeaderValuesMapActionTest";

				}
			}

			// Otherwise all out tests passed:

			testRunner.setTestResult(true,
				"The immutable Map returned from getRequestHeaderMap correctly threw an exception when written to.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testRunner.appendTestDetail(
				"The getRequestHeaderMap Map correctly contained all other properties returned by request.getProperties.");

			return "getRequestHeaderValuesMapActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getRequestHeaderValuesMapEventTest(TestRunnerBean testRunner) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

			testRunner.setTestComplete(true);

			String eventMsg = (String) extCtx.getRequestMap().get("com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testRunner.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				// All out tests passed:
				testRunner.setTestResult(true,
					"The immutable Map returned from getRequestHeaderValuesMap correctly threw an exception when written to.");
				testRunner.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly didn't contain the content-type property.");
				testRunner.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly didn't contain the content-length property.");
				testRunner.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
				testRunner.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
				testRunner.appendTestDetail(
					"The getRequestHeaderValuesMap Map correctly contained all other properties returned by request.getProperties.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, eventMsg);

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
	public String getRequestHeaderValuesMapRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains properties from the portlet request (that it should)
		// 2. Doesn't contain the Content-Type property
		// 3. Does include the Accept and Accept-Language

		Map<String, String[]> headerMap = extCtx.getRequestHeaderValuesMap();
		Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

		// Test for immutability
		try {
			String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });
			testRunner.setTestResult(false, "Failed: The Map returned from getRequestHeaderValuesMap isn't immutable.");

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
				testRunner.setTestResult(false,
					"Failed: The Map returned from getRequestHeaderValuesMap contains a content-type header but shouldn't.");

				return Constants.TEST_FAILED;
			}
			else if (key.equalsIgnoreCase("content-length")) {
				testRunner.setTestResult(false,
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

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

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
						testRunner.setTestResult(false,
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

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

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
					testRunner.setTestResult(false,
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
					testRunner.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderValuesMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;
				}
			}
		}

		// Otherwise all out tests passed:

		testRunner.setTestResult(true,
			"The immutable Map returned from getRequestHeaderValuesMap correctly threw an exception when written to.");
		testRunner.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly didn't contain the content-type property.");
		testRunner.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly didn't contain the content-length property.");
		testRunner.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
		testRunner.appendTestDetail(
			"The getRequestHeaderValuesMap Map correctly contained an Accept-Language property with a value equal to the concatenation of request.getLocales segmented by a comma.");
		testRunner.appendTestDetail(
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
	public String getRequestHeaderValuesMapResourceTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {
			// Test the following: 1. Map is immutable 2. Map contains properties from the portlet request (that it
			// should) 2. Doesn't contain the Content-Type property 3. Does include the Accept and Accept-Language

			Map<String, String[]> headerMap = extCtx.getRequestHeaderValuesMap();
			Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

			testRunner.setTestComplete(true);

			// Test for immutability
			try {
				String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });
				headerMap.put("TestKey", s);
				testRunner.setTestResult(false,
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

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

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
							testRunner.setTestResult(false,
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

					Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

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
						testRunner.setTestResult(false,
							"Failed: The Map returned from getRequestHeaderValuesMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

						return Constants.TEST_FAILED;
					}
				}
			}

			// Now enumerate the requests property Enumeration and make sure all are in this Map (except Content-Type)
			while (propertyNames.hasMoreElements()) {
				String prop = (String) propertyNames.nextElement();

				if (!headerMap.containsKey(prop)) {
					testRunner.setTestResult(false,
						"Failed: The Map returned from getRequestHeaderValuesMap didn't contain all the key/values from request.getProperties. Its missing: " +
						prop);

					return Constants.TEST_FAILED;

				}
			}

			// Otherwise all out tests passed:

			testRunner.setTestResult(true,
				"The immutable Map returned from getRequestHeaderValuesMap correctly threw an exception when written to.");
			testRunner.appendTestDetail(
				"The getRequestHeaderValuesMap Map correctly contained an Accept property with a value containing entries from the concatenation of request.getResponseContentTypes segmented by a comma.");
			testRunner.appendTestDetail(
				"The getRequestHeaderValuesMap Map correctly contained an Accept-Language key with a value equal to the concatenation of request.getLocales segmented by a comma.");
			testRunner.appendTestDetail(
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
	public String getRequestParameterDefaultViewParamsTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String> paramMap = extCtx.getRequestParameterMap();

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
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestParameterMap() didn't properly expose the 'field1' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else if (!foundField2) {
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestParameterMap() didn't properly expose the 'field2' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else {
			testRunner.setTestResult(true,
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
	public String getRequestParameterMapCoreTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map (during action) contains the parameters in the underlying
			// request 2. Map (during render) doesn't contain any params other than the VIEW_STATE_PARAM

			Map<String, String> paramMap = extCtx.getRequestParameterMap();
			Map<String, String[]> requestParamMap = ((PortletRequest) extCtx.getRequest()).getParameterMap();

			// Test for immutability
			try {
				paramMap.put("TestKey", "TestValue");
				testRunner.setTestResult(false,
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
					testRunner.setTestResult(false,
						"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is missing a key returned in request.getParameterMap: " +
						key);

					return "getRequestParameterMapCoreTest";
				}
				else if (!value.equals(requestVal)) {
					testRunner.setTestResult(false,
						"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap contains a different value for an entry vs. request.getParameterMap.  key: " +
						key + " ExtCtx.value: " + value + " RequestValue: " + requestVal);

					return "getRequestParameterMapCoreTest";
				}
			}

			// Otherwise all out tests passed:
			// Now make sure the two form fields are there:
			if (!foundField1) {
				testRunner.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is either missing the form's field1 parameter or its value wasn't 'value1'.");

				return "getRequestParameterMapCoreTest";
			}
			else if (!foundField2) {
				testRunner.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is either missing the form's field2 parameter or its value wasn't 'value2'.");

				return "getRequestParameterMapCoreTest";
			}

			testRunner.setTestResult(true,
				"Passed Action Phase: The immutable Map returned from getRequestParameterMap correctly threw an exception when written to.");
			testRunner.appendTestDetail(
				"Passed Action Phase: The getRequestParameterMap Map correctly contained all parameters in the underlying request.getParameterMap.");

			return "getRequestParameterMapCoreTest";
		}
		else {
			testRunner.setTestComplete(true);

			// Now verify we only have the VIEW_STATE_PARAM
			Map<String, String> paramMap = extCtx.getRequestParameterMap();

			if (paramMap.get(ResponseStateManager.VIEW_STATE_PARAM) == null) {
				testRunner.setTestResult(false,
					"Render Phase extCtx.getRequestParameterMap() doesn't contain the ResponseStateManager.VIEW_STATE parameter. Test Result from the prior action phase was: " +
					testRunner.getTestResult());

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
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterMap() incorrectly contains the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (foundField2) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterMap() incorrectly contains the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.appendTestDetail(
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
	public String getRequestParameterNamesCoreTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 2. Map (during action) contains the parameters in the underlying request 2. Map
			// (during render) doesn't contain any params other than the VIEW_STATE_PARAM

			Iterator<String> namesIterator = extCtx.getRequestParameterNames();

			boolean foundField1 = false;
			boolean foundField2 = false;

			while (namesIterator.hasNext()) {
				String name = namesIterator.next();

				// Can't exact match by key because JSF munges this id
				if (name.indexOf("formDataField1") > -1)
					foundField1 = true;

				if (name.indexOf("formDataField2") > -1)
					foundField2 = true;

				Enumeration<String> requestNamesEnum = ((PortletRequest) extCtx.getRequest()).getParameterNames();
				boolean foundName = false;

				// verify its in the underlying request Enumeration
				while (requestNamesEnum.hasMoreElements()) {

					if (name.contains(requestNamesEnum.nextElement())) {
						foundName = true;

						break;
					}
				}

				if (!foundName) {
					testRunner.setTestResult(false,
						"Failed Action Phase: The Iterator returned from ExternalContext.getRequestParameterNames is missing a name returned in request.getParameterNames: " +
						name);

					return "getRequestParameterNamesCoreTest";
				}
			}

			// Otherwise all out tests passed:
			// Now make sure the two form fields are there:
			if (!foundField1) {
				testRunner.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is missing the form's field1 parameter.");

				return "getRequestParameterNamesCoreTest";
			}
			else if (!foundField2) {
				testRunner.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterMap is missing the form's field2.");

				return "getRequestParameterNamesCoreTest";
			}

			testRunner.setTestResult(true,
				"Passed Action Phase: The getRequestParameterNames iterator correctly contained all parameters in the underlying request.getParameterNames.");

			return "getRequestParameterNamesCoreTest";
		}
		else {
			testRunner.setTestComplete(true);

			// Now verify we only have the VIEW_STATE_PARAM
			Iterator<String> namesIterator = extCtx.getRequestParameterNames();
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
				testRunner.setTestResult(false,
					"Render phase extCtx.getRequestParameterNames() doesn't contain the ResponseStateManager.VIEW_STATE parameter. Test Result from the prior action Phase was: " +
					testRunner.getTestResult());

				return Constants.TEST_FAILED;
			}
			else if (foundField1) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterName() incorrectly contains the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (foundField2) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterName() incorrectly contains the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.appendTestDetail(
					"Passed RenderPhase: The getRequestParameterName() correctly excluded the submitted form fields from the render phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.50
	@BridgeTest(test = "getRequestParameterNamesDefaultViewParamsTest")
	public String getRequestParameterNamesDefaultViewParamsTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Iterator<String> namesIterator = extCtx.getRequestParameterNames();

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
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestParameterNames() didn't properly expose the 'field1' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else if (!foundField2) {
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestParameterNames() didn't properly expose the 'field2' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else {
			testRunner.setTestResult(true,
				"Passed RenderPhase: The getRequestParameterNames Iterator correctly exposed the query string fields in defaultViewId.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Test whether actionParameters are preserved into
	// the Render if the config value is set.
	// Test #6.49
	@BridgeTest(test = "getRequestParameterNamesPreserveParamsTest")
	public String getRequestParameterNamesPreserveParamsTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Iterator<String> namesIterator = extCtx.getRequestParameterNames();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			return "getRequestParameterNamesPreserveParamsTest";
		}
		else {
			testRunner.setTestComplete(true);

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
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterNames() didn't properly preserve the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterNames() didn't properly preserve the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.setTestResult(true,
					"Passed RenderPhase: The getRequestParameterNames Iterator correctly preserved the submitted form fields into the render phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- Test whether actionParameters are preserved into
	// the Render if the config value is set.
	// Test #6.46
	@BridgeTest(test = "getRequestParameterPreserveParamsTest")
	public String getRequestParameterPreserveParamsTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String> paramMap = extCtx.getRequestParameterMap();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			return "getRequestParameterPreserveParamsTest";
		}
		else {
			testRunner.setTestComplete(true);

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
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterMap() didn't properly preserve the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterMap() didn't properly preserve the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.setTestResult(true,
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
	public String getRequestParameterValuesMapCoreTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			// Test the following: 1. Map is immutable 2. Map (during action) contains the parameters in the underlying
			// request 2. Map (during render) doesn't contain any params other than the VIEW_STATE_PARAM

			Map<String, String[]> paramMap = extCtx.getRequestParameterValuesMap();
			Map<String, String[]> requestParamMap = ((PortletRequest) extCtx.getRequest()).getParameterMap();

			// Test for immutability
			try {
				paramMap.put("TestKey", new String[] { "TestValue" });
				testRunner.setTestResult(false,
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
					testRunner.setTestResult(false,
						"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap is missing a key returned in request.getParameterMap: " +
						key);

					return "getRequestParameterValuesMapCoreTest";
				}

				for (int i = 0; i < values.length; i++) {

					if (!values[i].equals(requestVals[i])) {
						testRunner.setTestResult(false,
							"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap contains a different value for an entry vs. request.getParameterMap.  key: " +
							key + " ExtCtx.value: " + values[i] + " RequestValue: " + requestVals[i]);

						return "getRequestParameterValuesMapCoreTest";
					}
				}
			}

			// Otherwise all out tests passed:
			// Now make sure the two form fields are there:
			if (!foundField1) {
				testRunner.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap is either missing the form's field1 parameter or its value wasn't 'value1'.");

				return "getRequestParameterValuesMapCoreTest";
			}
			else if (!foundField2) {
				testRunner.setTestResult(false,
					"Failed Action Phase: The Map returned from ExternalContext.getRequestParameterValuesMap is either missing the form's field2 parameter or its value wasn't 'value2'.");

				return "getRequestParameterValuesMapCoreTest";
			}

			testRunner.setTestResult(true,
				"Passed Action Phase: The immutable Map returned from getRequestParameterValuesMap correctly threw an exception when written to.");
			testRunner.appendTestDetail(
				"Passed Action Phase: The getRequestParameterValuesMap Map correctly contained all parameters in the underlying request.getParameterMap.");

			return "getRequestParameterValuesMapCoreTest";
		}
		else {
			testRunner.setTestComplete(true);

			// Now verify we only have the VIEW_STATE_PARAM
			Map<String, String[]> paramMap = extCtx.getRequestParameterValuesMap();

			if (paramMap.get(ResponseStateManager.VIEW_STATE_PARAM) == null) {
				testRunner.setTestResult(false,
					"Render Phase extCtx.getRequestParameterValuesMap() doesn't contain the ResponseStateManager.VIEW_STATE parameter. Test Result from the prior action phase was: " +
					testRunner.getTestResult());

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
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterValuesMap() incorrectly contains the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (foundField2) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterValuesMap() incorrectly contains the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.appendTestDetail(
					"Passed RenderPhase: The getRequestParameterValuesMap Map correctly excluded the submitted form fields from the render Phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.53
	@BridgeTest(test = "getRequestParameterValuesMapDefaultViewParamsTest")
	public String getRequestParameterValuesMapDefaultViewParamsTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String[]> paramMap = extCtx.getRequestParameterValuesMap();

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
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestParameterValuesMap() didn't properly expose the 'field1' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else if (!foundField2) {
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestParameterValuesMap() didn't properly expose the 'field2' query string parameter from the defaultViewId.");

			return Constants.TEST_FAILED;
		}
		else {
			testRunner.setTestResult(true,
				"Passed RenderPhase: The getRequestParameterValuesMap Map correctly exposed the query string fields in defaultViewId.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Test whether actionParameters are preserved into
	// the Render if the config value is set.
	// Test #6.52
	@BridgeTest(test = "getRequestParameterValuesMapPreserveParamsTest")
	public String getRequestParameterValuesMapPreserveParamsTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Now verify we have the form params that were passed to the action
		Map<String, String[]> paramMap = extCtx.getRequestParameterValuesMap();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			return "getRequestParameterValuesMapPreserveParamsTest";
		}
		else {
			testRunner.setTestComplete(true);

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
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterValuesMap() didn't properly preserve the value for the 'field1' form parameter.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testRunner.setTestResult(false,
					"Failed Render Phase: extCtx.getRequestParameterValuesMap() didn't properly preserve the value for the 'field2' form parameter.");

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.setTestResult(true,
					"Passed RenderPhase: The getRequestParameterValuesMap Map correctly preserved the submitted form fields into the render Phase.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.54
	@BridgeTest(test = "getRequestPathInfoTest")
	public String getRequestPathInfoTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		String pathInfo = extCtx.getRequestPathInfo();

		// As this web.xml maps the faces servlet using extension mapping
		// the value should be null
		if (pathInfo == null) {
			testRunner.setTestResult(true,
				"Passed RenderPhase: extCtx.getRequestPathInfo() correctly returned a null value as the Faces servlet is extension mapped.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestPathInfo() returned a non-null value though null was expected as the Faces servlet is extension mapped.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- tests whether parameters encoded in the defaultViewId's
	// queryString are exposed as request parameters.
	// Test #6.55
	@BridgeTest(test = "getRequestServletPathTest")
	public String getRequestServletPathTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		String servletPath = extCtx.getRequestServletPath();

		// As this web.xml maps the faces servlet using extension mapping
		// the value should be viewId.mappingext i.e. viewid.jsf
		String viewId = ctx.getViewRoot().getViewId();
		int dot = viewId.indexOf('.');
		viewId = viewId.substring(0, (dot >= 0) ? dot : viewId.length()) + ".jsf";

		if (servletPath.equalsIgnoreCase(viewId)) {
			testRunner.setTestResult(true,
				"Passed RenderPhase: extCtx.getRequestServletPath() correctly returned the unmapped (extension mapped) viewId: " +
				servletPath);

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"Failed Render Phase: extCtx.getRequestServletPath() returned something other than the the unmapped (extension mapped) viewId.  Expected: " +
				viewId + " getRequestServletPath returned: " + servletPath);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.61
	@BridgeTest(test = "getResponseCharacterEncodingActionTest")
	public String getResponseCharacterEncodingActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			try {
				String encoding = extCtx.getResponseCharacterEncoding();
				testRunner.setTestResult(false,
					"extCtx.getResponseCharacterEncoding() didn't throw an IllegalStateException when called during an Action.");
			}
			catch (IllegalStateException e) {
				testRunner.setTestResult(true,
					"extCtx.getResponseCharacterEncoding() correctly threw an IllegalStateException when called during an Action.");
			}

			return "getResponseCharacterEncodingActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getResponseCharacterEncodingEventTest(TestRunnerBean testRunner) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

			testRunner.setTestComplete(true);

			String eventMsg = (String) extCtx.getRequestMap().get("com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testRunner.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testRunner.setTestResult(true,
					"extCtx.getResponseCharacterEncoding() correctly threw an IllegalStateException when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, eventMsg);

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
	public String getResponseCharacterEncodingRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		MimeResponse response = (MimeResponse) extCtx.getResponse();

		String charEncoding = extCtx.getResponseCharacterEncoding();
		String renderCharEncoding = response.getCharacterEncoding();

		if (((charEncoding == null) && (renderCharEncoding == null)) || charEncoding.equals(renderCharEncoding)) {
			testRunner.setTestResult(true,
				"extCtx.getResponseCharacterEncoding() correctly returned the same value as renderResponse.getCharacterEncoding()");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"extCtx.getResponseCharacterEncoding() incorrectly returned a different value than renderResponse.getCharacterEncoding(). " +
				"Expected: " + renderCharEncoding + " but received: " + charEncoding);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.127
	@BridgeTest(test = "getResponseCharacterEncodingResourceTest")
	public String getResponseCharacterEncodingResourceTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceResponse resourceResponse = (ResourceResponse) extCtx.getResponse();

			String charEncoding = extCtx.getResponseCharacterEncoding();
			String resourceCharEncoding = resourceResponse.getCharacterEncoding();

			if (((charEncoding == null) && (resourceCharEncoding == null)) ||
					charEncoding.equals(resourceCharEncoding)) {
				testRunner.setTestResult(true,
					"extCtx.getResponseCharacterEncoding() correctly returned the same value as resourceResponse.getCharacterEncoding()");
			}
			else {
				testRunner.setTestResult(false,
					"extCtx.getResponseCharacterEncoding() incorrectly returned a different value than resourceResponse.getCharacterEncoding(). " +
					"Expected: " + resourceCharEncoding + " but received: " + charEncoding);
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getResponseContentTypeActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			try {
				String contentType = extCtx.getResponseContentType();
				testRunner.setTestResult(false,
					"extCtx.getResponseContentType() didn't throw an IllegalStateException when called during an Action.");
			}
			catch (IllegalStateException e) {
				testRunner.setTestResult(true,
					"extCtx.getResponseContentType() correctly threw an IllegalStateException when called during an Action.");
			}

			return "getResponseContentTypeActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getResponseContentTypeEventTest(TestRunnerBean testRunner) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

			testRunner.setTestComplete(true);

			String eventMsg = (String) extCtx.getRequestMap().get("com.liferay.faces.bridge.tck.eventTestResult");

			if (eventMsg == null) {
				testRunner.setTestResult(false,
					"Unexpected error:  the test's event handler wasn't called and hence the test didn't run.");

				return Constants.TEST_FAILED;
					// All out tests passed:
			}
			else if (eventMsg.equals(Constants.TEST_SUCCESS)) {

				testRunner.setTestResult(true,
					"extCtx.getResponseContentType() correctly returned null when called during the event phase.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, eventMsg);

				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}

	// Test #6.62
	@BridgeTest(test = "getResponseContentTypeRenderTest")
	public String getResponseContentTypeRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		MimeResponse response = (MimeResponse) extCtx.getResponse();
		String contentType = extCtx.getResponseContentType();
		String renderContentType = response.getContentType();

		if (((contentType == null) && (renderContentType == null)) || contentType.equals(renderContentType)) {
			testRunner.setTestResult(true,
				"extCtx.getResponseContentType() correctly returned the same value as renderResponse.getContentType().");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"extCtx.getResponseContentType() incorrectly returned a different value than renderResponse.getContentType(): expected: " +
				renderContentType + " but received: " + contentType);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.129
	@BridgeTest(test = "getResponseContentTypeResourceTest")
	public String getResponseContentTypeResourceTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RESOURCE_PHASE) {
			ResourceResponse resourceResponse = (ResourceResponse) extCtx.getResponse();
			String contentType = extCtx.getResponseContentType();
			String resourceContentType = resourceResponse.getContentType();

			if (((contentType == null) && (resourceContentType == null)) || contentType.equals(resourceContentType)) {
				testRunner.setTestResult(true,
					"extCtx.getResponseContentType() correctly returned the same value as resourceRequest.getContentType()");

			}
			else {
				testRunner.setTestResult(false,
					"extCtx.getResponseContentType() incorrectly returned the different value than resourceRequest.getContentType(). " +
					"Expected: " + resourceContentType + " but received: " + contentType);
			}

			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String getSetRequestObjectTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			ActionRequest aRequest = new ActionRequestWrapper((ActionRequest) extCtx.getRequest());

			extCtx.setRequest(aRequest);

			if (extCtx.getRequest() == aRequest) {
				testRunner.setTestResult(true,
					"Successfully set/retrieved a new ActionRequest using ExternalContext.set/getRequest.");

				return "getSetRequestObjectTest"; // action Navigation result
			}
			else {
				testRunner.setTestResult(false,
					"ExternalContext.set/getRequest a new ActionRequest failed as the retrieved object isn't the same as the one set.");

				return "getSetRequestObjectTest";
			}
		}
		else {
			testRunner.setTestComplete(true);

			// Now do the same thing for render
			RenderRequest rRequest = new RenderRequestWrapper((RenderRequest) extCtx.getRequest());

			extCtx.setRequest(rRequest);

			if (extCtx.getRequest() == rRequest) {
				testRunner.appendTestDetail(
					"Successfully set/retrieved a new RenderRequest using ExternalContext.set/getRequest.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"ExternalContext.set/getRequest a new RenderRequest failed as the retrieved object isn't the same as the one set.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #6.66
	@BridgeTest(test = "illegalRedirectRenderTest")
	public String illegalRedirectRenderTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		testRunner.setTestComplete(true);

		String viewId = ctx.getViewRoot().getViewId();

		if (viewId.equals("/tests/singleRequestTest.xhtml")) {

			try {
				extCtx.redirect(ctx.getApplication().getViewHandler().getResourceURL(ctx, "/tests/NonJSFView.portlet"));
			}
			catch (IllegalStateException i) {
				testRunner.setTestResult(true,
					"extCtx.redirect() during render correctly threw the IllegalStateException when redirecting to a nonFaces view.");

				return Constants.TEST_SUCCESS;
			}
			catch (Exception e) {
				testRunner.setTestResult(false,
					"Calling extCtx.redirect() threw an unexpected exception: " + e.getMessage());

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(false,
				"extCtx.redirect() during render failed: it didn't throw an illegalStateException when redirecting to a nonfaces view.");

			return Constants.TEST_FAILED;
		}
		else {
			testRunner.setTestResult(false,
				"extCtx.redirect() during render failed: we started out in an unexpected view: " + viewId);

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.64
	@BridgeTest(test = "redirectActionTest")
	public String redirectActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    redirect to a new view   set attribute on request scope   call redirect to the renderview   when
			 * called during render -- check we are at the new view and attribute is gone   i.e. request scope isn't
			 * preserved in case of redirect.
			 */
			extCtx.getRequestMap().put("myRedirectRequestObject", Boolean.TRUE);

			String target = ctx.getApplication().getViewHandler().getActionURL(ctx,
					"/tests/redirectTestResultRenderCheck.xhtml");

			try {
				extCtx.redirect(target);
			}
			catch (Exception e) {
				testRunner.setTestResult(false, "Calling extCtx.redirect() threw an exception: " + e.getMessage());

				return "redirectActionTest";
			}

			return
				""; // return something that won't be mapped in faces-config.xml to confirm  redirect is what is causing
					// the page transition
		}
		else {
			testRunner.setTestComplete(true);

			String viewId = ctx.getViewRoot().getViewId();

			if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				// the redirect worked

				// now verify that the scope wasn't saved.
				if (extCtx.getRequestMap().get("myRedirectRequestObject") == null) {
					testRunner.setTestResult(true,
						"extCtx.redirect() during action worked correctly as we were redirected to the new view without retaining request scoped beans.");
				}
				else {
					testRunner.setTestResult(false,
						"extCtx.redirect() during action failed as though we were redirected to the new view the request scope was retained.");
				}
			}
			else {
				testRunner.setTestResult(false,
					"extCtx.redirect() during action failed as we weren't redirected to the new view. The viewId should be /tests/redirectTestResultRenderCheck.xhtml but is: " +
					viewId);
			}

			if (testRunner.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
	}

	// Test #6.131
	@BridgeTest(test = "redirectEventTest")
	public String redirectEventTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    set attribute on request scope   raise event which will invoke redirect   when called during render --
			 * check we are at the new view and attribute is gone   i.e. request scope isn't preserved in case of
			 * redirect.
			 */
			extCtx.getRequestMap().put("myRedirectRequestObject", Boolean.TRUE);

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(EVENT_QNAME, EVENT_NAME), testRunner.getTestName());

			return Constants.TEST_SUCCESS; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			String viewId = ctx.getViewRoot().getViewId();

			if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				// the redirect worked

				// now verify that the scope wasn't saved.
				if (extCtx.getRequestMap().get("myRedirectRequestObject") == null) {
					testRunner.setTestResult(true,
						"extCtx.redirect() during event worked correctly as we were redirected to the new view without retaining request scoped beans.");
				}
				else {
					testRunner.setTestResult(false,
						"extCtx.redirect() during event failed as though we were redirected to the new view the request scope was retained.");
				}
			}
			else {
				testRunner.setTestResult(false,
					"extCtx.redirect() during event failed as we weren't redirected to the new view. The viewId should be /tests/redirectTestResultRenderCheck.xhtml but is: " +
					viewId);
			}

			if (testRunner.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;

			}
		}
	}

	// Test #6.65
	@BridgeTest(test = "redirectRenderPRP1Test")
	public String redirectRenderPRP1Test(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    set a new value for the PRP by updating the model -- this will cause the PRP to come in the render in
			 * the subsequent render:  set an attribute indicating we are redirecting; redirect; check to see if the PRP
			 * is in the request.
			 *
			 * Key difference between PRP1Test and PRP2Test is that PRP1 already calls encodeActionURL on url prior to
			 * redirect while   PRP2 does not.
			 */
			extCtx.getRequestMap().put("modelPRP", testRunner.getTestName());

			return "redirectRenderPRP1Test";
		}
		else {
			String viewId = ctx.getViewRoot().getViewId();

			if (viewId.equals("/tests/multiRequestTestResultRenderCheck.xhtml")) {
				String target = ctx.getApplication().getViewHandler().getActionURL(ctx,
						"/tests/redirectTestResultRenderCheck.xhtml");

				try {
					extCtx.redirect(extCtx.encodeActionURL(target));
				}
				catch (Exception e) {
					testRunner.setTestComplete(true);
					testRunner.setTestResult(false, "Calling extCtx.redirect() threw an exception: " + e.getMessage());

					return Constants.TEST_FAILED;
				}

				return "";
			}
			else if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				testRunner.setTestComplete(true);

				// ensure that both the public render paramter and the model are there and have the same value
				RenderRequest request = (RenderRequest) extCtx.getRequest();
				String[] prpArray = request.getPublicParameterMap().get("testPRP");
				String modelPRP = (String) extCtx.getRequestMap().get("modelPRP");

				if (prpArray == null) {
					testRunner.setTestResult(false,
						"redirected request didn't carry forward the public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (modelPRP == null) {
					testRunner.setTestResult(false,
						"redirected request didn't update the model from the passed public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(prpArray[0])) {
					testRunner.setTestResult(false,
						"redirected request:  passed public render parameter value doesn't match underlying one.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(testRunner.getTestName())) {
					testRunner.setTestResult(false,
						"redirected request:  public render parameter didn't contain expected value.  PRP value: " +
						modelPRP + " but expected: " + testRunner.getTestName());

					return Constants.TEST_FAILED;
				}
				else {
					testRunner.setTestResult(true,
						"extCtx.redirect() during render worked correctly as we were redirected to the new view.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {
				testRunner.setTestResult(false,
					"extCtx.redirect() during render failed as we ended up in an unexpected view: " + viewId);

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #6.65
	@BridgeTest(test = "redirectRenderPRP2Test")
	public String redirectRenderPRP2Test(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			/* Test works as follows:
			 *    set a new value for the PRP by updating the model -- this will cause the PRP to come in the render in
			 * the subsequent render:  set an attribute indicating we are redirecting; redirect; check to see if the PRP
			 * is in the request.
			 *
			 * Key difference between PRP1Test and PRP2Test is that PRP1 already calls encodeActionURL on url prior to
			 * redirect while   PRP2 does not.
			 */
			extCtx.getRequestMap().put("modelPRP", testRunner.getTestName());

			return "redirectRenderPRP2Test";
		}
		else {
			String viewId = ctx.getViewRoot().getViewId();

			if (viewId.equals("/tests/multiRequestTestResultRenderCheck.xhtml")) {
				String target = ctx.getApplication().getViewHandler().getActionURL(ctx,
						"/tests/redirectTestResultRenderCheck.xhtml");

				try {
					extCtx.redirect(target);
				}
				catch (Exception e) {
					testRunner.setTestComplete(true);
					testRunner.setTestResult(false, "Calling extCtx.redirect() threw an exception: " + e.getMessage());

					return Constants.TEST_FAILED;
				}

				return "";
			}
			else if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
				testRunner.setTestComplete(true);

				// ensure that both the public render paramter and the model are there and have the same value
				RenderRequest request = (RenderRequest) extCtx.getRequest();
				String[] prpArray = request.getPublicParameterMap().get("testPRP");
				String modelPRP = (String) extCtx.getRequestMap().get("modelPRP");

				if (prpArray == null) {
					testRunner.setTestResult(false,
						"redirected request didn't carry forward the public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (modelPRP == null) {
					testRunner.setTestResult(false,
						"redirected request didn't update the model from the passed public render parameter.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(prpArray[0])) {
					testRunner.setTestResult(false,
						"redirected request:  passed public render parameter value doesn't match underlying one.");

					return Constants.TEST_FAILED;
				}
				else if (!modelPRP.equals(testRunner.getTestName())) {
					testRunner.setTestResult(false,
						"redirected request:  public render parameter didn't contain expected value.  PRP value: " +
						modelPRP + " but expected: " + testRunner.getTestName());

					return Constants.TEST_FAILED;
				}
				else {
					testRunner.setTestResult(true,
						"extCtx.redirect() during render worked correctly as we were redirected to the new view.");

					return Constants.TEST_SUCCESS;
				}
			}
			else {
				testRunner.setTestResult(false,
					"extCtx.redirect() during render failed as we ended up in an unexpected view: " + viewId);

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #6.65
	@BridgeTest(test = "redirectRenderTest")
	public String redirectRenderTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		String viewId = ctx.getViewRoot().getViewId();

		if (viewId.equals("/tests/singleRequestTest.xhtml")) {
			String target = ctx.getApplication().getViewHandler().getActionURL(ctx,
					"/tests/redirectTestResultRenderCheck.xhtml");

			try {
				extCtx.redirect(target);
			}
			catch (Exception e) {
				testRunner.setTestComplete(true);
				testRunner.setTestResult(false, "Calling extCtx.redirect() threw an exception: " + e.getMessage());

				return Constants.TEST_FAILED;
			}

			return "";
		}
		else if (viewId.equals("/tests/redirectTestResultRenderCheck.xhtml")) {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(true,
				"extCtx.redirect() during render worked correctly as we were redirected to the new view.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"extCtx.redirect() during render failed as we ended up in an unexpected view: " + viewId);

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render only
	// Test #6.42
	/**
	 * getRequestMap Tests
	 */
	@BridgeTest(test = "requestMapCoreTest")
	public String requestMapCoreTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Test the following:
		// 1. Map is mutable
		// 2. Map contains attributes in the underlying request
		// a) set on portlet request -- get via map
		// b) set on map -- get via portlet request
		// 3. Remove in request -- gone from Map
		// 4. Remove from Map -- gone in request

		PortletRequest request = (PortletRequest) extCtx.getRequest();
		Map<String, Object> extCtxRequestMap = extCtx.getRequestMap();

		// ensure they start out identical
		if (!containsIdenticalEntries(extCtxRequestMap, (Enumeration<String>) request.getAttributeNames(), request)) {
			testRunner.setTestResult(false,
				"Failed: Portlet request attributes and the externalContext requestMap entries aren't identical.");

			return Constants.TEST_FAILED;
		}

		// Test for mutability
		try {
			extCtxRequestMap.put("Test0Key", "Test0Value");
			request.setAttribute("Test1Key", "Test1Value");
		}
		catch (Exception e) {
			testRunner.setTestResult(false,
				"Failed: Putting an attribute on the ExternalContext's requestmap threw and exception: " +
				e.toString());

			return Constants.TEST_FAILED;
		}

		// test that we can read an attribute set on the portlet request via this Map
		// and vice-versa -- as we have just written an attribute on the extCtx and
		// the test portlet wrote one on the portlet request -- the act of verifying
		// the Maps contain the same keys/values should do the trick.
		if (!containsIdenticalEntries(extCtxRequestMap, (Enumeration<String>) request.getAttributeNames(), request)) {
			testRunner.setTestResult(false,
				"Failed: After setting an attribute on the portlet request and the externalContext requestMap they no longer contain identical entries.");

			return Constants.TEST_FAILED;
		}

		// Now remove the attribute we put in the  -- do the remove on the opposite object
		extCtxRequestMap.remove("Test1Key");
		request.removeAttribute("Test0Key");

		if (!containsIdenticalEntries(extCtxRequestMap, (Enumeration<String>) request.getAttributeNames(), request)) {
			testRunner.setTestResult(false,
				"Failed: After removing an attribute on the portlet request and the externalContext requestMap they no longer contain identical entries.");

			return Constants.TEST_FAILED;
		}

		// Otherwise all out tests passed:

		testRunner.setTestResult(true, "The Map returned from getRequestMap is mutable.");
		testRunner.appendTestDetail(
			"The getRequestMap Map correctly expresses attributes in the underlying request that have been added there.");
		testRunner.appendTestDetail(
			"The getRequestMap Map correctly reflects attrbiutes into the underlying request that have been added to it.");
		testRunner.appendTestDetail(
			"The getRequestMap Map correctly doesn't express attrbiutes that have been removed from the underlying request");
		testRunner.appendTestDetail(
			"The getRequestMap Map correctly cause the underlying request to remove any attributes removed from it");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.44
	@BridgeTest(test = "requestMapPreDestroyRemoveWithinActionTest")
	public String requestMapPreDestroyRemoveWithinActionTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Application app = ctx.getApplication();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// ensure the managed beans come into existence
			Boolean isIn = (Boolean) app.evaluateExpressionGet(ctx, "#{predestroyBean1.inBridgeRequestScope}",
					Object.class);
			Map<String, Object> m = extCtx.getRequestMap();
			m.remove("predestroyBean1");

			// Now verify that things worked correctly We expect that the beans were not added to the bridge scope (yet)
			// and hence only the Predestroy was called
			Boolean notifiedAddedToBridgeScope = (Boolean) m.get("PreDestroyBean1.attributeAdded");
			Boolean notifiedPreDestroy = (Boolean) m.get("PreDestroyBean1.servletPreDestroy");
			Boolean notifiedBridgePreDestroy = (Boolean) m.get("PreDestroyBean1.bridgePreDestroy");

			if ((notifiedAddedToBridgeScope == null) && (notifiedBridgePreDestroy == null) &&
					(notifiedPreDestroy != null) && notifiedPreDestroy.equals(Boolean.TRUE)) {

				// Only the regular PreDestroy was called and so it would have cleaned itself up
				testRunner.setTestResult(true,
					"The bridge request scope behaved correctly in handling preDestroy of a removed attribute prior to it being added to the bridge's scope in that:");
				testRunner.appendTestDetail(
					"     a) the bean wasn't notified it had been added to the bridge request scope.");
				testRunner.appendTestDetail("     b) the bean didn't have its BridgePreDestroy called.");
				testRunner.appendTestDetail("     c) the bean did have its Predestroy called.");
			}
			else {
				testRunner.setTestResult(false,
					"The bridge request scope didn't behaved correctly in handling preDestroy of a removed attribute prior to it being added to the bridge's scope in that:");

				if (notifiedAddedToBridgeScope != null)
					testRunner.appendTestDetail("::::: it notified the bean it was added to the bridge request scope.");

				if (notifiedBridgePreDestroy != null)
					testRunner.appendTestDetail(
						"::::: it notified the bean it was removed from the bridge request scope.");

				if (notifiedPreDestroy == null)
					testRunner.appendTestDetail("::::: it didn't notify the bean's PreDestroy.");

				if ((notifiedPreDestroy != null) && notifiedPreDestroy.equals(Boolean.FALSE))
					testRunner.appendTestDetail(
						"::::: the bean's Predestroy was called but it thought it had been added to the bridge request scope.");
			}

			// Now remove them manually to see if they are cleaned up correctly

			return "requestMapPreDestroyRemoveWithinActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String requestMapRequestScopeTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> m = extCtx.getRequestMap();

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
			m.put("myRequestObject", extCtx.getRequest()); // should be excluded because of value type
			m.put("myFacesContext", ctx); // should be excluded because of value type
			m.put("javax.faces.myKey1", Boolean.TRUE); // should be excluded because its in exlcuded namespace
			m.put("javax.faces.myNamespace.myKey1", Boolean.TRUE); // should be retained because excluded namespaces
																   // don't recurse
			m.put("myKey1", Boolean.TRUE); // should be retained
			m.put("myExcludedNamespace.myKey1", Boolean.TRUE); // should be excluded as defined in portlet.xml
			m.put("myExcludedKey", Boolean.TRUE); // defined as excluded in the portlet.xml
			m.put("myExcludedNamespace.myIncludedNamespace.myKey1", Boolean.TRUE); // should be retained as excluded
																				   // namespaces don't recurse
			m.put("myFacesConfigExcludedNamespace.myKey1", Boolean.TRUE); // should be excluded as defined in
																		  // faces-config.xml
			m.put("myFacesConfigExcludedKey", Boolean.TRUE); // defined as excluded in the faces-config.xml
			m.put("myFacesConfigExcludedNamespace.myIncludedNamespace.myKey1", Boolean.TRUE); // should be retained as
																							  // excluded namespaces
																							  // don't recurse

			return "requestMapRequestScopeTest";
		}
		else {
			testRunner.setTestComplete(true);

			// make sure that the attrbiute added before ExternalContext acquired is missing (set in the TestPortlet's
			// action handler)
			if (m.get("verifyPreBridgeExclusion") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute that existed prior to FacesContext being acquired.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myRequestObject") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose value is the PortletRequest object.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myFacesContext") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose value is the FacesContext object.");

				return Constants.TEST_FAILED;
			}

			if (m.get("javax.faces.myKey1") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute in the predefined exlcuded namespace javax.faces.");

				return Constants.TEST_FAILED;
			}

			if (m.get("javax.faces.myNamespace.myKey1") == null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly exlcuded an attribute that is in a subnamespace of the javax.faces namespace.  Exclusion rules aren't recursive.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myKey1") == null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly excluded an attribute that wasn't defined as excluded.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myExcludedNamespace.myKey1") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key is in a portlet defined excluded namespace.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myExcludedKey") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key matches a portlet defined excluded attribute");

				return Constants.TEST_FAILED;
			}

			if (m.get("myExcludedNamespace.myIncludedNamespace.myKey1") == null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly exlcuded an attribute that is in a subnamespace of a portlet excluded namespace.  Exclusion rules aren't recursive.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myFacesConfigExcludedNamespace.myKey1") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key is in a faces-config.xml defined excluded namespace.");

				return Constants.TEST_FAILED;
			}

			if (m.get("myFacesConfigExcludedKey") != null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly preserved an attribute whose key matches a faces-config.xml defined excluded attribute");

				return Constants.TEST_FAILED;
			}

			if (m.get("myFacesConfigExcludedNamespace.myIncludedNamespace.myKey1") == null) {
				testRunner.setTestResult(false,
					"The bridge request scope incorrectly exlcuded an attribute that is in a subnamespace of a faces-config.xml excluded namespace.  Exclusion rules aren't recursive.");

				return Constants.TEST_FAILED;
			}

			testRunner.setTestResult(true,
				"The bridge request scope behaved correctly for each of the following tests:");
			testRunner.appendTestDetail(
				"     a) it excluded attributes whose values were of the type PortletRequest and FacesContext.");
			testRunner.appendTestDetail(
				"     b) it excluded attributes from both a predefined namespace and one the portlet defined.");
			testRunner.appendTestDetail(
				"     c) it included attributes from both a predefined namespace and one the portlet defined when the attribute name contained a further namespace qualification.");
			testRunner.appendTestDetail(
				"     d) it excluded attributes that were added before the bridge was invoked (FacesContext acquired).");
			testRunner.appendTestDetail(
				"     b) it excluded attributes from each of a predefined namespace, a portlet defined namespace, and a faces-config.xml defiend namespace.");
			testRunner.appendTestDetail("     b) it included attributes it was supposed to.");

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
	public String setRequestCharacterEncodingActionTest(TestRunnerBean testRunner) {
		final String utf8 = "UTF-8";
		final String utf16 = "UTF-16";
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			String s = extCtx.getRequestCharacterEncoding();
			String testEncoding = null;

			// A number of container have trouble with this test --
			// All one should have to do is get the parameters to cause the test state to be set
			// but some containers require the reader be called -- issue is -- the spec also
			// says you can't get a reader if its a form postback.
			// Anyway -- first try to get a reader -- then to read the parameters to
			// be most accomodative
			try {
				BufferedReader b = ((ClientDataRequest) extCtx.getRequest()).getReader();
				int i = 0;
			}
			catch (Exception e) {

				// container likely did the right thing -- but make sure by reading the parameters
				Map m = ((PortletRequest) extCtx.getRequest()).getParameterMap();
			}

			if ((s == null) || ((s != null) && !s.equalsIgnoreCase(utf8))) {
				testEncoding = utf8;
			}
			else {
				testEncoding = utf16;
			}

			try {
				extCtx.setRequestCharacterEncoding(testEncoding);

				String v = extCtx.getRequestCharacterEncoding();

				if (((v == null) && (s == null)) || ((v != null) && (s != null) && v.equalsIgnoreCase(s))) {
					testRunner.setTestResult(true,
						"setRequestCharacterEncoding was correctly ignored after reading a parameter in an action request.");
				}
				else {
					testRunner.setTestResult(false,
						"setRequestCharacterEncoding incorrectly set a new encoding after reading a parameter in an action request.");
				}
			}
			catch (Exception e) {
				testRunner.setTestResult(false,
					"setRequestCharacterEncoding was correctly ignored after reading a parameter in an action request.");
			}

			return "setRequestCharacterEncodingActionTest";
		}
		else {
			testRunner.setTestComplete(true);

			if (testRunner.getTestStatus()) {
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
	public String setRequestCharacterEncodingRenderTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// Call setRequestCharacterEncoding -- fail if an exception is thrown
		try {
			extCtx.setRequestCharacterEncoding("UTF-8");

			// In portlet 1.0 there is no supprt for this -- so spec says ignore
			testRunner.setTestResult(true,
				"setRequestCharacterEncoding correctly didn't throw an exception when called during the render Phase.");

			return Constants.TEST_SUCCESS;
		}
		catch (Exception e) {
			testRunner.setTestResult(false,
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
