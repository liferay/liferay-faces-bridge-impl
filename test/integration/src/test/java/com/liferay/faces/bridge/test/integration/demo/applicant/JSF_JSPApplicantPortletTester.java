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
import org.openqa.selenium.interactions.Actions;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JSF_JSPApplicantPortletTester extends JSFApplicantPortletTester {

	@Override
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + getContext());

		// Wait to begin the test until the first name field is rendered.
		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		browserStateAsserter.assertElementDisplayed(getFirstNameFieldXpath());
		browserStateAsserter.assertElementDisplayed(getLastNameFieldXpath());
		browserStateAsserter.assertElementDisplayed(getEmailAddressFieldXpath());
		browserStateAsserter.assertElementDisplayed(getPhoneNumberFieldXpath());
		browserStateAsserter.assertElementDisplayed(getDateOfBirthFieldXpath());
		browserStateAsserter.assertElementDisplayed(getCityFieldXpath());
		browserStateAsserter.assertElementDisplayed(getProvinceIdFieldXpath());
		browserStateAsserter.assertElementDisplayed(getPostalCodeFieldXpath());
		browserStateAsserter.assertElementDisplayed(getAddAttachmentXpath());
		assertLibraryElementDisplayed(browserStateAsserter, "Mojarra", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge Impl", browserDriver);

		if (TestUtil.getContainer().contains("liferay")) {
			assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge Ext", browserDriver);
		}
	}

	@Override
	public void runApplicantPortletTest_C_FirstNameField() {

		BrowserDriver browserDriver = getBrowserDriver();
		submitPostalCodeAndWaitForPostback(browserDriver, "32802");

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElement(submitButtonXpath);

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		browserStateAsserter.assertElementNotDisplayed(firstNameFieldErrorXpath);
		browserDriver.clearElement(firstNameFieldXpath);
		browserDriver.clickElement(submitButtonXpath);
		browserStateAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_D_EmailValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		String emailAddressFieldXpath = getEmailAddressFieldXpath();
		browserDriver.sendKeysToElement(emailAddressFieldXpath, "test");

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String emailAddressFieldErrorXpath = getFieldErrorXpath(emailAddressFieldXpath);
		browserStateAsserter.assertTextPresentInElement("Invalid e-mail address", emailAddressFieldErrorXpath);
		browserDriver.clearElement(emailAddressFieldXpath);
		browserDriver.sendKeysToElement(emailAddressFieldXpath, "test@liferay.com");
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);
		browserStateAsserter.assertElementNotDisplayed(emailAddressFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);

		String submitButtonXpath = getSubmitButtonXpath();
		browserDriver.clickElement(submitButtonXpath);

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		browserStateAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(postalCodeFieldXpath));
		submitPostalCodeAndWaitForPostback(browserDriver, "32802");
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);
		browserStateAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getFirstNameFieldXpath()));
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

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		browserStateAsserter.assertTextPresentInElement("Invalid date format", dateOfBirthFieldErrorXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		browserDriver.sendKeysToElement(dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.clickElementAndWaitForRerender(submitButtonXpath);
		browserStateAsserter.assertElementNotDisplayed(dateOfBirthFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_I_FileUpload() {

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

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		browserStateAsserter.assertTextPresentInElement("Dear David,", getConfimationFormXpath());
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

		Keys[] clearPostalCodeKeys = {
				Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE
			};
		submitPostalCodeAndWaitForPostback(browserDriver, clearPostalCodeKeys);
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

	protected final void submitPostalCodeAndWaitForPostback(BrowserDriver browserDriver, CharSequence... postalCode) {

		String postalCodeFieldXpath = getPostalCodeFieldXpath();
		Actions actions = browserDriver.createActions(postalCodeFieldXpath);
		WebElement postalCodeField = browserDriver.findElementByXpath(postalCodeFieldXpath);
		actions.sendKeys(postalCodeField, postalCode);
		actions.sendKeys(Keys.TAB);
		browserDriver.performAndWaitForRerender(actions.build(), postalCodeFieldXpath);
	}
}
