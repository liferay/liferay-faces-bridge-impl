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

import org.junit.Test;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


/**
 * @author  Kyle Stiemann
 */
public class IceFacesIPCAjaxPushPortletsTester extends JSF_IPCPortletsTesterBase {

	// Private Xpaths
	protected static final String firstNameInputXpath = "//input[contains(@id, ':firstName')]";
	protected static final String lastNameInputXpath = "//input[contains(@id, ':lastName')]";

	@Test
	public void runIceFacesIPCAjaxPushPortletsTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));

		// Reset the start date.
		String customer1IdXpath = getCustomerIdXpath("1");
		String customer1FirstName = getCustomerFirstName(browser, customer1IdXpath);
		String customer1EditButtonXpath = getCustomerEditButtonXpath(customer1IdXpath);
		browser.waitForElementVisible(customer1EditButtonXpath);
		browser.click(customer1EditButtonXpath);

		String startDate1Xpath = "//input[contains(@id,':0:startDate')]";
		browser.waitForElementVisible(startDate1Xpath);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		Date today = new Date();
		String todayString = simpleDateFormat.format(today);
		browser.clear(startDate1Xpath);
		browser.sendKeys(startDate1Xpath, todayString, Keys.ENTER);
		browser.get(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));

		// Test that the first and last names are the same in both portlets.
		String customer2IdXpath = getCustomerIdXpath("2");
		String customer2FirstName = getCustomerFirstName(browser, customer2IdXpath);

		browser.waitForElementVisible(customer1EditButtonXpath);
		browser.click(customer1EditButtonXpath);
		browser.waitForElementVisible(firstNameInputXpath + "[@value='" + customer1FirstName + "']");
		SeleniumAssert.assertElementValue(browser, firstNameInputXpath, customer1FirstName);

		String customerLastNameXpath = customer1IdXpath + "/following-sibling::td[2]/span";
		WebElement lastNameElement = browser.findElementByXpath(customerLastNameXpath);
		String lastName = lastNameElement.getText();
		SeleniumAssert.assertElementValue(browser, lastNameInputXpath, lastName);

		// Test that editing the name changes it in the customers portlet.
		browser.clear(lastNameInputXpath);

		String editedName = lastName + "y";
		browser.sendKeys(lastNameInputXpath, editedName, Keys.ENTER);
		browser.waitForElementVisible(customerLastNameXpath + "[text()='" + editedName + "']");
		SeleniumAssert.assertElementTextVisible(browser, customerLastNameXpath, editedName);

		// Reset the name.
		browser.sendKeys(lastNameInputXpath, Keys.BACK_SPACE, Keys.ENTER);
		browser.waitForElementVisible(customerLastNameXpath + "[text()='" + lastName + "']");

		// Test that start date is today.
		browser.waitForElementVisible(startDate1Xpath);
		SeleniumAssert.assertElementValue(browser, startDate1Xpath, todayString);

		// Test that the start date can be set.
		browser.clear(startDate1Xpath);

		String startDate = "12/25/2999";
		browser.sendKeys(startDate1Xpath, startDate, Keys.ENTER);

		browser.get(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));
		String customer2EditButtonXpath = getCustomerEditButtonXpath(customer2IdXpath);
		browser.waitForElementVisible(customer2EditButtonXpath);
		browser.click(customer2EditButtonXpath);
		browser.waitForElementVisible(firstNameInputXpath + "[@value='" + customer2FirstName + "']");

		// Check that the start date has not changed.
		browser.get(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));
		browser.waitForElementVisible(customer1EditButtonXpath);
		browser.click(customer1EditButtonXpath);
		browser.waitForElementVisible(firstNameInputXpath + "[@value='" + customer1FirstName + "']");
		browser.waitForElementVisible(startDate1Xpath);
		SeleniumAssert.assertElementValue(browser, startDate1Xpath, startDate);
	}

	private String getCustomerFirstName(Browser browser, String customerIdXpath) {

		String customerFirstNameXpath = customerIdXpath + "/following-sibling::td[1]/span";
		WebElement firstNameElement = browser.findElementByXpath(customerFirstNameXpath);
		return firstNameElement.getText();
	}

	private String getCustomerIdXpath(String customerId) {
		return "//td/span[text()='" + customerId + "']/..";
	}

	private String getCustomerEditButtonXpath(String customerIdXpath) {
		return customerIdXpath + "/..//input";
	}
}
