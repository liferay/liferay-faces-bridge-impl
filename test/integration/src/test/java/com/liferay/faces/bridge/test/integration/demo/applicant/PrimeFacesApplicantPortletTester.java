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

import org.openqa.selenium.WebElement;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;


/**
 * @author  Kyle Stiemann
 */
public class PrimeFacesApplicantPortletTester extends BridgeApplicantPortletTester {

	// Private Constants
	private static final String SELECT_PROVINCE_ID_XPATH = "//select[contains(@id,':provinceId')]";

	@Override
	public void runApplicantPortletTest_F_AutoPopulateCityState() {

		BrowserDriver browserDriver = getBrowserDriver();
		sendKeysTabAndWaitForRerender(browserDriver, getPostalCodeFieldXpath(), "32801");

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		browserStateAsserter.assertTextPresentInElementValue("Orlando", getCityFieldXpath());
		browserStateAsserter.assertTextPresentInElementValue("3", SELECT_PROVINCE_ID_XPATH, false);
	}

	@Override
	protected void assertFileUploadChooserDisplayed(BrowserDriver browserDriver,
		BrowserStateAsserter browserStateAsserter) {
		browserStateAsserter.assertElementDisplayed(getFileUploadChooserXpath() + "/..");
	}

	@Override
	protected void clearProvince(BrowserDriver browserDriver) {
		selectProvinceOption(browserDriver, "0");
	}

	@Override
	protected String getExtraLibraryName() {
		return "PrimeFaces";
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {

		String errorMessage =
			"/../div[contains(@class, 'ui-message-error')]/span[contains(@class, 'ui-message-error-detail')]";

		return "(" + fieldXpath + errorMessage + "|" + fieldXpath + "/.." + errorMessage + ")";
	}

	@Override
	protected String getPortletPageName() {
		return "primefaces-applicant";
	}

	@Override
	protected String getProvinceIdFieldXpath() {
		return "//div[contains(@id,':provinceId')]";
	}

	@Override
	protected String getSubmitAnotherApplicationButton() {
		return "//button[@type='submit'][contains(., 'Submit Another Application')]";
	}

	@Override
	protected String getSubmitButtonXpath() {
		return "//button/span[contains(text(),'Submit')]/..";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return "//button/span[contains(text(),'Upload')]/..";
	}

	@Override
	protected String getUploadedFileXpath() {
		return "//tbody[contains(@id, ':uploadedFilesTable')]/tr/td[2]";
	}

	@Override
	protected void selectDate(BrowserDriver browserDriver) {

		browserDriver.clickElement("//button[contains(@class, 'ui-datepicker-trigger')]");

		String dateCellXpath = "//table[contains(@class, 'ui-datepicker-calendar')]//a[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateCellXpath);
		browserDriver.clickElement(dateCellXpath);
	}

	@Override
	protected void selectProvince(BrowserDriver browserDriver) {
		selectProvinceOption(browserDriver, "3");
	}

	@Override
	protected void submitFile(BrowserDriver browserDriver) {

		super.submitFile(browserDriver);
		browserDriver.waitForElementNotDisplayed("//table[contains(@class, 'ui-fileupload-files')]/tbody/tr");
	}

	private void selectProvinceOption(BrowserDriver browserDriver, String optionValue) {

		// p:selectOneMenu becomes indisplayed to selenium after interacting with it once, so use JavaScript to set the
		// value.
		WebElement provinceIdField = browserDriver.findElementByXpath(SELECT_PROVINCE_ID_XPATH);
		browserDriver.executeScriptInCurrentWindow("arguments[0].value=" + optionValue, provinceIdField);
	}
}
