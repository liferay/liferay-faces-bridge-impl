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

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class RichFacesApplicantPortletTester extends ApplicantTesterBase {

	@Override
	protected void assertFileUploadChooserDisplayed(BrowserDriver browserDriver, WaitingAsserter waitingAsserter) {
		waitingAsserter.assertElementDisplayed(getFileUploadChooserXpath() + "/..");
	}

	@Override
	protected String getExtraLibraryName() {
		return "RichFaces 4.6.8.ayg";
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {

		String errorMessageXpath = "/../span/span[@class='rf-msg-err']/span[@class='rf-msg-det']";

		return "(" + fieldXpath + errorMessageXpath + "|" + fieldXpath + "/../.." + errorMessageXpath + ")";
	}

	@Override
	protected String getPortletPageName() {
		return "richfaces-applicant";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return "//span[contains(text(), 'Upload')]";
	}

	@Override
	protected String getUploadedFileXpath() {
		return "//tbody[contains(@id, ':attachmentsTable')]/tr/td[2]";
	}

	@Override
	protected void selectDate(BrowserDriver browserDriver) {

		browserDriver.clickElement("//img[contains(@class, 'rf-cal-btn')]");

		// FACES-3044 RichFaces tester fails during month which contains 14th week of year (April)
		String dateCellXpath = "//table[contains(@class, 'rf-cal-popup')]//td[contains(text(), '14')][@onclick]";
		browserDriver.waitForElementEnabled(dateCellXpath);
		browserDriver.clickElement(dateCellXpath);
	}
}
