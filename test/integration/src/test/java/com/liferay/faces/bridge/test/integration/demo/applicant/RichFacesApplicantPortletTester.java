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

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 */
public class RichFacesApplicantPortletTester extends BridgeApplicantPortletTester {

	@Override
	protected void assertFileUploadChooserVisible(Browser browser) {
		SeleniumAssert.assertElementVisible(browser, getFileUploadChooserXpath() + "/..");
	}

	@Override
	protected String getExtraLibraryName() {
		return "RichFaces 4.5.17.Final";
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
		return "//tbody[contains(@id, ':uploadedFilesTable')]/tr/td[2]";
	}

	@Override
	protected void selectDate(Browser browser) {

		browser.click("//img[contains(@class, 'rf-cal-btn')]");

		String dateElement = "//table[contains(@class, 'rf-cal-popup')]//td[contains(text(), '14')]";
		browser.waitForElementVisible(dateElement);
		browser.click(dateElement);
	}
}
