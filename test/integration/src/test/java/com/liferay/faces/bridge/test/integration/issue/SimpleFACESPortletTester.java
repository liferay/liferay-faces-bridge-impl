/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
import java.util.Locale;

import org.junit.Assert;

import org.openqa.selenium.WebElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;


/**
 * This class is designed to test FACES issue portlets that simply output a pass/fail result (potentially after some
 * element has been clicked). This class is designed to be similar to TCKTestCase.java so that it can be reused to test
 * many different portlets (each of which contains the complex test code in the portlet itself).
 *
 * @author  Kyle Stiemann
 */
public abstract class SimpleFACESPortletTester extends BrowserDriverManagingTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SimpleFACESPortletTester.class);

	// Protected Constants
	protected static final String TEST_ACTION_XPATH = "//*[contains(@class,'testAction')]";

	protected static String getResultStatusXpath(String testPage) {
		return getResultXpath(testPage, true);
	}

	protected static String getResultXpath(String testPage, boolean status) {

		String testPageUpperCase = testPage.toUpperCase(Locale.ENGLISH);
		String resultIdSuffix = "status";

		if (!status) {
			resultIdSuffix = "detail";
		}

		return "//div[contains(@class,'liferay-faces-bridge-body')]//*[@id='" + testPageUpperCase + "-result-" +
			resultIdSuffix + "']";
	}

	protected static boolean isElementDisplayed(BrowserDriver browserDriver, String xpath) {

		List<WebElement> elements = browserDriver.findElementsByXpath(xpath);

		return !elements.isEmpty() && elements.get(0).isDisplayed();
	}

	private static boolean isResultsDisplayed(List<WebElement> resultStatusElements) {
		return !resultStatusElements.isEmpty() && resultStatusElements.get(0).isDisplayed();
	}

	public final void runSimpleFACESPortletTest(String testPage) {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL(testPage));
		runSimpleFACESPortletTest(browserDriver, testPage);
	}

	public final void runSimpleFACESPortletTest(BrowserDriver browserDriver, String testPage) {

		String resultStatusXpath = getResultStatusXpath(testPage);

		if (!isElementDisplayed(browserDriver, resultStatusXpath)) {

			List<WebElement> testActionElements = browserDriver.findElementsByXpath(TEST_ACTION_XPATH);

			if (!testActionElements.isEmpty()) {

				testActionElements.get(0).click();
				browserDriver.waitForElementDisplayed(resultStatusXpath);
			}
		}

		String resultStatus = "FAILURE";
		String resultDetail = "No test results appeared on the page.";
		List<WebElement> resultStatusElements = browserDriver.findElementsByXpath(resultStatusXpath);

		if (isResultsDisplayed(resultStatusElements)) {

			resultDetail = "No test result details appeared on the page.";
			resultStatus = resultStatusElements.get(0).getText();

			List<WebElement> resultDetailElements = browserDriver.findElementsByXpath(getResultXpath(testPage, false));

			if (!resultDetailElements.isEmpty()) {
				resultDetail = resultDetailElements.get(0).getText();
			}
		}

		Assert.assertEquals(resultDetail, "SUCCESS", resultStatus);
		logger.info("{} test passed: {}", testPage, resultDetail);
	}
}
