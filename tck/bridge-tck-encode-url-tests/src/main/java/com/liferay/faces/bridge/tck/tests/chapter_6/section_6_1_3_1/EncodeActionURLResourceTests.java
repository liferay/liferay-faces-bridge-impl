/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.beans.ViewBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class EncodeActionURLResourceTests {

	// Test Resource followed by Action + Render
	// Test #6.111
	@BridgeTest(test = "encodeActionURLJSFViewResourceTest")
	public String encodeActionURLJSFViewResourceTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		// If this method is reached in the RESOURCE_PHASE of the portlet lifecycle, then the "Run Test" button was
		// clicked after the initial render of TestPage121. Set a flag such that a subsequent click of the "Run Test"
		// button will be a full page (rather than an Ajax) postback.
		if (portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE) {

			FacesMessage facesMessage = new FacesMessage(
					"Ajax postback executed. Click link to run a subsequent full page postback.");
			facesContext.addMessage(null, facesMessage);

			ViewBean viewBean = getViewBean(facesContext);
			viewBean.setAjaxEnabled(false);

			return "";
		}

		// Otherwise, if this method is reached in the ACTION_PHASE of the portlet lifecycle then the Ajax re-render of
		// TestPage121 must have rendered a form with a postback URL that correctly caused the RESTORE_VIEW phase to
		// restore viewId=ajaxNonAjaxTest.xhtml (meaning, the viewId must have been encoded in a BridgeURL found in the
		// form's "action" attribute).
		else if (portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) {

			// Here in the ACTION_PHASE, return an action outcome that will cause the FacesBridge navigation-handler to
			// execute a navigation-rule in order to navigate to a different view (in the ACTION_PHASE) by:
			//
			// 1) Creating an action BridgeURL that encodes target viewId=multiRequestTestResultRenderCheck.xhtml (in
			// the RI, see the BridgeNavigationHandlerImpl.handleNavigation(FacesContext,String,String) method).
			//
			// 2) Extracting portlet mode, window state, and render parameters (a.k.a. Portlet 3.0 "render state") from
			// the action BridgeURL and setting them on the StateAwareResponse (in the RI, see the
			// BridgeNavigationUtil.navigate(PortletRequest,StateAwareResponse,Map<String,String[]> method).
			//
			// Note that the client window id "jsfwid" parameter is encoded on the actionURL in #1 and set as a render
			// parameter in #2 so that it will be picked up in the HEADER_PHASE.
			ActionResponse actionResponse = (ActionResponse) externalContext.getResponse();
			String actionClientWindowId = requestParameterMap.get(ResponseStateManager.CLIENT_WINDOW_URL_PARAM);

			if (actionClientWindowId != null) {
				actionResponse.setRenderParameter("actionClientWindowId", actionClientWindowId);
			}

			return "encodeActionURLJSFViewResourceTest"; // action Navigation result
		}

		// Otherwise, if this method is reached in the HEADER_PHASE of the portlet lifecycle then the bridge executed a
		// navigation-rule to viewId=multiRequestTestResultRenderCheck.xhtml in order to display the test results. This
		// verifies that the action BridgeURL created in the ACTION_PHASE correctly encoded the viewId (and if client
		// window mode is enabled, the "jsfwid" parameter as well).
		else {

			String actionClientWindowId = requestParameterMap.get("actionClientWindowId");
			String renderClientWindowId = requestParameterMap.get(ResponseStateManager.CLIENT_WINDOW_URL_PARAM);

			if (ClientWindowTestUtil.isClientWindowEnabled(externalContext)) {

				String clientWindowId = ClientWindowTestUtil.getClientWindowId(externalContext);

				if ((actionClientWindowId != null) && (actionClientWindowId.length() > 0)) {

					if (actionClientWindowId.equals(clientWindowId)) {

						if (actionClientWindowId.equals(renderClientWindowId)) {
							testBean.setTestResult(true,
								"encodeActionURL correctly encoded the viewId in the RESOURCE_PHASE of the portlet " +
								"lifecycle and the " + ResponseStateManager.CLIENT_WINDOW_URL_PARAM +
								" request parameter value=[" + actionClientWindowId +
								"] is equal to ClientWindow.getId().");
						}
						else {
							testBean.setTestResult(true,
								"The " + ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " RENDER parameter value=[" +
								renderClientWindowId + "] is NOT equal to the ACTION parameter value=[" +
								actionClientWindowId + "]. This can indicate one of two problems: 1) encodeActionURL " +
								" did not encode " + ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " in the " +
								"ACTION_PHASE or the navigation handler did not set the " +
								ResponseStateManager.CLIENT_WINDOW_URL_PARAM +
								" RENDER parameter in the ACTION_PHASE.");
						}
					}
					else {
						testBean.setTestResult(false,
							"encodeActionURL returned a URL such that the " +
							ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " request parameter value=[" +
							actionClientWindowId + "] did NOT equal ClientWindow.getId()=[" + clientWindowId + "].");
					}
				}
				else {
					testBean.setTestResult(false,
						"encodeActionURL returned a URL that contained a null value for the " +
						ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " request parameter.");
				}
			}
			else {

				if (renderClientWindowId != null) {

					testBean.setTestResult(false,
						"Incorrectly found a non-null " + ResponseStateManager.CLIENT_WINDOW_URL_PARAM +
						" request parameter with value=[" + renderClientWindowId + "] in the " + portletRequestPhase +
						" even though the client window feature is disabled.");
				}
				else if (actionClientWindowId != null) {
					testBean.setTestResult(false,
						"encodeActionURL returned a URL that (when generated in the RESOURCE_PHASE) incorrectly " +
						"included the " + ResponseStateManager.CLIENT_WINDOW_URL_PARAM +
						" request parameter with value=[" + actionClientWindowId +
						"] even though the client window feature is disabled.");
				}
				else {
					testBean.setTestResult(true,
						"encodeActionURL correctly encoded the viewId in the RESOURCE_PHASE of the portlet lifecycle.");
				}
			}

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

	// Test is MultiRequest -- Render/Action
	// Test #6.113
	@BridgeTest(test = "encodeActionURLWithInvalidModeResourceTest")
	public String encodeActionURLWithInvalidModeResourceTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		final String ENCODE_ACTIONURL_TEST_STRING =
			"/tests/viewLink.jsf?javax.portlet.faces.PortletMode=blue&param1=testValue";

		// ensure this url is prefixed by the ContextPath as all encode routines expect this for things starting with /
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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
		String testString = externalContext.getRequestContextPath() + ENCODE_ACTIONURL_TEST_STRING;

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

	private ViewBean getViewBean(FacesContext facesContext) {

		Application application = facesContext.getApplication();
		ELResolver elResolver = application.getELResolver();
		ELContext elContext = facesContext.getELContext();

		return (ViewBean) elResolver.getValue(elContext, null, "viewBean");
	}
}
