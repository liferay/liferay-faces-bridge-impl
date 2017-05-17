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
package com.liferay.faces.bridge.test.integration.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;


/**
 * @author  Kyle Stiemann
 */
public class JSF_IPCPortletsTesterBase extends IntegrationTesterBase {

	// Private Xpaths
	protected static final String firstNameInputXpath = "//input[contains(@id, ':firstName')]";
	protected static final String lastNameInputXpath = "//input[contains(@id, ':lastName')]";
	protected static final String submitButtonXpath = "//input[@value='Submit']";

	public void runJSF_IPCPortletsTest(BrowserDriver browserDriver) {

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		testEditName(browserDriver, browserStateAsserter, "1", false);

		// Test that start date is today.
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		Date today = new Date();
		String todayString = simpleDateFormat.format(today);
		String startDate1Xpath = "//input[contains(@id, ':0:startDate')]";
		browserStateAsserter.assertTextPresentInElementValue(todayString, startDate1Xpath);

		// Test that the start date can be set.
		browserDriver.clearElement(startDate1Xpath);

		String startDate = "12/25/2999";
		browserDriver.sendKeysToElement(startDate1Xpath, startDate);
		browserDriver.clickElement(submitButtonXpath);

		testEditButton(browserDriver, browserStateAsserter, "2");
		testEditButton(browserDriver, browserStateAsserter, "3");

		// Check that the start date has not changed.
		String customer1EditButtonXpath = "//td[text()='1']/..//input";
		browserDriver.waitForElementEnabled(customer1EditButtonXpath);
		browserDriver.clickElement(customer1EditButtonXpath);
		browserStateAsserter.assertTextPresentInElementValue(startDate, startDate1Xpath);

		// Reset the start date.
		browserDriver.clearElement(startDate1Xpath);
		browserDriver.sendKeysToElement(startDate1Xpath, todayString);
		browserDriver.clickElement(submitButtonXpath);
	}

	private void testEditButton(BrowserDriver browserDriver, BrowserStateAsserter browserStateAsserter,
		String customerId) {
		testEditName(browserDriver, browserStateAsserter, customerId, true);
	}

	private void testEditName(BrowserDriver browserDriver, BrowserStateAsserter browserStateAsserter, String customerId,
		boolean editFirstName) {

		// Test that the first and last names are the same in both portlets.
		String customerIdXpath = "//td[text()='" + customerId + "']";
		String customerEditButtonXpath = customerIdXpath + "/..//input";
		browserDriver.waitForElementEnabled(customerEditButtonXpath);
		browserDriver.clickElement(customerEditButtonXpath);
		browserDriver.waitForElementEnabled(firstNameInputXpath);

		String customerFirstNameXpath = customerIdXpath + "/following-sibling::td[1]";
		WebElement firstNameElement = browserDriver.findElementByXpath(customerFirstNameXpath);
		String customerLastNameXpath = customerIdXpath + "/following-sibling::td[2]";
		WebElement lastNameElement = browserDriver.findElementByXpath(customerLastNameXpath);
		String firstName = firstNameElement.getText();
		browserStateAsserter.assertTextPresentInElementValue(firstName, firstNameInputXpath);

		String lastName = lastNameElement.getText();
		browserStateAsserter.assertTextPresentInElementValue(lastName, lastNameInputXpath);

		// Test that editing the name changes it in the customers portlet.
		String inputXpath = firstNameInputXpath;
		String name = firstName;
		String customerNameXpath = customerFirstNameXpath;

		if (!editFirstName) {

			inputXpath = lastNameInputXpath;
			name = lastName;
			customerNameXpath = customerLastNameXpath;
		}

		browserDriver.clearElement(inputXpath);

		String editedName = name + "y";
		browserDriver.sendKeysToElement(inputXpath, editedName);
		browserDriver.clickElement(submitButtonXpath);
		browserStateAsserter.assertTextPresentInElement(editedName, customerNameXpath);

		// Reset the name.
		browserDriver.sendKeysToElement(inputXpath, Keys.BACK_SPACE);
		browserDriver.clickElement(submitButtonXpath);
	}
}
