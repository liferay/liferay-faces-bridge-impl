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
package com.liferay.faces.test.bridge.showcase;

import org.junit.Test;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;
import com.liferay.faces.test.showcase.TesterBase;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class PortletActionURLGeneralTester extends TesterBase {

	@Test
	public void runPortletActionURLGeneralTest() {

		Browser browser = Browser.getInstance();
		navigateToUseCase(browser, "portlet", "actionURL", "general");

		// Click "Submit Non-Faces Postback" and check that it opens the Non-Faces Postback page
		String toActionParamPageXpath =
			"//div[@class='showcase-example-usage']//input[@value='Submit Non-Faces Postback']";
		browser.click(toActionParamPageXpath);

		String ReturnToPortletActionURLXpath =
			"//div[contains(@class,'portlet-body') or contains(@class,'body')]//a[contains(text(),'Return to portlet:actionURL')]";
		browser.waitForElementVisible(ReturnToPortletActionURLXpath);
		SeleniumAssert.assertElementVisible(browser,
			"//div[@class='portlet-body' or contains(@class,'body')]//pre[text()='foo=1234']");
		SeleniumAssert.assertElementVisible(browser, ReturnToPortletActionURLXpath);

		// Click "Return to portlet:actionURL in the Liferay Faces Showcase" and check that it opens the
		// PortletActionURL page
		browser.click(ReturnToPortletActionURLXpath);
		waitForShowcasePageReady(browser);
		SeleniumAssert.assertElementVisible(browser, toActionParamPageXpath);
	}
}
