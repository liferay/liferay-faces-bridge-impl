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
package com.liferay.faces.bridge.test.integration.issue;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;


/**
 * @author  Kyle Stiemann
 */
public class FACES_224PortletTester extends BrowserDriverManagingTesterBase {

	@Test
	public void runFACES_224PortletTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-224"));
		browserDriver.waitForTextPresentInElement("This is view1.xhtml",
			"//div[contains(@class, 'liferay-faces-bridge')]");
		browserDriver.clickElement("//input[contains(@value,'view2.xhtml')]");
		browserDriver.waitForTextPresentInElement("This is view2.xhtml",
			"//div[contains(@class, 'liferay-faces-bridge')]");

		String viewParamValue1 = browserDriver.findElementByXpath("//span[contains(@id,':viewParamValue1')]").getText();
		String viewParam1 = browserDriver.findElementByXpath("//span[contains(@id,':viewParam1')]").getText();
		Assert.assertTrue("View ", viewParam1.endsWith(viewParamValue1));

		String viewParamValue2 = browserDriver.findElementByXpath("//span[contains(@id,':viewParamValue2')]").getText();
		String viewParam2 = browserDriver.findElementByXpath("//span[contains(@id,':viewParam2')]").getText();
		Assert.assertTrue("", viewParam2.endsWith(viewParamValue2));
	}
}
