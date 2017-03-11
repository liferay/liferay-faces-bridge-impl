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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_9;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#isPreserveActionParameters method works as stated in section 4.2.9. - Returns the
 * boolean value corresponding to the String value represented in the portlet initialization parameter,
 * javax.portlet.faces.preserveActionParams. If this initialization parameter doesn't exist, false is returned.
 */

public class IsPreserveActionParametersMethodTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";
	private static String IS_PRESERVE_ACTION_PARAMS_INIT_PARAM = "javax.portlet.faces.preserveActionParams";

	public boolean isPreserveActionParameters() {
		boolean returnBoolean = super.isPreserveActionParameters();

		String initParamValue = getPortletConfig().getInitParameter(IS_PRESERVE_ACTION_PARAMS_INIT_PARAM);

		StringBuilder msg = new StringBuilder();
		boolean expectedResult = false;

		if (initParamValue == null) {
			msg.append("isPreserveActionParams is not set, method returned from isPreserveActionParams() is ");
			expectedResult = false;
		}
		else {
			msg.append("isPreserveActionParams is set to ").append(initParamValue).append(
				", method returned from isPreserveActionParams() is ");
			expectedResult = Boolean.valueOf(initParamValue);
		}

		msg.append(Boolean.toString(returnBoolean)).append(".");

		if (expectedResult == returnBoolean) {
			getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(), msg.toString());
		}
		else {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(), msg.toString());
		}

		return returnBoolean;
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
