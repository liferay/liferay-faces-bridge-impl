/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_2_1;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.portlet.faces.annotation.PortletNamingContainer;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests {

	// Test is SingleRequest -- Render
	// Test #6.88
	@BridgeTest(test = "hasRenderContentAfterViewAttributeTest")
	public String hasRenderContentAfterViewAttributeTest(TestBean testBean) {

		testBean.setTestComplete(true);
		testBean.setTestResult(true,
			"This test is no longer necessary due to <a href=\"https://issues.liferay.com/browse/FACES-2613\">FACES-2613</a>");

		return Constants.TEST_SUCCESS;
	}

	// Test is SingleRequest -- Render
	// Test #6.132
	@BridgeTest(test = "implementsBridgeWriteBehindResponseTest")
	public String implementsBridgeWriteBehindResponseTest(TestBean testBean) {

		testBean.setTestComplete(true);
		testBean.setTestResult(true,
			"This test is no longer necessary due to <a href=\"https://issues.liferay.com/browse/FACES-2613\">FACES-2613</a>");

		return Constants.TEST_SUCCESS;
	}

	// Test is SingleRequest -- Render
	// Test #6.86
	@BridgeTest(test = "isPortletNamingContainerTest")
	public String isPortletNamingContainerTest(TestBean testBean) {
		UIViewRoot uiViewRoot = FacesContext.getCurrentInstance().getViewRoot();
		testBean.setTestComplete(true);

		if (uiViewRoot.getClass().getAnnotation(PortletNamingContainer.class) != null) {
			testBean.setTestResult(true,
				"UIViewRoot is correctly annotated with javax.portlet.faces.annotation.PortletNamingContainer.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"UIViewRoot is not annotated with javax.portlet.faces.annotation.PortletNamingContainer.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render
	// Test #6.89
	@BridgeTest(test = "renderContentAfterViewTest")
	public String renderContentAfterViewTest(TestBean testBean) {

		testBean.setTestComplete(true);
		testBean.setTestResult(true, "Content Rendered After View.");

		return Constants.TEST_SUCCESS;
	}

	// Test is SingleRequest -- Render
	// Test #6.133
	@BridgeTest(test = "usesConfiguredRenderResponseWrapperTest")
	public String usesConfiguredRenderResponseWrapperTest(TestBean testBean) {
		testBean.setTestComplete(true);

		// test is run/checked in the jsp which adds request attrs which we read here

		Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		Boolean b = (Boolean) requestMap.get("com.liferay.faces.bridge.TCK.status");

		if (b == null) {
			testBean.setTestResult(false,
				"Error in running the test.  The JSP for this test didn't set the expected request attributes indicating the test status");

			return Constants.TEST_FAILED;
		}
		else if (b.equals(Boolean.TRUE)) {
			testBean.setTestResult(true, (String) requestMap.get("com.liferay.faces.bridge.TCK.detail"));

			return Constants.TEST_SUCCESS;

		}
		else {
			testBean.setTestResult(false, (String) requestMap.get("com.liferay.faces.bridge.TCK.detail"));

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render
	// Test #6.134
	@BridgeTest(test = "usesConfiguredResourceResponseWrapperTest")
	public String usesConfiguredResourceResponseWrapperTest(TestBean testBean) {
		testBean.setTestComplete(true);

		// test is run/checked in the jsp which adds request attrs which we read here

		Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		Boolean b = (Boolean) requestMap.get("com.liferay.faces.bridge.TCK.status");

		if (b == null) {
			testBean.setTestResult(false,
				"Error in running the test.  The JSP for this test didn't set the expected request attributes indicating the test status");

			return Constants.TEST_FAILED;
		}
		else if (b.equals(Boolean.TRUE)) {
			testBean.setTestResult(true, (String) requestMap.get("com.liferay.faces.bridge.TCK.detail"));

			return Constants.TEST_SUCCESS;

		}
		else {
			testBean.setTestResult(false, (String) requestMap.get("com.liferay.faces.bridge.TCK.detail"));

			return Constants.TEST_FAILED;
		}
	}

}
