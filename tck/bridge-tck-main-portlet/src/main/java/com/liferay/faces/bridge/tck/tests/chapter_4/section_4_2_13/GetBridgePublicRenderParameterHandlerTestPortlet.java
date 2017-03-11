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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_13;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.BridgePublicRenderParameterHandler;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getBridgePublicRenderParameterHandler method works as stated in section 4.2.13. - The
 * GenericFacesPortlet getBridgePublicRenderParameterHandler getBridgeEventHandler by returning an instance of the
 * configured prp handler corresponding to the value of the portlet initialization parameter
 * javax.portlet.faces.bridgePublicRenderParameter or null if this parameter doesn't exist.
 */
public class GetBridgePublicRenderParameterHandlerTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		executeText(getBridgePublicRenderParameterHandler());

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

	private void executeText(BridgePublicRenderParameterHandler prpHandler) {

		try {

			if (getTestName().equals("getBridgePublicRenderParameterHandlerMethodSetTest")) {

				if (prpHandler == null) {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"getBridgePublicRenderParameter returned null but a PRPHandler was configured.");
				}
				else if (isClassNameInDelegationChain(prpHandler,
							"com.liferay.faces.bridge.tck.tests.chapter_5.section_5_3.Tests")) {
					getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
						"getBridgePublicRenderParameter correctly returned the configured PRPHandler instance.");
				}
				else {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"getBridgePublicRenderParameter incorrectly returned a different PRPHandler instance than expected.  Expected: and instanceof Tests but received: " +
						prpHandler.getClass().getName());
				}
			}
			else if (getTestName().equals("getBridgePublicRenderParameterHandlerMethodNotSetTest")) {

				if (prpHandler == null) {
					getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
						"getBridgePublicRenderParameter correctly returned null as there isn't a configured PRPHandler.");
				}
				else {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"getBridgePublicRenderParameter incorrectly returned an BridgePRPHandler instance though none was configured.  Received: " +
						prpHandler.getClass().getName());
				}
			}
			else {
				getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
					"Unexpected Test invocation: " + getTestName());
			}
		}
		catch (Exception e) {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
				"getBridgePublicRenderParameter unexpected Exception: " + e.toString());
		}
	}

	private boolean isClassNameInDelegationChain(BridgePublicRenderParameterHandler bridgePublicRenderParameterHandler,
		String className) {

		Class<? extends BridgePublicRenderParameterHandler> bridgePublicRenderParameterHandlerClass =
			bridgePublicRenderParameterHandler.getClass();

		if (bridgePublicRenderParameterHandlerClass.getName().startsWith(className)) {
			return true;
		}
		else {

			try {
				Method getWrappedMethod = bridgePublicRenderParameterHandlerClass.getMethod("getWrapped");
				getWrappedMethod.setAccessible(true);
				bridgePublicRenderParameterHandler = (BridgePublicRenderParameterHandler) getWrappedMethod.invoke(
						bridgePublicRenderParameterHandler);

				return isClassNameInDelegationChain(bridgePublicRenderParameterHandler, className);
			}
			catch (Exception e) {
				return false;
			}
		}
	}
}
