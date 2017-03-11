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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_10;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getResponseContentType method works as stated in section 4.2.10. - If not overridden,
 * the GenericFacesPortlet returns the value of the portlet initialization parameter
 * javax.portlet.faces.defaultContentType, or, if this parameter doesn't exist, the portlet container's indication of
 * the preferred content type for this request.
 */

public class GetResponseContentTypeMethodTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";

	public String getResponseContentType(PortletRequest request) {
		String returnType = super.getResponseContentType(request);

		String expectedContentType = request.getResponseContentType();

		if (expectedContentType.toUpperCase().startsWith(returnType.toUpperCase())) {
			getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
				"getResponseContentType correctly returns a value equivalent to the preferred content type for this response.");
		}
		else {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
				"getResponseContentType incorrectly returned a different value than the preferred content type for this response. It returned: " +
				returnType + "but the preferred CT is: " + expectedContentType);
		}

		return returnType;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		// Call the method thereby running the test
		getResponseContentType(request);

		// Actually set the content type manually, the test might have used
		// an invalid value.
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
