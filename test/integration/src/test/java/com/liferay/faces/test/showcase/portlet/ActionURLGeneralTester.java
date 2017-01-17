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
package com.liferay.faces.test.showcase.portlet;

import org.junit.Test;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;
import com.liferay.faces.test.showcase.TesterBase;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class ActionURLGeneralTester extends TesterBase {

	@Test
	public void runPortletActionURLGeneralTest() {

		Browser browser = Browser.getInstance();
		navigateToUseCase(browser, "portlet", "actionURL", "general");

		// Submit the form and test that the browser navigated to the Non-Faces Postback page which displays the
		// submitted value for the "foo" parameter.
		String toActionParamPageXpath =
			"//div[@class='showcase-example-usage']//input[@value='Submit Non-Faces Postback']";
		browser.click(toActionParamPageXpath);

		String returnToPortletActionURLXpath =
			"//div[contains(@class,'portlet-body') or contains(@class,'body')]//a[contains(text(),'Return to portlet:actionURL')]";
		browser.waitForElementVisible(returnToPortletActionURLXpath);
		SeleniumAssert.assertElementVisible(browser,
			"//div[@class='portlet-body' or contains(@class,'body')]//pre[text()='foo=1234']");
		SeleniumAssert.assertElementVisible(browser, returnToPortletActionURLXpath);

		// Click the return link and test that the browser returned to the correct page.
		browser.click(returnToPortletActionURLXpath);

		// Due to SennaJS/SPA race condition, we cannot use waitForShowcasePageReady() in this case.
		browser.waitForElementVisible(toActionParamPageXpath);
		SeleniumAssert.assertElementVisible(browser, toActionParamPageXpath);
	}
}
