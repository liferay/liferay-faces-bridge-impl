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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_15;

import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	@BridgeTest(test = "getFacesBridgeMethodTest")
	public String getFacesBridgeMethodTest(TestRunnerBean testRunner) {

		// Section 4.2.12
		// The Run Test action simply forwards to a results page.
		if (BridgeUtil.getPortletRequestPhase() == Bridge.PortletPhase.ACTION_PHASE) {
			return "getFacesBridgeMethodTest";
		}
		else {
			testRunner.setTestComplete(true);

			testRunner.setTestResult(true,
				"Successfully performed an action request and a render request by means of a GenericFacesPortlet subclass using the bridge returned by GenericFacesPortlet.getFacesBridge().");

			return Constants.TEST_SUCCESS;
		}
	}
}
