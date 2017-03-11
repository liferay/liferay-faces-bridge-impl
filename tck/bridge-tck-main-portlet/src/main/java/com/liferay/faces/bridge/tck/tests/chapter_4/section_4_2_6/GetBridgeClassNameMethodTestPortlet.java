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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getBridgeClassName method works as stated in section 4.2.6. Compares return value
 * with the expected value.
 */
public class GetBridgeClassNameMethodTestPortlet extends GenericFacesTestSuitePortlet {
	public static String TEST_NAME = "getBridgeClassNameMethodTest";

	private static String TEST_ATTR_PREFIX = "test.";
	private static String TEST_RUN = "testRun";
	private static String TEST_FAIL = "testFailMsg";
	private static String TEST_PASS = "testPassMsg";
	private static String TEST_BRIDGE_SERVICE_CLASSPATH = "META-INF/services/javax.portlet.faces.Bridge";

	public String getBridgeClassName() {
		String bridgeClassName = super.getBridgeClassName();

		if (getPortletConfig().getPortletContext().getAttribute(TEST_ATTR_PREFIX + getPortletName() + TEST_RUN) ==
				null) {
			getPortletConfig().getPortletContext().setAttribute(TEST_ATTR_PREFIX + getPortletName() + TEST_RUN,
				Boolean.TRUE);

			// Get expected class name from PortletContext attribute
			String expectedClassName = getPortletConfig().getPortletContext().getInitParameter(
					"javax.portlet.faces.BridgeClassName");

			StringBuilder failMsg = new StringBuilder();

			if (expectedClassName == null) {
				expectedClassName = getFromServicesPath(getPortletConfig().getPortletContext(),
						TEST_BRIDGE_SERVICE_CLASSPATH);
			}

			StringBuilder msg = new StringBuilder();

			if (expectedClassName == null) {
				msg.append("Bridge class name not set.");
				getPortletConfig().getPortletContext().setAttribute(TEST_ATTR_PREFIX + TEST_FAIL, msg.toString());
			}
			else {
				msg.append("Expected bridge class name is ").append(expectedClassName).append(
					", value returned from getBridgeClassName() is ").append(bridgeClassName).append(".");

				if (expectedClassName.equals(bridgeClassName)) {
					getPortletConfig().getPortletContext().setAttribute(TEST_ATTR_PREFIX + TEST_PASS, msg.toString());
				}
				else {
					getPortletConfig().getPortletContext().setAttribute(TEST_ATTR_PREFIX + TEST_FAIL, msg.toString());
				}
			}
		}

		return bridgeClassName;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		String passMsg = (String) getPortletConfig().getPortletContext().getAttribute(TEST_ATTR_PREFIX + TEST_PASS);

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(TEST_NAME);

		if (passMsg != null) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(passMsg);
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail((String) getPortletConfig().getPortletContext().getAttribute(
					TEST_ATTR_PREFIX + TEST_FAIL));
		}

		out.println(resultWriter.toString());
	}

	private String getFromServicesPath(PortletContext context, String resourceName) {

		// Check for a services definition
		String result = null;
		BufferedReader reader = null;
		InputStream stream = null;

		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();

			if (cl == null) {
				return null;
			}

			stream = cl.getResourceAsStream(resourceName);

			if (stream != null) {

				// Deal with systems whose native encoding is possibly
				// different from the way that the services entry was created
				try {
					reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
				}
				catch (UnsupportedEncodingException e) {
					reader = new BufferedReader(new InputStreamReader(stream));
				}

				result = reader.readLine();

				if (result != null) {
					result = result.trim();
				}

				reader.close();
				reader = null;
				stream = null;
			}
		}
		catch (IOException e) {
		}
		catch (SecurityException e) {
		}
		finally {

			if (reader != null) {

				try {
					reader.close();
					stream = null;
				}
				catch (Throwable t) {
					;
				}

				reader = null;
			}

			if (stream != null) {

				try {
					stream.close();
				}
				catch (Throwable t) {
					;
				}

				stream = null;
			}
		}

		return result;
	}
}
