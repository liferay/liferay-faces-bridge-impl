/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
public class FACES1478PortletTest extends TesterBase {

	private static final String formTagXpath = "//form[@method='post']";

	private static final String secondLinkXpath = "//form[@method='post']/a[2]";

	static final String url = baseUrl + webContext + "/faces-1478";

	@FindBy(xpath = formTagXpath)
	private WebElement formTag;
	@FindBy(xpath = secondLinkXpath)
	private WebElement secondLink;

	@Drone
	WebDriver browser;

	@Test
	@RunAsClient
	@InSequence(1000)
	public void FACES1478PortletParameters() throws Exception {

		if ("pluto".equals(portal)) {
			signIn(browser);
		}

		logger.log(Level.INFO, "browser.navigate().to(" + url + ")");
		browser.navigate().to(url);
		logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle());
		logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());
		logger.log(Level.INFO, "formTag.getText() = " + formTag.getText());
		logger.log(Level.INFO, "secondLink.getAttribute('href') = " + secondLink.getAttribute("href"));

		assertTrue("formTag.isDisplayed()", formTag.isDisplayed());
		assertTrue("secondLink.isDisplayed()", secondLink.isDisplayed());

		int firstParameter = secondLink.getAttribute("href").indexOf("testParam=foo");
		logger.log(Level.INFO, "The firstParameter was found at position = " + firstParameter);

		int secondParameter = secondLink.getAttribute("href").indexOf("testParam=bar");
		logger.log(Level.INFO, "The secondParameter was found at position = " + secondParameter);

		assertTrue("firstParameter is in the url", firstParameter > -1);
		assertTrue("secondParameter is in the url", secondParameter > -1);
		assertTrue("firstParameter occurs before the secondParameter", firstParameter < secondParameter);

	}

}
//J+
