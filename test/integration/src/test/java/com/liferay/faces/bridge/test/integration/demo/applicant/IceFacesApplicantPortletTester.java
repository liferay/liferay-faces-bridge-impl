/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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

import java.util.List;

import com.liferay.faces.test.selenium.browser.BrowserDriver;


/**
 * @author  Kyle Stiemann
 */
public class IceFacesApplicantPortletTester extends ApplicantTesterBase {

	@Override
	protected List<String> getExtraLibraryNames() {
		return unmodifiableList("ICEfaces");
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {

		String errorMessageXpath = "/../span/span[@class='portlet-msg-error']";

		return "(" + fieldXpath + errorMessageXpath + "|" + fieldXpath + "/.." + errorMessageXpath + "|" + fieldXpath +
			"/../.." + errorMessageXpath + ")";
	}

	@Override
	protected String getPortletPageName() {
		return "icefaces-applicant";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post' and @enctype]/input[@type='submit' and @value='Add Attachment']";
	}

	@Override
	protected String getUploadedFileXpath() {
		return "//tr[contains(@class,'ui-datatable-even')]/td[2]";
	}

	@Override
	protected boolean isViewScopedApplicantBacking() {
		return true;
	}

	@Override
	protected void selectDate(BrowserDriver browserDriver) {

		browserDriver.clickElement(getDateOfBirthFieldXpath());

		String dateElement = "//table[contains(@class, 'ui-datepicker-calendar')]//a[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateElement);
		browserDriver.clickElement(dateElement);
		browserDriver.waitForElementNotDisplayed(dateElement);
	}
}
