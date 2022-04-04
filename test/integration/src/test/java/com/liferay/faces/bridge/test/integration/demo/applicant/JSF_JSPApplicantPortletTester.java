/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.io.IOException;

import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Ignore;

import org.junit.runners.MethodSorters;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JSF_JSPApplicantPortletTester extends JSFApplicantPortletTester {

	@Override
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		BrowserDriver browserDriver = getBrowserDriver();
		navigateWindowToDemo(browserDriver);

		// Wait to begin the test until the first name field is rendered.
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertElementDisplayed(getFirstNameFieldXpath());
		waitingAsserter.assertElementDisplayed(getLastNameFieldXpath());
		waitingAsserter.assertElementDisplayed(getEmailAddressFieldXpath());
		waitingAsserter.assertElementDisplayed(getPhoneNumberFieldXpath());
		waitingAsserter.assertElementDisplayed(getDateOfBirthFieldXpath());
		waitingAsserter.assertElementDisplayed(getCityFieldXpath());
		waitingAsserter.assertElementDisplayed(getProvinceIdFieldXpath());
		waitingAsserter.assertElementDisplayed(getPostalCodeFieldXpath());
		waitingAsserter.assertElementDisplayed(getAddAttachmentXpath());
		assertLibraryElementDisplayed(waitingAsserter, browserDriver, "Mojarra");
		assertLibraryElementDisplayed(waitingAsserter, browserDriver, "Liferay Faces Bridge Impl");

		if (TestUtil.getContainer().contains("liferay")) {
			assertLibraryElementDisplayed(waitingAsserter, browserDriver, "Liferay Faces Bridge Ext");
		}
	}

	@Override
	public void runApplicantPortletTest_C_FirstNameField() {

		BrowserDriver browserDriver = getBrowserDriver();
		submitPostalCodeAndWaitForPostback(browserDriver, "32802");

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		waitingAsserter.assertElementNotDisplayed(firstNameFieldErrorXpath);
		browserDriver.clearElement(firstNameFieldXpath);
		browserDriver.clickElement(submitButtonXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_D_EmailValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		String emailAddressFieldXpath = getEmailAddressFieldXpath();
		browserDriver.sendKeysToElement(emailAddressFieldXpath, "test");

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String emailAddressFieldErrorXpath = getFieldErrorXpath(emailAddressFieldXpath);
		waitingAsserter.assertTextPresentInElement("Invalid e-mail address", emailAddressFieldErrorXpath);
		browserDriver.clearElement(emailAddressFieldXpath);
		browserDriver.sendKeysToElement(emailAddressFieldXpath, "test@liferay.com");
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);
		waitingAsserter.assertElementNotDisplayed(emailAddressFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(postalCodeFieldXpath));
		submitPostalCodeAndWaitForPostback(browserDriver, "32802");
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getFirstNameFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getLastNameFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getEmailAddressFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getPhoneNumberFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getDateOfBirthFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getCityFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getProvinceIdFieldXpath()));
		clearAllFields(browserDriver);
	}

	/**
	 * The auto-populate city and state feature does not exist in the JSP applicant portlet.
	 */
	@Ignore
	@Override
	public void runApplicantPortletTest_F_AutoPopulateCityState() {
		Assume.assumeTrue(false);
	}

	/**
	 * The comments feature does not exist in the JSP applicant portlet.
	 */
	@Ignore
	@Override
	public void runApplicantPortletTest_G_Comments() {
		Assume.assumeTrue(false);
	}

	@Override
	public void runApplicantPortletTest_H_DateValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browserDriver.waitForElementEnabled(dateOfBirthFieldXpath);
		submitPostalCodeAndWaitForPostback(browserDriver, "32802");
		browserDriver.clearElement(dateOfBirthFieldXpath);
		browserDriver.sendKeysToElement(dateOfBirthFieldXpath, "12/34/5678");

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		waitingAsserter.assertTextPresentInElement("Invalid date format", dateOfBirthFieldErrorXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		browserDriver.sendKeysToElement(dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);
		waitingAsserter.assertElementNotDisplayed(dateOfBirthFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_I_FileUpload() throws IOException {

		BrowserDriver browserDriver = getBrowserDriver();
		String addAttachmentXpath = getAddAttachmentXpath();
		browserDriver.waitForElementEnabled(addAttachmentXpath);
		browserDriver.clickElement(addAttachmentXpath);
		browserDriver.waitForElementEnabled(getFileUploadChooserXpath());
		super.runApplicantPortletTest_I_FileUpload();
	}

	@Override
	public void runApplicantPortletTest_J_Submit() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElement(submitButtonXpath);

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.waitForElementEnabled(firstNameFieldXpath);

		browserDriver.sendKeysToElement(firstNameFieldXpath, "David");
		browserDriver.sendKeysToElement(getLastNameFieldXpath(), "Samuel");
		browserDriver.sendKeysToElement(getEmailAddressFieldXpath(), "no_need@just.pray");
		browserDriver.sendKeysToElement(getPhoneNumberFieldXpath(), "(way) too-good");
		selectDate(browserDriver);
		browserDriver.sendKeysToElement(getCityFieldXpath(), "North Orlando");
		selectProvince(browserDriver);
		submitPostalCodeAndWaitForPostback(browserDriver, "32802");
		browserDriver.clickElement(submitButtonXpath);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("Thank you David", getConfimationFormXpath());
	}

	@Override
	protected void clearAllFields(BrowserDriver browserDriver) {

		browserDriver.clearElement(getFirstNameFieldXpath());
		browserDriver.clearElement(getLastNameFieldXpath());
		browserDriver.clearElement(getEmailAddressFieldXpath());
		browserDriver.clearElement(getPhoneNumberFieldXpath());
		browserDriver.clearElement(getDateOfBirthFieldXpath());
		browserDriver.clearElement(getCityFieldXpath());
		clearProvince(browserDriver);

		submitPostalCodeAndWaitForPostback(browserDriver, Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE,
			Keys.BACK_SPACE, Keys.BACK_SPACE);
	}

	protected String getAddAttachmentXpath() {
		return "//input[contains(@value,'Add Attachment')]";
	}

	@Override
	protected String getEditModeXpath() {
		return "//input[contains(@value,'Edit Preferences')]";
	}

	@Override
	protected String getFileUploadChooserXpath() {
		return "(//input[@type='file'])[1]";
	}

	@Override
	protected String getPortletPageName() {
		return "jsf-jsp-applicant";
	}

	protected final void submitPostalCodeAndWaitForPostback(BrowserDriver browserDriver, CharSequence... postalCode) {

		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		Actions actions = browserDriver.createActions(postalCodeFieldXpath);
		WebElement postalCodeField = browserDriver.findElementByXpath(postalCodeFieldXpath);
		actions.sendKeys(postalCodeField, postalCode);
		actions.sendKeys(Keys.TAB);
		browserDriver.performAndWaitForRerender(actions.build(), postalCodeFieldXpath);
	}
}
