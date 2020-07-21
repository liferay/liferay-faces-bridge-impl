/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests {

	// Test is MultiRequest -- Render/Action
	// Test # -- 4.20
	@BridgeTest(test = "isAutoDispatchEventsNotSetTest")
	public String isAutoDispatchEventsNotSetTest(TestBean testBean) {
		return isAutoDispatchEventsTest(testBean);
	}

	// Test is MultiRequest -- Render/Action
	// Test # -- 4.19
	@BridgeTest(test = "isAutoDispatchEventsSetTest")
	public String isAutoDispatchEventsSetTest(TestBean testBean) {
		return isAutoDispatchEventsTest(testBean);
	}

	public String isAutoDispatchEventsTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// Create and raise the event -- this ensures isAutoDispatchEvents will get called
			StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();
			stateAwareResponse.setEvent(new QName("http://liferay.com/faces/event_ns",
					"faces.liferay.com.tck.testEvent"), testBean.getTestName());

			return "isAutoDispatchEventsTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			String failedMsg = (String) externalContext.getRequestMap().get("test.fail.");
			String successMsg = (String) externalContext.getRequestMap().get("test.pass.");

			if (successMsg != null) {
				testBean.setTestResult(true, successMsg);

				return Constants.TEST_SUCCESS;
			}
			else if (failedMsg != null) {
				testBean.setTestResult(false, failedMsg);

				return Constants.TEST_FAILED;
			}
			else {
				testBean.setTestResult(false, "Unexpected failure: No result string set by the portlet");

				return Constants.TEST_FAILED;
			}
		}
	}

}
