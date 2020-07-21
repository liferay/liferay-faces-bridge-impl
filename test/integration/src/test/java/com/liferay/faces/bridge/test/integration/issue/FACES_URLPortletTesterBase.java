/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assume;
import org.junit.BeforeClass;

import org.openqa.selenium.WebElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import static com.liferay.faces.bridge.test.integration.issue.SimpleFACESPortletTester.getResultStatusXpath;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;

import junit.runner.BaseTestRunner;


/**
 * @author  Kyle Stiemann
 */
public class FACES_URLPortletTesterBase extends SimpleFACESPortletTester {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FACES_URLPortletTesterBase.class);

	// Private Constants
	private static final String TEST_PAGE_LINKS_XPATH_TEMPLATE =
		"//div[contains(@class,'liferay-faces-bridge-body')]//a[not(contains(text(),'{0}'))]";

	@BeforeClass
	public static void allowFullLengthTestResultMessages() {
		BaseTestRunner.setPreference("maxmessage", "-1");
	}

	private static void appendTestLinkText(StringBuilder testResults, String testLinkText) {

		if (testResults.length() > 0) {
			testResults.append(", ");
		}

		testResults.append("\"");
		testResults.append(testLinkText);
		testResults.append("\"");
	}

	private static String getTestResultMessage(StringBuilder failureTestResults, StringBuilder errorTestResults) {

		StringBuilder stringBuilder = new StringBuilder();

		if (failureTestResults.length() > 0) {

			stringBuilder.append("The following test links failed: ");
			stringBuilder.append(failureTestResults);
		}

		if (errorTestResults.length() > 0) {

			stringBuilder.append("\nThe following test links threw an error: ");
			stringBuilder.append(errorTestResults);
		}

		return stringBuilder.toString();
	}

	public final void runFACES_URLPortletTest(String facesIssue) {

		String container = TestUtil.getContainer();
		Assume.assumeTrue(container.startsWith("liferay"));

		BrowserDriver browserDriver = getBrowserDriver();
		Map<String, Throwable> testResults = new LinkedHashMap<String, Throwable>();
		testResults.putAll(getFACES_URLPortletTestResults(browserDriver, facesIssue, false));
		testResults.putAll(getFACES_URLPortletTestResults(browserDriver, facesIssue, true));

		if (!testResults.isEmpty()) {

			boolean error = false;
			Set<String> keySet = testResults.keySet();
			StringBuilder errorTestResults = new StringBuilder();
			StringBuilder failureTestResults = new StringBuilder();

			for (String key : keySet) {

				Throwable t = testResults.get(key);
				String messagePrefix = "Failure: ";

				if (t instanceof AssertionError) {
					appendTestLinkText(failureTestResults, key);
				}
				else {

					error = true;
					messagePrefix = "Error: ";
					appendTestLinkText(errorTestResults, key);
				}

				logger.error(messagePrefix + "\"" + key + "\" link test:", t);
			}

			if (error) {
				throw new RuntimeException(getTestResultMessage(failureTestResults, errorTestResults));
			}
			else {
				throw new AssertionError(getTestResultMessage(failureTestResults, errorTestResults));
			}
		}
	}

	private Map<String, Throwable> getFACES_URLPortletTestResults(BrowserDriver browserDriver, String facesIssue,
		boolean friendlyURL) {

		facesIssue = facesIssue.toLowerCase(Locale.ENGLISH);

		String testPortlet = facesIssue;

		if (friendlyURL) {
			testPortlet += "-friendlyURL";
		}

		String issuePageURL = BridgeTestUtil.getIssuePageURL(testPortlet);
		browserDriver.navigateWindowTo(issuePageURL);

		String testPageLinksXpath = TEST_PAGE_LINKS_XPATH_TEMPLATE.replace("{0}",
				facesIssue.toUpperCase(Locale.ENGLISH));
		List<WebElement> linkElements = browserDriver.findElementsByXpath(testPageLinksXpath);

		if (linkElements.isEmpty()) {
			throw new RuntimeException("No test links founds.");
		}

		Map<String, Throwable> testResults = new LinkedHashMap<String, Throwable>();

		for (int i = 1; i <= linkElements.size(); i++) {

			browserDriver.navigateWindowTo(issuePageURL);

			String testPageLinkXpath = "(" + testPageLinksXpath + ")[" + i + "]";
			WebElement testPageLink = browserDriver.findElementByXpath(testPageLinkXpath);
			String testLinkName = testPageLink.getText();

			try {

				browserDriver.clickElement(testPageLinkXpath);

				String resultStatusXpath = getResultStatusXpath(facesIssue);

				if (!(isElementDisplayed(browserDriver, resultStatusXpath) ||
							isElementDisplayed(browserDriver, TEST_ACTION_XPATH))) {
					browserDriver.waitForElementEnabled("(" + TEST_ACTION_XPATH + "|" + resultStatusXpath + ")");
				}

				runSimpleFACESPortletTest(browserDriver, facesIssue);
			}
			catch (Throwable t) {

				String key = testLinkName;

				if (friendlyURL) {
					key += " friendlyURL";
				}

				testResults.put(key, t);
			}
		}

		return testResults;
	}
}
