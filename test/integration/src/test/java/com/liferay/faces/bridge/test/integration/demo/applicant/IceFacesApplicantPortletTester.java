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
import com.liferay.faces.test.selenium.applicant.ApplicantTesterBase;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IceFacesApplicantPortletTester extends ApplicantTesterBase {

	@Override
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + getContext());

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertElementDisplayed(getFirstNameFieldXpath());
		waitingAsserter.assertElementDisplayed(getLastNameFieldXpath());
		waitingAsserter.assertElementDisplayed(getEmailAddressFieldXpath());
		waitingAsserter.assertElementDisplayed(getPhoneNumberFieldXpath());
		waitingAsserter.assertElementDisplayed(getDateOfBirthFieldXpath());
		waitingAsserter.assertElementDisplayed(getCityFieldXpath());
		waitingAsserter.assertElementDisplayed(getProvinceIdFieldXpath());
		waitingAsserter.assertElementDisplayed(getPostalCodeFieldXpath());
		waitingAsserter.assertElementDisplayed(getShowHideCommentsLinkXpath());
		assertFileUploadChooserDisplayed(browserDriver, waitingAsserter);
		assertLibraryElementDisplayed(waitingAsserter, "Mojarra", browserDriver);
		assertLibraryElementDisplayed(waitingAsserter, "Liferay Faces Alloy", browserDriver);
		assertLibraryElementDisplayed(waitingAsserter, "Liferay Faces Bridge for ICEfaces", browserDriver);
		assertLibraryElementDisplayed(waitingAsserter, "ICEfaces", browserDriver);
	}

	@Override
	public void runApplicantPortletTest_B_EditMode() {

		// Test that changing the date pattern via preferences changes the Birthday value in the portlet.
		BrowserDriver browserDriver = getBrowserDriver();
		String editModeXpath = getEditModeXpath();
		browserDriver.clickElement(editModeXpath);

		String datePatternPreferencesXpath = getDatePatternPreferencesXpath();

		try {
			browserDriver.waitForElementEnabled(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		browserDriver.clearElement(datePatternPreferencesXpath);

		String newDatePattern = "MM/dd/yy";
		browserDriver.sendKeysToElement(datePatternPreferencesXpath, newDatePattern);

		String preferencesSubmitButtonXpath = getPreferencesSubmitButtonXpath();
		browserDriver.clickElement(preferencesSubmitButtonXpath);

		String requestProcessedSuccessfullyTextXpath =
			"//ul//li//span[contains(text(), 'Your request processed successfully.')]";
		browserDriver.waitForElementEnabled(requestProcessedSuccessfullyTextXpath);

		String returnToFullPageToolbarLinkXpath =
			"//menu[contains(@type,'toolbar')]//a[contains(text(), 'Return to Full Page')]";
		browserDriver.clickElement(returnToFullPageToolbarLinkXpath);

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();

		try {
			browserDriver.waitForElementEnabled(dateOfBirthFieldXpath);
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
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElementValue(todayString, dateOfBirthFieldXpath);

		// Test that resetting the date pattern via preferences changes the Birthday year back to the long version.
		browserDriver.clickElement(editModeXpath);

		try {
			browserDriver.waitForElementEnabled(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String preferencesResetButtonXpath = getPreferencesResetButtonXpath();
		browserDriver.clickElement(preferencesResetButtonXpath);
		browserDriver.waitForElementEnabled(requestProcessedSuccessfullyTextXpath);

		browserDriver.clickElement(returnToFullPageToolbarLinkXpath);

		try {
			browserDriver.waitForElementEnabled(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String oldDatePattern = "MM/dd/yyyy";
		simpleDateFormat.applyPattern(oldDatePattern);
		todayString = simpleDateFormat.format(today);
		waitingAsserter.assertTextPresentInElementValue(todayString, dateOfBirthFieldXpath);
	}

	@Override
	public void runApplicantPortletTest_C_FirstNameField() {

		BrowserDriver browserDriver = getBrowserDriver();
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();

		browserDriver.clearElement(firstNameFieldXpath);
		browserDriver.createActions().sendKeys(Keys.TAB).perform();

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);

		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();
		browserDriver.waitForElementNotDisplayed(firstNameFieldErrorXpath);
		waitingAsserter.assertElementNotDisplayed(firstNameFieldErrorXpath);
		browserDriver.clearElement(firstNameFieldXpath);
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

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);
		browserDriver.clickElement(getSubmitButtonXpath());

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String firstNameFieldErrorXpath = getFieldErrorXpath(getFirstNameFieldXpath());
		waitingAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getLastNameFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getEmailAddressFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getPhoneNumberFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getDateOfBirthFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getCityFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getProvinceIdFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getPostalCodeFieldXpath()));
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

		BrowserDriver browserDriver = getBrowserDriver();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browserDriver.clearElement(dateOfBirthFieldXpath);
		browserDriver.sendKeysToElement(dateOfBirthFieldXpath, "12/34/5678");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		waitingAsserter.assertTextPresentInElement("Invalid date format", dateOfBirthFieldErrorXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		browserDriver.sendKeysToElement(dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();
		browserDriver.waitForElementNotDisplayed(dateOfBirthFieldErrorXpath);
		waitingAsserter.assertElementNotDisplayed(dateOfBirthFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_I_FileUpload() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.switchToFrame("//iframe[contains(@id,':uploadFrame')]");

		try {
			super.runApplicantPortletTest_I_FileUpload();
		}
		finally {

			// Workaround for https://github.com/ariya/phantomjs/issues/13647
			browserDriver.switchToWindow(browserDriver.getCurrentWindowId());
		}
	}

	@Override
	protected void assertFileUploadChooserDisplayed(BrowserDriver browserDriver, WaitingAsserter waitingAsserter) {

		browserDriver.switchToFrame("//iframe[contains(@id,':uploadFrame')]");
		super.assertFileUploadChooserDisplayed(browserDriver, waitingAsserter);

		// Workaround for https://github.com/ariya/phantomjs/issues/13647
		browserDriver.switchToWindow(browserDriver.getCurrentWindowId());
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

	protected void selectDate(BrowserDriver browserDriver) {

		String datePickerPopupButtonXpath = "//input[contains(@id,':dateOfBirth')][@class='iceSelInpDateOpenPopup']";
		browserDriver.clickElement(datePickerPopupButtonXpath);

		String dateElement = "//table[contains(@class, 'iceSelInpDate')]//span[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateElement);
		browserDriver.clickElement(dateElement);
	}

	protected final void submitAndWaitForPostback(BrowserDriver browserDriver) {

		String submitButtonXpath = getSubmitButtonXpath();
		WebElement submitButton = browserDriver.findElementByXpath(submitButtonXpath);
		submitButton.click();
		browserDriver.waitFor(ExpectedConditions.stalenessOf(submitButton));
		browserDriver.waitForElementEnabled(submitButtonXpath);
	}

	@Override
	protected void submitFile(BrowserDriver browserDriver) {

		browserDriver.clickElement(getSubmitFileButtonXpath());

		// Workaround for https://github.com/ariya/phantomjs/issues/13647
		browserDriver.switchToWindow(browserDriver.getCurrentWindowId());
		browserDriver.waitForElementDisplayed(getUploadedFileXpath());
	}
}
