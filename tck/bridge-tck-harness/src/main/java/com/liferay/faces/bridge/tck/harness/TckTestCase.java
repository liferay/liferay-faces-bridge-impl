/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized.Parameters;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


/**
 * @author  Michael Freedman
 */
@RunWith(TckParameterized.class)
public class TckTestCase {

	private static final String MAX_WAIT = "60000";
	private static final int MAX_NUMBER_ACTIONS = 10;

	protected static Selenium sSelenium;

	// XPaths for results using span ids
	private static final String TEST_RESULT_STATUS_XPATH = "//span[@id=\"{0}-result-status\"]";
	private static final String TEST_RESULT_DETAIL_XPATH = "//span[@id=\"{0}-result-detail\"]";

	// XPaths for Ajax results which do not include TCK span ids.
	private static final String TEST_RESULT_STATUS_PPR_XPATH = "//span[contains(.,\"{0}\")]/p[contains(.,\"Status\")]";

	private static final String[] ACTION_FPR_XPATHS = {
			"//input[not(@class=\"run-ppr-button\") and @type=\"submit\" and @value=\"Run Test\"]",
			"//a[not(@class=\"ppr-redisplay-link\") and text()=\"Run Test\"]"
		};

	private static final String[] ACTION_PPR_XPATHS = {
			"//input[@class=\"run-ppr-button\" and @type=\"submit\" and @value=\"Run Test\"]",
			"//a[@class=\"ppr-redisplay-link\" and contains(.,\"Run Test\")]"
		};

	private static final String IFRAME_XPATH = "//iframe[@name=\"tck-iframe\"]";
	private static final String IFRAME_NAME = "tck-iframe";
	private static final String IFRAME_TIMEOUT = "5000";

	private static final String TCK_HOME = "BRIDGE_301_TCK_HOME";
	private static final String PROPS_FILE = "bridge.tck.properties";
	private static final String LOGIN_FILE = "bridge.tck.login.properties";
	private static final String LOGIN_FILE_KEY = "bridge.tck.login.file";

	private static final String CLIENT_BASE_URL_KEY = "bridge.tck.test.base-url";
	private static final String TEST_FILE_KEY = "bridge.tck.test.file";
	private static final String TEST_FILTER_KEY = "bridge.tck.test.filter";
	private static final String TEST_EXCLUSIONS_FILE_KEY = "bridge.tck.test.exclusions.file";
	private static final String SELENIUM_BROWSER_KEY = "bridge.tck.browser";
	private static final String SELENIUM_HOST_KEY = "bridge.tck.selenium.host";
	private static final String SELENIUM_PORT_KEY = "bridge.tck.selenium.port";

	private static final String DEFAULT_SELENIUM_BROWSER = "*firefox";
	private static final String DEFAULT_SELENIUM_HOST = "localhost";
	private static final String DEFAULT_SELENIUM_PORT = "4444";

	private static String sClientBaseUrl;
	private static String sTestFile;
	private static String sTestExclusionsFile;
	private static String sTestFilter;
	private static String sSeleniumBrowser; // = DEFAULT_SELENIUM_BROWSER;
	private static String sSeleniumHost; // = DEFAULT_SELENIUM_HOST;
	private static int sSeleniumPort; // = DEFAULT_SELENIUM_PORT;

	// Name value pairs for fields in login page
	private static List<String[]> sLoginFields;

	// Name of button in login page
	private static String sLoginButton;

	static {
		log("static block");

		String tckHomeDir = System.getenv(TCK_HOME);

		if (tckHomeDir == null) {

			// Default value for the TCK home.
			tckHomeDir = System.getProperty("user.home") + File.separator + "BridgeTckHome";
		}

		File propsFile = new File(tckHomeDir, PROPS_FILE);
		Properties fileProps = null;

		try {
			log("Home dir: " + tckHomeDir);

			FileInputStream fis = null;

			fis = new FileInputStream(propsFile);
			fileProps = new Properties();
			fileProps.load(fis);
		}
		catch (FileNotFoundException fnfe) {

			// Not necessarily an error.
			log("No TCK properties file, " + propsFile + " does not exist");
			propsFile = null;
		}
		catch (IOException ioe) {
			errLog("Error loading TCK properties file, " + propsFile.getName());
			System.exit(1);
		}

		sClientBaseUrl = getProperty(fileProps, CLIENT_BASE_URL_KEY, null, true);
		sTestFilter = getProperty(fileProps, TEST_FILTER_KEY, null, false);
		sTestFile = getProperty(fileProps, TEST_FILE_KEY, null, true);
		sTestExclusionsFile = getProperty(fileProps, TEST_EXCLUSIONS_FILE_KEY, null, true);

		sSeleniumHost = getProperty(fileProps, SELENIUM_HOST_KEY, DEFAULT_SELENIUM_HOST, false);
		sSeleniumPort = Integer.parseInt(getProperty(fileProps, SELENIUM_PORT_KEY, DEFAULT_SELENIUM_PORT, false));

		sSeleniumBrowser = getProperty(fileProps, SELENIUM_BROWSER_KEY, DEFAULT_SELENIUM_BROWSER, false);

		// Login properties file
		try {
			FileInputStream fis = null;

			if (System.getProperty(LOGIN_FILE_KEY) != null) {
				fis = new FileInputStream(System.getProperty(LOGIN_FILE_KEY));
			}
			else {
				fis = new FileInputStream(new File(tckHomeDir, LOGIN_FILE));
			}

			Properties props = new Properties();
			props.load(fis);

			Enumeration propsNameList = props.propertyNames();

			while (propsNameList.hasMoreElements()) {
				String loginProp = (String) propsNameList.nextElement();

				if (props.get(loginProp).equals("")) {

					// A button login property is defined as name only
					sLoginButton = loginProp;
				}
				else {

					if (sLoginFields == null) {
						sLoginFields = new ArrayList<String[]>();
					}

					sLoginFields.add(new String[] { loginProp, (String) props.get(loginProp) });
				}
			}
		}
		catch (FileNotFoundException fnfe) {
			// No login properties file.  Not an error condition.
		}
		catch (IOException ioe) {
			errLog("Error loading login properties file, " + propsFile.getName());
			System.exit(1);
		}

		log("test file: " + sTestFile);
		log("test filter: " + sTestFilter);

		log("End of static block");
	}

	private String mPageName;
	private String mTestName;

	public TckTestCase(String pageName, String testName) {
		mPageName = pageName;
		mTestName = testName;
	}

	public static void errLog(String msg) {
		Logger.getLogger("global").log(Level.SEVERE, msg);
	}

	public static void log(String msg) {
		Logger.getLogger("global").log(Level.INFO, msg);
	}

	public static void main(String[] args) {

		// junit.textui.TestRunner.run(suite());
		new org.junit.runner.JUnitCore().runClasses(TckTestCase.class);
	}

	@AfterClass
	public static void runAfterClass() throws Exception {

		if (sSelenium != null) {
			sSelenium.stop();
			sSelenium.shutDownSeleniumServer();
		}
	}

	@Parameters
	public static Collection testData() {
		log("testData()");

		List testList = new ArrayList<String[]>(200);
		FileInputStream exIs = null;
		FileInputStream tis = null;
		Pattern filterPattern = null;

		if ((sTestFilter != null) && (sTestFilter.length() > 0)) {
			filterPattern = Pattern.compile(sTestFilter);
		}

		try {
			exIs = new FileInputStream(sTestExclusionsFile);
		}
		catch (Exception e) {
			errLog("Unable to acccess test exclusions file, " + sTestExclusionsFile + " Exception thrown: " +
				e.getMessage());
			System.exit(1);
		}

		Properties exProps = new Properties();

		try {
			exProps.loadFromXML(exIs);
		}
		catch (Exception e) {
			errLog("Unable to parse test exclusions file, " + sTestExclusionsFile + " Exception thrown: " +
				e.getMessage());
			System.exit(1);
		}

		try {
			tis = new FileInputStream(sTestFile);
		}
		catch (Exception e) {
			errLog("Unable to acccess test description file, " + sTestFile + " Exception thrown: " + e.getMessage());
			System.exit(1);
		}

		Properties testProps = new Properties();

		try {
			testProps.loadFromXML(tis);
		}
		catch (Exception e) {
			errLog("Unable to parse test description file, " + sTestFile + " Exception thrown: " + e.getMessage());
			System.exit(1);
		}

		Enumeration tests = testProps.propertyNames();

		while (tests.hasMoreElements()) {
			String page = (String) tests.nextElement();
			String testName = testProps.getProperty(page);
			boolean filterMatch = true;

			if (filterPattern != null) {
				Matcher m = filterPattern.matcher(testName);
				filterMatch = m.matches();
			}

			if (filterMatch) {

				if (exProps.getProperty(testName) == null) {
					testList.add(new String[] { page, testName });
				}
			}
		}

		return testList;
	}

	private static String getProperty(Properties fileProperties, String key, String defaultValue, boolean required) {
		boolean checkFirst = (defaultValue != null);

		if (System.getProperty(key) != null) {
			return System.getProperty(key);
		}

		if ((fileProperties != null) && ((String) fileProperties.get(key) != null)) {
			return (String) fileProperties.get(key);
		}

		if (defaultValue != null) {
			return defaultValue;
		}
		else if (required) {
			errLog("Property " + key + " has not been set.");
			System.exit(1);
		}

		return defaultValue;
	}

	@BeforeClass
	public static void setUpSession() throws Exception {
		log("setUpSession");

		try {
			log("SeleniumHost: " + sSeleniumHost);
			log("SeleniumPort: " + sSeleniumPort);
			log("SeleniumBrowser: " + sSeleniumBrowser);
			log("ClientBaseUrl: " + sClientBaseUrl);
			sSelenium = new DefaultSelenium(sSeleniumHost, sSeleniumPort, sSeleniumBrowser, sClientBaseUrl);
			sSelenium.start();
			sSelenium.setTimeout(MAX_WAIT);
		}
		catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	public void login() {

		if (sLoginFields != null) {

			for (String[] field : sLoginFields) {

				if (!sSelenium.isElementPresent("//input[@name='" + field[0] + "']")) {

					// Not a login page
					return;
				}
			}

			for (String[] field : sLoginFields) {
				sSelenium.type(field[0], field[1]);
			}

			sSelenium.click(sLoginButton);
			sSelenium.waitForPageToLoad(MAX_WAIT);
		}
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		sSelenium.open(mPageName);
		sSelenium.waitForPageToLoad(MAX_WAIT);
	}

	@Test
	public void testPage() {
		login();

		try {
			loadTckIFrame();

			if (isResultPage()) {
				recordResult();
			}
			else if (isFPRActionPage()) {
				performFPRAction();
			}
			else if (isPPRActionPage()) {
				performPPRAction();
			}
			else {

				// Default case, unrecognised page content.
				StringBuilder sb = new StringBuilder().append("Unexpected page content.\n").append(
						sSelenium.getBodyText());
				fail(sb.toString());
			}
		}
		catch (SeleniumException se) {
			fail(se.getMessage());
		}
	}

	private void loadTckIFrame() {

		// Will load the iframe if present or throw an exception if the iframe
		// can not be loaded.
		if (sSelenium.isElementPresent(IFRAME_XPATH)) {
			sSelenium.selectFrame(IFRAME_XPATH);

			if (!sSelenium.isElementPresent("//body")) {

				// Only wait for iframe to load if it hasn't already.  Waiting for
				// an already loaded iframe will time out.
				try {
					sSelenium.waitForFrameToLoad(IFRAME_NAME, IFRAME_TIMEOUT);

				}
				catch (SeleniumException se) {
					StringBuilder sb = new StringBuilder().append("Exception thrown while loading tck-iframe.\n")
						.append(se.getMessage()).append("\n").append(sSelenium.getBodyText());
					throw new SeleniumException(sb.toString());
				}
			}

			sSelenium.selectFrame("relative=parent");
		}
	}

	private void performFPRAction() {

outer:
		for (int i = 0; i < MAX_NUMBER_ACTIONS; i++) {

			for (String action : ACTION_FPR_XPATHS) {

				if (sSelenium.isElementPresent(action)) {

					// Click component
					sSelenium.click(action);
					sSelenium.waitForPageToLoad(MAX_WAIT);

					if (isResultPage()) {

						// If results page shows record result.
						recordResult();

						return;
					}

					// If no results page carry on looking for components to click on.
					continue outer;
				}
			}

			StringBuilder sb = new StringBuilder().append(i).append(
					" full page request action(s) have been performed on this portlet without any final result or test components to exercise.\n")
				.append(sSelenium.getBodyText());
			throw new SeleniumException(sb.toString());
		}

		StringBuilder sb = new StringBuilder().append(MAX_NUMBER_ACTIONS).append(
				" actions have been performed on this portlet without any final result.\n").append(
				sSelenium.getBodyText());
		throw new SeleniumException(sb.toString());
	}

	private void performPPRAction() {
		// There's no andWait equivalent for a PPR request.  Instead, when clicked
		// a PPR component portlet will either return a result or a different
		// component to click.

// Click first "Run Test "component found in portlet.
outer:

		// Wait for a result or another control to be rendered.
		for (int i = 1; i <= MAX_NUMBER_ACTIONS; i++) {

			for (String pprPath : ACTION_PPR_XPATHS) {

				if (sSelenium.isElementPresent(pprPath)) {
					sSelenium.click(pprPath);
					waitForValidPPResponse(pprPath);

					String result = MessageFormat.format(TEST_RESULT_STATUS_PPR_XPATH, new Object[] { mTestName });

					if (sSelenium.isElementPresent(result)) {
						recordResult();

						return;
					}

					continue outer;
				}
			}

			StringBuilder sb = new StringBuilder().append(i).append(" PPR action(s) executed with no valid result.\n")
				.append(sSelenium.getBodyText());
			throw new SeleniumException(sb.toString());
		}
	}

	private void recordResult() {
		String failMsg = null;
		String details = null;
		String testStatus = null;

		// Look for results that use the tck span ids
		String tckResultXPath = MessageFormat.format(TEST_RESULT_STATUS_XPATH, new Object[] { mTestName });

		if (sSelenium.isElementPresent(tckResultXPath)) {

			try {
				testStatus = sSelenium.getText(tckResultXPath);

				String tckDetailsXPath = MessageFormat.format(TEST_RESULT_DETAIL_XPATH, new Object[] { mTestName });
				details = sSelenium.getText(tckDetailsXPath);
			}
			catch (SeleniumException se) {

				// Invalid result format
				StringBuilder sb = new StringBuilder().append("Test failed but no test result details found.\n").append(
						se.getMessage()).append("\n").append(sSelenium.getBodyText());
				throw new SeleniumException(sb.toString());
			}
		}
		else {

			// PPR content with no TCK span ids.
			if ((sSelenium.isElementPresent("//p[contains (.,\"FAILED\")]"))) {

				// Portlet shows a failure result so display details
				try {
					details = sSelenium.getText("//p[contains(.,\"Detail\")]//p[1]");
				}
				catch (SeleniumException se) {

					// Portlet contains no detail section, should never happen.
					StringBuilder sb = new StringBuilder().append("Test failed but no test result details found.\n")
						.append(se.getMessage()).append("\n").append(sSelenium.getBodyText());
					throw new SeleniumException(sb.toString());
				}
			}
			else if (sSelenium.isElementPresent("//p[contains (.,\"SUCCESS\")]")) {
				testStatus = "SUCCESS";
			}
		}

		failMsg = new StringBuilder().append("FAILED").append("\n").append(details).toString();

		assertTrue(failMsg, ("SUCCESS".equals(testStatus)));
	}

	private void waitForValidPPResponse(String previousPath) {

		// Waits for a valid response and returns or throws an exception.
		// Valid responses are a success/fail result or a different PPR "Run Test"
		// component.
		String result = MessageFormat.format(TEST_RESULT_STATUS_PPR_XPATH, new Object[] { mTestName });
		int i = 0;

		for (i = 0; i < 3; i++) {

			if (sSelenium.isElementPresent(result)) {

				// A result
				return;
			}

			for (String pprPath : ACTION_PPR_XPATHS) {

				if (!pprPath.equals(previousPath)) {

					if (sSelenium.isElementPresent(pprPath)) {
						return;
					}
				}
			}

			// No valid response, wait for about a sec and retry
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ie) {
			}
		}

		StringBuilder sb = new StringBuilder().append("No valid response to PPR action.\n").append(
				sSelenium.getBodyText());
		throw new SeleniumException(sb.toString());
	}

	private boolean isFPRActionPage() {

		for (String path : ACTION_FPR_XPATHS) {

			if (sSelenium.isElementPresent(path)) {
				return true;
			}
		}

		return false;
	}

	private boolean isPPRActionPage() {

		for (String path : ACTION_PPR_XPATHS) {

			if (sSelenium.isElementPresent(path)) {
				return true;
			}
		}

		return false;
	}

	private boolean isResultPage() {
		return (sSelenium.isElementPresent("//p[contains (.,\"Status\")]"));
	}

	public String getName() {
		return mTestName;
	}
}
