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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_12;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.BridgeEventHandler;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * Checks that GenericFacesPortlet#getBridgeEventHandler method works as stated in section 4.2.12. - The
 * GenericFacesPortlet implements getBridgeEventHandler by returning an instance of the configured event handler
 * corresponding to the value of the portlet initialization parameter javax.portlet.faces.bridgeEventHandler or null if
 * this parameter doesn't exist.
 */
public class GetBridgeEventHandlerTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		executeTest(getBridgeEventHandler());

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

	private void executeTest(BridgeEventHandler eventHandler) {

		try {

			if (getTestName().equals("getBridgeEventHandlerMethodSetTest")) {

				if (eventHandler == null) {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"getBridgeEventHandler returned null but an EventHandler was configured.");
				}
				else if (isClassNameInDelegationChain(eventHandler,
							"com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2.TestEventHandler")) {
					getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
						"getBridgeEventHandler correctly returned the configured EventHandler instance.");
				}
				else {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"getBridgeEventHandler incorrectly returned a different EventHandler instance than expected.  Expected: and instanceof TestEventHandler but received: " +
						eventHandler.getClass().getName());
				}
			}
			else if (getTestName().equals("getBridgeEventHandlerMethodNotSetTest")) {

				if (eventHandler == null) {
					getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
						"getBridgeEventHandler correctly returned null as there isn't a configured EventHandler.");
				}
				else {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"getBridgeEventHandler incorrectly returned an BridgeEventHandler instance though none was configured.  Received: " +
						eventHandler.getClass().getName());
				}
			}
			else {
				getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
					"Unexpected Test invocation: " + getTestName());
			}
		}
		catch (Exception e) {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
				"getBridgeEventHandler unexpected Exception: " + e.toString());
		}
	}

	private boolean isClassNameInDelegationChain(BridgeEventHandler bridgeEventHandler, String className) {

		Class<? extends BridgeEventHandler> bridgeEventHandlerClass = bridgeEventHandler.getClass();

		if (bridgeEventHandlerClass.getName().startsWith(className)) {
			return true;
		}
		else {

			try {
				Method getWrappedMethod = bridgeEventHandlerClass.getMethod("getWrapped");
				getWrappedMethod.setAccessible(true);
				bridgeEventHandler = (BridgeEventHandler) getWrappedMethod.invoke(bridgeEventHandler);

				return isClassNameInDelegationChain(bridgeEventHandler, className);
			}
			catch (Exception e) {
				return false;
			}
		}
	}
}
