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
package com.liferay.faces.bridge.tck.tests.chapter_3;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.xml.namespace.QName;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2.TestEventHandler;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	// Test is MultiRequest -- Render/Action
	// Should never get to the render portion of this
	@BridgeTest(test = "actionDestroyTest")
	public String actionDestroyTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "actionDestroyTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(false,
				"unexpectedly got to a render JSP in this test -- should have been handled by the test portlet.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is MultiRequest -- Render/Action
	// Should never get to the render portion of this
	@BridgeTest(test = "actionNullRequestTest")
	public String actionNullRequestTest(TestRunnerBean testRunner) {

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "actionNullRequestTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(false,
				"unexpectedly got to a render JSP in this test -- should have been handled by the test portlet.");

			return Constants.TEST_FAILED;
		}
	}

	// Test #3.22
	@BridgeTest(test = "defaultRenderKitIdTest")
	public String defaultRenderKitIdTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		testRunner.setTestComplete(true);

		// Test that the defaultRenderKit parameter is set
		String renderKitId = extCtx.getRequestParameterMap().get(ResponseStateManager.RENDER_KIT_ID_PARAM);

		if (renderKitId == null) {
			testRunner.setTestResult(false,
				"ResponseStateManager.RENDER_KIT_ID_PARAM request parameter isn't set though this portlet has configured a defaultRenderKitId.");

			return Constants.TEST_FAILED;
		}
		else if (!renderKitId.equalsIgnoreCase(RenderKitFactory.HTML_BASIC_RENDER_KIT)) {
			testRunner.setTestResult(false,
				"ResponseStateManager.RENDER_KIT_ID_PARAM request parameter is set but has an incorrect value.  Expected: " +
				RenderKitFactory.HTML_BASIC_RENDER_KIT + " received: " + renderKitId);

			return Constants.TEST_FAILED;
		}

		testRunner.setTestResult(true,
			"ResponseStateManager.RENDER_KIT_ID_PARAM request parameter properly set to value of defaultRenderKitId portlet initParam.");

		return Constants.TEST_SUCCESS;
	}

	// Test is MultiRequest -- Render/Action
	// Should never get to the render portion of this
	@BridgeTest(test = "eventDestroyTest")
	public String eventDestroyTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName(TestEventHandler.EVENT_QNAME, TestEventHandler.EVENT_NAME),
				testRunner.getTestName());

			return "eventDestroyTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(false,
				"unexpectedly got to a render JSP in this test -- should have been handled by the test portlet.");

			return Constants.TEST_FAILED;
		}
	}

	/*
	 * Because Lifecycle_ID is a web.xml (application) config setting we need a different portlet app for each test.
	 * Because of this we can test all policies in a single test method.
	 */
	@BridgeTest(test = "lifecycleTest")
	public String lifecycleTest(TestRunnerBean testRunner) {
		Boolean pass = false;
		String msg;

		// Get the configured render policy
		ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> m = extCtx.getRequestMap();

		// Lifecycle check done in the FacesContextFactory -- so test/results set on every request
		msg = (String) m.get("javax.portlet.faces.tck.testLifecyclePass");

		if (msg != null) {
			pass = true;
		}
		else {
			msg = (String) m.get("javax.portlet.faces.tck.testLifecycleFail");
		}

		// remove them in case we are called to render more than once
		m.remove("javax.portlet.faces.tck.testLifecyclePass");
		m.remove("javax.portlet.faces.tck.testLifecycleFail");

		testRunner.setTestResult(pass, msg);

		if (pass) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;
		}
	}

	@BridgeTest(test = "modeViewIDTest")
	public String modeViewIDTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		PortletRequest req = (PortletRequest) extCtx.getRequest();

		testRunner.setTestComplete(true);

		// Are we in the rightview???
		String s = ctx.getViewRoot().getViewId();

		if (!s.contains("modeViewIdResult")) {
			testRunner.setTestResult(false,
				"defaultViewId test failed:  entered EDIT mode at view: " + s + " but the default is: " +
				"modeViewIdResult.xhtml");

			return Constants.TEST_FAILED;
		}

		// Are we in edit mode???
		if (!req.getPortletMode().equals(PortletMode.EDIT)) {
			testRunner.setTestResult(false,
				"defaultViewId test failed:  though in the correct view we aren't in EDIT mode.  So why did we get here?");

			return Constants.TEST_FAILED;
		}

		testRunner.setTestResult(true, "defaultViewId for EDIT mode was correctly used: " + s);

		return Constants.TEST_SUCCESS;
	}

	@BridgeTest(test = "portletSetsViewIdTest")
	public String portletSetsViewIdTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();

		testRunner.setTestComplete(true);

		// Are we in the rightview???
		String s = ctx.getViewRoot().getViewId();

		if (s.contains("Success")) {
			testRunner.setTestResult(true, "correctly rendered the view explicitly set by the portlet: " + s);

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"didn't render the view 'PorletSetsViewIdSuccess.jsp' explicitly set by the portlet, instead rendered: " +
				s);

			return Constants.TEST_FAILED;
		}
	}

	@BridgeTest(test = "portletSetsViewPathTest")
	public String portletSetsViewPathTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();

		testRunner.setTestComplete(true);

		// Are we in the rightview???
		String s = ctx.getViewRoot().getViewId();

		if (s.contains("Success")) {
			testRunner.setTestResult(true, "correctly rendered the view explicitly set by the portlet: " + s);

			return Constants.TEST_SUCCESS;
		}
		else {
			testRunner.setTestResult(false,
				"didn't render the view 'PorletSetsViewIdSuccess.jsp' explicitly set by the portlet, instead rendered: " +
				s);

			return Constants.TEST_FAILED;
		}
	}

	/*
	 * Because RenderPolicy is a web.xml (application) config setting we need a different portlet app for each test.
	 * Because of this we can test all policies in a single test method.
	 */
	@BridgeTest(test = "renderPolicyTest")
	public String renderPolicyTest(TestRunnerBean testRunner) {

		testRunner.setTestComplete(true);
		testRunner.setTestResult(true,
			"This test is no longer necessary due to <a href=\"https://issues.liferay.com/browse/FACES-2613\">FACES-2613</a>");

		return Constants.TEST_SUCCESS;
	}
}
