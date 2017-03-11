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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1638PortletTester extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = Logger.getLogger(FACES_1638PortletTester.class.getName());

	static {
		logger.setLevel(TestUtil.getLogLevel());
	}

	@Test
	public void runFACES_1638PortletTest() {

		String container = TestUtil.getContainer();
		Assume.assumeTrue("The FACES-1635 test is only valid on Liferay Portal.", container.contains("liferay"));

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-1638"));

		List<WebElement> listItems = browser.findElements(By.xpath(
					"//div[contains(@class,'liferay-faces-bridge-body')]//ul/li"));
		int expectedNumberOfListItems = 6;
		Assert.assertEquals("There are not " + expectedNumberOfListItems + " links on the page.",
			expectedNumberOfListItems, listItems.size());

		// Test that each link contains only one *-Item param which has the correct value.
		for (WebElement listItem : listItems) {

			WebElement paramNameSpan = listItem.findElement(By.xpath(".//span[@class='param-name']"));
			String paramName = paramNameSpan.getText();

			WebElement paramValueSpan = listItem.findElement(By.xpath(".//span[@class='param-value']"));
			String paramValue = paramValueSpan.getText();

			String param = paramName + "=" + paramValue;

			WebElement link = listItem.findElement(By.xpath(".//a[contains(text(),'-Item')][@href]"));
			String url = link.getAttribute("href");

			logger.log(Level.INFO, "URL:\n\n{0}", new String[] { url });
			Assert.assertTrue("The link does not contain the parameter.", url.contains(param));

			int expectedOccurencesOfParamName = 1;
			Assert.assertEquals("The link contains more than than " + expectedOccurencesOfParamName + " \"" +
				paramName + "\" parameter.", expectedOccurencesOfParamName, occurencesOf(paramName, url));
		}
	}

	private int occurencesOf(String occuringString, String inString) {
		return (inString.length() - inString.replace(occuringString, "").length()) / occuringString.length();
	}
}
