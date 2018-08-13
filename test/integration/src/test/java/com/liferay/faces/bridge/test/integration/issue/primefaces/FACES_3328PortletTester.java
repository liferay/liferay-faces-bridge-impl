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
package com.liferay.faces.bridge.test.integration.issue.primefaces;

import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class FACES_3328PortletTester extends BrowserDriverManagingTesterBase {

	@Test
	public void testFACES_3328PortletTester() {

		String container = TestUtil.getContainer();
		Assume.assumeTrue("This is a SennaJS/SPA related test for Liferay 7.0+",
			(container.startsWith("liferay") && !container.equals("liferay62")));

		// Navigate the browser to the portal page that contains the FACES-1513-2185 portlet.
		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-3328-start"));
		browserDriver.clickElement("//a[@href='" + BridgeTestUtil.getIssuePageURL("faces-3328") + "']");

		String datePickerButtonXpath = "//button[contains(@class, 'ui-datepicker-trigger')]";
		browserDriver.waitForElementEnabled(datePickerButtonXpath);
		browserDriver.clickElement(datePickerButtonXpath);

		String dateCellXpath = "//table[contains(@class, 'ui-datepicker-calendar')]//a[contains(text(), '14')]";
		browserDriver.waitForElementEnabled(dateCellXpath);
		browserDriver.clickElement(dateCellXpath);
		browserDriver.clickElement("//a[@href='" + BridgeTestUtil.getIssuePageURL("faces-3328-end") + "']");

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		List<WebElement> elements = browserDriver.findElementsByXpath(
				"//table[contains(@class, 'ui-datepicker-calendar')]");
		waitingAsserter.assertTrue(ExpectedConditions.invisibilityOfAllElements(elements));
		elements = browserDriver.findElementsByXpath("//*[contains(@id,'p_selectOneMenu')]");
		waitingAsserter.assertTrue(ExpectedConditions.invisibilityOfAllElements(elements));
	}
}
