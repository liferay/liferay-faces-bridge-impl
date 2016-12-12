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

import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Ignore;

import org.junit.runners.MethodSorters;

import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.applicant.ApplicantTesterBase;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IceFacesApplicantPortletTester extends ApplicantTesterBase {

	@Override
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
		SeleniumAssert.assertElementVisible(browser, getShowHideCommentsLinkXpath());
		assertFileUploadChooserVisible(browser);
		SeleniumAssert.assertLibraryVisible(browser, "Mojarra");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Alloy");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge for ICEfaces");
		SeleniumAssert.assertLibraryVisible(browser, "ICEfaces");
	}

	@Override
	public void runApplicantPortletTest_B_EditMode() {

		// Test that changing the date pattern via preferences changes the Birthday value in the portlet.
		Browser browser = Browser.getInstance();
		String editModeXpath = getEditModeXpath();
		browser.click(editModeXpath);

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

		String requestProcessedSuccessfullyTextXpath =
			"//ul//li//span[contains(text(), 'Your request processed successfully.')]";
		browser.waitForElementPresent(requestProcessedSuccessfullyTextXpath);

		String returnToFullPageToolbarLinkXpath =
			"//menu[contains(@type,'toolbar')]//a[contains(text(), 'Return to Full Page')]";
		browser.click(returnToFullPageToolbarLinkXpath);

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
		browser.click(editModeXpath);

		try {
			browser.waitForElementVisible(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String preferencesResetButtonXpath = getPreferencesResetButtonXpath();
		browser.click(preferencesResetButtonXpath);
		browser.waitForElementPresent(requestProcessedSuccessfullyTextXpath);

		browser.click(returnToFullPageToolbarLinkXpath);

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

	@Override
	public void runApplicantPortletTest_C_FirstNameField() {

		Browser browser = Browser.getInstance();
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.sendKeys(firstNameFieldXpath, "asdf");
		browser.createActions().sendKeys(Keys.TAB).perform();

		browser.clear(firstNameFieldXpath);
		browser.createActions().sendKeys(Keys.TAB).perform();

		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		browser.waitForElementVisible(firstNameFieldErrorXpath);
		SeleniumAssert.assertElementTextVisible(browser, firstNameFieldErrorXpath, "Value is required");

		browser.sendKeys(firstNameFieldXpath, "asdf");
		browser.createActions().sendKeys(Keys.TAB).perform();
		browser.waitForElementNotPresent(firstNameFieldErrorXpath);
		SeleniumAssert.assertElementNotPresent(browser, firstNameFieldErrorXpath);
		browser.clear(firstNameFieldXpath);
	}

	/**
	 * This test is not valid in JSF 1.2 because f:validateRegex does not exist in JSF 1.2.
	 */
	@Ignore
	@Override
	public void runApplicantPortletTest_D_EmailValidation() {
		Assume.assumeTrue(false);
	}

	@Override
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);
		browser.click(getSubmitButtonXpath());

		String firstNameFieldErrorXpath = getFieldErrorXpath(getFirstNameFieldXpath());
		browser.waitForElementPresent(firstNameFieldErrorXpath);
		SeleniumAssert.assertElementTextVisible(browser, firstNameFieldErrorXpath, "Value is required");
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
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(getPostalCodeFieldXpath()),
			"Value is required");
	}

	/**
	 * The auto-populate city and state feature does not work in a test environment.
	 */
	@Ignore
	@Override
	public void runApplicantPortletTest_F_AutoPopulateCityState() {
		Assume.assumeTrue(false);
	}

	@Override
	public void runApplicantPortletTest_H_DateValidation() {

		Browser browser = Browser.getInstance();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browser.clear(dateOfBirthFieldXpath);
		browser.centerElementInView(dateOfBirthFieldXpath);
		browser.sendKeys(dateOfBirthFieldXpath, "12/34/5678");
		browser.createActions().sendKeys(Keys.TAB).perform();

		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		browser.waitForElementVisible(dateOfBirthFieldErrorXpath);
		SeleniumAssert.assertElementTextVisible(browser, dateOfBirthFieldErrorXpath, "Invalid date format");
		browser.clear(dateOfBirthFieldXpath);
		browser.sendKeys(dateOfBirthFieldXpath, "01/02/3456");
		browser.createActions().sendKeys(Keys.TAB).perform();
		browser.waitForElementNotPresent(dateOfBirthFieldErrorXpath);
		SeleniumAssert.assertElementNotPresent(browser, dateOfBirthFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_I_FileUpload() {

		Browser browser = Browser.getInstance();
		switchToFileUploadIframe(browser);

		try {
			super.runApplicantPortletTest_I_FileUpload();
		}
		finally {

			// Workaround for https://github.com/ariya/phantomjs/issues/13647
			browser.switchTo().window(browser.getWindowHandle());
		}
	}

	@Override
	protected void assertFileUploadChooserVisible(Browser browser) {

		switchToFileUploadIframe(browser);
		super.assertFileUploadChooserVisible(browser);

		// Workaround for https://github.com/ariya/phantomjs/issues/13647
		browser.switchTo().window(browser.getWindowHandle());
	}

	protected String getContext() {
		return BridgeTestUtil.getDemoContext("icefaces-applicant");
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {
		String errorMessageXpath = "/../span/span[@class='iceMsgError portlet-msg-error']";

		return "(" + fieldXpath + errorMessageXpath + "|" + fieldXpath + "/.." + errorMessageXpath + "|" + fieldXpath +
			"/../.." + errorMessageXpath + ")";
	}

	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post' and @enctype]/input[@type='submit' and @value='Upload']";
	}

	protected String getUploadedFileXpath() {
		return "//tr[contains(@class,'portlet-section-body results-row')]/td[2]";
	}

	protected void selectDate(Browser browser) {

		String datePickerPopupButtonXpath = "//input[contains(@id,':dateOfBirth')][@class='iceSelInpDateOpenPopup']";
		browser.centerElementInView(datePickerPopupButtonXpath);
		browser.click(datePickerPopupButtonXpath);

		String dateElement = "//table[contains(@class, 'iceSelInpDate')]//span[contains(text(), '14')]";
		browser.waitForElementVisible(dateElement);
		browser.click(dateElement);
	}

	protected final void submitAndWaitForPostback(Browser browser) {

		String submitButtonXpath = getSubmitButtonXpath();
		WebElement submitButton = browser.findElementByXpath(submitButtonXpath);
		submitButton.click();
		browser.waitUntil(ExpectedConditions.stalenessOf(submitButton));
		browser.waitForElementVisible(submitButtonXpath);
	}

	@Override
	protected void submitFile(Browser browser) {

		browser.click(getSubmitFileButtonXpath());

		// Workaround for https://github.com/ariya/phantomjs/issues/13647
		browser.switchTo().window(browser.getWindowHandle());
		browser.waitForElementVisible(getUploadedFileXpath());
	}

	private void switchToFileUploadIframe(Browser browser) {

		String fileUploadIframeXpath = "//iframe[contains(@id,':uploadFrame')]";
		WebElement fileUploadIframe = browser.findElementByXpath(fileUploadIframeXpath);
		String fileUploadIframeId = fileUploadIframe.getAttribute("id");
		browser.switchTo().frame(fileUploadIframeId);
	}
}
