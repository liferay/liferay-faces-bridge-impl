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
package com.liferay.faces.bridge.tck.tests.chapter_9.section_9_1;

import jakarta.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class Tests {

	// Test 9.1
	@BridgeTest(test = "body1Test")
	public String body1Test(TestBean testBean) {

		// Test is basically a no-op since the TCK framework requires a 1::1 mapping between a test portlet and an
		// annotated test bean method.
		return "";
	}

	// Test 9.1
	@BridgeTest(test = "body2Test")
	public String body2Test(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (isResourcePhase(facesContext)) {

			testBean.setTestResult(false,
				"This is a placeholder that should be replaced by the jsf.ajax event handler in resourceAjaxBodyResult.xhtml");

			return Constants.TEST_FAILED;
		}

		return "";
	}

	public boolean isResourcePhase(FacesContext facesContext) {
		return (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE);
	}
}
