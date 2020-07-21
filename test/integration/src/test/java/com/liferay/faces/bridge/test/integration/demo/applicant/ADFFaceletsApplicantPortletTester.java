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

/**
 * @author  Neil Griffin
 */
public class ADFFaceletsApplicantPortletTester extends ApplicantTesterBase {

	protected String getConfimationFormXpath() {
		return "//form[contains(@id, 'confirmation')]";
	}

	protected String getDateFieldErrorXpath(String fieldXpath) {
		return fieldXpath +
			"/../../../../../../../../tr[2]/td/div/div/table[contains(@class, 'af_message_container')]/tbody/tr[1]/td[2]";
	}

	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath +
			"/../../../../../../../tr[2]/td/div/div/table[contains(@class, 'af_message_container')]/tbody/tr[1]/td[2]";
	}

	protected String getInvalidDateFormatMessage() {
		return "date or time entered is not valid";
	}

	@Override
	protected String getPortletPageName() {
		return "adf-facelets-applicant";
	}

	protected String getPreferencesResetButtonXpath() {
		return "//button[contains(text(), 'Reset')]";
	}

	protected String getPreferencesSubmitButtonXpath() {
		return "//button[contains(text(), 'Submit')]";
	}

	@Override
	protected String getProvinceIsRequiredMessage() {
		return "selection is required";
	}

	protected String getShowHideCommentsLinkXpath() {
		return "//a[contains(@class, 'af_showDetail_disclosure-link')]";
	}

	protected String getSubmitButtonXpath() {
		return "//button[contains(@id, 'submit') and text()='Submit']";
	}

	protected String getSubmitFileButtonXpath() {
		return "//form[@method='POST'][@enctype='multipart/form-data']/button[text()='Submit']";
	}

	protected String getUploadedFileXpath() {
		return "//tr[contains(@class, 'af_table_data-row')]/td[2]";
	}

	protected String getValueIsRequiredMessage() {
		return "value is required";
	}

	protected boolean isAjaxRedirectOnSubmit() {
		return false;
	}

	protected boolean isCommentsRetainedAfterShowHide() {
		return false;
	}

	protected boolean isInputFieldPartialSubmitEnabled() {
		return false;
	}

	@Override
	protected boolean isLiferayFacesAlloyIncluded() {
		return false;
	}

	protected boolean isSubmitButtonRerendersWhenClicked() {
		return false;
	}
}
