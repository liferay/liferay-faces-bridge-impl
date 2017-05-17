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
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JSF_JSPApplicantPortletTester extends JSFApplicantPortletTester {

	@Override
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + getContext());

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		browserStateAsserter.assertElementDisplayed(getFirstNameFieldXpath());
		browserStateAsserter.assertElementDisplayed(getLastNameFieldXpath());
		browserStateAsserter.assertElementDisplayed(getEmailAddressFieldXpath());
		browserStateAsserter.assertElementDisplayed(getPhoneNumberFieldXpath());
		browserStateAsserter.assertElementDisplayed(getDateOfBirthFieldXpath());
		browserStateAsserter.assertElementDisplayed(getCityFieldXpath());
		browserStateAsserter.assertElementDisplayed(getProvinceIdFieldXpath());
		browserStateAsserter.assertElementDisplayed(getPostalCodeFieldXpath());
		browserStateAsserter.assertElementDisplayed(getAddAttachmentXpath());
		assertLibraryElementDisplayed(browserStateAsserter, "Mojarra", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge Impl", browserDriver);

		if (TestUtil.getContainer().contains("liferay")) {
			assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge Ext", browserDriver);
		}
	}

	@Override
	public void runApplicantPortletTest_I_FileUpload() {

		BrowserDriver browserDriver = getBrowserDriver();
		String addAttachmentXpath = getAddAttachmentXpath();
		browserDriver.waitForElementEnabled(addAttachmentXpath);
		browserDriver.clickElement(addAttachmentXpath);
		browserDriver.waitForElementEnabled(getFileUploadChooserXpath());
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
