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

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 */
public class JSF_IPCPortletsTesterBase extends IntegrationTesterBase {

	// Private Xpaths
	protected static final String firstNameInputXpath = "//input[contains(@id, ':firstName')]";
	protected static final String lastNameInputXpath = "//input[contains(@id, ':lastName')]";
	protected static final String submitButtonXpath = "//input[@value='Submit']";

	public void runJSF_IPCPortletsTest(Browser browser) {

		testEditName(browser, "1", false);

		// Test that start date is today.
		String startDate1Xpath = "//input[contains(@id, ':0:startDate')]";
		browser.waitForElementVisible(startDate1Xpath);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		Date today = new Date();
		String todayString = simpleDateFormat.format(today);
		SeleniumAssert.assertElementValue(browser, startDate1Xpath, todayString);

		// Test that the start date can be set.
		browser.clear(startDate1Xpath);

		String startDate = "12/25/2999";
		browser.sendKeys(startDate1Xpath, startDate);
		browser.click(submitButtonXpath);

		testEditButton(browser, "2");
		testEditButton(browser, "3");

		// Check that the start date has not changed.
		String customer1EditButtonXpath = "//td[text()='1']/..//input";
		browser.waitForElementVisible(customer1EditButtonXpath);
		browser.click(customer1EditButtonXpath);
		browser.waitForElementVisible(startDate1Xpath);
		SeleniumAssert.assertElementValue(browser, startDate1Xpath, startDate);

		// Reset the start date.
		browser.clear(startDate1Xpath);
		browser.sendKeys(startDate1Xpath, todayString);
		browser.click(submitButtonXpath);
	}

	private void testEditButton(Browser browser, String customerId) {
		testEditName(browser, customerId, true);
	}

	private void testEditName(Browser browser, String customerId, boolean editFirstName) {

		// Test that the first and last names are the same in both portlets.
		String customerIdXpath = "//td[text()='" + customerId + "']";
		String customerEditButtonXpath = customerIdXpath + "/..//input";
		browser.waitForElementVisible(customerEditButtonXpath);
		browser.click(customerEditButtonXpath);
		browser.waitForElementVisible(firstNameInputXpath);

		String customerFirstNameXpath = customerIdXpath + "/following-sibling::td[1]";
		WebElement firstNameElement = browser.findElementByXpath(customerFirstNameXpath);
		String customerLastNameXpath = customerIdXpath + "/following-sibling::td[2]";
		WebElement lastNameElement = browser.findElementByXpath(customerLastNameXpath);
		String firstName = firstNameElement.getText();
		SeleniumAssert.assertElementValue(browser, firstNameInputXpath, firstName);

		String lastName = lastNameElement.getText();
		SeleniumAssert.assertElementValue(browser, lastNameInputXpath, lastName);

		// Test that editing the name changes it in the customers portlet.
		String inputXpath = firstNameInputXpath;
		String name = firstName;
		String customerNameXpath = customerFirstNameXpath;

		if (!editFirstName) {

			inputXpath = lastNameInputXpath;
			name = lastName;
			customerNameXpath = customerLastNameXpath;
		}

		browser.clear(inputXpath);

		String editedName = name + "y";
		browser.sendKeys(inputXpath, editedName);
		browser.click(submitButtonXpath);
		browser.waitForElementVisible(customerEditButtonXpath);
		SeleniumAssert.assertElementTextVisible(browser, customerNameXpath, editedName);

		// Reset the name.
		browser.sendKeys(inputXpath, Keys.BACK_SPACE);
		browser.click(submitButtonXpath);
	}
}
