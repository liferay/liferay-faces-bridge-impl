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

import java.util.Set;
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
public class JsfExportPdfPortletTest extends TesterBase {

	private static final String brianExportXpath =
		"//a[contains(text(),'Export')]/../following-sibling::td[1][contains(text(),'Green')]/preceding-sibling::td[1]/a";
	private static final String LizExportXpath =
			"//a[contains(text(),'Export')]/../following-sibling::td[1][contains(text(),'Kessler')]/preceding-sibling::td[1]/a";
	private static final String richExportXpath =
			"//a[contains(text(),'Export')]/../following-sibling::td[1][contains(text(),'Shearer')]/preceding-sibling::td[1]/a";

	static final String url = baseUrl + webContext + "/jsf-pdf";

	@FindBy(xpath = brianExportXpath)
	private WebElement brianExport;
	@FindBy(xpath = LizExportXpath)
	private WebElement LizExport;
	@FindBy(xpath = richExportXpath)
	private WebElement richExport;

	@Drone
	WebDriver browser;

	@Test
	@RunAsClient
	@InSequence(1000)
	public void renderViewMode() throws Exception {

		signIn(browser);
		logger.log(Level.INFO, "browser.navigate().to(" + url + ")");
		browser.navigate().to(url);
		logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle());
		logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());

		assertTrue("An 'Export' link for Brian should be visible, but it is not there.", isThere(browser, brianExportXpath));

		if (isThere(browser, brianExportXpath)) {
			logger.log(Level.INFO, "isThere(brianExportXpath) = " + isThere(browser, brianExportXpath));
			richExport.click();
			long sleepDuration = 500;
			Thread.sleep(sleepDuration);

			Set<String> handles = browser.getWindowHandles();
			for (String window : handles) {
				browser.switchTo().window(window);
				logger.log(Level.INFO, "window = " + window);
				logger.log(Level.INFO, "browser.getTitle() = " + browser.getTitle());
				logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());
				logger.log(Level.INFO, "browser.getPageSource().contains(Shearer) = " + browser.getPageSource().contains("Shearer"));
				logger.log(Level.INFO, "browser.getPageSource() = " + browser.getPageSource());
				logger.log(Level.INFO, " -------------------------------------------------- ");
				if (browser.getPageSource().contains("Shearer")) {
					break;
				}
			}

			boolean done = false;
			while (!done) {
				if (browser.getPageSource().contains("Shearer")) {
					logger.log(Level.INFO, "browser.getCurrentUrl() = " + browser.getCurrentUrl());
					logger.log(Level.INFO, "browser.getPageSource().contains(Shearer) after " + sleepDuration + " milliseconds");
					done = true;
				}
				else {
					if (sleepDuration < 3000) {
						sleepDuration += 50;
						Thread.sleep(50);
					}
					else {
						done = true;
					}
				}
			}

			logger.log(Level.INFO, "browser.getPageSource().contains('Shearer') = " + browser.getPageSource().contains("Shearer"));
			logger.log(Level.INFO, " -------------------------------------------------- ");

			assertTrue("The pdf document should contain 'Shearer', but instead it contains: " + browser.getPageSource(),
				browser.getPageSource().contains("Shearer")
			);

		}

	}

}
//J+
