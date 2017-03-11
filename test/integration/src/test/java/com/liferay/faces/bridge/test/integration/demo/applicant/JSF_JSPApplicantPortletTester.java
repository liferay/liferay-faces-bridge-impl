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

import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Ignore;

import org.junit.runners.MethodSorters;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JSF_JSPApplicantPortletTester extends JSFApplicantPortletTester {

	@Override
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		Browser browser = Browser.getInstance();
		browser.get(TestUtil.DEFAULT_BASE_URL + getContext());

		// Wait to begin the test until the first name field is rendered.
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.waitForElementVisible(firstNameFieldXpath);

		SeleniumAssert.assertElementVisible(browser, firstNameFieldXpath);
		SeleniumAssert.assertElementVisible(browser, getLastNameFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getEmailAddressFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getPhoneNumberFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getDateOfBirthFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getCityFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getProvinceIdFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getPostalCodeFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getAddAttachmentXpath());
		SeleniumAssert.assertLibraryVisible(browser, "Mojarra");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Impl");

		if (TestUtil.getContainer().contains("liferay")) {
			SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Ext");
		}

		String extraLibraryName = getExtraLibraryName();

		if (extraLibraryName != null) {
			SeleniumAssert.assertLibraryVisible(browser, getExtraLibraryName());
		}
	}

	@Override
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

	@Override
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

	@Override
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);
		browser.click(getSubmitButtonXpath());

		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		browser.waitForElementVisible(postalCodeFieldXpath);
		SeleniumAssert.assertElementTextVisible(browser, getFieldErrorXpath(postalCodeFieldXpath), "Value is required");
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

		Browser browser = Browser.getInstance();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browser.waitForElementVisible(dateOfBirthFieldXpath);
		submitPostalCodeAndWaitForPostback(browser, "32802");
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

	@Override
	public void runApplicantPortletTest_I_FileUpload() {

		Browser browser = Browser.getInstance();
		String addAttachmentXpath = getAddAttachmentXpath();
		browser.waitForElementVisible(addAttachmentXpath);
		browser.click(addAttachmentXpath);
		browser.waitForElementVisible(getFileUploadChooserXpath());
		super.runApplicantPortletTest_I_FileUpload();
	}

	@Override
	public void runApplicantPortletTest_J_Submit() {

		Browser browser = Browser.getInstance();
		clearAllFields(browser);

		String submitButtonXpath = getSubmitButtonXpath();
		browser.click(submitButtonXpath);

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.waitForElementVisible(firstNameFieldXpath);

		browser.sendKeys(firstNameFieldXpath, "David");
		browser.sendKeys(getLastNameFieldXpath(), "Samuel");
		browser.sendKeys(getEmailAddressFieldXpath(), "no_need@just.pray");
		browser.sendKeys(getPhoneNumberFieldXpath(), "(way) too-good");
		selectDate(browser);
		browser.sendKeys(getCityFieldXpath(), "North Orlando");
		selectProvince(browser);
		submitPostalCodeAndWaitForPostback(browser, "32802");
		browser.click(submitButtonXpath);
		browser.waitForElementVisible(getSubmitAnotherApplicationButton());
		SeleniumAssert.assertElementTextVisible(browser, getConfimationFormXpath(), "Dear David,");
	}

	@Override
	protected void clearAllFields(Browser browser) {

		browser.clear(getFirstNameFieldXpath());
		browser.clear(getLastNameFieldXpath());
		browser.clear(getEmailAddressFieldXpath());
		browser.clear(getPhoneNumberFieldXpath());
		browser.clear(getDateOfBirthFieldXpath());
		browser.clear(getCityFieldXpath());
		clearProvince(browser);

		Keys[] clearPostalCodeKeys = {
				Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE
			};
		submitPostalCodeAndWaitForPostback(browser, clearPostalCodeKeys);
	}

	protected String getAddAttachmentXpath() {
		return "//input[contains(@value,'Add Attachment')]";
	}

	@Override
	protected String getContext() {
		return BridgeTestUtil.getDemoContext("jsf-jsp-applicant");
	}

	@Override
	protected String getEditModeXpath() {
		return "//input[contains(@value,'Edit Preferences')]";
	}

	@Override
	protected String getFileUploadChooserXpath() {
		return "(//input[@type='file'])[1]";
	}

	protected final void submitAndWaitForPostback(Browser browser) {

		String submitButtonXpath = getSubmitButtonXpath();
		WebElement submitButton = browser.findElementByXpath(submitButtonXpath);
		submitButton.click();
		browser.waitUntil(ExpectedConditions.stalenessOf(submitButton));
		browser.waitForElementVisible(submitButtonXpath);
	}

	protected final void submitPostalCodeAndWaitForPostback(Browser browser, CharSequence... postalCode) {

		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		WebElement postalCodeField = browser.findElementByXpath(postalCodeFieldXpath);
		postalCodeField.sendKeys(postalCode);
		postalCodeField.sendKeys(Keys.TAB);
		browser.waitUntil(ExpectedConditions.stalenessOf(postalCodeField));
		browser.waitForElementVisible(postalCodeFieldXpath);
	}
}
