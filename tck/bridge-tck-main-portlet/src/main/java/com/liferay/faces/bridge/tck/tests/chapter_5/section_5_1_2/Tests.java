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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_1_2;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.AnnotatedExcludedBean;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests {

	private static final String MESSAGE_VALUE1 = "Test Message1 Retention.";
	private static final String MESSAGE_VALUE2 = "Test Message2 Retention.";
	private static final String REQUEST_ATTR_VALUE = "T1";

	// Test is MultiRequest -- Render/Action
	// Test #5.10
	@BridgeTest(test = "excludedAttributesTest")
	public String excludedAttributesTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			// Add elements that should be preserved: Messages need to be added after InvokeApplication (i.e. once
			// navigation has occurred -- so we attach to the right ViewRoot The lifecycleListener is in the portlet
			// that drives this test

			requestMap.put("com.liferay.faces.bridge.tck.IncludedAttr", REQUEST_ATTR_VALUE);
			requestMap.put("com.liferay.faces.bridge.tck.WildcardFacesConfigTest.include.IncludedAttr",
				REQUEST_ATTR_VALUE);
			requestMap.put("com.liferay.faces.bridge.tck.WildcardPortletXmlTest.include.IncludedAttr",
				REQUEST_ATTR_VALUE);

			// Add elements that should be excluded
			requestMap.put("com.liferay.faces.bridge.tck.ExcludeByAnnotation", new AnnotatedExcludedBean());
			requestMap.put("com.liferay.faces.bridge.tck.ExcludeByFacesConfigRef", REQUEST_ATTR_VALUE);
			requestMap.put("com.liferay.faces.bridge.tck.WildcardFacesConfigTest.ExcludeByFacesConfigRef",
				REQUEST_ATTR_VALUE);
			requestMap.put("com.liferay.faces.bridge.tck.ExcludeByPortletXmlRef", REQUEST_ATTR_VALUE);
			requestMap.put("com.liferay.faces.bridge.tck.WildcardPortletXmlTest.ExcludeByPortletXmlRef",
				REQUEST_ATTR_VALUE);

			return "excludedAttributesTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Now verify that what should have been carried forward has and what shouldn't hasn't.

			String s = (String) requestMap.get("com.liferay.faces.bridge.tck.IncludedAttr");

			if ((s == null) || !s.equals(REQUEST_ATTR_VALUE)) {
				testBean.setTestResult(false,
					"Expected request attribute not retained: com.liferay.faces.bridge.tck.IncludedAttr.");

				return Constants.TEST_FAILED;
			}

			s = (String) requestMap.get("com.liferay.faces.bridge.tck.WildcardFacesConfigTest.include.IncludedAttr");

			if ((s == null) || !s.equals(REQUEST_ATTR_VALUE)) {
				testBean.setTestResult(false,
					"Expected request attribute not retained: com.liferay.faces.bridge.tck.WildcardFacesConfigTest.include.IncludedAttr.");

				return Constants.TEST_FAILED;
			}

			s = (String) requestMap.get("com.liferay.faces.bridge.tck.WildcardPortletXmlTest.include.IncludedAttr");

			if ((s == null) || !s.equals(REQUEST_ATTR_VALUE)) {
				testBean.setTestResult(false,
					"Expected request attribute not retained: com.liferay.faces.bridge.tck.WildcardPortletXmlTest.include.IncludedAttr.");

				return Constants.TEST_FAILED;
			}

			// Check elements that should be excluded
			Object o = requestMap.get("com.liferay.faces.bridge.tck.ExcludeByAnnotation");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute annotated (for exclusion) unexpectedly retained: com.liferay.faces.bridge.tck.ExcludeByAnnotation.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.ExcludeByFacesConfigRef");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute annotated (for exclusion) unexpectedly retained: com.liferay.faces.bridge.tck.ExcludeByFacesConfigRef.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.WildcardFacesConfigTest.ExcludeByFacesConfigRef");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute annotated (for exclusion) unexpectedly retained: com.liferay.faces.bridge.tck.WildcardFacesConfigTest.ExcludeByFacesConfigRef.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.ExcludeByPortletXmlRef");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute annotated (for exclusion) unexpectedly retained: com.liferay.faces.bridge.tck.ExcludeByPortletXmlRef.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.WildcardPortletXmlTest.ExcludeByPortletXmlRef");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute annotated (for exclusion) unexpectedly retained: com.liferay.faces.bridge.tck.WildcardPortletXmlTest.ExcludeByPortletXmlRef.");

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true, "Correctly retained expected attributes and excluded expected attributes.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.4
	@BridgeTest(test = "requestNoScopeOnModeChangeTest")
	public String requestNoScopeOnModeChangeTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Add elements that should be preserved but won't be because we are navigating from VIEW to EDIT mode
			// due to the presence of javax.portlet.faces.PortletMode=edit in the navigation-rule.
			requestMap.put("com.liferay.faces.bridge.tck.TestRequestScope_a", REQUEST_ATTR_VALUE);

			return "requestNoScopeOnModeChangeTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Now verify that no scope data was carried forward.

			String s = (String) requestMap.get("com.liferay.faces.bridge.tck.TestRequestScope_a");

			if (s == null) {
				testBean.setTestResult(true, "Request attribute not retained as expected.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Request attribute unexpectedly retained though the portlet mode changed.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test #5.5 -- covered by #5.1 -- I.e. it jusst says that request scope is preserved through render

	// Test is MultiRequest -- Render/Action
	// Test #5.3
	@BridgeTest(test = "requestNoScopeOnRedirectTest")
	public String requestNoScopeOnRedirectTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Add elements that should be preserved but won't be because we are redirecting
			requestMap.put("com.liferay.faces.bridge.tck.TestRequestScope_a", REQUEST_ATTR_VALUE);

			return "requestNoScopeOnRedirectTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Now verify that no scope data was carried forward.

			String s = (String) requestMap.get("com.liferay.faces.bridge.tck.TestRequestScope_a");

			if (s == null) {
				testBean.setTestResult(true, "Request attribute not retained as expected.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "Request attribute unexpectedly retained through a redirect.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.6
	@BridgeTest(test = "requestPreserveActionParamsTest")
	public String requestPreserveActionParamsTest(TestBean testBean) {

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			return "requestPreserveActionParamsTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Now verify that no scope data was carried forward.

			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, String> paramMap = externalContext.getRequestParameterMap();

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
				testBean.setTestResult(false, "Expected action parameter 'field1' not correctly preserved.");

				return Constants.TEST_FAILED;
			}
			else if (!foundField2) {
				testBean.setTestResult(false, "Expected action parameter 'field2' not correctly preserved.");

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(true, "Action parameters correctly preserved.");

				return Constants.TEST_SUCCESS;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.9
	@BridgeTest(test = "requestRedisplayOutOfScopeTest")
	public String requestRedisplayOutOfScopeTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			externalContext.getRequestMap().put("com.liferay.faces.bridge.tck.TestRequestScope_a", REQUEST_ATTR_VALUE);

			return "requestRedisplayOutOfScopeTest"; // action Navigation result
		}
		else {

			// If redisplay hasn't been invoked yet -- merely return
			if (externalContext.getRequestParameterMap().get("org.apache.portlet.faces.tck.redisplay") == null) {
				return "requestRedisplayOutOfScopeTest";
			}

			testBean.setTestComplete(true);

			String s = (String) externalContext.getRequestMap().get("com.liferay.faces.bridge.tck.TestRequestScope_a");

			if (s == null) {
				testBean.setTestResult(true,
					"Second render correctly didn't use original request scope as we entered a new mode.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"Second render incorrectly used original request scope though we entered a new mode.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.7
	@BridgeTest(test = "requestRenderIgnoresScopeViaCreateViewTest")
	public String requestRenderIgnoresScopeViaCreateViewTest(TestBean testBean) {

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			return "requestRenderIgnoresScopeViaCreateViewTest"; // action Navigation result
		}
		else {
			ExternalContext externalContext = facesContext.getExternalContext();

			// If redisplay hasn't been invoked yet -- merely return
			if (externalContext.getRequestParameterMap().get("org.apache.portlet.faces.tck.redisplay") == null) {
				return "requestRenderIgnoresScopeViaCreateViewTest";
			}

			testBean.setTestComplete(true);

			// Now verify create was used and not restore.
			Boolean b = (Boolean) externalContext.getRequestMap().get("com.liferay.faces.bridge.tck.viewCreated");

			if (Boolean.TRUE.equals(b)) {
				testBean.setTestResult(true, "CreateView used when view restored in a mismatched scope.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "CreateView not used when view restored in a mismatched scope.");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest -- Render/Action
	// Test #5.1
	@BridgeTest(test = "requestScopeContentsTest")
	public String requestScopeContentsTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// mark the test as compelte -- put in session in case the bridge fails in preserving the request scope
			externalContext.getSessionMap().put("com.liferay.faces.bridge.tck.testComplete", Boolean.TRUE);
			// Add elements that should be preserved: Messages need to be added after InvokeApplication (i.e. once
			// navigation has occurred -- so we attach to the right ViewRoot The lifecycleListener is in the portlet
			// that drives this test

			requestMap.put("com.liferay.faces.bridge.tck.TestRequestScope_a", REQUEST_ATTR_VALUE);

			// Add elements that should be excluded
			requestMap.put("com.liferay.faces.bridge.tck.TestRequestScope_b", new AnnotatedExcludedBean());
			// Don't need to add additional excludedAttributes tests as they are covered in chapter 6 tests

			PortletConfig portletConfig = (PortletConfig) facesContext.getELContext().getELResolver().getValue(
					facesContext.getELContext(), null, "portletConfig");

			if (portletConfig == null) {
				throw new IllegalStateException("Unable to acquire the portletConfig!");
			}

			requestMap.put("com.liferay.faces.bridge.tck.PortletConfig", portletConfig);

			requestMap.put("com.liferay.faces.bridge.tck.PortletContext", externalContext.getContext());
			requestMap.put("com.liferay.faces.bridge.tck.PortletRequest", externalContext.getRequest());
			requestMap.put("com.liferay.faces.bridge.tck.PortletResponse", externalContext.getResponse());
			requestMap.put("com.liferay.faces.bridge.tck.PortletSession", externalContext.getSession(true));

			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			PortletPreferences prefs = portletRequest.getPreferences();
			requestMap.put("com.liferay.faces.bridge.tck.PortletPreferences", prefs);

			PortalContext pc = portletRequest.getPortalContext();
			requestMap.put("com.liferay.faces.bridge.tck.PortalContext", pc);

			requestMap.put("com.liferay.faces.bridge.tck.FacesContext", facesContext);
			requestMap.put("com.liferay.faces.bridge.tck.ExternalContext", externalContext);

			// Note:  no way to test the servlet objects

			requestMap.put("javax.portlet.TestRequestScope", REQUEST_ATTR_VALUE);
			requestMap.put("javax.portlet.faces.TestRequestScope", REQUEST_ATTR_VALUE);
			requestMap.put("javax.faces.TestRequestScope", REQUEST_ATTR_VALUE);
			requestMap.put("javax.servlet.TestRequestScope", REQUEST_ATTR_VALUE);
			requestMap.put("javax.servlet.include.TestRequestScope", REQUEST_ATTR_VALUE);

			return "requestScopeContentsTest"; // action Navigation result
		}
		else {

			if (externalContext.getSessionMap().get("com.liferay.faces.bridge.tck.testComplete") == null) {
				return "Test is still in progress ...";
			}
			else {
				externalContext.getSessionMap().remove("com.liferay.faces.bridge.tck.testComplete");
				testBean.setTestComplete(true);
			}
			// Now verify that what should have been carried forward has and what shouldn't hasn't.

			// Check for the message we aded
			boolean found1 = false;
			boolean found2 = false;

			for (Iterator<FacesMessage> i = facesContext.getMessages(uiViewRoot.getClientId(facesContext));
					i.hasNext();) {
				FacesMessage message = i.next();

				if (message.getSummary().equals(MESSAGE_VALUE1) && !found2) {
					found1 = true;
				}
				else if (message.getSummary().equals(MESSAGE_VALUE2) && found1) {
					found2 = true;
				}
			}

			if (!found1 || !found2) {
				testBean.setTestResult(false,
					"Messages added to UIViewRoot not retained as expected. Either there weren't preserved or they weren't preserved in the correct order.");

				return Constants.TEST_FAILED;
			}

			String s = (String) requestMap.get("com.liferay.faces.bridge.tck.TestRequestScope_a");

			if ((s == null) || !s.equals(REQUEST_ATTR_VALUE)) {
				testBean.setTestResult(false,
					"Expected request attribute not retained: com.liferay.faces.bridge.tck.TestRequestScope_a.");

				return Constants.TEST_FAILED;
			}

			// Add elements that should be excluded
			Object o = requestMap.get("com.liferay.faces.bridge.tck.TestRequestScope_b");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute annotated (for exclusion) unexpectedly retained: com.liferay.faces.bridge.tck.TestRequestScope_b.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortletConfig");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortletConfig unexpectedly retained: com.liferay.faces.bridge.tck.PortletConfig.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortletContext");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortletContext unexpectedly retained: com.liferay.faces.bridge.tck.PortletContext.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortletRequest");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortletRequest unexpectedly retained: com.liferay.faces.bridge.tck.PortletRequest.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortletResponse");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortletResponse unexpectedly retained: com.liferay.faces.bridge.tck.PortletResponse.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortletSession");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortletSession unexpectedly retained: com.liferay.faces.bridge.tck.PortletSession.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortletPreferences");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortletPreferences unexpectedly retained: com.liferay.faces.bridge.tck.PortletPreferences.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.PortalContext");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing PortalContext unexpectedly retained: com.liferay.faces.bridge.tck.PortalContext.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.FacesContext");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing FacesContext unexpectedly retained: com.liferay.faces.bridge.tck.FacesContext.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("com.liferay.faces.bridge.tck.ExternalContext");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute containing ExternalContext unexpectedly retained: com.liferay.faces.bridge.tck.ExternalContext.");

				return Constants.TEST_FAILED;
			}

			// Note:  no way to test the servlet objects

			o = requestMap.get("javax.portlet.TestRequestScope");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute in the javax.portlet namesapce unexpectedly retained: javax.portlet.TestRequestScope.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("javax.portlet.faces.TestRequestScope");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute in the javax.portlet.faces namesapce unexpectedly retained: javax.portlet.faces.TestRequestScope.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("javax.faces.TestRequestScope");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute in the javax.faces namesapce unexpectedly retained: javax.faces.TestRequestScope.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("javax.servlet.TestRequestScope");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute in the javax.servlet namesapce unexpectedly retained: javax.servlet.TestRequestScope.");

				return Constants.TEST_FAILED;
			}

			o = requestMap.get("javax.servlet.include.TestRequestScope");

			if (o != null) {
				testBean.setTestResult(false,
					"Attribute in the javax.servlet.include namesapce unexpectedly retained: javax.servlet.include.TestRequestScope.");

				return Constants.TEST_FAILED;
			}

			testBean.setTestResult(true, "Correctly retained expected attributes and excluded expected attributes.");

			return Constants.TEST_SUCCESS;
		}
	}

	// Test is MultiAction(2)Request -- Render/Action
	// Test #5.2
	@BridgeTest(test = "requestScopeRestartedOnActionTest")
	public String requestScopeRestartedOnActionTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {
			Map<String, Object> sessionMap = externalContext.getSessionMap();
			Boolean b = (Boolean) sessionMap.get("com.liferay.faces.bridge.tck.firstActionOccurred");

			if (b == null) {

				// This is the first action
				sessionMap.put("com.liferay.faces.bridge.tck.firstActionOccurred", new Boolean(true));

				// push a new request attribute that we can test later doesn't exist
				FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(
					"com.liferay.faces.bridge.tck.TestRequestScope_a", REQUEST_ATTR_VALUE);

				return "requestScopeRestartedOnActionTest_Action1";
			}
			else {

				// Test for the attribute put in the first action
				String s = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(
						"com.liferay.faces.bridge.tck.TestRequestScope_a");

				if (s == null) {
					testBean.setTestResult(true, "Request scope attribute not retained across requests.");
				}
				else {
					testBean.setTestResult(false, "Request scope attribute retained across action requests.");
				}

				testBean.setTestComplete(true);

				return "requestScopeRestartedOnActionTest_Action2";
			}
		}
		else {

			if (testBean.isTestComplete()) {

				if (testBean.getTestStatus()) {
					return Constants.TEST_SUCCESS;
				}
				else {
					return Constants.TEST_FAILED;
				}
			}
			else {
				return Constants.TEST_SUCCESS;
			}
		}
	}

}
