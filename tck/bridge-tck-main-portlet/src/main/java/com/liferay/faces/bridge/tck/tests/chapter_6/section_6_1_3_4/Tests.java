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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_4;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.portlet.PortletSession;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class Tests {

	// Test 6.157
	@BridgeTest(test = "getSessionMaxInactiveIntervalTest")
	public String getSessionMaxInactiveIntervalTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		PortletSession portletSession = (PortletSession) externalContext.getSession(true);

		if (portletSession.getMaxInactiveInterval() == externalContext.getSessionMaxInactiveInterval()) {
			testBean.setTestResult(true, "ExternalContext.getSessionMaxInactiveInterval() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getSessionMaxInactiveInterval() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

	// Test 6.159
	@BridgeTest(test = "isSecureTest")
	public String isSecureTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		if (externalContext.isSecure()) {
			testBean.setTestResult(true, "ExternalContext.isSecure() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.isSecure() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

	// Test 6.158
	@BridgeTest(test = "setSessionMaxInactiveIntervalTest")
	public String setSessionMaxInactiveIntervalTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		int sessionMaxInactiveInterval = externalContext.getSessionMaxInactiveInterval() + 1;

		externalContext.setSessionMaxInactiveInterval(sessionMaxInactiveInterval);

		if (sessionMaxInactiveInterval == externalContext.getSessionMaxInactiveInterval()) {
			testBean.setTestResult(true, "ExternalContext.setSessionMaxInactiveInterval() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.setSessionMaxInactiveInterval() returned an incorrect value");

		return Constants.TEST_FAILED;
	}
}
