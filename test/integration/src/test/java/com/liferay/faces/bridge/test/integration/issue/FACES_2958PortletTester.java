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
package com.liferay.faces.bridge.test.integration.issue;

import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public class FACES_2958PortletTester extends SimpleFACESPortletTester {

	// Private Constants
	private static final String TEST_PAGE_LINKS_XPATH =
		"//div[contains(@class,'liferay-faces-bridge-body')]//a[not(contains(text(),'FACES-2958'))]";

	@Test
	public void runFACES_2958PortletTest() {
		String container = TestUtil.getContainer();

		Assume.assumeTrue(container.startsWith("liferay"));

		BrowserDriver browserDriver = getBrowserDriver();
		runFACES_2958PortletTest(browserDriver, "faces-2958");
		runFACES_2958PortletTest(browserDriver, "faces-2958-friendlyURL");
	}

	private void runFACES_2958PortletTest(BrowserDriver browserDriver, String testPortlet) {

		String issuePageURL = BridgeTestUtil.getIssuePageURL(testPortlet);
		browserDriver.navigateWindowTo(issuePageURL);

		List<WebElement> linkElements = browserDriver.findElementsByXpath(TEST_PAGE_LINKS_XPATH);

		if (linkElements.isEmpty()) {
			throw new RuntimeException("No test links founds.");
		}

		for (int i = 1; i <= linkElements.size(); i++) {

			browserDriver.navigateWindowTo(issuePageURL);
			browserDriver.clickElement("(" + TEST_PAGE_LINKS_XPATH + ")[" + i + "]");

			String resultStatusXpath = getResultStatusXpath("faces-2958");

			if (!(isElementDisplayed(browserDriver, resultStatusXpath) ||
						isElementDisplayed(browserDriver, TEST_ACTION_XPATH))) {
				browserDriver.waitForElementEnabled("(" + TEST_ACTION_XPATH + "|" + resultStatusXpath + ")");
			}

			runSimpleFACESPortletTest(browserDriver, "faces-2958");
		}
	}
}
