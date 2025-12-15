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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import jakarta.faces.context.FacesContextFactory;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * This portlet sets one attribute in PortletRequest and makes sure we can read that attribute back using
 * PortletRequest.getAttribute()
 */

/* Test #6.1 */
public class FacesContextFactoryServiceProviderTest extends GenericPortlet {

	public static String TEST_NAME = "facesContextFactoryServiceProviderTest";

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		response.setContentType("text/html");

		PrintWriter responsePrintWriter = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(TEST_NAME);

		String className = getFromServicesPath(this.getPortletContext(),
				"META-INF/services/jakarta.faces.context.FacesContextFactory");

		if (className == null) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail("META-INF/services/jakarta.faces.context.FacesContextFactory not found.");

			return;
		}

		// See if we can load the class
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class c = (Class<? extends FacesContextFactory>) loader.loadClass(className);
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail("Located and loaded the Bridge's FacesContextFactory class: " + className);
		}
		catch (ClassNotFoundException cnfe) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail("Located but unable to load the Bridge's FacesContextFactory class: " + className);
		}

		responsePrintWriter.println(resultWriter.toString());
	}

	private String getFromServicesPath(PortletContext context, String resourceName) {

		// Check for a services definition
		String result = null;
		BufferedReader reader = null;
		InputStream inputStream = null;

		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();

			if (cl == null) {
				return null;
			}

			inputStream = cl.getResourceAsStream(resourceName);

			if (inputStream != null) {

				// Deal with systems whose native encoding is possibly
				// different from the way that the services entry was created
				try {
					reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				}
				catch (UnsupportedEncodingException e) {
					reader = new BufferedReader(new InputStreamReader(inputStream));
				}

				result = reader.readLine();

				if (result != null) {
					result = result.trim();
				}

				reader.close();
				reader = null;
				inputStream = null;
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
					inputStream = null;
				}
				catch (Throwable t) {
					;
				}

				reader = null;
			}

			if (inputStream != null) {

				try {
					inputStream.close();
				}
				catch (Throwable t) {
					;
				}

				inputStream = null;
			}
		}

		return result;
	}

}
