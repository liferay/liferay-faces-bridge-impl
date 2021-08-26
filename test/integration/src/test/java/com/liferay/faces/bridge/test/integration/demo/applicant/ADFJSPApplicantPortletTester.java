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

/**
 * @author  Neil Griffin
 */
public class ADFJSPApplicantPortletTester extends ADFFaceletsApplicantPortletTester {

	protected String getDateFieldErrorXpath(String fieldXpath) {
		return fieldXpath +
			"/../../../../../../../../tr[2]/td/div/div/table[contains(@class, 'af_message_container')]/tbody/tr[2]/td[2]";
	}

	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath +
			"/../../../../../../../tr[2]/td/div/div/table[contains(@class, 'af_message_container')]/tbody/tr[2]/td[2]";
	}

	protected String getInvalidDateFormatMessage() {
		return "Enter a valid date or time";
	}

	@Override
	protected String getPortletPageName() {
		return "adf-jsp-applicant";
	}
}
