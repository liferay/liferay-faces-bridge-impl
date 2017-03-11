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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_7;

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

	// Test is MultiRequest --
	// Test #6.97
	@BridgeTest(test = "restoredViewStateParameterTest")
	public String restoredViewStateParameterTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			return "restoredViewStateParameterTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			// Now see if isPostback returns true as it should
			String vsp = ctx.getExternalContext().getRequestParameterMap().get(ResponseStateManager.VIEW_STATE_PARAM);

			if (vsp != null) {
				testRunner.setTestResult(true,
					"Correctly restored VIEW_STATE parameter in render request following an action (Postback).");

				return Constants.TEST_SUCCESS;
			}
			else {
				testRunner.setTestResult(false,
					"Didn't restore VIEW_STATE parameter in render request following an action (Postback).");

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is MultiRequest --
	// Test #6.98
	@BridgeTest(test = "setsIsPostbackAttributeTest")
	public String setsIsPostbackAttributeTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();

		// This tests that we can encode a new mode in an actionURL
		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			return "setsIsPostbackAttributeTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			// Now see if isPostback returns true as it should
			Boolean isPostback = (Boolean) ctx.getExternalContext().getRequestMap().get(
					"javax.portlet.faces.isPostback");

			if (isPostback == null) {
				testRunner.setTestResult(false,
					"Didn't set the javax.portlet.faces.isPostback attribute in render request following an action (Postback).");

				return Constants.TEST_FAILED;
			}
			else {

				if (isPostback.booleanValue()) {
					testRunner.setTestResult(true,
						"Correctly set the javax.portlet.faces.isPostback attribute as TRUE in render request following an action (Postback).");

					return Constants.TEST_SUCCESS;
				}
				else {
					testRunner.setTestResult(false,
						"Incorrectly set the javax.portlet.faces.isPostback attribute as FALSE in render request following an action (Postback).");

					return Constants.TEST_FAILED;
				}
			}
		}
	}

}
