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

import org.junit.Test;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 */
public class FACES_2921PortletTester extends IntegrationTesterBase {

	@Test
	public void runFACES_2921PortletTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2921"));
		browser.sendKeys("//input[contains(@id,':text')]", "text");
		browser.click("//div[contains(@id,':switch')]");
		browser.sendKeys("//input[contains(@id,':slider2')]", "10");
		browser.sendKeys("//input[contains(@id,':slider1')]", "5");
		browser.clickAndWaitForAjaxRerender("//button[contains(@id,'submit')]");

		SeleniumAssert.assertElementTextVisible(browser, "//span[contains(@id,':textOutput')]", "text");
		SeleniumAssert.assertElementTextVisible(browser, "//span[contains(@id,':switchOutput')]", "true");
		SeleniumAssert.assertElementTextVisible(browser, "//span[contains(@id,':slider1Output')]", "5");
		SeleniumAssert.assertElementTextVisible(browser, "//span[contains(@id,':slider2Output')]", "10");
	}
}
