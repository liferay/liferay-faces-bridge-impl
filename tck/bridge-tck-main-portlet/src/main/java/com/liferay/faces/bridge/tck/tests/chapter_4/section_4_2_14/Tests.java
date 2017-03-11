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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_14;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.xml.namespace.QName;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	// Test is MultiRequest -- Render/Action
	// Test # -- 4.20
	@BridgeTest(test = "isAutoDispatchEventsNotSetTest")
	public String isAutoDispatchEventsNotSetTest(TestRunnerBean testRunner) {
		return isAutoDispatchEventsTest(testRunner);
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 4.19
	@BridgeTest(test = "isAutoDispatchEventsSetTest")
	public String isAutoDispatchEventsSetTest(TestRunnerBean testRunner) {
		return isAutoDispatchEventsTest(testRunner);
	}

	public String isAutoDispatchEventsTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event -- this ensures isAutoDispatchEvents will get called
			StateAwareResponse response = (StateAwareResponse) extCtx.getResponse();
			response.setEvent(new QName("http://liferay.com/faces/event_ns", "faces.liferay.com.tck.testEvent"),
				testRunner.getTestName());

			return "isAutoDispatchEventsTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			String failedMsg = (String) extCtx.getRequestMap().get("test.fail.");
			String successMsg = (String) extCtx.getRequestMap().get("test.pass.");

			if (successMsg != null) {
				testRunner.setTestResult(true, successMsg);

				return Constants.TEST_SUCCESS;
			}
			else if (failedMsg != null) {
				testRunner.setTestResult(false, failedMsg);

				return Constants.TEST_FAILED;
			}
			else {
				testRunner.setTestResult(false, "Unexpected failure: No result string set by the portlet");

				return Constants.TEST_FAILED;
			}
		}
	}

}
