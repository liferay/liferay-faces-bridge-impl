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
package com.liferay.faces.bridge.test.integration.issue.primefaces;

import java.io.IOException;

import org.junit.Test;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.bridge.test.integration.issue.SimpleFACESPortletTester;
import com.liferay.faces.test.selenium.browser.BrowserDriver;


/**
 * @author  Kyle Stiemann
 */
public class FACES_3327PortletTester extends SimpleFACESPortletTester {

	@Test
	public void runFACES_3327PortletTest() throws IOException {

		// Navigate the browser to the portal page that contains the FACES-3327 portlet.
		BrowserDriver browserDriver = getBrowserDriver();
		String faces3327PageURL = BridgeTestUtil.getIssuePageURL("faces-3327");
		browserDriver.navigateWindowTo(faces3327PageURL);

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
		// Click the *result.xhtml* button.
		// Verify that the test result status shows "SUCCESS" and not "FAILURE".
		//J+
		runSimpleFACESPortletTest(browserDriver, "faces-3327");

		// Navigate the browser to the portal page that contains the FACES-3327 portlet.
		browserDriver.navigateWindowTo(faces3327PageURL);

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
		// Click the *result.xhtml* button.
		// Verify that the test result status shows "SUCCESS" and not "FAILURE".
		//J+
		runSimpleFACESPortletTest(browserDriver, "faces-3327");

		// Navigate the browser to the portal page that contains the FACES-3327 portlet.
		browserDriver.navigateWindowTo(faces3327PageURL);

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

		//J-
		// Click the *result.xhtml* button.
		// Verify that the test result status shows "SUCCESS" and not "FAILURE".
		//J+
		runSimpleFACESPortletTest(browserDriver, "faces-3327");
	}
}
