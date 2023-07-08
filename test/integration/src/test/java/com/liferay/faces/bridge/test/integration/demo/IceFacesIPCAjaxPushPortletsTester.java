/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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

import org.junit.Test;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class IceFacesIPCAjaxPushPortletsTester extends JSF_IPCPortletsTesterBase {

	// Private Xpaths
	protected static final String firstNameInputXpath = "//input[contains(@id, ':firstName')]";
	protected static final String lastNameInputXpath = "//input[contains(@id, ':lastName')]";

	@Test
	public void runIceFacesIPCAjaxPushPortletsTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));

		// Reset the start date.
		String customer1IdXpath = getCustomerIdXpath("1");
		String customer1FirstName = getCustomerFirstName(browserDriver, customer1IdXpath);
		String customer1EditButtonXpath = getCustomerEditButtonXpath(customer1IdXpath);
		browserDriver.clickElement(customer1EditButtonXpath);

		String startDate1Xpath = "//input[contains(@id,':0:startDate')]";
		browserDriver.waitForElementEnabled(startDate1Xpath);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		Date today = new Date();
		String todayString = simpleDateFormat.format(today);
		browserDriver.clearElement(startDate1Xpath);
		browserDriver.sendKeysToElement(startDate1Xpath, todayString, Keys.ENTER);
		browserDriver.navigateWindowTo(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));

		// Test that the first and last names are the same in both portlets.
		String customer2IdXpath = getCustomerIdXpath("2");
		String customer2FirstName = getCustomerFirstName(browserDriver, customer2IdXpath);

		browserDriver.waitForElementEnabled(customer1EditButtonXpath);
		browserDriver.clickElement(customer1EditButtonXpath);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElementValue(customer1FirstName, firstNameInputXpath);

		String customerLastNameXpath = customer1IdXpath + "/following-sibling::td[2]/span";
		WebElement lastNameElement = browserDriver.findElementByXpath(customerLastNameXpath);
		String lastName = lastNameElement.getText();
		waitingAsserter.assertTextPresentInElementValue(lastName, lastNameInputXpath);

		// Test that editing the name changes it in the customers portlet.
		browserDriver.clearElement(lastNameInputXpath);

		String editedName = lastName + "y";
		browserDriver.sendKeysToElement(lastNameInputXpath, editedName, Keys.ENTER);
		browserDriver.waitForElementEnabled(customerLastNameXpath + "[text()='" + editedName + "']");
		waitingAsserter.assertTextPresentInElement(editedName, customerLastNameXpath);

		// Reset the name.
		browserDriver.sendKeysToElement(lastNameInputXpath, Keys.BACK_SPACE, Keys.ENTER);

		// Test that start date is today.
		waitingAsserter.assertTextPresentInElementValue(todayString, startDate1Xpath);

		// Test that the start date can be set.
		browserDriver.clearElement(startDate1Xpath);

		String startDate = "12/25/2999";
		browserDriver.sendKeysToElement(startDate1Xpath, startDate, Keys.ENTER);

		browserDriver.navigateWindowTo(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));

		String customer2EditButtonXpath = getCustomerEditButtonXpath(customer2IdXpath);
		browserDriver.waitForElementEnabled(customer2EditButtonXpath);
		browserDriver.clickElement(customer2EditButtonXpath);
		browserDriver.waitForElementEnabled(firstNameInputXpath + "[@value='" + customer2FirstName + "']");

		// Check that the start date has not changed.
		browserDriver.navigateWindowTo(BridgeTestUtil.getDemoPageURL("icefaces-ipc"));
		browserDriver.waitForElementEnabled(customer1EditButtonXpath);
		browserDriver.clickElement(customer1EditButtonXpath);
		waitingAsserter.assertTextPresentInElementValue(startDate, startDate1Xpath);
	}

	private String getCustomerEditButtonXpath(String customerIdXpath) {
		return customerIdXpath + "/..//input";
	}

	private String getCustomerFirstName(BrowserDriver browserDriver, String customerIdXpath) {

		String customerFirstNameXpath = customerIdXpath + "/following-sibling::td[1]/span";
		WebElement firstNameElement = browserDriver.findElementByXpath(customerFirstNameXpath);

		return firstNameElement.getText();
	}

	private String getCustomerIdXpath(String customerId) {
		return "//td/span[text()='" + customerId + "']/..";
	}
}
