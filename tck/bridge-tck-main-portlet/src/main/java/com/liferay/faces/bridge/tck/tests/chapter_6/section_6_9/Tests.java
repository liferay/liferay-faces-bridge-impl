/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.renderkit.TestRenderKitFactory;


/**
 * @author  Michael Freedman
 */
public class Tests {

	public static final String ACTION_TEST_RESULT = "com.liferay.faces.bridge.tck.actionTestResult";

	// Test is MultiRequest --
	// Test #6.135
	@BridgeTest(test = "usesConfiguredRenderKitTest")
	public String usesConfiguredRenderKitTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Verify that the configured attribute is passed as a parameter
			Map<String, String> rParams = externalContext.getRequestParameterMap();
			Map<String, String[]> rParamValues = externalContext.getRequestParameterValuesMap();
			Iterator<String> rParamNames = externalContext.getRequestParameterNames();

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
			else if (!vals[0].equalsIgnoreCase(TestRenderKitFactory.TEST_RENDER_KIT_ID)) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterValuesMap map during the action request contains an unexpected value.  Expected: TestRenderKit but received:" +
					vals[0] + ".<br> ";
			}

			String val = rParams.get(ResponseStateManager.RENDER_KIT_ID_PARAM);

			if (val == null) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterMap map during the action request.<br> ";
			}
			else if (!val.equalsIgnoreCase(TestRenderKitFactory.TEST_RENDER_KIT_ID)) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterMap map during the action request contains an unexpected value.  Expected: TestRenderKit but received:" +
					val + ".<br> ";
			}

			if (detail.length() == 0) {
				detail = Constants.TEST_SUCCESS;
			}

			requestMap.put(ACTION_TEST_RESULT, detail);

			return "usesConfiguredRenderKitTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			String actionResult = (String) requestMap.get(ACTION_TEST_RESULT);

			if (actionResult == null) {
				testBean.setTestResult(false, "Action result not available during the render");

				return Constants.TEST_FAILED;
			}
			else if (!actionResult.equals(Constants.TEST_SUCCESS)) {
				testBean.setTestResult(false, actionResult);

				return Constants.TEST_FAILED;
			}

			// Otherwise verify all things are set in the render
			// Verify that the configured attribute is passed as a parameter
			Map<String, String> rParams = externalContext.getRequestParameterMap();
			Map<String, String[]> rParamValues = externalContext.getRequestParameterValuesMap();
			Iterator<String> rParamNames = externalContext.getRequestParameterNames();

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
			else if (!vals[0].equalsIgnoreCase(TestRenderKitFactory.TEST_RENDER_KIT_ID)) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterValuesMap map during the render request contains an unexpected value.  Expected: TestRenderKit but received:" +
					vals[0] + ".<br> ";
			}

			String val = rParams.get(ResponseStateManager.RENDER_KIT_ID_PARAM);

			if (val == null) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name isn't in the ExternalContext.getRequestParameterMap map during the render request.<br> ";
			}
			else if (!val.equalsIgnoreCase(TestRenderKitFactory.TEST_RENDER_KIT_ID)) {
				detail +=
					"There is a portlet specific configured renderkit but its corresponding parameter name from the ExternalContext.getRequestParameterMap map during the render request contains an unexpected value.  Expected: TestRenderKit but received:" +
					val + ".<br> ";
			}

			if (detail.length() == 0) {
				testBean.setTestResult(true,
					"The portlet configured renderkit was correctly expressed as a request parameter (via the ExternalContext apis) in both the action and header phases.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, detail);

				return Constants.TEST_FAILED;
			}
		}
	}

}
