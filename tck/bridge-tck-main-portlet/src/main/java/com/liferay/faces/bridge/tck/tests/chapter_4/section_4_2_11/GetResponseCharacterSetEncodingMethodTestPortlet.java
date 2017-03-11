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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_11;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getResponseCharacterSetEncoding method works as stated in section 4.2.11. - The
 * GenericFacesPortlet implements getResponseCharacterSetEncoding by always returning null.
 */
public class GetResponseCharacterSetEncodingMethodTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";

	public String getResponseCharacterSetEncoding(PortletRequest request) {
		String returnEncoding = super.getResponseCharacterSetEncoding(request);

		if (returnEncoding == null) {
			getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
				"getResponseCharacterSetEncoding correctly returned null.");
		}
		else {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
				"getResponseCharacterSetEncoding returned: " + returnEncoding + " but it should have returned null.");
		}

		return returnEncoding;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		// Call the method thereby running the test
		getResponseCharacterSetEncoding(request);

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

		if (getPortletContext().getAttribute(TEST_FAIL_PREFIX + getPortletName()) == null) {
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
