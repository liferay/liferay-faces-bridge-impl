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
package com.liferay.faces.bridge.tck.tests.chapter_3;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * This portlet sets one attribute in PortletRequest and makes sure we can read that attribute back using
 * PortletRequest.getAttribute()
 */

// Test #3.1

public class BridgeVersionTestPortlet extends GenericPortlet {

	public static String TEST_NAME = "bridgeVersionTest";

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(TEST_NAME);

		// Get the version info from the Bridge class
		try {
			Class c = Class.forName("javax.portlet.faces.Bridge");

			String name = c.getPackage().getSpecificationTitle();
			String version = c.getPackage().getSpecificationVersion();

			if ((name == null) || !name.equals("Portlet 2.0 Bridge for JavaServer Faces 1.2")) {
				resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
				resultWriter.setDetail("Incorrect Specification Title: " + name +
					" should be 'Portlet 2.0 Bridge for JavaServer Faces 1.2'");
			}
			else if ((version == null) || !version.equals("1.0")) {
				resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
				resultWriter.setDetail("Incorrect Specification Version: " + version + " should be '2.0'");
			}
			else {
				resultWriter.setStatus(BridgeTCKResultWriter.PASS);
				resultWriter.setDetail("Correct Specification Title: " + name + " and correct specification version: " +
					version);
			}
		}
		catch (ClassNotFoundException e) {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail("javax.portlet.faces.Bridge class not found.");
		}

		out.println(resultWriter.toString());
	}
}
