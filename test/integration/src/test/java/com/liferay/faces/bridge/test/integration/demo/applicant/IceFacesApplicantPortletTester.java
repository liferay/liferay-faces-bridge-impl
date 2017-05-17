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
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.applicant.ApplicantTesterBase;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;


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

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		browserStateAsserter.assertElementDisplayed(getFirstNameFieldXpath());
		browserStateAsserter.assertElementDisplayed(getLastNameFieldXpath());
		browserStateAsserter.assertElementDisplayed(getEmailAddressFieldXpath());
		browserStateAsserter.assertElementDisplayed(getPhoneNumberFieldXpath());
		browserStateAsserter.assertElementDisplayed(getDateOfBirthFieldXpath());
		browserStateAsserter.assertElementDisplayed(getCityFieldXpath());
		browserStateAsserter.assertElementDisplayed(getProvinceIdFieldXpath());
		browserStateAsserter.assertElementDisplayed(getPostalCodeFieldXpath());
		browserStateAsserter.assertElementDisplayed(getShowHideCommentsLinkXpath());
		assertFileUploadChooserDisplayed(browserDriver, browserStateAsserter);
		assertLibraryElementDisplayed(browserStateAsserter, "Mojarra", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Alloy", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge for ICEfaces", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "ICEfaces", browserDriver);
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
		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		browserStateAsserter.assertTextPresentInElementValue(todayString, dateOfBirthFieldXpath);

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
		browserStateAsserter.assertTextPresentInElementValue(todayString, dateOfBirthFieldXpath);
	}

	@Override
	public void runApplicantPortletTest_C_FirstNameField() {

		BrowserDriver browserDriver = getBrowserDriver();
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();

		browserDriver.clearElement(firstNameFieldXpath);
		browserDriver.createActions().sendKeys(Keys.TAB).perform();

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		browserStateAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);

		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();
		browserDriver.waitForElementNotDisplayed(firstNameFieldErrorXpath);
		browserStateAsserter.assertElementNotDisplayed(firstNameFieldErrorXpath);
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

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String firstNameFieldErrorXpath = getFieldErrorXpath(getFirstNameFieldXpath());
		browserStateAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getLastNameFieldXpath()));
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getEmailAddressFieldXpath()));
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getPhoneNumberFieldXpath()));
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getDateOfBirthFieldXpath()));
		browserStateAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getCityFieldXpath()));
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getProvinceIdFieldXpath()));
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getPostalCodeFieldXpath()));
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

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		browserStateAsserter.assertTextPresentInElement("Invalid date format", dateOfBirthFieldErrorXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		browserDriver.sendKeysToElement(dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.createActions().sendKeys(Keys.TAB).perform();
		browserDriver.waitForElementNotDisplayed(dateOfBirthFieldErrorXpath);
		browserStateAsserter.assertElementNotDisplayed(dateOfBirthFieldErrorXpath);
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
	protected void assertFileUploadChooserDisplayed(BrowserDriver browserDriver,
		BrowserStateAsserter browserStateAsserter) {

		browserDriver.switchToFrame("//iframe[contains(@id,':uploadFrame')]");
		super.assertFileUploadChooserDisplayed(browserDriver, browserStateAsserter);

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
