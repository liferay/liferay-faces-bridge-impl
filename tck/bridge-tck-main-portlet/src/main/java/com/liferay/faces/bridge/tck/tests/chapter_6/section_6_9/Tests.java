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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_9;

import java.util.Iterator;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	public static final String ACTION_TEST_RESULT = "com.liferay.faces.bridge.tck.actionTestResult";

	// Test is MultiRequest --
	// Test #6.135
	@BridgeTest(test = "usesConfiguredRenderKitTest")
	public String usesConfiguredRenderKitTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> m = extCtx.getRequestMap();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Verify that the configured attribute is passed as a parameter
			Map<String, String> rParams = extCtx.getRequestParameterMap();
			Map<String, String[]> rParamValues = extCtx.getRequestParameterValuesMap();
			Iterator<String> rParamNames = extCtx.getRequestParameterNames();

			String detail = "";
			boolean found = false;

			for (Iterator<String> s = rParamNames; s.hasNext();) {

				if (s.next().equals(ResponseStateManager.RENDER_KIT_ID_PARAM)) {
					found = true;

					break;
				}
			}

			if (!found) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterNames iterator during the action request.<br> ";
			}

			String[] vals = rParamValues.get(ResponseStateManager.RENDER_KIT_ID_PARAM);

			if (vals == null) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterValuesMap map during the action request.<br> ";
			}
			else if (!vals[0].equalsIgnoreCase("TestRenderKit")) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterValuesMap map during the action request contains an unexpected value.  Expected: TestRenderKit but received:" +
					vals[0] + ".<br> ";
			}

			String val = rParams.get(ResponseStateManager.RENDER_KIT_ID_PARAM);

			if (val == null) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterMap map during the action request.<br> ";
			}
			else if (!val.equalsIgnoreCase("TestRenderKit")) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterMap map during the action request contains an unexpected value.  Expected: TestRenderKit but received:" +
					val + ".<br> ";
			}

			if (detail.length() == 0) {
				detail = Constants.TEST_SUCCESS;
			}

			m.put(ACTION_TEST_RESULT, detail);

			return "usesConfiguredRenderKitTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			String actionResult = (String) m.get(ACTION_TEST_RESULT);

			if (actionResult == null) {
				testRunner.setTestResult(false, "Action result not available during the render");

				return Constants.TEST_FAILED;
			}
			else if (!actionResult.equals(Constants.TEST_SUCCESS)) {
				testRunner.setTestResult(false, actionResult);

				return Constants.TEST_FAILED;
			}

			// Otherwise verify all things are set in the render
			// Verify that the configured attribute is passed as a parameter
			Map<String, String> rParams = extCtx.getRequestParameterMap();
			Map<String, String[]> rParamValues = extCtx.getRequestParameterValuesMap();
			Iterator<String> rParamNames = extCtx.getRequestParameterNames();

			String detail = "";
			boolean found = false;

			for (Iterator<String> s = rParamNames; s.hasNext();) {

				if (s.next().equals(ResponseStateManager.RENDER_KIT_ID_PARAM)) {
					found = true;

					break;
				}
			}

			if (!found) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterNames iterator during the render request.<br> ";
			}

			String[] vals = rParamValues.get(ResponseStateManager.RENDER_KIT_ID_PARAM);

			if (vals == null) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterValuesMap map during the render request.<br> ";
			}
			else if (!vals[0].equalsIgnoreCase("TestRenderKit")) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterValuesMap map during the render request contains an unexpected value.  Expected: TestRenderKit but received:" +
					vals[0] + ".<br> ";
			}

			String val = rParams.get(ResponseStateManager.RENDER_KIT_ID_PARAM);

			if (val == null) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterMap map during the render request.<br> ";
			}
			else if (!val.equalsIgnoreCase("TestRenderKit")) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterMap map during the render request contains an unexpected value.  Expected: TestRenderKit but received:" +
					val + ".<br> ";
			}

			if (detail.length() == 0) {
				testRunner.setTestResult(true,
					"The portlet configured renderkit was correctly expressed as a request parameter (via the ExternalContext apis) in both the action and render phases.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false, detail);

				return Constants.TEST_FAILED;
			}
		}
	}

}
