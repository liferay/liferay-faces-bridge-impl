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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class JSFFlowsPortletTester extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(JSFFlowsPortletTester.class);

	@Test
	public void runJSFFlowsPortletTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getDemoPageURL("jsf-flows"));

		// Test that libraries are displayed.
		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		assertLibraryElementDisplayed(browserStateAsserter, "Mojarra", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Alloy", browserDriver);
		assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge Impl", browserDriver);

		if (TestUtil.getContainer().contains("liferay")) {
			assertLibraryElementDisplayed(browserStateAsserter, "Liferay Faces Bridge Ext", browserDriver);
		}

		assertLibraryElementDisplayed(browserStateAsserter, "Weld", browserDriver);

		// Test that exiting the bookings flow scope causes beans to go out of scope.
		String enterBookingFlowButtonXpath = "//input[@value='Enter Booking Flow']";
		browserDriver.clickElement(enterBookingFlowButtonXpath);

		String exitBookingFlowButtonXpath = "//input[@value='Exit Booking Flow']";
		browserDriver.waitForElementEnabled(exitBookingFlowButtonXpath);
		browserDriver.clickElement(exitBookingFlowButtonXpath);
		browserStateAsserter.assertElementDisplayed(enterBookingFlowButtonXpath);
		assertFlowBeansOutOfScope(browserStateAsserter);

		// Test that a flight can be found.
		browserDriver.clickElement(enterBookingFlowButtonXpath);

		String bookingTypeXpath = "//select[contains(@id,':bookingTypeId')]";
		browserDriver.waitForElementEnabled(bookingTypeXpath);
		createSelect(browserDriver, bookingTypeXpath).selectByVisibleText("Flight");

		String departureXpath = "//select[contains(@id,':departureId')]";
		browserDriver.waitForElementEnabled(departureXpath);
		selectOptionContainingText(browserDriver, departureXpath, "LAX");

		String arrivalXpath = "//select[contains(@id,':arrivalId')]";
		selectOptionContainingText(browserDriver, arrivalXpath, "SDF");
		browserDriver.sendKeysToElement("//input[contains(@id,':departureDate')]", "2015-08-12");

		String searchFlightsButtonXpath = "//input[@value='Search Flights']";
		browserDriver.clickElement(searchFlightsButtonXpath);

		String addToCartButtonXpath = "(//input[@value='Add To Cart'])[1]";
		browserStateAsserter.assertElementDisplayed(addToCartButtonXpath);

		// Test that a flight can be added to the cart.
		browserDriver.clickElement(addToCartButtonXpath);

		String bookAdditionalTravelButtonXpath = "//input[@value='Book Additional Travel']";
		String removeButtonXpath = "(//input[@value='Remove'])[1]";
		browserStateAsserter.assertElementDisplayed(removeButtonXpath);

		// Test that non-flight options are not implemented.
		browserDriver.clickElement(bookAdditionalTravelButtonXpath);
		browserDriver.waitForElementEnabled(bookingTypeXpath);

		createSelect(browserDriver, bookingTypeXpath).selectByVisibleText("Cruise");
		browserStateAsserter.assertTextPresentInElement(
			"'Flight' is currently the only type of booking that is implemented in this demo.",
			"//fieldset[contains(@id,':bookingTypeFieldSet')]/p");

		// Test that flights can be purchased.
		createSelect(browserDriver, bookingTypeXpath).selectByVisibleText("Flight");
		browserDriver.waitForElementEnabled(departureXpath);
		selectOptionContainingText(browserDriver, departureXpath, "SDF");
		selectOptionContainingText(browserDriver, arrivalXpath, "MCO");
		browserDriver.sendKeysToElement("//input[contains(@id,':departureDate')]", "2015-08-12");
		browserDriver.clickElement(searchFlightsButtonXpath);
		browserDriver.waitForElementEnabled(addToCartButtonXpath);
		browserDriver.clickElement(addToCartButtonXpath);
		browserDriver.waitForElementEnabled(removeButtonXpath);
		browserDriver.clickElement("//input[@value='Checkout']");

		String titleFieldXpath = "//select[contains(@id,':titleId')]";
		browserDriver.waitForElementEnabled(titleFieldXpath);
		createSelect(browserDriver, titleFieldXpath).selectByVisibleText("Mr.");
		browserDriver.sendKeysToElement("//input[contains(@id,':firstName')]", "John");
		browserDriver.sendKeysToElement("//input[contains(@id,':lastName')]", "Adams");
		browserDriver.sendKeysToElement("//input[contains(@id,':emailAddress')]", "John@Adams.org");
		browserDriver.sendKeysToElement("//input[contains(@id,':phoneNumber')]", "1234567890");
		browserDriver.sendKeysToElement("//input[contains(@id,':addressLine1')]", "123 Gilgod Ave");
		browserDriver.sendKeysToElement("//input[contains(@id,':city')]", "Hollywood");
		createSelect(browserDriver, "//select[contains(@id,':provinceId')]").selectByVisibleText("California");
		createSelect(browserDriver, "//select[contains(@id,':paymentTypeId')]").selectByVisibleText("Visa");
		browserDriver.sendKeysToElement("//input[contains(@id,':accountNumber')]", "12345678901234567890");
		browserDriver.sendKeysToElement("//input[contains(@id,':expirationMonth')]", "01/35");
		browserDriver.clickElement("//input[@value='Purchase']");

		String cvvFieldXpath = "//input[contains(@id,':cvv')]";
		String cvvFieldErrorXpath = cvvFieldXpath + "/../span[@class='portlet-msg-error']";
		browserStateAsserter.assertTextPresentInElement("Value is required", cvvFieldErrorXpath);
		browserDriver.sendKeysToElement(cvvFieldXpath, "123");
		browserDriver.clickElement("//input[@value='Purchase']");

		String callSurveyFlowButtonXpath = "//input[@value='Call Survey Flow']";
		browserStateAsserter.assertTextPresentInElement("Thank you John for your purchase.",
			"//div[contains(@class,'liferay-faces-bridge-body')]//form");

		// Test the survey flow scope.
		browserDriver.clickElement(callSurveyFlowButtonXpath);

		String finishButtonXpath = "//input[@value='Finish']";
		browserDriver.waitForElementEnabled(finishButtonXpath);
		browserDriver.sendKeysToElement("//div[contains(@id,':question1')]/input", "Liferay");
		browserDriver.sendKeysToElement("//div[contains(@id,':question2')]/input", "Cockpit");
		browserDriver.clickElement(finishButtonXpath);
		browserStateAsserter.assertTextPresentInElement("Thank you John for participating in our survey.",
			"//div[contains(@class,'liferay-faces-bridge-body')]//form");

		// Test that exiting the survey flow scope causes beans to go out of scope.
		browserDriver.clickElement("//input[@value='Return From Survey Flow']");
		assertFlowBeansOutOfScope(browserStateAsserter);
	}

	@Before
	public void setUpJSFFlowsPortletTester() {
		getBrowserDriver().setWaitTimeOut(TestUtil.getBrowserDriverWaitTimeOut(10));
	}

	@After
	public void tearDownJSFFlowsPortletTester() {
		getBrowserDriver().setWaitTimeOut(TestUtil.getBrowserDriverWaitTimeOut());
	}

	protected void assertLibraryElementDisplayed(BrowserStateAsserter browserStateAsserter, String libraryName,
		BrowserDriver browserDriver) {

		String libraryVersionXpath = "//li[contains(.,'" + libraryName + "')]";
		browserStateAsserter.assertElementDisplayed(libraryVersionXpath);

		if (logger.isInfoEnabled()) {

			WebElement libraryVersionElement = browserDriver.findElementByXpath(libraryVersionXpath);
			logger.info(libraryVersionElement.getText());
		}
	}

	private void assertFlowBeansOutOfScope(BrowserStateAsserter browserStateAsserter) {

		browserStateAsserter.assertTextPresentInElement("bookingFlowModelBeanInScope=false",
			"//li/em[contains(text(),'bookingFlowModelBeanInScope')]");
		browserStateAsserter.assertTextPresentInElement("cartModelBeanInScope=false",
			"//li/em[contains(text(),'cartModelBeanInScope')]");
		browserStateAsserter.assertTextPresentInElement("flightSearchModelBeanInScope=false",
			"//li/em[contains(text(),'flightSearchModelBeanInScope')]");
		browserStateAsserter.assertTextPresentInElement("surveyFlowModelBeanInScope=false",
			"//li/em[contains(text(),'surveyFlowModelBeanInScope')]");
	}

	private Select createSelect(BrowserDriver browserDriver, String selectXpath) {

		WebElement element = browserDriver.findElementByXpath(selectXpath);

		return new Select(element);
	}

	private void selectOptionContainingText(BrowserDriver browserDriver, String selectXpath, String text) {

		WebElement option = browserDriver.findElementByXpath(selectXpath + "/option[contains(text(),'" + text + "')]");
		String value = option.getAttribute("value");
		createSelect(browserDriver, selectXpath).selectByValue(value);
	}
}
