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
package com.liferay.faces.bridge.test.integration.issue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1478PortletTester extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = Logger.getLogger(FACES_1478PortletTester.class.getName());

	static {
		logger.setLevel(TestUtil.getLogLevel());
	}

	@Test
	public void runFACES_1478PortletTest() {

		String container = TestUtil.getContainer();
		Assume.assumeTrue("The FACES-1635 test is only valid on Liferay Portal.", container.contains("liferay"));

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-1478"));
		browser.waitForElementVisible("//a[contains(text(),'FACES-1478')]");

		// Test that the visible url is the same as the link's href url.
		WebElement urlSpan = browser.findElementByXpath("//span[contains(@id,':url')]");
		String url = urlSpan.getText();
		WebElement link = browser.findElementByXpath("//a[contains(text(),'should be the same as the href')]");
		String href = link.getAttribute("href");
		logger.log(Level.INFO, "URL:\n\n{0}\nLink href value:\n\n{1}", new String[] { url, href });
		Assert.assertEquals("The URL value does not equal the value of the link's href attribute", url, href);

		// Test that the url contains both parameters.
		WebElement parameter1Span = browser.findElementByXpath("//span[contains(@id,':parameter1')]");
		String parameter1 = parameter1Span.getText();
		Assert.assertTrue("The URL does not contain the first parameter: " + parameter1, url.contains(parameter1));

		WebElement parameter2Span = browser.findElementByXpath("//span[contains(@id,':parameter2')]");
		String parameter2 = parameter2Span.getText();
		Assert.assertTrue("The URL does not contain the second parameter: " + parameter2, url.contains(parameter2));
	}
}
