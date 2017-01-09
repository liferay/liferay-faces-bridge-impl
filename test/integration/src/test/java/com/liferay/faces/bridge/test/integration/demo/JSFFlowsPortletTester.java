/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class JSFFlowsPortletTester extends IntegrationTesterBase {

	@BeforeClass
	public static void setUpJSFFlowsPortletTester() {
		Browser.getInstance().setWaitTimeOut(TestUtil.getBrowserWaitTimeOut(10));
	}

	@AfterClass
	public static void tearDownJSFFlowsPortletTester() {
		Browser.getInstance().setWaitTimeOut(TestUtil.getBrowserWaitTimeOut());
	}

	@Test
	public void runJSFFlowsPortletTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getDemoPageURL("jsf-flows"));

		// Test that libraries are visible.
		SeleniumAssert.assertLibraryVisible(browser, "Mojarra");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Alloy");
		SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Impl");

		if (TestUtil.getContainer().contains("liferay")) {
			SeleniumAssert.assertLibraryVisible(browser, "Liferay Faces Bridge Ext");
		}

		SeleniumAssert.assertLibraryVisible(browser, "Weld");

		// Test that exiting the bookings flow scope causes beans to go out of scope.
		String enterBookingFlowButtonXpath = "//input[@value='Enter Booking Flow']";
		browser.click(enterBookingFlowButtonXpath);

		String exitBookingFlowButtonXpath = "//input[@value='Exit Booking Flow']";
		browser.waitForElementVisible(exitBookingFlowButtonXpath);
		browser.click(exitBookingFlowButtonXpath);
		browser.waitForElementVisible(enterBookingFlowButtonXpath);
		assertFlowBeansOutOfScope(browser);
		SeleniumAssert.assertElementVisible(browser, enterBookingFlowButtonXpath);

		// Test that a flight can be found.
		browser.click(enterBookingFlowButtonXpath);

		String bookingTypeXpath = "//select[contains(@id,':bookingTypeId')]";
		browser.waitForElementVisible(bookingTypeXpath);
		createSelect(browser, bookingTypeXpath).selectByVisibleText("Flight");

		String departureXpath = "//select[contains(@id,':departureId')]";
		browser.waitForElementVisible(departureXpath);
		createSelect(browser, departureXpath).selectByVisibleText("Los Angeles: Los Angeles Intl");

		String arrivalXpath = "//select[contains(@id,':arrivalId')]";
		createSelect(browser, arrivalXpath).selectByVisibleText("Louisville: Louisville International Airport");
		browser.sendKeys("//input[contains(@id,':departureDate')]", "2015-08-12");

		String searchFlightsButtonXpath = "//input[@value='Search Flights']";
		browser.click(searchFlightsButtonXpath);

		String addToCartButtonXpath = "(//input[@value='Add To Cart'])[1]";
		browser.waitForElementVisible(addToCartButtonXpath);
		SeleniumAssert.assertElementVisible(browser, addToCartButtonXpath);

		// Test that a flight can be added to the cart.
		browser.click(addToCartButtonXpath);

		String bookAdditionalTravelButtonXpath = "//input[@value='Book Additional Travel']";
		String removeButtonXpath = "(//input[@value='Remove'])[1]";
		browser.waitForElementVisible(removeButtonXpath);
		SeleniumAssert.assertElementVisible(browser, removeButtonXpath);

		// Test that non-flight options are not implemented.
		browser.click(bookAdditionalTravelButtonXpath);
		browser.waitForElementVisible(bookingTypeXpath);

		createSelect(browser, bookingTypeXpath).selectByVisibleText("Cruise");

		String bookingTypeFieldSetXpath = "//fieldset[contains(@id,':bookingTypeFieldSet')]/p";
		browser.waitForElementVisible(bookingTypeFieldSetXpath);
		SeleniumAssert.assertElementTextVisible(browser, bookingTypeFieldSetXpath,
			"'Flight' is currently the only type of booking that is implemented in this demo.");

		// Test that flights can be purchased.
		createSelect(browser, bookingTypeXpath).selectByVisibleText("Flight");
		browser.waitForElementVisible(departureXpath);
		createSelect(browser, departureXpath).selectByVisibleText("Louisville: Louisville International Airport");
		createSelect(browser, arrivalXpath).selectByVisibleText("Orlando: Orlando Intl");
		browser.sendKeys("//input[contains(@id,':departureDate')]", "2015-08-12");
		browser.click(searchFlightsButtonXpath);
		browser.waitForElementVisible(addToCartButtonXpath);
		browser.click(addToCartButtonXpath);
		browser.waitForElementVisible(removeButtonXpath);
		browser.click("//input[@value='Checkout']");

		String titleFieldXpath = "//select[contains(@id,':titleId')]";
		browser.waitForElementVisible(titleFieldXpath);
		createSelect(browser, titleFieldXpath).selectByVisibleText("Mr.");

		String firstNameFieldXpath = "//input[contains(@id,':firstName')]";
		browser.sendKeys(firstNameFieldXpath, "Gilbert");

		String lastNameFieldXpath = "//input[contains(@id,':lastName')]";
		browser.sendKeys(lastNameFieldXpath, "Godfried");

		String emailAddressFieldXpath = "//input[contains(@id,':emailAddress')]";
		browser.sendKeys(emailAddressFieldXpath, "Gilbert@Godfried.org");

		String phoneNumberFieldXpath = "//input[contains(@id,':phoneNumber')]";
		browser.sendKeys(phoneNumberFieldXpath, "1234567890");

		String addressLine1FieldXpath = "//input[contains(@id,':addressLine1')]";
		browser.sendKeys(addressLine1FieldXpath, "123 Gilgod Ave");

		String cityFieldXpath = "//input[contains(@id,':city')]";
		browser.sendKeys(cityFieldXpath, "Hollywood");

		String provinceIdFieldXpath = "//select[contains(@id,':provinceId')]";
		createSelect(browser, provinceIdFieldXpath).selectByVisibleText("California");

		String paymentTypeIdFieldXpath = "//select[contains(@id,':paymentTypeId')]";
		createSelect(browser, paymentTypeIdFieldXpath).selectByVisibleText("Visa");

		String accountNumberFieldXpath = "//input[contains(@id,':accountNumber')]";
		browser.sendKeys(accountNumberFieldXpath, "12345678901234567890");

		String expirationMonthFieldXpath = "//input[contains(@id,':expirationMonth')]";
		browser.sendKeys(expirationMonthFieldXpath, "01/35");

		browser.click("//input[@value='Purchase']");
		String cvvFieldXpath = "//input[contains(@id,':cvv')]";
		browser.waitForElementVisible(cvvFieldXpath + "/../span[@class='portlet-msg-error']");
		SeleniumAssert.assertElementTextVisible(browser, cvvFieldXpath + "/../span[@class='portlet-msg-error']", "Value is required");
		browser.sendKeys(cvvFieldXpath, "123");

		browser.click("//input[@value='Purchase']");

		String callSurveyFlowButtonXpath = "//input[@value='Call Survey Flow']";
		browser.waitForElementVisible(callSurveyFlowButtonXpath);
		SeleniumAssert.assertElementTextVisible(browser, "//div[contains(@class,'liferay-faces-bridge-body')]//form",
			"Thank you Gilbert for your purchase.");

		// Test the survey flow scope.
		browser.click(callSurveyFlowButtonXpath);

		String finishButtonXpath = "//input[@value='Finish']";
		browser.waitForElementVisible(finishButtonXpath);
		browser.sendKeys("//div[contains(@id,':question1')]/input", "Liferay");
		browser.sendKeys("//div[contains(@id,':question2')]/input", "Cockpit");
		browser.click(finishButtonXpath);

		String returnFromSurveyFlowButtonXpath = "//input[@value='Return From Survey Flow']";
		browser.waitForElementVisible(returnFromSurveyFlowButtonXpath);
		SeleniumAssert.assertElementTextVisible(browser, "//div[contains(@class,'liferay-faces-bridge-body')]//form",
			"Thank you Gilbert for participating in our survey.");

		// Test that exiting the survey flow scope causes beans to go out of scope.
		browser.click(returnFromSurveyFlowButtonXpath);
		browser.waitForElementVisible(enterBookingFlowButtonXpath);
		assertFlowBeansOutOfScope(browser);
	}

	private void assertFlowBeansOutOfScope(Browser browser) {

		SeleniumAssert.assertElementTextVisible(browser, "//li/em[contains(text(),'bookingFlowModelBeanInScope')]",
			"bookingFlowModelBeanInScope=false");
		SeleniumAssert.assertElementTextVisible(browser, "//li/em[contains(text(),'cartModelBeanInScope')]",
			"cartModelBeanInScope=false");
		SeleniumAssert.assertElementTextVisible(browser, "//li/em[contains(text(),'flightSearchModelBeanInScope')]",
			"flightSearchModelBeanInScope=false");
		SeleniumAssert.assertElementTextVisible(browser, "//li/em[contains(text(),'surveyFlowModelBeanInScope')]",
			"surveyFlowModelBeanInScope=false");
	}

	private Select createSelect(Browser browser, String selectXpath) {

		WebElement element = browser.findElementByXpath(selectXpath);

		return new Select(element);
	}
}
