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
package com.liferay.faces.bridge.tck.harness;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized.Parameters;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitUtil;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;


/**
 * @author  Michael Freedman
 */
@RunWith(TckParameterized.class)
public class TckTestCase extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = Logger.getLogger(TckTestCase.class.getName());

	// XPath
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
	private static final String TEST_FILE_KEY = "integration.tests.file";
	private static final String EXCLUDED_TESTS_FILE_KEY = "integration.excluded.tests.file";
	private static final String PORTLET_CONTAINER = TestUtil.getContainer("liferay");
	private static final String TESTS_FILE_PATH = TestUtil.getSystemPropertyOrDefault(TEST_FILE_KEY,
			"/" + PORTLET_CONTAINER + "-tests.xml");
	private static final String EXCLUDED_TESTS_FILE_PATH = TestUtil.getSystemPropertyOrDefault(EXCLUDED_TESTS_FILE_KEY,
			"/excluded-tests.xml");
	private static final String TCK_CONTEXT;
	private static final int DEFAULT_BROWSER_WAIT_TIMEOUT;
	private static final int MAX_NUMBER_ACTIONS = 10;

	static {

		logger.setLevel(TestUtil.getLogLevel());

		String tckContext = "/";
		int defaultBrowserWaitTimeout = 5;

		if (PORTLET_CONTAINER.contains("liferay")) {
			tckContext = "/group/bridge-tck/";
		}
		else if (PORTLET_CONTAINER.contains("pluto")) {
			tckContext = TestUtil.DEFAULT_PLUTO_CONTEXT + "/";
			defaultBrowserWaitTimeout = 1;
		}

		TCK_CONTEXT = tckContext;
		DEFAULT_BROWSER_WAIT_TIMEOUT = defaultBrowserWaitTimeout;
	}

	private String mPageName;
	private String mTestName;

	public TckTestCase(String pageName, String testName) {
		mPageName = pageName;
		mTestName = testName;
	}

	public static void main(String[] args) {

		// junit.textui.TestRunner.run(suite());
		new org.junit.runner.JUnitCore().runClasses(TckTestCase.class);
	}

	@Parameters
	public static Collection testData() {

		logger.log(Level.INFO, "testData()");

		List testList = new ArrayList<String[]>(200);
		InputStream excludedTestsInputStream = null;
		InputStream testsInputStream = null;

		try {
			excludedTestsInputStream = TckTestCase.class.getResourceAsStream(EXCLUDED_TESTS_FILE_PATH);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to acccess test exclusions file, {0} Exception thrown: {1}",
				new String[] { EXCLUDED_TESTS_FILE_PATH, e.getMessage() });
			System.exit(1);
		}

		Properties exProps = new Properties();

		try {
			exProps.loadFromXML(excludedTestsInputStream);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to parse test exclusions file, {0} Exception thrown: {1}",
				new String[] { EXCLUDED_TESTS_FILE_PATH, e.getMessage() });
			System.exit(1);
		}

		try {
			testsInputStream = TckTestCase.class.getResourceAsStream(TESTS_FILE_PATH);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to acccess test description file, {0} Exception thrown: {1}",
				new String[] { TESTS_FILE_PATH, e.getMessage() });
			System.exit(1);
		}

		Properties testProps = new Properties();

		try {
			testProps.loadFromXML(testsInputStream);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to parse test description file, {0} Exception thrown: {1}",
				new String[] { TESTS_FILE_PATH, e.getMessage() });
			System.exit(1);
		}

		Enumeration<?> tests = testProps.propertyNames();
		Pattern testFilterPattern = null;
		String testFilter = TestUtil.getSystemPropertyOrDefault("integration.test.filter", null);

		if (testFilter != null) {
			testFilterPattern = Pattern.compile(testFilter);
		}

		while (tests.hasMoreElements()) {

			String page = (String) tests.nextElement();
			String testName = testProps.getProperty(page);

			if ((exProps.getProperty(testName) == null) &&
					((testFilterPattern == null) || testFilterPattern.matcher(testName).matches())) {
				testList.add(new String[] { page, testName });
			}
		}

		return testList;
	}

	public String getName() {
		return mTestName;
	}

	@Before
	public void runBeforeEachTest() throws Exception {

		Browser browser = Browser.getInstance();
		browser.get(TestUtil.DEFAULT_BASE_URL + TCK_CONTEXT + mPageName);
	}

	@Test
	public void testPage() {

		Browser browser = Browser.getInstance();
		testPage(browser, true);
	}

	@Override
	protected void doSetUp() {

		super.doSetUp();

		Browser browser = Browser.getInstance();
		int browserWaitTimeOut = TestUtil.getBrowserWaitTimeOut(DEFAULT_BROWSER_WAIT_TIMEOUT);
		browser.setWaitTimeOut(browserWaitTimeOut);
	}

	private void appendPageSourceIfNeccessary(Browser browser, StringBuilder stringBuilder) {

		if (TestUtil.getLogLevel().intValue() <= Level.CONFIG.intValue()) {

			stringBuilder.append("\n");

			String pageSource = browser.getPageSource().replaceAll("(\\s)+", "$1");
			stringBuilder.append(pageSource);
			stringBuilder.append("\n");
		}
	}

	private boolean areElementsVisible(Browser browser, String... elementXpaths) {

		boolean elementVisible = false;

		for (String elementXpath : elementXpaths) {

			List<WebElement> elements = browser.findElements(By.xpath(elementXpath));

			if (elements.size() > 0) {

				if (elements.get(0).isDisplayed()) {

					elementVisible = true;

					break;
				}
			}
		}

		return elementVisible;
	}

	private void loadImagesIfNecessary(Browser browser) {

		String browserName = browser.getName();

		if ("htmlunit".equals(browserName) && mTestName.equals("nonFacesResourceTest")) {

			// HtmlUnit does not load images by default while all other browsers load images by default. In order to
			// be consistent with other browsers, load any images that are on the page.
			HtmlUnitUtil.loadImages(browser);
		}
	}

	private void recordResult(Browser browser) {

		String failMsg = null;
		String details = null;
		String testStatus = null;

		// Look for results that use the tck span ids
		String tckResultXPath = MessageFormat.format(TEST_RESULT_STATUS_XPATH_TEMPLATE, new Object[] { mTestName });

		if (areElementsVisible(browser, tckResultXPath)) {

			try {

				WebElement resultElement = browser.findElementByXpath(tckResultXPath);
				testStatus = resultElement.getText();

				String tckDetailsXPath = MessageFormat.format(TEST_RESULT_DETAIL_XPATH_TEMPLATE,
						new Object[] { mTestName });
				WebElement detailsElement = browser.findElementByXpath(tckDetailsXPath);
				details = detailsElement.getText();
			}
			catch (WebDriverException e) {

				// Invalid result format
				StringBuilder sb = new StringBuilder();
				sb.append(mPageName);
				sb.append(": ");
				sb.append(mTestName);
				sb.append(": ");
				sb.append("Test failed but no test result details found.\n");

				String message = e.getMessage();
				sb.append(message);
				appendPageSourceIfNeccessary(browser, sb);
				throw new WebDriverException(sb.toString());
			}
		}
		else {

			// Ajax test content with no TCK span ids.
			if ((areElementsVisible(browser, "//p[contains (.,\"FAILED\")]"))) {

				// Portlet shows a failure result so display details
				try {

					WebElement detailsElement = browser.findElementByXpath("//p[contains(.,\"Detail\")]//p[1]");
					details = detailsElement.getText();
				}
				catch (WebDriverException e) {

					// Portlet contains no detail section, should never happen.
					StringBuilder sb = new StringBuilder();
					sb.append(mPageName);
					sb.append(": ");
					sb.append(mTestName);
					sb.append(": Test failed but no test result details found.\n");

					String message = e.getMessage();
					sb.append(message);
					appendPageSourceIfNeccessary(browser, sb);
					throw new WebDriverException(sb.toString());
				}
			}
			else if (areElementsVisible(browser, "//p[contains (.,\"SUCCESS\")]")) {
				testStatus = "SUCCESS";
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(mPageName);
		sb.append(": ");
		sb.append(mTestName);
		sb.append(": FAILED\n");
		sb.append(details);
		appendPageSourceIfNeccessary(browser, sb);
		failMsg = sb.toString();
		Assert.assertEquals(failMsg, "SUCCESS", testStatus);
	}

	private void runAjaxTest(Browser browser) {

		boolean resultObtained = false;

		// Wait for a result or another control to be rendered.
		for (int i = 0; i < MAX_NUMBER_ACTIONS; i++) {

			for (String xpath : RUN_AJAX_TEST_XPATHS) {

				if (areElementsVisible(browser, xpath)) {

					browser.click(xpath);
					switchToIFrameIfNecessary(browser);

					try {

						String resultXpath = MessageFormat.format(TEST_RESULT_AJAX_STATUS_XPATH,
								new Object[] { mTestName });
						browser.waitForElementVisible(resultXpath);
						recordResult(browser);
						resultObtained = true;
					}
					catch (TimeoutException e) {
						// continue.
					}
				}
			}

			if (!resultObtained && !areElementsVisible(browser, RUN_AJAX_TEST_XPATHS)) {

				StringBuilder sb = new StringBuilder();
				sb.append(mPageName);
				sb.append(": ");
				sb.append(mTestName);
				sb.append(": ");
				sb.append(i + 1);
				sb.append(" ajax action(s) executed with no valid result.");
				appendPageSourceIfNeccessary(browser, sb);
				throw new WebDriverException(sb.toString());
			}
		}

		if (!resultObtained) {

			StringBuilder sb = new StringBuilder();
			sb.append(mPageName);
			sb.append(": ");
			sb.append(mTestName);
			sb.append(": ");
			sb.append(MAX_NUMBER_ACTIONS);
			sb.append(" actions have been performed on this portlet without any final result.");
			appendPageSourceIfNeccessary(browser, sb);
			throw new WebDriverException(sb.toString());
		}
	}

	private void runTest(Browser browser) {

		boolean resultObtained = false;

		for (int i = 0; i < MAX_NUMBER_ACTIONS; i++) {

			for (String xpath : RUN_TEST_XPATHS) {

				if (areElementsVisible(browser, xpath)) {

					browser.click(xpath);
					browser.waitForElementVisible("//body");
					loadImagesIfNecessary(browser);
					switchToIFrameIfNecessary(browser);

					// If results page shows record result.
					if (areElementsVisible(browser, TEST_RESULT_STATUS_XPATH)) {

						recordResult(browser);
						resultObtained = true;
					}
					// If no results page carry on looking for components to click on.
				}
			}

			if (!resultObtained && !areElementsVisible(browser, RUN_TEST_XPATHS)) {

				StringBuilder sb = new StringBuilder();
				sb.append(mPageName);
				sb.append(": ");
				sb.append(mTestName);
				sb.append("\n: ");
				sb.append(i + 1);
				sb.append(
					" full page request action(s) have been performed on this portlet without any final result or test components to exercise.");
				appendPageSourceIfNeccessary(browser, sb);
				throw new WebDriverException(sb.toString());
			}
		}

		if (!resultObtained) {

			StringBuilder sb = new StringBuilder();
			sb.append(mPageName);
			sb.append(": ");
			sb.append(mTestName);
			sb.append("\n: ");
			sb.append(MAX_NUMBER_ACTIONS);
			sb.append(" actions have been performed on this portlet without any final result.");
			appendPageSourceIfNeccessary(browser, sb);
			throw new WebDriverException(sb.toString());
		}
	}

	private void switchToIFrameIfNecessary(Browser browser) {

		if (areElementsVisible(browser, TEST_IFRAME_XPATH)) {

			browser.switchTo().frame("tck-iframe");
			browser.waitForElementVisible("//body");
		}
	}

	private void testPage(Browser browser, boolean switchToIFrameIfNecessary) throws WebDriverException {

		try {

			if (areElementsVisible(browser, TEST_RESULT_STATUS_XPATH)) {
				recordResult(browser);
			}
			else if (areElementsVisible(browser, RUN_TEST_XPATHS)) {
				runTest(browser);
			}
			else if (areElementsVisible(browser, RUN_AJAX_TEST_XPATHS)) {
				runAjaxTest(browser);
			}
			else if (switchToIFrameIfNecessary && areElementsVisible(browser, TEST_IFRAME_XPATH)) {

				browser.switchTo().frame("tck-iframe");
				browser.waitForElementVisible("//body");

				testPage(browser, false);
			}
			else {

				// Default case, unrecognised page content.
				StringBuilder sb = new StringBuilder();
				sb.append(mPageName);
				sb.append(": ");
				sb.append(mTestName);
				sb.append("\n: Unexpected page content.");
				appendPageSourceIfNeccessary(browser, sb);
				Assert.fail(sb.toString());
			}
		}
		catch (WebDriverException e) {
			throw new AssertionError("Uncaught WebDriverException: " + e.getMessage(), e);
		}
	}
}
