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

import org.junit.FixMethodOrder;

import org.junit.runners.MethodSorters;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JSF_JSPApplicantPortletTester extends JSFApplicantPortletTester {

	@Override
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		Browser browser = Browser.getInstance();
		browser.get(TestUtil.DEFAULT_BASE_URL + getContext());

		// Wait to begin the test until the first name field is rendered.
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browser.waitForElementVisible(firstNameFieldXpath);
		SeleniumAssert.assertElementVisible(browser, firstNameFieldXpath);
		SeleniumAssert.assertElementVisible(browser, getLastNameFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getEmailAddressFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getPhoneNumberFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getDateOfBirthFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getCityFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getProvinceIdFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getPostalCodeFieldXpath());
		SeleniumAssert.assertElementVisible(browser, getAddAttachmentXpath());
		SeleniumAssert.assertLibraryVisible(browser, "Mojarra");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Impl");

		if (TestUtil.getContainer().contains("liferay")) {
			SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Ext");
		}
	}

	@Override
	public void runApplicantPortletTest_I_FileUpload() {

		Browser browser = Browser.getInstance();
		String addAttachmentXpath = getAddAttachmentXpath();
		browser.waitForElementVisible(addAttachmentXpath);
		browser.click(addAttachmentXpath);
		browser.waitForElementVisible(getFileUploadChooserXpath());
		super.runApplicantPortletTest_I_FileUpload();
	}

	protected String getAddAttachmentXpath() {
		return "//input[contains(@value,'Add Attachment')]";
	}

	@Override
	protected String getContext() {
		return BridgeTestUtil.getDemoContext("jsf-jsp-applicant");
	}

	@Override
	protected String getFileUploadChooserXpath() {
		return "(//input[@type='file'])[1]";
	}
}
