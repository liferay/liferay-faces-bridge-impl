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

import java.util.List;
import java.util.Locale;

import org.junit.Assert;

import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is designed to test FACES issue portlets that simply output a pass/fail result (potentially after some
 * element has been clicked). This class is designed to be similar to TCKTestCase.java so that it can be reused to test
 * many different portlets (each of which contains the complex test code in the portlet itself).
 *
 * @author  Kyle Stiemann
 */
public abstract class SimpleFACESPortletTester extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SimpleFACESPortletTester.class);

	public void runSimpleFACESPortletTest(String testPage) {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL(testPage));

		String testPageUpperCase = testPage.toUpperCase(Locale.ENGLISH);
		String resultStatusXpath = "//*[@id='" + testPageUpperCase + "-result-status']";
		List<WebElement> resultStatusElements = browserDriver.findElementsByXpath(resultStatusXpath);

		if (resultStatusElements.isEmpty()) {

			List<WebElement> testActionElements = browserDriver.findElementsByXpath(
					"//*[contains(@class,'testAction')]");

			if (!testActionElements.isEmpty()) {

				testActionElements.get(0).click();
				browserDriver.waitForElementDisplayed(resultStatusXpath);
			}
		}

		String resultStatus = "FAILURE";
		String resultDetail = "No test results appeared on the page.";
		resultStatusElements = browserDriver.findElementsByXpath(resultStatusXpath);

		if (!resultStatusElements.isEmpty()) {

			resultDetail = "No test result details appeared on the page.";
			resultStatus = resultStatusElements.get(0).getText();

			List<WebElement> resultDetailElements = browserDriver.findElementsByXpath("//*[@id='" + testPageUpperCase +
					"-result-detail']");

			if (!resultDetailElements.isEmpty()) {
				resultDetail = resultDetailElements.get(0).getText();
			}
		}

		Assert.assertEquals(resultDetail, "SUCCESS", resultStatus);
		logger.info("{0} test passed: {1}", testPage, resultDetail);
	}
}
