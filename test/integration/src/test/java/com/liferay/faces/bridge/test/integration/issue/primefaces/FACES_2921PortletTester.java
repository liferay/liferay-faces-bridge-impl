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
package com.liferay.faces.bridge.test.integration.issue.primefaces;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class FACES_2921PortletTester extends BrowserDriverManagingTesterBase {

	@Test
	public void runFACES_2921PortletTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-2921"));

		String renderKitIdXpath = "//code[@id='renderKitId']";
		browserDriver.waitForElementDisplayed(renderKitIdXpath);

		String renderKitId = browserDriver.findElementByXpath(renderKitIdXpath).getText();
		boolean primeFacesMobile = "PRIMEFACES_MOBILE".equals(renderKitId);
		Assert.assertTrue(primeFacesMobile || "HTML_BASIC".equals(renderKitId));
		Assume.assumeTrue("Skipping PrimeFaces Mobile test since PrimeFaces Mobile is not available", primeFacesMobile);
		browserDriver.sendKeysToElement("//input[contains(@id,':text')]", "text");
		browserDriver.clickElement("//div[contains(@id,':switch')]");
		browserDriver.sendKeysToElement("//input[contains(@id,':slider2')]", "10");
		browserDriver.sendKeysToElement("//input[contains(@id,':slider1')]", "5");
		browserDriver.clickElementAndWaitForRerender("//button[contains(@id,'submit')]");

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("text", "//span[contains(@id,':textOutput')]");
		waitingAsserter.assertTextPresentInElement("true", "//span[contains(@id,':switchOutput')]");
		waitingAsserter.assertTextPresentInElement("5", "//span[contains(@id,':slider1Output')]");
		waitingAsserter.assertTextPresentInElement("10", "//span[contains(@id,':slider2Output')]");
	}
}
