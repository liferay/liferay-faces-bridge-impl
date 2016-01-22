/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.test.util;
//J-

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * This class provides methods that may be used to help test portlets
 */
public class TesterBase {

	protected static final Logger logger = Logger.getLogger(TesterBase.class.getName());

	// elements for logging into legacy liferay
	private static final String emailFieldXpath = "//input[contains(@id,'_58_login')]";
	private static final String passwordFieldXpath = "//input[contains(@id,'_58_password')]";
	private static final String signInButtonXpath = "//button[@type='submit' and contains(text(),'Sign In')]";
	private static final String signedInTextXpath = "//div[contains(text(),'You are signed in as')]";

	@FindBy(xpath = emailFieldXpath)
	private WebElement emailField;
	@FindBy(xpath = passwordFieldXpath)
	private WebElement passwordField;
	@FindBy(xpath = signInButtonXpath)
	private WebElement signInButton;
	@FindBy(xpath = signedInTextXpath)
	private WebElement signedInText;

	// elements for logging into liferay
	private static final String lr70emailFieldXpath = "//input[contains(@id,'_com_liferay_login_web_portlet_LoginPortlet_login')]";
	private static final String lr70passwordFieldXpath = "//input[contains(@id,'_com_liferay_login_web_portlet_LoginPortlet_password')]";
	private static final String lr70signInButtonXpath = "//button[@type='submit' and contains(@id,'_com_liferay_login_web_portlet_LoginPortlet_')]";

	@FindBy(xpath = lr70emailFieldXpath)
	private WebElement lr70emailField;
	@FindBy(xpath = lr70passwordFieldXpath)
	private WebElement lr70passwordField;
	@FindBy(xpath = lr70signInButtonXpath)
	private WebElement lr70signInButton;

	// elements for logging into pluto
	private static final String userNameXpath = "//input[contains(@id,'j_username')]";
	private static final String passwordXpath = "//input[contains(@id,'j_password')]";
	private static final String loginButtonXpath = "//input[@type='submit' and @value='Login']";
	private static final String logoutXpath = "//a[contains(text(),'Logout')]";

	@FindBy(xpath = userNameXpath)
	private WebElement userName;
	@FindBy(xpath = passwordXpath)
	private WebElement password;
	@FindBy(xpath = loginButtonXpath)
	private WebElement loginButton;
	@FindBy(xpath = logoutXpath)
	private WebElement logout;

	// elements for switching to edit mode in liferay
	private static final String menuButtonXpath = "//a[contains(@title,'Options')]";
	private static final String menuPreferencesXpath = "//span[contains(text(),'Preferences')]/..";

	@FindBy(xpath = menuButtonXpath)
	private WebElement menuButton;
	@FindBy(xpath = menuPreferencesXpath)
	private WebElement menuPreferences;
	
	private static final String editLinkXpath = "//a[contains(@id,'editLink')]";
	@FindBy(xpath = editLinkXpath)
	private WebElement editLink;

	private static final String JERSEY_FILE = "liferay-jsf-jersey.png";

	public static final String portal = System.getProperty("integration.portal", "liferay");
	public static final String baseUrl = System.getProperty("integration.url", "http://localhost:8080");
	public static final String signInContext = System.getProperty("integration.signin", "/web/guest/home");
	public static final String webContext = System.getProperty("integration.context", "/web/bridge-demos");
	protected static final String signInUrl = baseUrl + signInContext;


	public void signIn(WebDriver browser) throws Exception {
		logger.log(Level.INFO, "portal = " + portal);
		if ("liferay".equals(portal)) {
			signIn(browser, lr70emailField, lr70passwordField, lr70signInButton, signedInText, signedInTextXpath, "test@liferay.com", "test");
		} else if (portal.contains("liferay")) {
			logger.log(Level.INFO, "assuming legacy emailFieldXpath = " + emailFieldXpath + " for sign in ...");
			signIn(browser, emailField, passwordField, signInButton, signedInText, signedInTextXpath, "test@liferay.com", "test");
		} else if ("pluto".equals(portal)) {
			signIn(browser, userName, password, loginButton, logout, logoutXpath, "pluto", "pluto");
		} else {
			logger.log(Level.SEVERE, "not a supported portal for this tester base: portal = " + portal + "");
		}
	}

	public void signIn(WebDriver browser, WebElement user, WebElement pass, WebElement button, WebElement text, String textXpath, String u, String p) throws Exception {

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);

		logger.log(Level.INFO, "browser.navigate().to(" + signInUrl + ")");
		browser.navigate().to(signInUrl);
		logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle() + " before signing in ...");

		if (browser.getTitle().contains("Status")) {
			logger.log(Level.INFO, "welcome-theme installed ...");
			String welcomeSignInUrl = signInUrl.replace("home", "welcome");
			logger.log(Level.INFO, "browser.navigate().to(" + welcomeSignInUrl + ")");
			browser.navigate().to(welcomeSignInUrl);
			logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle() + " before signing in ...");
		} else {
			logger.log(Level.INFO, "no welcome-theme, no problem ...");
		}

		if (isThere(browser, "//div[contains(text()[2],'was not found')]")) {

			// attempt to go to a Bridge Demos to get to the login page
			logger.log(Level.INFO, "Attempting to go to a Bridge Demos to get to the login page ...");
			String bridgeDemosSignInUrl = baseUrl + "/group/bridge-demos/";
			logger.log(Level.INFO, "browser.navigate().to(" + bridgeDemosSignInUrl + ")");
			browser.navigate().to(bridgeDemosSignInUrl);
			logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle() + " before signing in ...");

			waitForElement(browser, emailFieldXpath);
			user.clear();
			user.sendKeys(u);
			pass.clear();
			pass.sendKeys(p);
			button.click();

			waitForElement(browser, "//span[contains(text(),'Bridge Demos')]");

			return;
		}

		user.clear();
		user.sendKeys(u);
		pass.clear();
		pass.sendKeys(p);
		button.click();
		logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle() + " after clicking the sign in button.	Now waiting ...");
		waitForElement(browser, textXpath);
		logger.log(Level.INFO, text.getText());

	}

	public void selectEditMode(WebDriver browser, String portal) {
		editLink.click();
	}

	public String getPathToJerseyFile() {
		String path = "/tmp/";

		String os = System.getProperty("os.name");

		if (os.indexOf("win") > -1) {
			path = "C:\\WINDOWS\\Temp\\";
		}

		return path + JERSEY_FILE;
	}

	public void waitForElement(WebDriver browser, String xpath) {
		WebDriverWait wait = new WebDriverWait(browser, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.ByXPath.xpath(xpath)));
	}

	public boolean isThere(WebDriver browser, String xpath) {
		boolean isThere = false;
		int count = 0;
		count = browser.findElements(By.xpath(xpath)).size();

		if (count == 0) {
			isThere = false;
		}

		if (count > 0) {
			isThere = true;
		}

		if (count > 1) {
			logger.log(Level.WARNING,
				"The method 'isThere(xpath)' found " + count + " matches using xpath = " + xpath +
				" ... the word 'is' implies singluar, or 1, not " + count);
		}

		return isThere;
	}

}
//J+
