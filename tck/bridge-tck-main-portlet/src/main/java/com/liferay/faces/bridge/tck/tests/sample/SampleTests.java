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
package com.liferay.faces.bridge.tck.tests.sample;

import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * This portlet sets one attribute in PortletRequest and makes sure we can read that attribute back using
 * PortletRequest.getAttribute()
 */

public class SampleTests extends Object {

	@BridgeTest(test = "multiRequestTest")
	public String multiRequestTest(TestRunnerBean testRunner) {

		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "multiRequestTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(true, "multiRequestTest ran both its action and render.");

			return Constants.TEST_SUCCESS;
		}
	}

	@BridgeTest(test = "singleRequestTest")
	public String singleRequestTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);
		testRunner.setTestResult(true, "Correctly ran the single request test.");

		return Constants.TEST_SUCCESS;
	}
}
