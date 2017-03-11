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


/**
 * @author  Kyle Stiemann
 */
public class AlloyApplicantPortletTester extends BridgeApplicantPortletTester {

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {

		String errorMessageXpath =
			"/span[contains(@class,'alloy-message')][contains(@class,'help-inline') or contains(@class,'help-block')]";

		return "(" + fieldXpath + errorMessageXpath + "|" + fieldXpath + "/.." + errorMessageXpath + "|" + fieldXpath +
			"/../.." + errorMessageXpath + ")";
	}

	@Override
	protected String getPortletPageName() {
		return "alloy-applicant";
	}

	@Override
	protected String getPreferencesResetButtonXpath() {
		return "//button[contains(@class,'btn')][contains(text(),'Reset')]";
	}

	@Override
	protected String getPreferencesSubmitButtonXpath() {
		return "//button[contains(@class,'btn')][contains(text(),'Submit')]";
	}

	@Override
	protected String getSubmitAnotherApplicationButton() {
		return "//button[contains(@class,'btn')][contains(text(),'Submit Another Application')]";
	}

	@Override
	protected String getSubmitButtonXpath() {
		return "//button[contains(@class,'btn')][contains(text(),'Submit')]";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return
			"//form[@method='post'][@enctype='multipart/form-data']/button[contains(@class,'btn')][contains(text(),'Submit')]";
	}

	@Override
	protected String getUploadedFileXpath() {
		return
			"//h3[contains(text(),'Attachments')]/following-sibling::table[contains(@class,'alloy-data-table')]/tbody/tr/td[2]/span";
	}

	@Override
	protected void selectDate(Browser browser) {

		String calendarButtonXpath =
			"//button[contains(@id,'dateOfBirth')]/span[contains(@class,'calendar')][not(text())]";
		browser.centerElementInView(calendarButtonXpath);
		browser.click(calendarButtonXpath);

		String dateElementXpath =
			"//table[contains(@class, 'yui3-calendar-grid')]//td[contains(text(),'14')][not(contains(@class,'yui3-calendar-column-hidden'))]";
		browser.waitForElementVisible(dateElementXpath);
		browser.click(dateElementXpath);
	}
}
