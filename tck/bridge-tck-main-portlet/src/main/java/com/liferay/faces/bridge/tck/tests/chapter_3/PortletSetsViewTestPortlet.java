/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeInvalidViewPathException;

import com.liferay.faces.bridge.tck.common.portlet.RenderViewDispatchCompatPortlet;
import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * @author  Michael Freedman
 */
public class PortletSetsViewTestPortlet extends RenderViewDispatchCompatPortlet {

	private static final String SETSVIEWID_TEST = "portletSetsViewIdTest";
	private static final String SETSVIEWPATH_TEST = "portletSetsViewPathTest";
	private static final String SETSINVALIDVIEWPATH_TEST = "portletSetsInvalidViewPathTest";

	@Override
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
	}

	@Override
	public void renderView(RenderRequest renderRequest, MimeResponse mimeResponse) throws PortletException,
		IOException {

		if (getTestName().equals(SETSVIEWID_TEST)) {
			renderRequest.setAttribute(Bridge.VIEW_ID, "/tests/portletSetsViewIdTestSuccess.xhtml");
			dispatchToView(renderRequest, mimeResponse);
		}
		else if (getTestName().equals(SETSVIEWPATH_TEST)) {
			renderRequest.setAttribute(Bridge.VIEW_PATH, "/tests/portletSetsViewIdTestSuccess.jsf");
			dispatchToView(renderRequest, mimeResponse);
		}
		else if (getTestName().equals(SETSINVALIDVIEWPATH_TEST)) {
			renderRequest.setAttribute(Bridge.VIEW_PATH, "/tests/InvalidViewPath.jsp");

			Bridge bridge = super.getFacesBridge(renderRequest, mimeResponse);

			try {
				doBridgeFacesRequest(bridge, renderRequest, mimeResponse);
				outputInvalidViewPathTestResult(renderRequest, mimeResponse, false);
			}
			catch (BridgeInvalidViewPathException e) {
				outputInvalidViewPathTestResult(renderRequest, mimeResponse, true);
			}
			catch (Exception e) {
				outputInvalidViewPathTestResult(renderRequest, mimeResponse, false);
			}
		}
	}

	private void outputInvalidViewPathTestResult(PortletRequest portletRequest, MimeResponse response, boolean pass)
		throws IOException {

		if (getTestName().equals(SETSINVALIDVIEWPATH_TEST)) {

			BridgeTCKResultWriter resultWriter = new BridgeTCKResultWriter(getTestName());

			if (pass) {
				resultWriter.setStatus(BridgeTCKResultWriter.PASS);
				resultWriter.setDetail("Correctly threw BridgeInvalidViewPathException when passed a bad path.");
			}
			else {
				resultWriter.setStatus(BridgeTCKResultWriter.FAIL);
				resultWriter.setDetail("Didn't throw BridgeInvalidViewPathException when passed a bad path.");
			}

			if (response instanceof RenderResponse) {
				response.setContentType("text/html");

				PrintWriter out = response.getWriter();
				out.println(resultWriter.toString());
			}
			else {
				portletRequest.setAttribute(BridgeTCKResultWriter.class.getName(), resultWriter);
			}
		}
	}
}
