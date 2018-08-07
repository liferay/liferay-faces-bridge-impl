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
package com.liferay.faces.bridge.test.integration.issue.primefaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.junit.Assert;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.util.ClosableUtil;


/**
 * @author  Kyle Stiemann
 */
public final class ExporterComponentsTestUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ExporterComponentsTestUtil.class);

	// Private Constants
	private static final String EXPECTED_USERS_CSV_FILE_NAME = "FACES-1513-2185-users.csv";
	private static final String EXPECTED_USERS_CSV;

	static {

		InputStream inputStream = null;

		try {

			URL expectedUsersCSV_URL = FACES_1513_2185PortletTester.class.getResource("/" +
					EXPECTED_USERS_CSV_FILE_NAME);
			inputStream = expectedUsersCSV_URL.openStream();
			EXPECTED_USERS_CSV = getContents(inputStream);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			ClosableUtil.close(inputStream);
		}
	}

	private ExporterComponentsTestUtil() {
		throw new AssertionError();
	}

	/* package-private */ static void runDownloadUsersCSVFileTest(BrowserDriver browserDriver, String buttonId) throws IOException {

		// TECHNICAL NOTE: Since browsers handle file downloads differently, download files with a plain
		// HttpURLConnection in order to avoid handling each browser case.
		String buttonXpath = "//form/button[contains(@id,':" + buttonId + "')]";
		browserDriver.waitForElementEnabled(buttonXpath);

		String formXpath = buttonXpath + "/..";
		WebElement formElement = browserDriver.findElementByXpath(formXpath);
		String formActionURLString = formElement.getAttribute("action");
		URL formActionURL = new URL(formActionURLString);
		HttpURLConnection httpURLConnection = (HttpURLConnection) formActionURL.openConnection();
		InputStream inputStream = null;

		try {

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.addRequestProperty("Accept", "text/csv");

			// TECHNICAL NOTE: Add all cookies from the browser to the HttpURLConnection so that it can imitate the
			// session of the browser and access the test portlet and files.
			Set<Cookie> cookies = browserDriver.getBrowserCookies();
			String cookieString = "";

			for (Cookie cookie : cookies) {
				cookieString += cookie.getName() + "=" + cookie.getValue() + ";";
			}

			httpURLConnection.addRequestProperty("Cookie", cookieString);

			// TECHNICAL NOTE: Add all input (and button) name, value pairs to the POST parameters of the
			// HttpURLConnection so that it can imitate the session of the browser and download the files.
			List<WebElement> namedElements = new ArrayList<WebElement>(browserDriver.findElementsByXpath(
						formXpath + "/input"));
			WebElement button = browserDriver.findElementByXpath(buttonXpath);
			namedElements.add(button);

			StringBuilder requestBuilder = new StringBuilder();
			boolean first = true;

			for (WebElement namedElement : namedElements) {

				if (!first) {
					requestBuilder.append("&");
				}

				String name = namedElement.getAttribute("name");
				requestBuilder.append(URLEncoder.encode(name, "UTF-8")).append("=");

				String value = namedElement.getAttribute("value");

				if (value != null) {
					requestBuilder.append(URLEncoder.encode(value, "UTF-8"));
				}

				first = false;
			}

			String requestString = requestBuilder.toString();
			httpURLConnection.addRequestProperty("Content-Length", String.valueOf(requestString.length()));

			String enctype = formElement.getAttribute("enctype");
			httpURLConnection.addRequestProperty("Content-Type", enctype);

			Writer writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
			writer.write(requestString);
			writer.flush();

			Assert.assertEquals(HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());

			String contentDisposition = httpURLConnection.getHeaderField("Content-Disposition");

			if (contentDisposition == null) {
				contentDisposition = httpURLConnection.getHeaderField("Content-disposition");
			}

			if (contentDisposition == null) {
				contentDisposition = httpURLConnection.getHeaderField("content-disposition");
			}

			String usersCSVFileName;

			if ((contentDisposition == null) && TestUtil.getContainer().startsWith("pluto")) {
				usersCSVFileName = buttonId + "Users.csv";
			}
			else {
				usersCSVFileName = contentDisposition.replace("attachment;filename=", "");
			}

			inputStream = httpURLConnection.getInputStream();

			String downloadedUsersCSV = getContents(inputStream);
			httpURLConnection.disconnect();

			logger.info("Expected " + EXPECTED_USERS_CSV_FILE_NAME + " text:\n\n{}\nDownloaded " + usersCSVFileName +
				" text:\n\n{}", EXPECTED_USERS_CSV, downloadedUsersCSV);
			Assert.assertEquals("The downloaded " + usersCSVFileName + " file's text does not match the expected " +
				EXPECTED_USERS_CSV_FILE_NAME + " file's text.", EXPECTED_USERS_CSV, downloadedUsersCSV);
		}
		finally {

			ClosableUtil.close(inputStream);

			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	private static String getContents(InputStream inputStream) throws FileNotFoundException {

		Scanner scanner = null;

		try {

			scanner = new Scanner(inputStream, "UTF-8");
			scanner.useDelimiter("\\A");

			String string = "";

			if (scanner.hasNext()) {
				string = scanner.next();
			}

			return string;
		}
		finally {
			ClosableUtil.close(scanner);
		}
	}
}
