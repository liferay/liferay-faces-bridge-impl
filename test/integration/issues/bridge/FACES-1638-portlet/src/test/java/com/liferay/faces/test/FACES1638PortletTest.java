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
package com.liferay.faces.test;
//J-

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.liferay.faces.test.util.TesterBase;


/**
 * @author	Liferay Faces Team
 */
@RunWith(Arquillian.class)
public class FACES1638PortletTest extends TesterBase {

	private static final String formTagXpath = "//form[@method='post']";

	private static final String firstLinkWithItemIdParamXpath = "//a[contains(@href,'1638') and contains(@href,'_itemId=') and text()='First-Item']";
	private static final String secondLinkWithItemIdParamXpath = "//a[contains(@href,'1638') and contains(@href,'_itemId=') and text()='Second-Item']";
	private static final String thirdLinkWithItemIdParamXpath = "//a[contains(@href,'1638') and contains(@href,'_itemId=') and text()='Third-Item']";

	private static final String firstLinkWithItemNameParamXpath = "//a[contains(@href,'1638') and contains(@href,'_First-Item=') and text()='First-Item']";
	private static final String secondLinkWithItemNameParamXpath = "//a[contains(@href,'1638') and contains(@href,'_Second-Item=') and text()='Second-Item']";
	private static final String thirdLinkWithItemNameParamXpath = "//a[contains(@href,'1638') and contains(@href,'_Third-Item=') and text()='Third-Item']";

	// http://localhost:8080/web/bridge-issues/faces-1638?p_p_id=1_WAR_FACES1638portlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_1_WAR_FACES1638portlet__facesViewIdRender=%2Fviews%2Fview1.xhtml&_1_WAR_FACES1638portlet_itemId=3
	// http://localhost:8080/web/bridge-issues/faces-1638?p_p_id=1_WAR_FACES1638portlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_1_WAR_FACES1638portlet__facesViewIdRender=%2Fviews%2Fview1.xhtml&_1_WAR_FACES1638portlet_First-Item=1

	static final String url = baseUrl + webContext + "/faces-1638";
	static final String ITEM_ID = "itemId";

	@FindBy(xpath = formTagXpath)
	private WebElement formTag;

	@FindBy(xpath = firstLinkWithItemIdParamXpath)
	private WebElement firstLinkWithItemIdParam;
	@FindBy(xpath = secondLinkWithItemIdParamXpath)
	private WebElement secondLinkWithItemIdParam;
	@FindBy(xpath = thirdLinkWithItemIdParamXpath)
	private WebElement thirdLinkWithItemIdParam;

	@FindBy(xpath = firstLinkWithItemNameParamXpath)
	private WebElement firstLinkWithItemNameParam;
	@FindBy(xpath = secondLinkWithItemNameParamXpath)
	private WebElement secondLinkWithItemNameParam;
	@FindBy(xpath = thirdLinkWithItemNameParamXpath)
	private WebElement thirdLinkWithItemNameParam;

	@Drone
	WebDriver browser;

	@Test
	@RunAsClient
	@InSequence(1000)
	public void FACES1638Test() throws Exception {

		if ("pluto".equals(portal)) {
			signIn(browser);
		}

		logger.log(Level.INFO, "browser.navigate().to(" + url + ")");
		browser.navigate().to(url);
		logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle());
		logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());
		logger.log(Level.INFO, "formTag.getText() = " + formTag.getText());

		assertTrue("formTag.isDisplayed()", formTag.isDisplayed());

		testLink(firstLinkWithItemIdParam, ITEM_ID, "1", firstLinkWithItemIdParamXpath);
		testLink(secondLinkWithItemIdParam, ITEM_ID, "2", secondLinkWithItemIdParamXpath);
		testLink(thirdLinkWithItemIdParam, ITEM_ID, "3", thirdLinkWithItemIdParamXpath);

		testLink(firstLinkWithItemNameParam, "First-Item", "1", firstLinkWithItemNameParamXpath);
		testLink(secondLinkWithItemNameParam, "Second-Item", "2", secondLinkWithItemNameParamXpath);
		testLink(thirdLinkWithItemNameParam, "Third-Item", "3", thirdLinkWithItemNameParamXpath);
	}

	private int countSubstringOccurrences(String string, String substring) {
		return (string.length() - string.replace(substring, "").length()) / substring.length();
	}

	private void testLink(WebElement link, String parameterName, String expectedParameterValue, String linkXpath) {

		String href = link.getAttribute("href");
		// Add one to remove the equals (=) sign.
		int parameterValueIndex = href.indexOf(parameterName) + parameterName.length() + 1;
		String parameterValue = href.substring(parameterValueIndex, parameterValueIndex + 1);
		logger.log(Level.INFO, "{0} = {1}", new Object[] { parameterName, parameterValue });
		assertTrue(parameterName + " should equal " + expectedParameterValue + ", for\n" + linkXpath +
			"\nbut it does not.", parameterValue.equals(expectedParameterValue));
		int count = countSubstringOccurrences(href, parameterName);
		assertTrue(parameterName + " occured " + count + " times in the href attribute of\n" + linkXpath + ".\n" +
			parameterName + " should occur exactly once.", count == 1);
	}
}
//J+
