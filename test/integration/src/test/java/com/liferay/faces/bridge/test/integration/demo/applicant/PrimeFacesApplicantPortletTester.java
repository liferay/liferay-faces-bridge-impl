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

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 */
public class PrimeFacesApplicantPortletTester extends BridgeApplicantPortletTester {

	// Private Constants
	private static final String SELECT_PROVINCE_ID_XPATH = "//select[contains(@id,':provinceId')]";

	@Override
	public void runApplicantPortletTest_F_AutoPopulateCityState() {

		Browser browser = Browser.getInstance();
		sendKeysTabAndWaitForAjaxRerender(browser, getPostalCodeFieldXpath(), "32801");
		SeleniumAssert.assertElementValue(browser, getCityFieldXpath(), "Orlando");
		SeleniumAssert.assertElementValue(browser, SELECT_PROVINCE_ID_XPATH, "3", false);
	}

	@Override
	protected void assertFileUploadChooserVisible(Browser browser) {
		SeleniumAssert.assertElementVisible(browser, getFileUploadChooserXpath() + "/..");
	}

	@Override
	protected void clearProvince(Browser browser) {
		selectProvinceOption(browser, "0");
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
	protected void selectDate(Browser browser) {

		String datePickerTriggerXpath = "//button[contains(@class, 'ui-datepicker-trigger')]";
		browser.centerElementInView(datePickerTriggerXpath);
		browser.click(datePickerTriggerXpath);

		String dateElement = "//table[contains(@class, 'ui-datepicker-calendar')]//a[contains(text(), '14')]";
		browser.waitForElementVisible(dateElement);
		browser.click(dateElement);
	}

	@Override
	protected void selectProvince(Browser browser) {
		selectProvinceOption(browser, "3");
	}

	@Override
	protected void submitFile(Browser browser) {

		super.submitFile(browser);
		browser.waitForElementNotPresent("//table[contains(@class, 'ui-fileupload-files')]/tbody/tr");
	}

	private void selectProvinceOption(Browser browser, String optionValue) {

		// p:selectOneMenu becomes invisible to selenium after interacting with it once, so use JavaScript to set the
		// value.
		WebElement provinceIdField = browser.findElementByXpath(SELECT_PROVINCE_ID_XPATH);
		browser.executeScript("arguments[0].value=" + optionValue, provinceIdField);
	}
}
