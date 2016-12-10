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
public class PortletNamespaceGeneralTester extends TesterBase {

	@Test
	public void runPortletNamespaceGeneralTest() {

		Browser browser = Browser.getInstance();
		navigateToUseCase(browser, "portlet", "namespace", "general");

		// Test that the text properties are rendered in both examples on the page.
		SeleniumAssert.assertElementTextVisible(browser,
			"//label[contains(.,'Example')][contains(.,'Introducing a var into the EL')]/ancestor::div[@class='showcase-example']//pre",
			"portletNamespace=");
		SeleniumAssert.assertElementTextVisible(browser,
			"//label[contains(.,'Example')][contains(.,'Output directly to the response')]/ancestor::div[@class='showcase-example']//pre",
			"portletNamespace=");
		SeleniumAssert.assertElementTextVisible(browser,
			"//label[contains(.,'Example')][contains(.,'Introducing a var into the EL')]/ancestor::div[@class='showcase-example']//pre",
			"portlet_");
		SeleniumAssert.assertElementTextVisible(browser,
			"//label[contains(.,'Example')][contains(.,'Output directly to the response')]/ancestor::div[@class='showcase-example']//pre",
			"portlet_");
	}
}
