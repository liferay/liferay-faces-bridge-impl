/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_5;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class Tests {

	// Test 6.160
	@BridgeTest(test = "getApplicationContextPathTest")
	public String getApplicationContextPathTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> applicationMap = externalContext.getApplicationMap();
		String tckInitApplicationContextPath = (String) applicationMap.get("tckInitApplicationContextPath");

		if (externalContext.getApplicationContextPath().equals(tckInitApplicationContextPath)) {
			testBean.setTestResult(true, "ExternalContext.getApplicationContextPathTest() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getApplicationContextPathTest() returned an incorrect value");

		return Constants.TEST_FAILED;
	}
}
