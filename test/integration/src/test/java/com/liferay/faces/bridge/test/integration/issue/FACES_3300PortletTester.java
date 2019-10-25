/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.WebElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
public class FACES_3300PortletTester extends SimpleFACESPortletTester {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FACES_3300PortletTester.class);

	private static List<String> getURLsFromLinks(BrowserDriver browserDriver, String linksXpath) {
		return getURLsFromLinks(browserDriver, linksXpath, null);
	}

	private static List<String> getURLsFromLinks(BrowserDriver browserDriver, String linksXpath, String container) {

		List<WebElement> linkElements = browserDriver.findElementsByXpath(linksXpath);
		List<String> urls = new ArrayList<String>();

		for (WebElement linkElement : linkElements) {

			String link = linkElement.getAttribute("href");

			if ((container != null) && container.startsWith("liferay")) {
				link = link.replace("p_p_state=normal", "p_p_state=exclusive");
			}

			urls.add(link);
		}

		Assert.assertTrue(!urls.isEmpty());

		return Collections.unmodifiableList(urls);
	}

	@Test
	public void runFACES_3300PortletTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-3300"));

		String container = TestUtil.getContainer();
		List<String> validURLs = getURLsFromLinks(browserDriver, "//ol[@id='validTestURLs']/li/a");
		List<String> invalidURLs = getURLsFromLinks(browserDriver, "//ol[@id='invalidTestURLs']/li/a", container);

		WaitingAsserter waitingAsserter = getWaitingAsserter();

		for (String validURL : validURLs) {

			browserDriver.navigateWindowTo(validURL);
			runSimpleFACESPortletTest(browserDriver, "faces-3300");
		}

		for (String invalidURL : invalidURLs) {

			browserDriver.navigateWindowTo(invalidURL);

			if (container.startsWith("pluto")) {
				waitingAsserter.assertTextPresentInElement("Error rendering portlet FACES-3300.",
					"//div[@class='body']");
			}
			else if (container.startsWith("liferay")) {
				waitingAsserter.assertTextPresentInElement("FACES-3300 is temporarily unavailable.",
					"//div[contains(@class,'alert')][contains(@class,'alert-danger') or contains(@class,'alert-error')]");
			}
		}
	}
}
