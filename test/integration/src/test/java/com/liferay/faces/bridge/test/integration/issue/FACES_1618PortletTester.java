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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1618PortletTester extends BrowserDriverManagingTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FACES_1618PortletTester.class);

	@Test
	public void runFACES_1618PortletTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-1618"));

		String componentResourcesSizeXpath = "//span[@id='component_resources_size']";
		browserDriver.waitForElementDisplayed(componentResourcesSizeXpath);

		String resourcesXpath = "//ul[@id='FACES_1618_resources']/li";
		waitForAllResources(browserDriver, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources are rendered.
		List<WebElement> resources = browserDriver.findElementsByXpath(resourcesXpath);
		List<String> loadedResourceIds = new ArrayList<String>();
		String childSpanXpath = ".//span";

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.info(resourceText);
			assertRendered(resourceId, resourceText);
			loadedResourceIds.add(resourceId);
		}

		String immediateNavButtonXpath = "//input[contains(@value,'immediate=\"true\"')]";
		browserDriver.clickElement(immediateNavButtonXpath);

		String navButtonXpath = "//input[contains(@value,'immediate=\"false\"')]";
		browserDriver.waitForElementEnabled(navButtonXpath);
		waitForAllResources(browserDriver, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources loaded in the previous view are suppressed and not rendered/loaded a second time in
		// the current view. Also test that the resources new to this view are rendered and not suppressed.
		resources = browserDriver.findElementsByXpath(resourcesXpath);

		String container = TestUtil.getContainer();

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.info(resourceText);

			if (resourceShouldBeSuppressed(loadedResourceIds, resourceId, container)) {
				assertSuppressed(resourceId, resourceText);
			}
			else {

				assertRendered(resourceId, resourceText);
				loadedResourceIds.add(resourceId);
			}
		}

		browserDriver.clickElement(navButtonXpath);
		browserDriver.waitForElementEnabled(immediateNavButtonXpath);
		waitForAllResources(browserDriver, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources loaded in the previous views are still suppressed.
		resources = browserDriver.findElementsByXpath(resourcesXpath);

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.info(resourceText);

			if (resourceShouldBeSuppressed(loadedResourceIds, resourceId, container)) {
				assertSuppressed(resourceId, resourceText);
			}
			else {

				assertRendered(resourceId, resourceText);
				loadedResourceIds.add(resourceId);
			}
		}

		browserDriver.clickElement(immediateNavButtonXpath);
		browserDriver.waitForElementEnabled(navButtonXpath);
		waitForAllResources(browserDriver, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources loaded in the previous views are still suppressed.
		resources = browserDriver.findElementsByXpath(resourcesXpath);

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.info(resourceText);

			if (resourceShouldBeSuppressed(loadedResourceIds, resourceId, container)) {
				assertSuppressed(resourceId, resourceText);
			}
			else {

				assertRendered(resourceId, resourceText);
				loadedResourceIds.add(resourceId);
			}
		}
	}

	private void assertRendered(String resourceId, String resourceText) {
		Assert.assertTrue(resourceId + " was suppressed, but it should have been rendered.",
			resourceText.contains(resourceId) && resourceText.contains("was rendered"));
	}

	private void assertSuppressed(String resourceId, String resourceText) {
		Assert.assertTrue(resourceId + " was rendered, but it should have been suppressed.",
			resourceText.contains(resourceId) && resourceText.contains("was suppressed"));
	}

	private boolean resourceShouldBeSuppressed(List<String> loadedResourceIds, String resourceId, String container) {
		return loadedResourceIds.contains(resourceId) &&
			!(BridgeTestUtil.isContainerPluto(2, container) && resourceId.endsWith(".css"));
	}

	private void waitForAllResources(BrowserDriver browserDriver, String componentResourcesSizeXpath,
		String resourcesXpath) {

		WebElement componentResourcesSizeElement = browserDriver.findElementByXpath(componentResourcesSizeXpath);
		String componentResourcesSize = componentResourcesSizeElement.getText();
		browserDriver.waitForElementDisplayed(resourcesXpath + "[" + componentResourcesSize + "]");
	}
}
