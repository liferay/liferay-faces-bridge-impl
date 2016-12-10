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
package com.liferay.faces.test.bridge.showcase.inputfile;

import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;
import com.liferay.faces.test.showcase.input.InputTester;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class InputFileTester extends InputTester {

	// Private Constants
	private static final String LIFERAY_JSF_JERSEY_PNG_FILE_PATH = System.getProperty("java.io.tmpdir") +
		"liferay-jsf-jersey.png";
	private static final String PHANTOMJS_ALERT_CONFIRMATION_WORKAROUND =
		"window.alert = function(msg){ console.log(msg); };" +
		"window.confirm = function(msg){console.log(msg); return true;};";

	// Private Xpaths
	private static final String fileUploadChooserXpath = "//input[@type='file']";
	private static final String uploadedFileXpath = "(//pre/table/tbody/tr/td[2]|//pre/table/tbody/tr/td[2]/span)";
	private static final String deleteFileXpath =
		"(//input[contains(@src,'icon-delete.png')]|//button/span[contains(@class,'trash')])";

	protected void runBridgeInputFileTest(boolean ajax, boolean multiple) {

		Browser browser = Browser.getInstance();
		String useCase = "general";

		if (ajax) {
			useCase = "instant-ajax";
		}

		if (multiple) {

			// multiple file upload is currently unsupported on Selenium.
			// https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/2239
			useCase = "multiple";
		}

		navigateToUseCase(browser, "bridge", "inputFile", useCase);
		browser.sendKeys(fileUploadChooserXpath, LIFERAY_JSF_JERSEY_PNG_FILE_PATH);

		if (!ajax) {
			browser.click(submitButton1Xpath);
		}

		browser.waitForElementVisible(uploadedFileXpath);
		SeleniumAssert.assertElementTextVisible(browser, uploadedFileXpath, "jersey");

		String browserName = browser.getName();

		// Workaround https://github.com/detro/ghostdriver/issues/20: Implement all the Session Commands related to JS
		// Alert, Prompt and Confirm.
		if (browserName.contains("phantomjs")) {

			browser.executeScript(PHANTOMJS_ALERT_CONFIRMATION_WORKAROUND);
			browser.click(deleteFileXpath);
		}
		else {

			browser.click(deleteFileXpath);
			browser.waitUntil(ExpectedConditions.alertIsPresent());
			browser.switchTo().alert().accept();
		}

		browser.waitForElementNotPresent(uploadedFileXpath);
		SeleniumAssert.assertElementTextInvisible(browser, uploadedFileXpath, "jersey");
	}
}
