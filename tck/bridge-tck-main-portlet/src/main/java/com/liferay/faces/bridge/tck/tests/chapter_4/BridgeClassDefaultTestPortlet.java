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
package com.liferay.faces.bridge.tck.tests.chapter_4;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.GenericFacesPortlet;

import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that a bridge implementation class name has been picked up from either: - A portlet context attribute,
 * javax.portlet.faces.BridgeClassName - the resource META-INF/services/javax.portlet.faces.Bridge (default) bridge
 * implementation jar.
 */
public class BridgeClassDefaultTestPortlet extends GenericFacesPortlet {

	public static String TEST_NAME = "bridgeClassDefaultTest";

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		String className = getBridgeClassName();
		boolean pass = (className != null) && !(className.length() <= 0);
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(TEST_NAME);

		if (pass) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail("Bridge class name is " + className);
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail("No bridge class name found.");
		}

		out.println(resultWriter.toString());
	}
}
