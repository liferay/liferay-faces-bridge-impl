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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_1;

import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	// Test is MultiRequest -- Render/Action
	// Test #6.2
	@BridgeTest(test = "wrappedFacesContextTest")
	public String wrappedFacesContextTest(TestRunnerBean testRunner) {

		// done by navigation rule.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "wrappedFacesContextTest"; // action Navigation result
		}
		else {
			testRunner.setTestComplete(true);

			testRunner.setTestResult(true,
				"Successfully rendered and performed an action with a wrapped FacesContext indicating the bridge implementation doesn't depend on casting directly to its FacesContext class.");

			return Constants.TEST_SUCCESS;
		}
	}

}
