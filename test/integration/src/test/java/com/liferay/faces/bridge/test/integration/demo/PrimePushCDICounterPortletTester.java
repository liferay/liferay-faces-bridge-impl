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
package com.liferay.faces.bridge.test.integration.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;
import com.liferay.faces.test.selenium.webdriver.WebDriverFactory;


/**
 * @author  Kyle Stiemann
 */
public class PrimePushCDICounterPortletTester extends IntegrationTesterBase {

	// Private Data Members
	private BrowserDriver browserDriver2;

	@Test
	public void runPrimePushCDICounterPortletTest() {

		BrowserDriver browserDriver1 = getBrowserDriver();
		String primePushCDICounterURL = BridgeTestUtil.getDemoPageURL("primepush-cdi-counter");
		browserDriver1.navigateWindowTo(primePushCDICounterURL);
		browserDriver2.navigateWindowTo(primePushCDICounterURL);

		// Test that the initial count value is the same in both browsers.
		String countDisplayXpath = "//span[contains(@id,':countDisplay')]";
		WebElement countDisplayElement = browserDriver1.findElementByXpath(countDisplayXpath);
		String countString = countDisplayElement.getText().trim();
		BrowserStateAsserter browserStateAsserter2 = newBrowserStateAsserter(browserDriver2);
		browserStateAsserter2.assertTextPresentInElement(countString, countDisplayXpath);

		int count = Integer.parseInt(countString);

		// Click the increment button in the first browser and test that the count has been incremented in both
		// browsers.
		String incrementCounterButtonXpath = "//button[contains(@id,':incrementCounter')]";
		browserDriver1.clickElement(incrementCounterButtonXpath);
		count++;

		BrowserStateAsserter browserStateAsserter1 = getBrowserStateAsserter();
		countString = Integer.toString(count);
		browserStateAsserter1.assertTextPresentInElement(countString, countDisplayXpath);
		browserStateAsserter2.assertTextPresentInElement(countString, countDisplayXpath);

		// Click the increment button in the second browser and test that the count has been incremented in both
		// browsers.
		browserDriver2.clickElement(incrementCounterButtonXpath);
		count++;
		countString = Integer.toString(count);
		browserStateAsserter1.assertTextPresentInElement(countString, countDisplayXpath);
		browserStateAsserter2.assertTextPresentInElement(countString, countDisplayXpath);

		// Click the increment button 10 times in the first browser and test that the count is accurate in both
		// browsers.
		for (int i = 0; i < 10; i++) {

			browserDriver1.clickElement(incrementCounterButtonXpath);
			count++;
		}

		countString = Integer.toString(count);
		browserStateAsserter1.assertTextPresentInElement(countString, countDisplayXpath);
		browserStateAsserter2.assertTextPresentInElement(countString, countDisplayXpath);
	}

	@Before
	public void setUpBrowserDriver2() {

		String browserName = TestUtil.getSystemPropertyOrDefault("integration.browser.name", "chrome");
		String defaultBrowserHeadlessString = "true";

		if ("firefox".equals(browserName)) {
			defaultBrowserHeadlessString = "false";
		}

		String browserHeadlessString = TestUtil.getSystemPropertyOrDefault("integration.browser.headless",
				defaultBrowserHeadlessString);
		boolean browserHeadless = Boolean.parseBoolean(browserHeadlessString);
		String browserSimulateMobileString = TestUtil.getSystemPropertyOrDefault("integration.browser.simulate.mobile",
				"false");
		boolean browserSimulateMobile = Boolean.parseBoolean(browserSimulateMobileString);
		WebDriver webDriver = WebDriverFactory.getWebDriver(browserName, browserHeadless, browserSimulateMobile);
		browserDriver2 = newBrowserDriver(webDriver, browserHeadless, browserSimulateMobile);
		signIn(browserDriver2);
	}

	@After
	public void tearDownBrowserDriver2() {

		if (browserDriver2 != null) {

			browserDriver2.quit();
			browserDriver2 = null;
		}
	}
}
