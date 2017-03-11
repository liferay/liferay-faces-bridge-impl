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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * @author  Michael Freedman
 */
public class NonFacesViewTestPortlet extends GenericFacesTestSuitePortlet {

	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {
		String invokeTest = (String) renderRequest.getParameter("invokeTest");

		if (invokeTest == null) {
			super.doDispatch(renderRequest, renderResponse);

			return;
		}
		else {
			renderResponse.setContentType("text/html");

			PrintWriter out = renderResponse.getWriter();

			out.println(runTest(renderRequest, renderResponse));
		}

	}

	public void init(PortletConfig config) throws PortletException {
		super.init(config);

	}

	public String runTest(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		String target = request.getParameter("_jsfBridgeNonFacesView");

		if (target == null) {
			return outputTestResult(false, "_jsfBridgeNonFacesView parameter not (encoded in) nonFaces request.");
		}
		else if (!target.equalsIgnoreCase(request.getContextPath() + "/nonFacesViewTestPortlet.ptlt")) {
			return outputTestResult(false,
					"_jsfBridgeNonFacesView parameter (encoded in) nonFaces request but with unexpected value. Expected: " +
					request.getContextPath() + "/nonFacesViewTestPortlet.ptlt" + "but received: " + target);
		}

		String testName = getTestName();
		String paramResult = null;

		if (testName.equals("encodeActionURLNonJSFViewRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewResourceTest")) {
			return outputTestResult(true,
					"Correctly encoded nonFaces URL as a renderURL using the _jsfBridgeNonFacesView parameter.");
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithParamRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithParamResourceTest")) {
			paramResult = checkParameter(request, response);

			if (paramResult.equals("true")) {
				return outputTestResult(true,
						"Correctly encoded nonFaces URL as a renderURL using the _jsfBridgeNonFacesView parameter along with the additional parameter.");
			}
			else {
				return paramResult;
			}
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithModeRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithModeResourceTest")) {

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = request.getPortletMode();

			if ((mode == null) || !mode.toString().equalsIgnoreCase("edit")) {
				return outputTestResult(false,
						"encodeActionURL incorrectly encoded the new portlet mode.  The resulting request wasn't in the expected 'edit' mode.");
			}

			paramResult = checkParameter(request, response);

			if (paramResult.equals("true")) {
				return outputTestResult(true,
						"Correctly encoded nonFaces URL as a renderURL using the _jsfBridgeNonFacesView parameter including a change in mode and with the additional parameter.");
			}
			else {
				return paramResult;
			}
			// otherwise error output in checkParameter
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithInvalidModeRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithInvalidModeResourceTest")) {

			// Parameter/Mode encoded in the faces-config.xml target
			PortletMode mode = request.getPortletMode();

			if ((mode == null) || !mode.toString().equalsIgnoreCase("view")) {
				return outputTestResult(false,
						"encodeActionURL incorrectly encoded an invalid portlet mode.  The resulting request should have ignored the invalid mode and remained in 'view' mode.");
			}

			paramResult = checkParameter(request, response);

			if (paramResult.equals("true")) {
				return outputTestResult(true,
						"Correctly encoded nonFaces URL as a renderURL using the _jsfBridgeNonFacesView parameter excluding the invalid mode and including the additional parameter.");
			}
			else {
				return paramResult;
			}
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithWindowStateRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithWindowStateResourceTest")) {

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = request.getWindowState();

			if ((ws == null) || !ws.toString().equalsIgnoreCase("maximized")) {
				return outputTestResult(false,
						"encodeActionURL incorrectly encoded the new portlet window state.  The resulting request wasn't in the expected 'maximized' mode.");
			}

			paramResult = checkParameter(request, response);

			if (paramResult.equals("true")) {
				return outputTestResult(true,
						"Correctly encoded nonFaces URL as a renderURL using the _jsfBridgeNonFacesView parameter including a change in window state and with the additional parameter.");
			}
			else {
				return paramResult;
			}

		}
		else if (testName.equals("encodeActionURLNonJSFViewWithInvalidWindowStateRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithInvalidWindowStateResourceTest")) {

			// Parameter/Mode encoded in the faces-config.xml target
			WindowState ws = request.getWindowState();

			if ((ws == null) || !ws.toString().equalsIgnoreCase("normal")) {
				return outputTestResult(false,
						"encodeActionURL incorrectly encoded an invalid window state.  The resulting request should have ignored the invalid window state and remained in 'normal' mode.");
			}

			paramResult = checkParameter(request, response);

			if (paramResult.equals("true")) {
				return outputTestResult(true,
						"Correctly encoded nonFaces URL as a renderURL using the _jsfBridgeNonFacesView parameter excluding the invalid window state and with the additional parameter.");
			}
			else {
				return paramResult;
			}
		}
		else {
			return outputTestResult(false,
					"Unexpected error -- should not have hit this arm in the conditional statement.");
		}
	}

	private String checkParameter(RenderRequest request, RenderResponse response) throws IOException {
		String pVal = request.getParameter("param1");

		if ((pVal != null) && pVal.equals("testValue")) {
			return "true";
		}
		else if (pVal == null) {
			outputTestResult(false, "Expected additional parameter 'param1' is missing.");
		}
		else if (!pVal.equals("testValue")) {
			outputTestResult(false,
				"Expected additional parameter 'param1' has unexpected value. Expected: 'param1' but received " + pVal);
		}

		return "false";
	}

	private String outputTestResult(boolean pass, String msg) throws IOException {
		BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

		if (pass) {
			resultWriter.setStatus(BridgeTCKResultWriter.PASS);
			resultWriter.setDetail(msg);
		}
		else {
			resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
			resultWriter.setDetail(msg);
		}

		return resultWriter.toString();

	}
}
