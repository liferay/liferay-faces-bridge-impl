/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_6;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.lifecycle.ClientWindow;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class Tests {

	// Test 6.162
	@BridgeTest(test = "clientWindowTest")
	public String clientWindowTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		ClientWindow origClientWindow = externalContext.getClientWindow();
		ClientWindow clientWindow = new ClientWindowTCKImpl(origClientWindow);

		externalContext.setClientWindow(clientWindow);

		if (clientWindow.equals(externalContext.getClientWindow())) {
			testBean.setTestResult(true,
				"externalContext.getClientWindow() correctly returned the same value that was passed to ExternalContext.setClientWindow(ClientWindow).");
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getClientWindow() incorrectly returned a different value than was passed to ExternalContext.setClientWindow(ClientWindow).");
		}

		externalContext.setClientWindow(origClientWindow);

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;
		}
	}
}
