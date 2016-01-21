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
import java.util.logging.Logger;

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
public class FACES224PortletTest extends TesterBase {

	private static final Logger logger = Logger.getLogger(FACES224PortletTest.class.getName());

	// portlet topper and menu elements
	private static final String formTagXpath = "//form[@method='post']";

	// <input id="A2535:httpGetButton" type="button" value="Click me to render view2.xhtml via HTTP GET">
	private static final String buttonXpath = "//input[contains(@value,'Click me to render view2.xhtml')]";

	// <div class="portlet-body" id="aui_3_4_0_1_500"> <div id="A2535" class="liferay-faces-bridge-body">This is
	// view2.xhtml <br>viewParam1='' (if the issue is fixed, the value should be equal to 'abc') <br>viewParam2='' (if
	// the issue is fixed, the value should be equal to 'xyz')</div> </div>
	private static final String view2DivXpath = "//div[@class='portlet-body']/div[1]";

	static final String url = baseUrl + webContext + "/faces-224";

	@FindBy(xpath = formTagXpath)
	private WebElement formTag;
	@FindBy(xpath = buttonXpath)
	private WebElement button;
	@FindBy(xpath = view2DivXpath)
	private WebElement view2Div;

	@Drone
	WebDriver browser;

	@Test
	@RunAsClient
	@InSequence(1000)
	public void FACES224PortletViewMode() throws Exception {

		if ("pluto".equals(portal)) {
			signIn(browser);
		}

		logger.log(Level.INFO, "browser.navigate().to(" + url + ")");
		browser.navigate().to(url);
		logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle());
		logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());
		// getPortletDisplayName();
		logger.log(Level.INFO, "displayName.getText() = " + displayName.getText());

		logger.log(Level.INFO, "button.isDisplayed() = " + button.isDisplayed());

		assertTrue("button should be displayed but it is not, nothing found with buttonXpath = " + buttonXpath,
			button.isDisplayed());

	}

	@Test
	@RunAsClient
	@InSequence(2000)
	public void View2() throws Exception {

		button.click();
		Thread.sleep(500);

		logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());

		logger.log(Level.INFO, "view2Div.isDisplayed() = " + view2Div.isDisplayed());
		logger.log(Level.INFO, "view2Div.getText() = " + view2Div.getText());

		logger.log(Level.INFO,
			"view2Div.getText().contains('viewParam1') = " + view2Div.getText().contains("viewParam1"));
		logger.log(Level.INFO,
			"view2Div.getText().contains('viewParam2') = " + view2Div.getText().contains("viewParam2"));
		Thread.sleep(500);

		assertTrue("view2Div.getText() should contain viewParam1='abc', but instead it contains '" +
			view2Div.getText() + "'", view2Div.getText().contains("viewParam1='abc'"));
		assertTrue("view2Div.getText() should contain viewParam2='xyz', but instead it contains '" +
			view2Div.getText() + "'", view2Div.getText().contains("viewParam2='xyz'"));

	}

}
//J+
