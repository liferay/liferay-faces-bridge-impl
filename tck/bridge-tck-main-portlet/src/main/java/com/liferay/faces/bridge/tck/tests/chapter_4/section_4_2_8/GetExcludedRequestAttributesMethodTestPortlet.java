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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getExcludedRequestAttributes method works as stated in section 4.2.8. - Excluded
 * request attributes are defined in a portlet initialisation parameter with name
 * javax.portlet.faces.excludedRequestAttributes and value one or more comma separated strings. This test confirms that
 * the method returns a list of string values based on parsing the initialisation parameter value.
 */
public class GetExcludedRequestAttributesMethodTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";
	private static String EXCLUDED_REQUEST_ATTRIBUTES_INIT_PARAM = "javax.portlet.faces.excludedRequestAttributes";

	public List<String> getExcludedRequestAttributes() {
		List<String> returnList = super.getExcludedRequestAttributes();
		getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(), Boolean.TRUE);

		String excludedAttrValue = getPortletConfig().getInitParameter(EXCLUDED_REQUEST_ATTRIBUTES_INIT_PARAM);

		if (excludedAttrValue == null) {

			if (returnList == null) {
				getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
					"getExcludedRequestAttributes() correctly returned null.");

				return returnList;
			}
			else {
				StringBuilder failMsg = new StringBuilder();
				failMsg.append(
					"getExcludedRequestAttributes() incorrectly returned a non-null value.  It returned a list containing the attribute names: ");

				boolean listStarted = false;

				for (String name : returnList) {

					if (listStarted) {
						failMsg.append(", ");
					}
					else {
						listStarted = true;
					}

					failMsg.append(name);
				}

				getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(), failMsg.toString());

				return returnList;
			}
		}

		StringBuilder passMsg = new StringBuilder();
		StringBuilder failMsg = new StringBuilder();

		String[] excludedAttrs = excludedAttrValue.split(",");

		for (String expectedAttribute : excludedAttrs) {
			expectedAttribute = expectedAttribute.trim();

			boolean found = false;

			for (String methodAttribute : returnList) {

				if (expectedAttribute.equals(methodAttribute)) {
					found = true;

					if (passMsg.length() == 0) {
						passMsg.append(
							"getExcludedRequestAttributes() correctly returned the following excluded request attribute names: ");
					}
					else {
						passMsg.append(", ");
					}

					passMsg.append(expectedAttribute);
				}
			}

			if (!found) {

				// Expected list value not found
				if (failMsg.length() == 0) {
					failMsg.append(
						"getExcludedRequestAttributes() did not correctly return the following excluded request attribute names: ");
				}
				else {
					failMsg.append(", ");
				}

				failMsg.append(expectedAttribute);
			}
		}

		if (failMsg.length() != 0) {
			failMsg.append(".");
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(), failMsg.toString());
		}

		if (passMsg.length() != 0) {
			passMsg.append(".");
			getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(), passMsg.toString());
		}

		return returnList;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		boolean pass = getPortletContext().getAttribute(TEST_FAIL_PREFIX + getPortletName()) == null;

		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

		if (pass) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail((String) getPortletContext().getAttribute(TEST_PASS_PREFIX + getPortletName()));
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail((String) getPortletContext().getAttribute(TEST_FAIL_PREFIX + getPortletName()));
		}

		out.println(resultWriter.toString());
	}
}
