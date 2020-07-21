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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Kyle Stiemann
 */
public class HeaderTests {

	// Test is SingleRequest -- Header/Action
	// Test #5.33 --
	@BridgeTest(test = "headerPhaseListenerTest")
	public String headerPhaseListenerTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		testBean.setTestComplete(true);

		// Phase Listener (below) has set these attributes
		PhaseId lastBeforePhaseId = (PhaseId) requestMap.get("org.apache.portlet.faces.tck.lastBeforePhase");
		PhaseId lastAfterPhaseId = (PhaseId) requestMap.get("org.apache.portlet.faces.tck.lastAfterPhase");

		if ((lastBeforePhaseId == null) || (lastAfterPhaseId == null)) {
			testBean.setTestResult(false,
				"Header incorrectly didn't invoke either or both the RESTORE_VIEW before/after listener.");

			return Constants.TEST_FAILED;
		}
		else if ((lastBeforePhaseId == PhaseId.RESTORE_VIEW) && (lastAfterPhaseId == PhaseId.RESTORE_VIEW)) {
			testBean.setTestResult(true,
				"Header properly invoked the RESTORE_VIEW phase including calling its before/after listeners and didnt' execute any other action phases.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"Header incorrectly executed an action phase/listener post RESTORE_VIEW: lastBeforePhase: " +
				lastBeforePhaseId.toString() + " lastAfterPhase: " + lastAfterPhaseId.toString());

			return Constants.TEST_FAILED;
		}
	}
}
