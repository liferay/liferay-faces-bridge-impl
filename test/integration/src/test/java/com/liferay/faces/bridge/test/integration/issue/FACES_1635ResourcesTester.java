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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.openqa.selenium.WebElement;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1635ResourcesTester extends IntegrationTesterBase {

	@Test
	public void runFACES_1635ResourcesTest() {

		String container = TestUtil.getContainer();
		Assume.assumeTrue("The FACES-1635 test is only valid on Liferay Portal and Pluto 3.0+.",
			container.startsWith("liferay") || BridgeTestUtil.isContainerPluto(3, container));

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-1635") + "?p_p_parallel=0");
		browserDriver.waitForElementDisplayed(
			"//div[contains(@id,'jsf')][contains(@id,'applicant')][contains(@class,'liferay-faces-bridge-body')]//img[contains(@src, 'liferay-logo.png')]");
		browserDriver.waitForElementDisplayed(
			"//div[contains(@id,'prime')][contains(@id,'applicant')][contains(@class,'liferay-faces-bridge-body')]//img[contains(@src, 'liferay-logo.png')]");

		// Test that the head does not contain duplicate scripts.
		Set<String> resourceIds = new HashSet<String>();
		List<WebElement> headScripts = browserDriver.findElementsByXpath("//head//script");

		for (WebElement headScript : headScripts) {

			String scriptSrc = headScript.getAttribute("src");

			if (isExternalJavaScriptResource(headScript, scriptSrc)) {

				String resourceId = getResourceId(scriptSrc, container);

				if (resourceId != null) {
					Assert.assertTrue("Head contains duplicate script with resourceId=\"" + resourceId +
						"\" and src=\"" + scriptSrc + "\".", resourceIds.add(resourceId));
				}
			}
		}

		// Test that the head does not contain duplicate stylesheets.
		List<WebElement> headLinks = browserDriver.findElementsByXpath("//head//link");

		for (WebElement headLink : headLinks) {

			String linkHref = headLink.getAttribute("href");

			if (isCSSLink(headLink, linkHref)) {

				String resourceId = getResourceId(linkHref, container);

				if (resourceId != null) {
					Assert.assertTrue("Head contains duplicate stylesheet with resourceId=\"" + resourceId +
						"\" and href=\"" + linkHref + "\".", resourceIds.add(resourceId));
				}
			}
		}

		Assert.assertTrue("No head resources found.", !resourceIds.isEmpty());

		// Test that the entire document does not contain duplicate scripts.
		List<WebElement> bodyScripts = browserDriver.findElementsByXpath("//body//script");

		for (WebElement bodyScript : bodyScripts) {
			String scriptSrc = bodyScript.getAttribute("src");

			if (isExternalJavaScriptResource(bodyScript, scriptSrc)) {

				String resourceId = getResourceId(scriptSrc, container);

				if (resourceId != null) {
					Assert.assertTrue("Body contains duplicate script with resourceId=\"" + resourceId +
						"\" and src=\"" + scriptSrc + "\".", resourceIds.add(resourceId));
				}
			}
		}

		// Test that the entire document does not contain duplicate stylesheets.
		List<WebElement> bodyLinks = browserDriver.findElementsByXpath("//body//link");

		for (WebElement bodyLink : bodyLinks) {

			String linkHref = bodyLink.getAttribute("href");

			if (isCSSLink(bodyLink, linkHref)) {

				String resourceId = getResourceId(linkHref, container);

				if (resourceId != null) {
					Assert.assertTrue("Body contains duplicate stylesheet with resourceId=\"" + resourceId +
						"\" and href=\"" + linkHref + "\".", resourceIds.add(resourceId));
				}
			}
		}

		Assert.assertTrue("No resources found.", !resourceIds.isEmpty());
	}

	private String getResourceId(String resourceURL, String container) {

		String resourceId = "";
		String splitString = "[?]";

		if (BridgeTestUtil.isContainerPluto(container)) {
			splitString = "[/][_][_]rs[0-9][/].*?[_][_]rv[0-9][;]";
		}

		String[] baseURLAndQueryArray = resourceURL.split(splitString);

		if ((baseURLAndQueryArray.length > 1) && (baseURLAndQueryArray[1] != null) &&
				!"".equals(baseURLAndQueryArray[1])) {

			String queryString = baseURLAndQueryArray[1];
			splitString = "[&]";

			if (BridgeTestUtil.isContainerPluto(container)) {
				splitString = "[/][_][_]rv[0-9][;]";
			}

			String[] parameters = queryString.split(splitString);

			splitString = "[=]";

			if (BridgeTestUtil.isContainerPluto(container)) {
				splitString = "[:]";
			}

			for (String parameter : parameters) {

				if (parameter != null) {

					String[] parameterArray = parameter.split(splitString);
					String name = null;
					String value = null;

					if (parameterArray.length > 1) {

						name = parameterArray[0];
						value = parameterArray[1];
					}

					if ((name != null) && (value != null)) {

						if (name.endsWith("ln")) {
							resourceId = value + ":" + resourceId;
						}
						else if (name.endsWith("javax.faces.resource")) {
							resourceId += value;
						}
					}
				}
			}
		}

		if (resourceId.equals("")) {
			resourceId = null;
		}

		return resourceId;
	}

	private boolean isCSSLink(WebElement webElement, String linkHref) {

		String linkRel = webElement.getAttribute("rel");

		return ((linkHref != null) && !linkHref.equals("")) && "stylesheet".equals(linkRel);
	}

	private boolean isExternalJavaScriptResource(WebElement webElement, String scriptSrc) {

		String scriptType = webElement.getAttribute("type");

		return ((scriptSrc != null) && !scriptSrc.equals("")) &&
			((scriptType == null) || scriptType.equals("text/javascript"));
	}
}
