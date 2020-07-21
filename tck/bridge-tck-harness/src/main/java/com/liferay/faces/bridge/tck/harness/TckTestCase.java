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
package com.liferay.faces.bridge.tck.harness;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized.Parameters;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;


/**
 * @author  Michael Freedman
 * @author  Kyle Stiemann
 */
@RunWith(TckParameterized.class)
public class TckTestCase extends BrowserDriverManagingTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(TckTestCase.class);

	// Private XPath
	private static final String TEST_RESULT_STATUS_XPATH_TEMPLATE = "//span[@id=\"{0}-result-status\"]";
	private static final String TEST_RESULT_DETAIL_XPATH_TEMPLATE = "//span[@id=\"{0}-result-detail\"]";
	private static final String TEST_RESULT_STATUS_XPATH = "//p[contains (.,\"Status\")]";
	private static final String TEST_RESULT_AJAX_STATUS_XPATH = "//span[contains(.,\"{0}\")]/p[contains(.,\"Status\")]";
	private static final String TEST_IFRAME_XPATH = "//iframe[@name=\"tck-iframe\"]";
	private static final String[] RUN_TEST_XPATHS = {
			"//input[not(@class=\"run-ppr-button\") and @type=\"submit\" and @value=\"Run Test\"]",
			"//a[not(@class=\"ppr-redisplay-link\") and text()=\"Run Test\"]"
		};
	private static final String[] RUN_AJAX_TEST_XPATHS = {
			"//input[@class=\"run-ppr-button\" and @type=\"submit\" and @value=\"Run Test\"]",
			"//a[@class=\"ppr-redisplay-link\" and contains(.,\"Run Test\")]"
		};

	// Private Constants
	private static final String PORTLET_CONTAINER = TestUtil.getContainer("pluto");
	private static final String TESTS_FILE_PATH = TestUtil.getSystemPropertyOrDefault("integration.tests.file",
			"/" + PORTLET_CONTAINER + "-tests.xml");
	private static final String EXCLUDED_TESTS_FILE_PATH = TestUtil.getSystemPropertyOrDefault(
			"integration.excluded.tests.file", "/excluded-tests.xml");
	private static final String TCK_CONTEXT;
	private static final int DEFAULT_BROWSER_WAIT_TIMEOUT;
	private static final int MAX_NUMBER_ACTIONS = 10;

	static {

		String tckContext = "/";
		int defaultBrowserWaitTimeout = 5;

		if (PORTLET_CONTAINER.contains("pluto")) {
			tckContext = TestUtil.DEFAULT_PLUTO_CONTEXT + "/";
			defaultBrowserWaitTimeout = 1;
		}

		TCK_CONTEXT = tckContext;
		DEFAULT_BROWSER_WAIT_TIMEOUT = defaultBrowserWaitTimeout;
	}

	// Private Data Members
	private String testModule;
	private String pageName;
	private String testName;
	private String testPortletName;

	public TckTestCase(String pageName, String testName, String testPortletName, String testModule) {
		this.pageName = pageName;
		this.testName = testName;
		this.testPortletName = testPortletName;
		this.testModule = testModule;
	}

	public static void main(String[] args) {

		// junit.textui.TestRunner.run(suite());
		new org.junit.runner.JUnitCore().runClasses(TckTestCase.class);
	}

	@Parameters
	public static Collection testData() {

		logger.info("testData()");

		List<String[]> testList = new ArrayList<String[]>(200);
		InputStream excludedTestsInputStream = null;
		InputStream testsInputStream = null;

		try {
			excludedTestsInputStream = TckTestCase.class.getResourceAsStream(EXCLUDED_TESTS_FILE_PATH);
		}
		catch (Exception e) {

			logger.error("Unable to acccess test exclusions file, {} Exception thrown: {}", EXCLUDED_TESTS_FILE_PATH,
				e.getMessage());
			System.exit(1);
		}

		Properties exProps = new Properties();

		try {
			exProps.loadFromXML(excludedTestsInputStream);
		}
		catch (Exception e) {

			logger.error("Unable to parse test exclusions file, {} Exception thrown: {}", EXCLUDED_TESTS_FILE_PATH,
				e.getMessage());
			System.exit(1);
		}

		try {
			testsInputStream = TckTestCase.class.getResourceAsStream(TESTS_FILE_PATH);
		}
		catch (Exception e) {

			logger.error("Unable to acccess test description file, {} Exception thrown: {}", TESTS_FILE_PATH,
				e.getMessage());
			System.exit(1);
		}

		Properties testProps = new Properties();

		try {
			testProps.loadFromXML(testsInputStream);
		}
		catch (Exception e) {

			logger.error("Unable to parse test description file, {} Exception thrown: {}", TESTS_FILE_PATH,
				e.getMessage());
			System.exit(1);
		}

		Enumeration<?> tests = testProps.propertyNames();
		Pattern extractTestNamePattern = Pattern.compile("chapter[0-9_]+[a-zA-Z]*Tests-([_a-zA-Z0-9]+)-portlet");
		Pattern testFilterPattern = null;
		String testFilter = TestUtil.getSystemPropertyOrDefault("integration.test.filter", null);

		if (testFilter != null) {
			testFilterPattern = Pattern.compile(testFilter);
		}

		while (tests.hasMoreElements()) {

			String page = (String) tests.nextElement();
			String propertyValue = testProps.getProperty(page);
			String[] propertyValues = propertyValue.split("[|]");
			String testPortletName = propertyValues[0];
			boolean testEnabled = Boolean.valueOf(propertyValues[1]);
			String testModule = propertyValues[2];
			String testName = extractTestNamePattern.matcher(testPortletName).replaceFirst("$1");

			if (testEnabled && (exProps.getProperty(testName) == null) &&
					((testFilterPattern == null) || testFilterPattern.matcher(testName).matches())) {
				testList.add(new String[] { page, testName, testPortletName, testModule });
			}
		}

		return testList;
	}

	public String getName() {
		return testName;
	}

	public String getPageName() {
		return pageName;
	}

	public String getTestModule() {
		return testModule;
	}

	public String getTestName() {
		return testName;
	}

	public String getTestPortletName() {
		return testPortletName;
	}

	@Before
	public void runBeforeEachTest() throws Exception {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + TCK_CONTEXT + pageName);
	}

	@Test
	public void testPage() {

		BrowserDriver browserDriver = getBrowserDriver();
		testPage(browserDriver, true);
	}

	@Override
	protected void doSetUp() {

		super.doSetUp();

		BrowserDriver browserDriver = getBrowserDriver();
		int browserDriverWaitTimeOut = TestUtil.getBrowserDriverWaitTimeOut(DEFAULT_BROWSER_WAIT_TIMEOUT);
		browserDriver.setWaitTimeOut(browserDriverWaitTimeOut);
	}

	protected void runAfterEachFullPageTestAction() {
		// no-op
	}

	private boolean areElementsDisplayed(BrowserDriver browserDriver, String... elementXpaths) {

		boolean elementDisplayed = false;

		for (String elementXpath : elementXpaths) {

			List<WebElement> elements = browserDriver.findElementsByXpath(elementXpath);

			if (elements.size() > 0) {

				if (elements.get(0).isDisplayed()) {

					elementDisplayed = true;

					break;
				}
			}
		}

		return elementDisplayed;
	}

	private void failTckTestCase(String message, BrowserDriver browserDriver) {
		failTckTestCase(message, null, browserDriver);
	}

	private void failTckTestCase(String message, Exception e, BrowserDriver browserDriver) {
		throw new AssertionError(getTckTestCaseFailureMessage(message, e, browserDriver));
	}

	private String getTckTestCaseFailureMessage(String message, BrowserDriver browserDriver) {
		return getTckTestCaseFailureMessage(message, null, browserDriver);
	}

	private String getTckTestCaseFailureMessage(String message, Exception e, BrowserDriver browserDriver) {

		StringBuilder sb = new StringBuilder();
		sb.append(pageName);
		sb.append(": ");
		sb.append(testName);
		sb.append(":\n");
		sb.append(message);

		if (e != null) {
			sb.append(e.getMessage());
		}

		if (logger.isDebugEnabled()) {

			sb.append("\n");

			String pageSource = browserDriver.getCurrentDocumentMarkup().replaceAll("(\\s)+", "$1");
			sb.append(pageSource);
			sb.append("\n");
		}

		return sb.toString();
	}

	private void loadImagesIfNecessary(BrowserDriver browserDriver) {

		if (testName.equals("nonFacesResourceTest")) {
			browserDriver.loadCurrentWindowImages();
		}
	}

	private void recordResult(BrowserDriver browserDriver) {

		String details = null;
		String testStatus = null;

		// Look for results that use the tck span ids
		String tckResultXPath = MessageFormat.format(TEST_RESULT_STATUS_XPATH_TEMPLATE, new Object[] { testName });

		if (areElementsDisplayed(browserDriver, tckResultXPath)) {

			try {

				WebElement resultElement = browserDriver.findElementByXpath(tckResultXPath);
				testStatus = resultElement.getText();

				String tckDetailsXPath = MessageFormat.format(TEST_RESULT_DETAIL_XPATH_TEMPLATE,
						new Object[] { testName });
				WebElement detailsElement = browserDriver.findElementByXpath(tckDetailsXPath);
				details = detailsElement.getText();
			}
			catch (WebDriverException e) {

				// Invalid result format
				String failureMessage = "Test failed but no test result details found.\n";
				failTckTestCase(failureMessage, e, browserDriver);
			}
		}
		else {

			// Ajax test content with no TCK span ids.
			if ((areElementsDisplayed(browserDriver, "//p[contains (.,\"FAILED\")]"))) {

				// Portlet shows a failure result so display details
				try {

					WebElement detailsElement = browserDriver.findElementByXpath("//p[contains(.,\"Detail\")]//p[1]");
					details = detailsElement.getText();
				}
				catch (WebDriverException e) {

					// Portlet contains no detail section, should never happen.
					String failureMessage = "Test failed but no test result details found.\n";
					failTckTestCase(failureMessage, e, browserDriver);
				}
			}
			else if (areElementsDisplayed(browserDriver, "//p[contains (.,\"SUCCESS\")]")) {
				testStatus = "SUCCESS";
			}
		}

		String failureMessage = "FAILED: " + details;
		failureMessage = getTckTestCaseFailureMessage(failureMessage, browserDriver);
		Assert.assertEquals(failureMessage, "SUCCESS", testStatus);
	}

	private void runAjaxTest(BrowserDriver browserDriver) {

		boolean resultObtained = false;

		// Wait for a result or another control to be rendered.
		for (int i = 0; i < MAX_NUMBER_ACTIONS; i++) {

			for (String xpath : RUN_AJAX_TEST_XPATHS) {

				if (areElementsDisplayed(browserDriver, xpath)) {

					browserDriver.clickElement(xpath);
					switchToIFrameIfNecessary(browserDriver);

					String[] resultStatusXpaths = new String[] {
							TEST_RESULT_AJAX_STATUS_XPATH, TEST_RESULT_STATUS_XPATH
						};

					for (String resultStatusXpath : resultStatusXpaths) {

						try {
							String resultXpath = MessageFormat.format(resultStatusXpath, new Object[] { testName });
							browserDriver.waitForElementDisplayed(resultXpath);
							recordResult(browserDriver);
							resultObtained = true;

							break;
						}
						catch (TimeoutException e) {
							// continue.
						}
					}
				}
			}

			if (!resultObtained && !areElementsDisplayed(browserDriver, RUN_AJAX_TEST_XPATHS)) {

				String failureMessage = (i + 1) + " ajax action(s) executed with no valid result.";
				failTckTestCase(failureMessage, browserDriver);
			}
		}

		if (!resultObtained) {

			String failureMessage = MAX_NUMBER_ACTIONS +
				" actions have been performed on this portlet without any final result.";
			failTckTestCase(failureMessage, browserDriver);
		}
	}

	private void runTest(BrowserDriver browserDriver) {

		boolean resultObtained = false;

		for (int i = 0; i < MAX_NUMBER_ACTIONS; i++) {

			for (String xpath : RUN_TEST_XPATHS) {

				if (areElementsDisplayed(browserDriver, xpath)) {

					WebElement webElement = browserDriver.findElementByXpath(xpath);
					browserDriver.clickElement(xpath);
					browserDriver.waitFor(ExpectedConditions.stalenessOf(webElement));
					browserDriver.waitForElementDisplayed("//body");
					loadImagesIfNecessary(browserDriver);
					switchToIFrameIfNecessary(browserDriver);

					// If results page shows record result.
					if (areElementsDisplayed(browserDriver, TEST_RESULT_STATUS_XPATH)) {

						recordResult(browserDriver);
						resultObtained = true;
					}
					else {
						runAfterEachFullPageTestAction();
					}
					// Otherwise continue clicking on elements.
				}
			}

			if (!resultObtained && !areElementsDisplayed(browserDriver, RUN_TEST_XPATHS)) {

				String failureMessage = (i + 1) +
					" full page request action(s) have been performed on this portlet without any final result or test components to exercise.";
				failTckTestCase(failureMessage, browserDriver);
			}
		}

		if (!resultObtained) {

			String failureMessage = MAX_NUMBER_ACTIONS +
				" actions have been performed on this portlet without any final result.";
			failTckTestCase(failureMessage, browserDriver);
		}
	}

	private void switchToIFrameIfNecessary(BrowserDriver browserDriver) {

		if (areElementsDisplayed(browserDriver, TEST_IFRAME_XPATH)) {

			browserDriver.switchToFrame(TEST_IFRAME_XPATH);
			browserDriver.waitForElementDisplayed("//body");
		}
	}

	private void testPage(BrowserDriver browserDriver, boolean switchToIFrameIfNecessary) {

		try {

			if (areElementsDisplayed(browserDriver, TEST_RESULT_STATUS_XPATH)) {
				recordResult(browserDriver);
			}
			else if (areElementsDisplayed(browserDriver, RUN_TEST_XPATHS)) {
				runTest(browserDriver);
			}
			else if (areElementsDisplayed(browserDriver, RUN_AJAX_TEST_XPATHS)) {
				runAjaxTest(browserDriver);
			}
			else if (switchToIFrameIfNecessary && areElementsDisplayed(browserDriver, TEST_IFRAME_XPATH)) {

				browserDriver.switchToFrame(TEST_IFRAME_XPATH);
				browserDriver.waitForElementDisplayed("//body");
				testPage(browserDriver, false);
			}
			else {

				// Default case, unrecognised page content.
				failTckTestCase("Unexpected page content.", browserDriver);
			}
		}
		catch (WebDriverException e) {
			failTckTestCase("Uncaught WebDriverException: ", e, browserDriver);
		}
	}
}
