/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Vernon Singleton
 * @author  Philip White
 */
public class FACES_257PortletTester extends BrowserDriverManagingTesterBase {

	private static void assertURLEndsWith(BrowserDriver browserDriver, WaitingAsserter waitingAsserter,
		String linkXpath, String expectedURLEnding) {

		waitingAsserter.assertTrue(ExpectedConditions.attributeContains(By.xpath(linkXpath), "href",
				expectedURLEnding));
		Assert.assertTrue(browserDriver.getCurrentWindowUrl().endsWith(expectedURLEnding));
	}

	@Test
	public void runFACES_257PortletTest() {

		String container = TestUtil.getContainer();
		Assume.assumeTrue("The FACES-257 test is only valid on Liferay Portal.", container.startsWith("liferay"));

		// Navigate the browser to the portal page that contains the FACES-257 portlet.
		BrowserDriver browserDriver = getBrowserDriver();
		String issuePageURL = BridgeTestUtil.getIssuePageURL("faces-257");
		browserDriver.navigateWindowTo(issuePageURL);

		// Verify that the <span> that contains "requestedURL=..." is displayed
		browserDriver.waitForElementDisplayed("//span[contains(@id, ':requestedURL')]");

		// STEP 1: alpha=1 beta=2 gamma=0
		// Click on the button in step 1 in order to cause the browser to navigate to a friendly Liferay RenderURL via
		// HTTP GET.
		browserDriver.clickElementAndWaitForRerender(
			"//input[@type='button' and contains(@value, 'alpha=1 beta=2 gamma=0')]");

		// Verify that the current URL of the browser and the URL of the link end with the expected friendly URL
		// mapping.
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		assertURLEndsWith(browserDriver, waitingAsserter, "//a[contains(text(), 'alpha=1 beta=2 gamma=0')]",
			"/-/FACES-257/1/my-friendly-action/2");

		// Verify that the alpha, beta, and gamma parameter values appear in the markup
		String alphaXpath = "//span[contains(@id, ':alpha')]";
		waitingAsserter.assertTextPresentInElement("1", alphaXpath);

		String betaXpath = "//span[contains(@id, ':beta')]";
		waitingAsserter.assertTextPresentInElement("2", betaXpath);

		String gammaXpath = "//span[contains(@id, ':gamma')]";
		waitingAsserter.assertTextPresentInElement("0", gammaXpath);

		// STEP 2: alpha=1 beta=2 gamma=3
		// Click on the button in step 2 in order to cause the browser to navigate to a friendly Liferay RenderURL via
		// HTTP GET.
		browserDriver.clickElementAndWaitForRerender(
			"//input[@type='button' and contains(@value, 'alpha=1 beta=2 gamma=3')]");

		// Verify that the current URL of the browser and the URL of the link end with the expected friendly URL
		// mapping.
		assertURLEndsWith(browserDriver, waitingAsserter, "//a[contains(text(), 'alpha=1 beta=2 gamma=3')]",
			"/-/FACES-257/1/my-friendly-action/2/3");

		// Verify that the alpha, beta, and gamma parameter values appear in the markup
		waitingAsserter.assertTextPresentInElement("1", alphaXpath);
		waitingAsserter.assertTextPresentInElement("2", betaXpath);
		waitingAsserter.assertTextPresentInElement("3", gammaXpath);

		// STEP 3: alpha=4 beta=5 gamma=0
		// Click on the button in step 3 in order to cause the browser to navigate to a friendly Liferay RenderURL via
		// HTTP GET.
		browserDriver.clickElementAndWaitForRerender(
			"//input[@type='button' and contains(@value, 'alpha=4 beta=5 gamma=0')]");

		// Verify that the current URL of the browser and the URL of the link end with the expected friendly URL
		// mapping.
		assertURLEndsWith(browserDriver, waitingAsserter, "//a[contains(text(), 'alpha=4 beta=5 gamma=0')]",
			"/-/FACES-257/4/my-friendly-action/5");

		// Verify that the alpha, beta, and gamma parameter values appear in the markup
		waitingAsserter.assertTextPresentInElement("4", alphaXpath);
		waitingAsserter.assertTextPresentInElement("5", betaXpath);
		waitingAsserter.assertTextPresentInElement("0", gammaXpath);

		// STEP 4: alpha=4 beta=5 gamma=6
		// Click on the button in step 4 in order to cause the browser to navigate to a friendly Liferay RenderURL via
		// HTTP GET.
		browserDriver.clickElementAndWaitForRerender(
			"//input[@type='button' and contains(@value, 'alpha=4 beta=5 gamma=6')]");

		// Verify that the current URL of the browser and the URL of the link end with the expected friendly URL
		// mapping.
		assertURLEndsWith(browserDriver, waitingAsserter, "//a[contains(text(), 'alpha=4 beta=5 gamma=6')]",
			"/-/FACES-257/4/my-friendly-action/5/6");

		// Verify that the alpha, beta, and gamma parameter values appear in the markup
		waitingAsserter.assertTextPresentInElement("4", alphaXpath);
		waitingAsserter.assertTextPresentInElement("5", betaXpath);
		waitingAsserter.assertTextPresentInElement("6", gammaXpath);
	}
}
