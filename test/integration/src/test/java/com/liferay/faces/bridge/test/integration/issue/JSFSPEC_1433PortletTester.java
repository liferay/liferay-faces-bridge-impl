/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.test.integration.issue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class JSFSPEC_1433PortletTester extends BrowserDriverManagingTesterBase {

	// Private Constants
	private static final String UI_INPUT_COMPONENT_ELEMENTS_XPATH;

	static {

		String uiInputComponentElementsXpath =
			"//*[contains(@id,':h_')][contains(@id,'_input') or contains(@id,'_select')]";

		// Avoid getting selectItem children of select components.
		for (int i = 0; i < 10; i++) {
			uiInputComponentElementsXpath += "[not(contains(@id,':" + i + "'))]";
		}

		UI_INPUT_COMPONENT_ELEMENTS_XPATH = uiInputComponentElementsXpath;
	}

	@Test
	public void runJSFSPEC_1433PortletTester() {

		// 1. Navigate to the JSFSPEC-1433 page.
		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("jsfspec-1433"));

		//J-
		// 2. Click the *Run Test* button.
		// 3. Verify that a "Value is Required." message appeared for each component.
		//J+
		String failedComponents = getFailedComponents(browserDriver);

		failedComponents = failedComponents.replaceAll(", $", ".");
		Assert.assertEquals(
			"The following components failed to perform validation when the submitted value was null: " +
			failedComponents, "", failedComponents);
	}

	private String getFailedComponents(BrowserDriver browserDriver) {

		// TECHNICAL NOTE: Ensure that the components' "name" attributes have been removed.
		List<WebElement> uiInputComponentElements = browserDriver.findElementsByXpath(
				UI_INPUT_COMPONENT_ELEMENTS_XPATH);
		Assert.assertFalse(uiInputComponentElements.isEmpty());

		WaitingAsserter waitingAsserter = getWaitingAsserter();

		for (WebElement uiInputComponentElement : uiInputComponentElements) {
			waitingAsserter.assertTrue(new NameAttributeRemoved(uiInputComponentElement));
		}

		// Click the *Run Test* button.
		String runTestButtonXpath = "(//button[contains(text(),'Run Test')]|//input[contains(@value,'Run Test')])";
		browserDriver.waitForElementEnabled(runTestButtonXpath);
		browserDriver.clickElementAndWaitForRerender(runTestButtonXpath);

		String failedComponents = "";
		String fieldXpath = "//*[contains(@class,'html-field')]";
		List<WebElement> fieldElements = browserDriver.findElementsByXpath(fieldXpath);

		if (fieldElements.isEmpty()) {
			throw new NoSuchElementException(fieldXpath + " not found on page.");
		}

		for (WebElement fieldElement : fieldElements) {

			List<WebElement> messageElementList = fieldElement.findElements(By.xpath(
						".//*[contains(@class,'portlet-msg-error')]"));
			WebElement labelElement = fieldElement.findElement(By.xpath(".//label"));
			String componentName = labelElement.getText();
			componentName = componentName.replaceAll("\\s+.*$", "");

			// If a component does not have the correct error message ("Text verification failed." for portal:captcha,
			// "Value is required." for all other components), add it to the list of failed components.
			if (messageElementList.isEmpty()) {
				failedComponents += (componentName + ", ");
			}
			else {

				WebElement messageElement = messageElementList.get(0);
				String messageText = messageElement.getText();

				if (!"Value is required.".equals(messageText)) {
					failedComponents += (componentName + ", ");
				}
			}
		}

		return failedComponents;
	}

	private static class NameAttributeRemoved implements ExpectedCondition<Boolean> {

		// Private Data Members
		private WebElement webElement;

		public NameAttributeRemoved(WebElement webElement) {
			this.webElement = webElement;
		}

		@Override
		public Boolean apply(WebDriver webDriver) {

			String name = webElement.getAttribute("name");

			return ((name == null) || "".equals(name));
		}
	}
}
