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
package com.liferay.faces.bridge.test.integration.demo.applicant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runners.MethodSorters;

import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JSFApplicantPortletTester extends IntegrationTesterBase {

	// Private Constants
	private static final String EDIT_LINK_XPATH = "//a[contains(@id,'editLink')]";
	private static final String LIFERAY_JSF_JERSEY_PNG_FILE_PATH = System.getProperty("java.io.tmpdir") +
		"liferay-jsf-jersey.png";

	@BeforeClass
	public static void setUpApplicantTester() {
		Browser.getInstance().setWaitTimeOut(TestUtil.getBrowserWaitTimeOut(10));
	}

	@AfterClass
	public static void tearDownApplicantTester() {
		Browser.getInstance().setWaitTimeOut(TestUtil.getBrowserWaitTimeOut());
	}

	@Test
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		Browser browser = Browser.getInstance();
		browser.get(TestUtil.DEFAULT_BASE_URL + getContext());

		// Wait to begin the test until the logo is rendered.
		browser.waitForElementVisible(getLogoXpath());

		SeleniumAssert.assertElementVisible(browser, getFirstNameFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getLastNameFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getEmailAddressFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getPhoneNumberFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getDateOfBirthFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getCityFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getProvinceIdFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getPostalCodeFieldXpath());
		assertFileUploadChooserVisible(browser);
		;
		SeleniumAssert.assertLibraryVisible(browser, "Mojarra");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Alloy");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Impl");

		if (TestUtil.getContainer().contains("liferay")) {
			SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Ext");
		}

		String extraLibraryName = getExtraLibraryName();

		if (extraLibraryName != null) {
			SeleniumAssert.assertLibraryVisible(browser, getExtraLibraryName());
		}
	}

	@Test
	public void runApplicantPortletTest_B_EditMode() {

		// Test that changing the date pattern via preferences changes the Birthday value in the portlet.
		Browser browser = Browser.getInstance();
		browser.click(EDIT_LINK_XPATH);

		String datePatternPreferencesXpath = getDatePatternPreferencesXpath();

		try {
			browser.waitForElementVisible(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		browser.clear(datePatternPreferencesXpath);

		String newDatePattern = "MM/dd/yy";
		browser.sendKeys(datePatternPreferencesXpath, newDatePattern);

		String preferencesSubmitButtonXpath = getPreferencesSubmitButtonXpath();
		browser.click(preferencesSubmitButtonXpath);

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();

		try {
			browser.waitForElementVisible(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(newDatePattern);
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		String todayString = simpleDateFormat.format(today);
		SeleniumAssert.assertElementValue(browser, dateOfBirthFieldXpath, todayString);

		// Test that resetting the date pattern via preferences changes the Birthday year back to the long version.
		browser.click(EDIT_LINK_XPATH);

		try {
			browser.waitForElementVisible(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String preferencesResetButtonXpath = getPreferencesResetButtonXpath();
		browser.click(preferencesResetButtonXpath);

		try {
			browser.waitForElementVisible(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String oldDatePattern = "MM/dd/yyyy";
		simpleDateFormat.applyPattern(oldDatePattern);
		todayString = simpleDateFormat.format(today);
		SeleniumAssert.assertElementValue(browser, dateOfBirthFieldXpath, todayString);
	}

	@Test
	public void runApplicantPortletTest_C_FirstNameField() {

		Browser browser = Browser.getInstance();
		submitPostalCodeAndWaitForPostback(browser, "32802");

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.sendKeys(firstNameFieldXpath, "asdf");

		browser.click(getSubmitButtonXpath());

		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		SeleniumAssert.assertElementNotPresent(browser, firstNameFieldErrorXpath);
		browser.clear(firstNameFieldXpath);
		browser.click(getSubmitButtonXpath());
		browser.waitForElementValue(firstNameFieldXpath, "");
		SeleniumAssert.assertElementTextVisible(browser, firstNameFieldErrorXpath, "Value is required");
	}

	/**
	 * This test is not valid in JSF 1.2 because f:validateRegex does not exist in JSF 1.2.
	 */
	@Ignore
	@Test
	public void runApplicantPortletTest_D_EmailValidation() {

		Browser browser = Browser.getInstance();
		String emailAddressFieldXpath = getEmailAddressFieldXpath();
		browser.sendKeys(emailAddressFieldXpath, "test");
		submitAndWaitForPostback(browser);

		String emailAddressFieldErrorXpath = getFieldErrorXpath(emailAddressFieldXpath);
		SeleniumAssert.assertElementTextVisible(browser, emailAddressFieldErrorXpath, "Invalid e-mail address");
		browser.clear(emailAddressFieldXpath);
		browser.sendKeys(emailAddressFieldXpath, "test@liferay.com");
		submitAndWaitForPostback(browser);
		SeleniumAssert.assertElementNotPresent(browser, emailAddressFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);
		browser.click(getSubmitButtonXpath());
		browser.waitForElementVisible(getPostalCodeFieldXpath());
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getPostalCodeFieldXpath()),
			"Value is required");
		submitPostalCodeAndWaitForPostback(browser, "32802");
		submitAndWaitForPostback(browser);
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getFirstNameFieldXpath()),
			"Value is required");
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getLastNameFieldXpath()),
			"Value is required");
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getEmailAddressFieldXpath()),
			"Value is required");
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getPhoneNumberFieldXpath()),
			"Value is required");
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getDateOfBirthFieldXpath()),
			"Value is required");
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getCityFieldXpath()), "Value is required");
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getProvinceIdFieldXpath()),
			"Value is required");
		clearAllFields(browser);
	}

	@Test
	public void runApplicantPortletTest_F_AutoPopulateCityState() {

		Browser browser = Browser.getInstance();
		submitPostalCodeAndWaitForPostback(browser, "32801");
		SeleniumAssert.assertElementValue(browser, getCityFieldXpath(), "Orlando");
		SeleniumAssert.assertElementValue(browser, getProvinceIdFieldXpath(), "3");
	}

	@Test
	public void runApplicantPortletTest_G_DateValidation() {

		Browser browser = Browser.getInstance();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browser.clear(dateOfBirthFieldXpath);
		browser.centerElementInView(dateOfBirthFieldXpath);
		browser.sendKeys(dateOfBirthFieldXpath, "12/34/5678");
		submitAndWaitForPostback(browser);

		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		SeleniumAssert.assertElementTextVisible(browser, dateOfBirthFieldErrorXpath, "Invalid date format");
		browser.clear(dateOfBirthFieldXpath);
		browser.sendKeys(dateOfBirthFieldXpath, "01/02/3456");
		submitAndWaitForPostback(browser);
		SeleniumAssert.assertElementNotPresent(browser, dateOfBirthFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_H_FileUpload() {

		Browser browser = Browser.getInstance();

		String fileUploadChooserXpath = getFileUploadChooserXpath();

		try {
			browser.waitForElementVisible(fileUploadChooserXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		browser.click(getFileUploadChooserXpath());

		WebElement fileUploadChooser = browser.findElementByXpath(fileUploadChooserXpath);

		// Workaround PrimeFaces p:fileUpload being invisible to selenium.
		browser.executeScript("arguments[0].style.transform = 'none';", fileUploadChooser);

		// Workaround https://github.com/ariya/phantomjs/issues/10993 by removing the multiple attribute from <input
		// type="file" />
		if (browser.getName().equals("phantomjs")) {

			browser.executeScript(
				"var multipleFileUploadElements = document.querySelectorAll('input[type=\"file\"][multiple]');" +
				"for (var i = 0; i < multipleFileUploadElements.length; i++) {" +
				"multipleFileUploadElements[i].removeAttribute('multiple'); }");
		}

		fileUploadChooser.sendKeys(LIFERAY_JSF_JERSEY_PNG_FILE_PATH);
		submitFile(browser);
		SeleniumAssert.assertElementTextVisible(browser, getUploadedFileXpath(), "jersey");
	}

	@Test
	public void runApplicantPortletTest_I_Submit() throws InterruptedException {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);
		browser.click(getSubmitButtonXpath());

		browser.sendKeys(getFirstNameFieldXpath(), "David");
		browser.waitForElementVisible(getLastNameFieldXpath());
		browser.sendKeys(getLastNameFieldXpath(), "Samuel");
		browser.sendKeys(getEmailAddressFieldXpath(), "no_need@just.pray");
		browser.sendKeys(getPhoneNumberFieldXpath(), "(way) too-good");
		selectDate(browser);
		browser.sendKeys(getCityFieldXpath(), "North Orlando");
		selectProvince(browser);
		submitPostalCodeAndWaitForPostback(browser, "32802");
		browser.click(getSubmitButtonXpath());
		browser.waitForElementVisible(getSubmitAnotherApplicationButton());
		SeleniumAssert.assertElementTextVisible(browser, getConfimationFormXpath(), "Dear David,");
	}

	protected void assertFileUploadChooserVisible(Browser browser) {
		SeleniumAssert.assertElementVisible(browser, getFileUploadChooserXpath());
	}

	protected void clearProvince(Browser browser) {
		createSelect(browser, getProvinceIdFieldXpath()).selectByVisibleText("Select");
	}

	protected final Select createSelect(Browser browser, String selectXpath) {

		WebElement selectField = browser.findElementByXpath(selectXpath);

		return new Select(selectField);
	}

	protected String getCityFieldXpath() {
		return "//input[contains(@id,':city')]";
	}

	protected String getConfimationFormXpath() {
		return "//form[@method='post']";
	}

	protected String getContext() {
		return BridgeTestUtil.getDemoContext("jsf-applicant");
	}

	protected String getDateOfBirthFieldXpath() {
		return "//input[contains(@id,':dateOfBirth')]";
	}

	protected String getDatePatternPreferencesXpath() {
		return "//input[contains(@id,':datePattern')]";
	}

	protected String getEmailAddressFieldXpath() {
		return "//input[contains(@id,':emailAddress')]";
	}

	protected String getExtraLibraryName() {
		return null;
	}

	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath + "/../span[@class='portlet-msg-error']";
	}

	protected String getFileUploadChooserXpath() {
		return "//input[@type='file']";
	}

	protected String getFirstNameFieldXpath() {
		return "//input[contains(@id,':firstName')]";
	}

	protected String getLastNameFieldXpath() {
		return "//input[contains(@id,':lastName')]";
	}

	protected String getLogoXpath() {
		return "//img[contains(@src, 'liferay-logo.png')]";
	}

	protected String getPhoneNumberFieldXpath() {
		return "//input[contains(@id,':phoneNumber')]";
	}

	protected String getPostalCodeFieldXpath() {
		return "//input[contains(@id,':postalCode')]";
	}

	protected String getPostalCodeToolTipXpath() {
		return "//img[contains(@title, 'Type any of these ZIP codes')]";
	}

	protected String getPreferencesResetButtonXpath() {
		return "//input[@type='submit'][@value='Reset']";
	}

	protected String getPreferencesSubmitButtonXpath() {
		return "//input[@type='submit'][@value='Submit']";
	}

	protected String getProvinceIdFieldXpath() {
		return "//select[contains(@id,':provinceId')]";
	}

	protected String getSubmitAnotherApplicationButton() {
		return "//input[@type='submit'][contains(@value, 'Submit Another Application')]";
	}

	protected String getSubmitButtonXpath() {
		return "//input[@type='submit'][@value='Submit']";
	}

	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post'][@enctype='multipart/form-data']/input[@type='submit'][@value='Submit']";
	}

	protected String getUploadedFileXpath() {
		return "//tr[@class='portlet-section-body results-row']/td[2]";
	}

	protected void selectDate(Browser browser) {
		browser.sendKeys(getDateOfBirthFieldXpath(), "01/02/3456");
	}

	protected void selectProvince(Browser browser) {
		createSelect(browser, getProvinceIdFieldXpath()).selectByVisibleText("FL");
	}

	protected final void submitAndWaitForPostback(Browser browser) {

		String submitButtonXpath = getSubmitButtonXpath();
		WebElement submitButton = browser.findElementByXpath(submitButtonXpath);
		submitButton.click();
		browser.waitUntil(ExpectedConditions.stalenessOf(submitButton));
		browser.waitForElementVisible(submitButtonXpath);
	}

	protected void submitFile(Browser browser) {

		browser.click(getSubmitFileButtonXpath());
		browser.waitForElementVisible(getUploadedFileXpath());
	}

	protected final void submitPostalCodeAndWaitForPostback(Browser browser, String postalCode) {

		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		WebElement postalCodeField = browser.findElementByXpath(postalCodeFieldXpath);
		postalCodeField.sendKeys(postalCode);
		postalCodeField.sendKeys(Keys.TAB);
		browser.waitUntil(ExpectedConditions.stalenessOf(postalCodeField));
		browser.waitForElementVisible(postalCodeFieldXpath);
	}

	private void clearAllFields(Browser browser) {

		browser.clear(getFirstNameFieldXpath());
		browser.clear(getLastNameFieldXpath());
		browser.clear(getEmailAddressFieldXpath());
		browser.clear(getPhoneNumberFieldXpath());
		browser.clear(getDateOfBirthFieldXpath());
		browser.clear(getCityFieldXpath());
		clearProvince(browser);
		browser.clear(getPostalCodeFieldXpath());
	}

	private void resetBrowser() {

		// Reset everything in case there was an error.
		Browser browser = Browser.getInstance();
		browser.manage().deleteAllCookies();
		signIn(browser);
		browser.get(TestUtil.DEFAULT_BASE_URL + getContext());
		browser.waitForElementVisible(getLogoXpath());
	}
}
