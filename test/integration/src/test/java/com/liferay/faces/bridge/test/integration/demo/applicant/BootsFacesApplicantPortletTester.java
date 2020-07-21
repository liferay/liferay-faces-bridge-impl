/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public class BootsFacesApplicantPortletTester extends ApplicantTesterBase {

	@Override
	public void runApplicantPortletTest_G_Comments() {

		BrowserDriver browserDriver = getBrowserDriver();
		String showHideCommentsLinkXpath = getShowHideCommentsLinkXpath();
		browserDriver.clickElement(showHideCommentsLinkXpath);

		String commentsXpath = getCommentsXpath();
		WebElement commentsElement = browserDriver.findElementByXpath(commentsXpath);

		// Ensure that the value of the comments textarea are rendered inside the textarea (normally BootsFaces tracks
		// the value with JavaScript only).
		browserDriver.executeScriptInCurrentWindow(
			"arguments[0].addEventListener('keypress',function(e){this.innerHTML+=String.fromCharCode(e.charCode!=null?e.charCode:e.keyCode)});",
			commentsElement);
		browserDriver.sendKeysToElement(commentsXpath, "testing 1, 2, 3");
		browserDriver.clickElement(showHideCommentsLinkXpath);
		browserDriver.clickElement(showHideCommentsLinkXpath);
		getWaitingAsserter().assertTextPresentInElement("testing 1, 2, 3", commentsXpath, false);
	}

	@Before
	public void skipTestOnLiferay62() {

		// BootsFaces is unsupported on Liferay 6.2 due to Bootstrap incompatibilities.
		Assume.assumeFalse("liferay62".equals(TestUtil.getContainer()));
	}

	@Override
	protected String getDateOfBirthFieldXpath() {
		return "//input[contains(@id,':dateOfBirth_Input')]";
	}

	@Override
	protected String getExtraLibraryName() {
		return "BootsFaces";
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();

		if (fieldXpath.equals(dateOfBirthFieldXpath)) {
			fieldXpath = fieldXpath + "/..";
		}

		return fieldXpath + "/../following-sibling::div/div[contains(@class,'alert-danger')]";
	}

	@Override
	protected String getPortletPageName() {
		return "bootsfaces-applicant";
	}

	@Override
	protected String getPreferencesResetButtonXpath() {
		return "//button[@type='submit'][contains(text(),'Reset')]";
	}

	@Override
	protected String getPreferencesSubmitButtonXpath() {
		return "//button[@type='submit'][contains(text(),'Submit')]";
	}

	@Override
	protected String getShowHideCommentsLinkXpath() {
		return "//a[contains(text(),'Comments')]";
	}

	@Override
	protected String getSubmitAnotherApplicationButton() {
		return "//button[@type='submit'][contains(@value, 'Submit Another Application')]";
	}

	@Override
	protected String getSubmitButtonXpath() {
		return "//button[@type='submit'][contains(text(),'Submit')]";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return
			"//form[@method='post'][@enctype='multipart/form-data']/button[@type='submit'][contains(text(),'Submit')]";
	}

	@Override
	protected String getUploadedFileXpath() {
		return "//table[contains(@id,':attachmentsTable')]/tbody/tr[1]/td[2]";
	}

	@Override
	protected boolean isLiferayFacesAlloyIncluded() {
		return false;
	}

	@Override
	protected void selectDate(BrowserDriver browserDriver) {

		// Clicking a date on the calendar does not cause a rerender, so enter a valid date into the date picker to
		// remove the error message.
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.clickElement("//i[contains(@id, 'dateOfBirth_icon')]");

		String dateCellXpath = "//div[contains(@class, 'datepicker-days')]//td[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateCellXpath);
		browserDriver.clickElement(dateCellXpath);
	}

	@Override
	protected void selectProvince(BrowserDriver browserDriver) {

		// Clear the province Id field and wait for rerender.
		String provinceIdFieldXpath = getProvinceIdFieldXpath();
		WebElement provinceIdFieldElement = browserDriver.findElementByXpath(provinceIdFieldXpath);
		clearProvince(browserDriver);
		browserDriver.waitFor(ExpectedConditions.stalenessOf(provinceIdFieldElement));
		browserDriver.waitForElementEnabled(provinceIdFieldXpath);
		super.selectProvince(browserDriver);
	}
}
