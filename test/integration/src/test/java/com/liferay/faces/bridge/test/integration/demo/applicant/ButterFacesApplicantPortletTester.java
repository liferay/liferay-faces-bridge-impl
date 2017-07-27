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

import org.junit.Assume;
import org.junit.Before;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class ButterFacesApplicantPortletTester extends BridgeApplicantPortletTester {

	@Override
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);
		browserDriver.clickElementAndWaitForRerender(getSubmitButtonXpath());

		// Verify that 8 "Value is required" messages appear.
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertElementDisplayed(getFieldErrorXpath(getFirstNameFieldXpath()) +
			"[contains(text(),'Value is required')][8]");
	}

	@Before
	public void skipTestOnLiferay62() {

		// BootsFaces is unsupported on Liferay 6.2 due to Bootstrap incompatibilities.
		Assume.assumeFalse("liferay62".equals(TestUtil.getContainer()));
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath +
			"/../../../../../preceding-sibling::ul[contains(@id,'globalMessages')]/li[contains(@class,'portlet-msg-error')]";
	}

	@Override
	protected String getPortletPageName() {
		return "butterfaces-applicant";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post'][@enctype='multipart/form-data']/a[contains(.,'Submit')]";
	}

	@Override
	protected void selectDate(BrowserDriver browserDriver) {

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "01/02/3456");
		browserDriver.clickElement("//span[contains(@class, 'icon-calendar')]");

		String dateCellXpath = "//div[contains(@class, 'datepicker-days')]//td[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateCellXpath);
		browserDriver.clickElement(dateCellXpath);
	}
}
