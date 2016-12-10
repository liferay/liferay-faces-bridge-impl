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
import com.liferay.faces.test.showcase.image.ImageTester;


/**
 * @author  Kyle Stiemann
 * @author  Philip White
 */
public class PortletResourceURLGeneralTester extends ImageTester {

	@Test
	public void runPortletResourceURLGeneralTest() {

		Browser browser = Browser.getInstance();
		navigateToUseCase(browser, "portlet", "resourceURL", "general");

		// Test that the image resource is rendered on the page.
		String imageXpath =
			"//div[@class='showcase-example']//img[contains(@src,'javax.faces.resource')][contains(@src,'ln=images') or contains(@src,'ln:images')]";
		assertImageRendered(browser, imageXpath);
	}
}
