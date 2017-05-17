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
package com.liferay.faces.test.showcase.portlet;

import org.junit.Test;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserStateAsserter;
import com.liferay.faces.test.showcase.TesterBase;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class ActionURLGeneralTester extends TesterBase {

	@Test
	public void runPortletActionURLGeneralTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		navigateToUseCase(browserDriver, "portlet", "actionURL", "general");

		// Submit the form and test that the browser navigated to the Non-Faces Postback page which displays the
		// submitted value for the "foo" parameter.
		String toActionParamPageXpath =
			"//div[@class='showcase-example-usage']//input[@value='Submit Non-Faces Postback']";
		browserDriver.clickElement(toActionParamPageXpath);

		BrowserStateAsserter browserStateAsserter = getBrowserStateAsserter();
		String returnToPortletActionURLXpath =
			"//div[contains(@class,'portlet-body') or contains(@class,'body')]//a[contains(text(),'Return to portlet:actionURL')]";
		browserStateAsserter.assertElementDisplayed(
			"//div[@class='portlet-body' or contains(@class,'body')]//pre[text()='foo=1234']");
		browserStateAsserter.assertElementDisplayed(returnToPortletActionURLXpath);

		// Click the return link and test that the browser returned to the correct page.
		browserDriver.clickElement(returnToPortletActionURLXpath);
		browserStateAsserter.assertElementDisplayed(toActionParamPageXpath);
	}
}
