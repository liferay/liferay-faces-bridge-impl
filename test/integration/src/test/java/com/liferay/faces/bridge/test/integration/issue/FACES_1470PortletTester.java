/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;
import com.liferay.faces.test.selenium.expectedconditions.WindowOpened;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1470PortletTester extends BrowserDriverManagingTesterBase {

	// Xpaths
	private static final String as7LeakInstanceXpath = "//span[contains(@id,'as7LeakInstances')]//ul/li";

	@After
	public void reset() {

		BrowserDriver browserDriver = getBrowserDriver();
		Set<String> windowIds = browserDriver.getWindowIds();
		int numberOfWindowsToClose = windowIds.size() - 1;

		for (int i = 0; i < numberOfWindowsToClose; i++) {
			browserDriver.closeCurrentWindow();
		}

		windowIds = browserDriver.getWindowIds();
		browserDriver.switchToWindow(windowIds.iterator().next());
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-1470"));

		signOut(browserDriver);
		super.doSetUp();
		browserDriver.setWaitTimeOut(TestUtil.getBrowserDriverWaitTimeOut());
	}

	@Test
	public void runFACES_1470PortletTest() {

		String container = TestUtil.getContainer();
		Assume.assumeTrue("The FACES-1470 test is only valid on Liferay Portal.", container.startsWith("liferay"));

		// 1. Sign in to the portal.
		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-1470"));

		String navigateToView1LinkXpath = "//a[contains(text(), 'Navigate to view1.xhtml')]";
		browserDriver.waitForElementEnabled(navigateToView1LinkXpath);

		// 2. Open the *Navigate to view1.xhtml* link in a new tab.
		String as7LeakInstanceTrackerWindowId = browserDriver.getCurrentWindowId();
		Set<String> windowIds = browserDriver.getWindowIds();
		int numberOfWindows = windowIds.size();
		browserDriver.clickElement(navigateToView1LinkXpath);
		browserDriver.waitFor(new WindowOpened(numberOfWindows));
		windowIds = browserDriver.getWindowIds();

		// 3. Switch back to the first tab (as7LeakTracker.xhtml) and click the *Refresh AS7Leak List* button and
		// confirm that several AS7Leak class instances appear.
		Iterator<String> iterator = windowIds.iterator();
		String viewsWindowId = null;

		while (iterator.hasNext()) {

			String windowId = iterator.next();

			if (!as7LeakInstanceTrackerWindowId.equals(windowId)) {

				viewsWindowId = windowId;

				break;
			}
		}

		if (viewsWindowId == null) {
			throw new IllegalStateException("No new tab opened when *Navigate to view1.xhtml* was clicked.");
		}

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		List<WebElement> as7LeakInstanceElements = new ArrayList<WebElement>();
		testAS7LeakIntancesCreated(browserDriver, waitingAsserter, as7LeakInstanceTrackerWindowId,
			as7LeakInstanceElements);

		// 4. Switch back to the tab with view1.xhtml and click the *Click me to navigate to view2.xhtml via Ajax*
		// button.
		switchToWindow(browserDriver, viewsWindowId);

		String navToView2AjaxButtonXpath = "//input[contains(@value,'Click me to navigate to view2.xhtml via Ajax')]";
		clickButton(browserDriver, navToView2AjaxButtonXpath);

		// 5. Click the *Click me to navigate to view1.xhtml via Ajax* button.
		String navToView1AjaxButtonXpath = "//input[contains(@value,'Click me to navigate to view1.xhtml via Ajax')]";
		clickButton(browserDriver, navToView1AjaxButtonXpath);

		// 6. Switch back to the first tab (as7LeakTracker.xhtml) and click the Refresh AS7Leak List button and confirm
		// that several AS7Leak class instances appear.
		testAS7LeakIntancesCreated(browserDriver, waitingAsserter, as7LeakInstanceTrackerWindowId,
			as7LeakInstanceElements);

		// 7. Switch back to the tab with view1.xhtml and click the *Click me to navigate to view2.xhtml via non-Ajax
		// (full postback)* button.
		switchToWindow(browserDriver, viewsWindowId);

		String navToView2NonAjaxButtonXpath =
			"//input[contains(@value,'Click me to navigate to view2.xhtml via non-Ajax (full postback)')]";
		clickButton(browserDriver, navToView2NonAjaxButtonXpath);

		// 8. Click the *Click me to navigate to view1.xhtml via non-Ajax (full postback)* button.
		String navToView1NonAjaxButtonXpath =
			"//input[contains(@value,'Click me to navigate to view1.xhtml via non-Ajax (full postback)')]";
		clickButton(browserDriver, navToView1NonAjaxButtonXpath);

		// 9. Switch back to the first tab (as7LeakTracker.xhtml) and click the *Refresh AS7Leak List* button and
		// confirm that several AS7Leak class instances appear.
		testAS7LeakIntancesCreated(browserDriver, waitingAsserter, as7LeakInstanceTrackerWindowId,
			as7LeakInstanceElements);

		// 10. Switch back to the tab with view1.xhtml and click the *Click me to navigate to view2.xhtml via Ajax*
		// button.
		switchToWindow(browserDriver, viewsWindowId);
		clickButton(browserDriver, navToView2AjaxButtonXpath);

		// 11. Click the *Click me to navigate to view1.xhtml via non-Ajax (full postback)* button.
		clickButton(browserDriver, navToView1NonAjaxButtonXpath);

		// 12. Switch back to the first tab (as7LeakTracker.xhtml) and click the *Refresh AS7Leak List* button and
		// confirm that several AS7Leak class instances appear.
		testAS7LeakIntancesCreated(browserDriver, waitingAsserter, as7LeakInstanceTrackerWindowId,
			as7LeakInstanceElements);

		// 13. Switch back to the tab with view1.xhtml and click the *Click me to navigate to view2.xhtml via non-Ajax
		// (full postback)* button.
		switchToWindow(browserDriver, viewsWindowId);
		clickButton(browserDriver, navToView2NonAjaxButtonXpath);

		// 14. Click the *Click me to navigate to view1.xhtml via Ajax* button.
		clickButton(browserDriver, navToView1AjaxButtonXpath);

		// 15. Switch back to the first tab (as7LeakTracker.xhtml) and click the *Refresh AS7Leak List* button and
		// confirm that several AS7Leak class instances appear.
		testAS7LeakIntancesCreated(browserDriver, waitingAsserter, as7LeakInstanceTrackerWindowId,
			as7LeakInstanceElements);

		// 16. Switch back to the tab with view1.xhtml, navigate to the as7LeakTracker.xhtml portlet page (to avoid
		// loading any AS7Leak instances as an unauthenticated user), and sign out of the portal.
		switchToWindow(browserDriver, viewsWindowId);
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-1470"));
		signOut(browserDriver);
		browserDriver.closeCurrentWindow();

		// 17. Switch back to the first tab (as7LeakTracker.xhtml) and click the *Refresh AS7Leak List* button and
		// confirm that several AS7Leak class instances appear.
		testAS7LeakIntancesCreated(browserDriver, waitingAsserter, as7LeakInstanceTrackerWindowId,
			as7LeakInstanceElements);

		// 18. Click the *Perform Garbage Collection* button and confirm that no AS7Leak class instances appear. Note:
		// since this portlet uses System.gc() to initiate garbage collection, it may be necessary to click the *Perform
		// Garbage Collection* multiple times before the garbage is actually collected. For more details see here:
		// http://docs.oracle.com/javase/6/docs/api/java/lang/System.html#gc().
		ExpectedCondition<Boolean> as7LeakElementsNotPresent = ExpectedConditions.not(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath(as7LeakInstanceXpath)));

		// Attempt garbage collection 5 times before giving up.
		for (int i = 0; i < 5; i++) {

			clickButton(browserDriver, "//input[contains(@value,'Perform Garbage Collection')]");

			try {

				browserDriver.waitFor(as7LeakElementsNotPresent);

				break;
			}
			catch (TimeoutException e) {
				// Retry.
			}
		}

		waitingAsserter.assertElementNotPresent(as7LeakInstanceXpath);
	}

	@Before
	public void setUpFACES_1470PortletTester() {
		getBrowserDriver().setWaitTimeOut(TestUtil.getBrowserDriverWaitTimeOut(
				BridgeTestUtil.DOUBLED_DEFAULT_BROWSER_DRIVER_WAIT_TIME_OUT));
	}

	private void clickButton(BrowserDriver browserDriver, String buttonXpath) {

		// Workaround element not visible problem in headless Chrome.
		if (browserDriver.isBrowserHeadless() && "chrome".equals(browserDriver.getBrowserName())) {

			browserDriver.waitForElementEnabled(buttonXpath, false);

			WebElement buttonElement = browserDriver.findElementByXpath(buttonXpath);
			browserDriver.executeScriptInCurrentWindow("arguments[0].click();", buttonElement);
		}
		else {

			browserDriver.waitForElementEnabled(buttonXpath);
			browserDriver.clickElement(buttonXpath);
		}
	}

	private void signOut(BrowserDriver browserDriver) {

		String container = TestUtil.getContainer();

		if (container.startsWith("liferay")) {
			browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + "/c/portal/logout");
		}

		browserDriver.clearBrowserCookies();
	}

	private void switchToWindow(BrowserDriver browserDriver, String windowId) {

		browserDriver.switchToWindow(windowId);
		browserDriver.waitFor(new SwitchedToWindow(windowId));
	}

	private void testAS7LeakIntancesCreated(BrowserDriver browserDriver, WaitingAsserter waitingAsserter,
		String as7LeakInstanceTrackerWindowId, List<WebElement> previousAS7LeakInstanceElements) {

		switchToWindow(browserDriver, as7LeakInstanceTrackerWindowId);

		String refreshAS7LeakListButtonXpath = "//input[contains(@value,'Refresh AS7Leak List')]";
		browserDriver.clickElementAndWaitForRerender(refreshAS7LeakListButtonXpath);

		List<WebElement> as7LeakInstanceElements = browserDriver.findElementsByXpath(as7LeakInstanceXpath);
		int previousNumberOfAS7LeakInstances = previousAS7LeakInstanceElements.size();
		int currentNumberOfAS7LeakInstances = as7LeakInstanceElements.size();
		Assert.assertTrue(
			"The size of the list of AS7Leak instances was expected to grow (or remain the same) after the previous action, but instead it was reduced.\n" +
			"Previous size was: " + previousNumberOfAS7LeakInstances + ". Current size is: " +
			currentNumberOfAS7LeakInstances,
			(currentNumberOfAS7LeakInstances > 0) &&
			(currentNumberOfAS7LeakInstances >= previousNumberOfAS7LeakInstances));
		previousAS7LeakInstanceElements.clear();
		previousAS7LeakInstanceElements.addAll(as7LeakInstanceElements);

		// Test that at least one non-garbage collected AS7Leak instance is displayed. Non-garbage collected instances
		// will be rendered as an <li> with <code> tags containing the text "AS7Leak". Garbage collected instances will
		// be displayed as an empty <li>.
		waitingAsserter.assertElementDisplayed(as7LeakInstanceXpath + "/code[contains(text(),'AS7Leak')]");
	}

	private static class SwitchedToWindow implements ExpectedCondition<Boolean> {

		// Private Data Members
		private String expectedWindowId;

		public SwitchedToWindow(String expectedWindowId) {
			this.expectedWindowId = expectedWindowId;
		}

		@Override
		public Boolean apply(WebDriver webDriver) {

			String currrentWindowId = webDriver.getWindowHandle();

			return expectedWindowId.equals(currrentWindowId);
		}
	}
}
