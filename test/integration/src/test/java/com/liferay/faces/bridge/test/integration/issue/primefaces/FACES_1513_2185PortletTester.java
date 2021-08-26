/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.test.integration.issue.primefaces;

import java.io.IOException;

import org.junit.Test;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1513_2185PortletTester extends BrowserDriverManagingTesterBase {

	@Test
	public void runFACES_1513_2185PortletTest() throws IOException {

		// Navigate the browser to the portal page that contains the FACES-1513-2185 portlet.
		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.navigateWindowTo(BridgeTestUtil.getIssuePageURL("faces-1513-2185"));

		//J-
		// Click the *Export via p:dataExporter* button.
		// Verify that a CSV file was downloaded and the downloaded CSV file contains the following text exactly:
		/*
"First Name","Last Name"
"Neil","Griffin"
"Vernon","Singleton"
"Kyle","Stiemann"

		*/
		//J+
		ExporterComponentsTestUtil.runDownloadUsersCSVFileTest(browserDriver, "p_dataExporter");

		//J-
		// Click the *Export via pe:exporter* button.
		// Verify that a CSV file was downloaded and the downloaded CSV file contains the following text exactly:
		/*
"First Name","Last Name"
"Neil","Griffin"
"Vernon","Singleton"
"Kyle","Stiemann"

		*/
		//J+
		ExporterComponentsTestUtil.runDownloadUsersCSVFileTest(browserDriver, "pe_exporter");

		//J-
		// Click the *Export via p:fileDownload* button.
		// Verify that a CSV file was downloaded and the downloaded CSV file contains the following text exactly:
		/*
"First Name","Last Name"
"Neil","Griffin"
"Vernon","Singleton"
"Kyle","Stiemann"

		*/
		//J+
		ExporterComponentsTestUtil.runDownloadUsersCSVFileTest(browserDriver, "p_fileDownload");
	}
}
