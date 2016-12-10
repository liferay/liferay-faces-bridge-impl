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
public class PortletRenderURLGeneralTester extends TesterBase {

	@Test
	public void runPortletRenderURLGeneralTest() {

		Browser browser = Browser.getInstance();
		navigateToUseCase(browser, "portlet", "renderURL", "general");

		// Click "Hyperlink targeting the Showcase portlet" and check that it opens the Showcase portlet home page
		String hyperlinkTargetingTheShowcasePortletXpath =
			"(//div[@class='showcase-example-usage'])/a[text()='Hyperlink targeting the Showcase portlet']";
		browser.click(hyperlinkTargetingTheShowcasePortletXpath);

		// due to SPA race condition, we cannot use "waitForShowcasePageReady(browser);"
		browser.waitForElementVisible("(//div[@class='showcase-home'])//a[text()='JSR 378']");
		SeleniumAssert.assertElementVisible(browser, "(//div[@class='showcase-home'])//a[text()='JSR 378']");
	}
}
