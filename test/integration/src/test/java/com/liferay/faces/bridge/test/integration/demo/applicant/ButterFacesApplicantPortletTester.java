/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
import org.junit.Before;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class ButterFacesApplicantPortletTester extends ApplicantTesterBase {

	// Private Constants
	private static final String TOOLTIP_XPATH_FRAGMENT =
		"/following-sibling::div[@role='tooltip']//*[contains(@class,'butter-component-tooltip')][contains(@class,'error')]";

	@Override
	public void runApplicantPortletTest_C_FirstNameField() {

		BrowserDriver browserDriver = getBrowserDriver();
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.createActions().sendKeys(Keys.TAB).perform();
		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");

		String lastNameFieldXpath = getLastNameFieldXpath();
		Action lastNameFieldClick = browserDriver.createClickElementAction(lastNameFieldXpath);
		browserDriver.performAndWaitForRerender(lastNameFieldClick, firstNameFieldXpath);

		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertElementNotDisplayed(firstNameFieldErrorXpath);
		browserDriver.clearElement(firstNameFieldXpath);
		lastNameFieldClick = browserDriver.createClickElementAction(lastNameFieldXpath);
		browserDriver.performAndWaitForRerender(lastNameFieldClick, firstNameFieldXpath);
		mouseOverElement(browserDriver, firstNameFieldXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_D_EmailValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.createActions().sendKeys(Keys.TAB).perform();

		String emailAddressFieldXpath = getEmailAddressFieldXpath();
		sendKeysTabAndWaitForRerender(browserDriver, emailAddressFieldXpath, "test");
		mouseOverElement(browserDriver, emailAddressFieldXpath);

		String emailAddressFieldErrorXpath = getFieldErrorXpath(emailAddressFieldXpath);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("Invalid e-mail address", emailAddressFieldErrorXpath);
		sendKeysTabAndWaitForRerender(browserDriver, emailAddressFieldXpath, "@liferay.com");
		waitingAsserter.assertElementNotDisplayed(emailAddressFieldErrorXpath);
	}

	@Override
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);
		browserDriver.clickElementAndWaitForRerender(getSubmitButtonXpath());

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		assertFieldRequired(browserDriver, waitingAsserter, getPostalCodeFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getProvinceIdFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getCityFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getDateOfBirthFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getPhoneNumberFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getEmailAddressFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getLastNameFieldXpath());
		assertFieldRequired(browserDriver, waitingAsserter, getFirstNameFieldXpath());
		mouseOverElement(browserDriver, getShowHideCommentsLinkXpath());
		browserDriver.waitForElementNotDisplayed(getFieldErrorXpath("//*"));
	}

	@Override
	public void runApplicantPortletTest_H_DateValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browserDriver.centerElementInCurrentWindow(dateOfBirthFieldXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "12/34/5678");
		mouseOverElement(browserDriver, dateOfBirthFieldXpath);

		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("Invalid date format", dateOfBirthFieldErrorXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "01/02/3456");
		waitingAsserter.assertElementNotDisplayed(dateOfBirthFieldErrorXpath);
	}

	@Before
	public void skipTestOnLiferay62() {

		// BootsFaces is unsupported on Liferay 6.2 due to Bootstrap incompatibilities.
		Assume.assumeFalse("liferay62".equals(TestUtil.getContainer()));
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {
		return "(" + fieldXpath + "/../.." + TOOLTIP_XPATH_FRAGMENT + "|" + fieldXpath + TOOLTIP_XPATH_FRAGMENT + ")";
	}

	@Override
	protected String getPortletPageName() {
		return "butterfaces-applicant";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post'][@enctype='multipart/form-data']/a[contains(.,'Submit')]";
	}

	@Override
	protected void selectDate(BrowserDriver browserDriver) {

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.clickElement("//span[contains(@class,'icon-calendar')]");

		String dateCellXpath = "//div[contains(@class, 'datepicker-days')]//td[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateCellXpath);
		browserDriver.clickElement(dateCellXpath);
	}

	private void assertFieldRequired(BrowserDriver browserDriver, WaitingAsserter waitingAsserter, String fieldXpath) {

		mouseOverElement(browserDriver, fieldXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(fieldXpath));
	}

	private void mouseOverElement(BrowserDriver browserDriver, String elementXpath) {

		WebElement webElement = browserDriver.findElementByXpath(elementXpath);
		browserDriver.createActions().moveToElement(webElement).perform();
	}
}
