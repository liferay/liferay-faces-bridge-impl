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

import java.util.Locale;

import org.junit.Test;

import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class FACES_3250PortletTester extends BrowserDriverManagingTesterBase {

	public static void testFACES_3250FileUpload(BrowserDriver browserDriver, WaitingAsserter waitingAsserter,
		String mode) {

		WebElement fileUploadChooser = browserDriver.findElementByXpath("//input[contains(@id,':" + mode +
				"FileUpload')]");

		// Set PrimeFaces p:fileUpload transform style to "none" since it causes the element to not be displayed
		// according to Selenium (although the element is visible to users).
		browserDriver.executeScriptInCurrentWindow("arguments[0].style.transform = 'none';", fileUploadChooser);

		// Workaround https://github.com/ariya/phantomjs/issues/10993 by removing the multiple attribute from <input
		// type="file" />
		if (browserDriver.getBrowserName().equals("phantomjs")) {

			browserDriver.executeScriptInCurrentWindow(
				"var multipleFileUploadElements = document.querySelectorAll('input[type=\"file\"][multiple]');" +
				"for (var i = 0; i < multipleFileUploadElements.length; i++) {" +
				"multipleFileUploadElements[i].removeAttribute('multiple'); }");
		}

		fileUploadChooser.sendKeys(TestUtil.JAVA_IO_TMPDIR + "liferay-jsf-jersey.png");

		if (mode.toLowerCase(Locale.ENGLISH).contains("simplemode")) {
			browserDriver.clickElementAndWaitForRerender("//button[contains(@id,':" + mode + "SubmitButton')]");
		}

		waitingAsserter.assertTextPresentInElement("jersey", "//span[contains(@id,':" + mode + "FileName')]");
	}

	@Test
	public void testFACES_3250Portlet() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-3250"));

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		testFACES_3250FileUpload(browserDriver, waitingAsserter, "advancedMode");
		testFACES_3250FileUpload(browserDriver, waitingAsserter, "simpleMode");
		testFACES_3250FileUpload(browserDriver, waitingAsserter, "skinSimpleMode");
	}
}
