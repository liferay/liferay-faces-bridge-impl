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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_7;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getDefaultViewIdMap method works as stated in section 4.2.7. - The default view id
 * for a portlet mode is defined as a portlet initialization parameter named javax.portlet.faces.defaultViewId.[mode].
 * This test confirms that the method returns a map where the key is the string version of [mode] and the map value is
 * the portlet initialization parameter value.
 */
public class GetDefaultViewIdMapMethodTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_NAME = "getDefaultViewIdMapMethodTest";
	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";
	private static String DEFAULT_VIEW_ID_INIT_PARAM = "javax.portlet.faces.defaultViewId.";

	public Map getDefaultViewIdMap() {
		Map returnMap = super.getDefaultViewIdMap();
		getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(), Boolean.TRUE);

		Enumeration<String> names = getPortletConfig().getInitParameterNames();

		while (names.hasMoreElements()) {

			String name = names.nextElement();

			if (name.startsWith(DEFAULT_VIEW_ID_INIT_PARAM)) {
				String mode = name.substring(DEFAULT_VIEW_ID_INIT_PARAM.length());
				String viewId = (String) returnMap.get(mode);

				if (!getPortletConfig().getInitParameter(name).equals(viewId)) {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName() + "." + mode, Boolean.TRUE);
					getPortletContext().removeAttribute(TEST_PASS_PREFIX + getPortletName());
				}
			}
		}

		return returnMap;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		boolean pass = getPortletContext().getAttribute(TEST_PASS_PREFIX + getPortletName()) != null;

		StringBuilder failMsg = null;

		if (!pass) {
			failMsg = new StringBuilder();

			Enumeration<String> names = getPortletContext().getAttributeNames();

			while (names.hasMoreElements()) {
				String name = names.nextElement();
				out.println("--- " + name + " ---");

				int idx = name.indexOf(TEST_FAIL_PREFIX + getPortletName() + ".");

				if (name.startsWith(TEST_FAIL_PREFIX)) {
					String prefix = TEST_FAIL_PREFIX + getPortletName() + ".";
					failMsg.append("Missing mode: " + name.substring(prefix.length()) + ".");
				}
			}
		}

		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(TEST_NAME);

		if (pass) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail("getDefaultViewIdMap() method returns successfully.");
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(failMsg.toString());
		}

		out.println(resultWriter.toString());
	}
}
