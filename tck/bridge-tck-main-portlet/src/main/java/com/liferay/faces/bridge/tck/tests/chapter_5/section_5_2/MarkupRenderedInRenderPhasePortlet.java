/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * This portlet is used by chapter5_2Tests-markupRenderedInRenderPhaseTest-portlet in order to verify that the JSF
 * lifecycle (including the RENDER_RESPONSE phase) is executed in the HEADER_PHASE of the Portlet 3.0 lifecycle rather
 * than the RENDER_PHASE of the Portlet 1.0/2.0 lifecycle. It also verifies that markup is captured by the bridge during
 * the HEADER_PHASE and then written to the response during the RENDER_PHASE.
 *
 * @author  Kyle Stiemann
 */
public class MarkupRenderedInRenderPhasePortlet extends GenericFacesTestSuitePortlet {

	// Private Constants
	private static final String HEADER_PHASE_ATTEMPTED_WRITE =
		"com.liferay.faces.bridge.tck.HEADER_PHASE_ATTEMPTED_WRITE";
	private static final String MARKUP_CAPTURED = "com.liferay.faces.bridge.tck.MARKUP_CAPTURED";

	/**
	 * This method executes during the RENDER_PHASE of the portlet lifecycle. Its purpose
	 */
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		// If a write operation was attempted in the HEADER_PHASE of the Portlet 3.0 lifecycle, then report a failure
		// since the bridge is required to capture write operations in the HEADER_PHASE and not actually write them to
		// the response.
		boolean headerPhaseAttemptedWrite = (boolean) renderRequest.getAttribute(HEADER_PHASE_ATTEMPTED_WRITE);
		String testResultStatus;
		String testResultDetail;

		if (headerPhaseAttemptedWrite) {

			testResultStatus = "FAILURE";
			testResultDetail = "The bridge incorrectly attempted to write to the response during the header phase.";
		}

		// Otherwise,
		else {

			// Although writing will be suppressed, instruct the bridge to write the captured markup to the response.
			SuppressedRenderResponse suppressedRenderResponse = new SuppressedRenderResponse(renderResponse);
			super.render(renderRequest, suppressedRenderResponse);

			// If the bridge attempted to write the captured markup to the response, then report success.
			SuppressedPrintWriter suppressedPrintWriter = suppressedRenderResponse.getWriter();

			if (suppressedPrintWriter.isAttemptedWrite()) {

				boolean markupCaptured = (Boolean) renderRequest.getAttribute(MARKUP_CAPTURED);

				if (markupCaptured) {
					testResultStatus = "SUCCESS";
					testResultDetail =
						"The bridge correctly captured the response markup in the header phase and rendered the markup in the render phase.";
				}
				else {

					testResultStatus = "FAILURE";
					testResultDetail = "The bridge did not capture the response markup in the " +
						Bridge.RENDER_RESPONSE_OUTPUT + " request attribute.";
				}
			}

			// Otherwise, report failure.
			else {

				testResultStatus = "FAILURE";
				testResultDetail = "The bridge never wrote to the response during the render phase.";
			}
		}

		// Since writing to the response has been suppressed by this test, take the opportunity now to write out the
		// test results directly to the response.
		PrintWriter writer = renderResponse.getWriter();

		//J-
		writer
			.append("<p>Test: <span id=\"markupRenderedInRenderPhaseTest-test-name\">markupRenderedInRenderPhaseTest</span></p>")
			.append("<p>Status: <span id=\"markupRenderedInRenderPhaseTest-result-status\">").append(testResultStatus).append("</span></p>")
			.append("<p>Detail: <span id=\"markupRenderedInRenderPhaseTest-result-detail\">").append(testResultDetail).append("</p>");
		//J+
	}

	/**
	 * This method executes during the HEADER_PHASE of the Portlet 3.0 lifecycle. Its purpose is to determine whether or
	 * not write operations are performed when the FacesBridge executes the JSF lifecycle.
	 */
	@Override
	public void renderHeaders(HeaderRequest headerRequest, HeaderResponse headerResponse) throws PortletException,
		IOException {

		// Decorate the header response with one that suppresses write operations.
		headerResponse = new SuppressedHeaderResponse(headerResponse);

		// Delegate to the FacesBridge in order to have the JSF lifecycle executed.
		super.renderHeaders(headerRequest, headerResponse);

		// Determine whether or not write operations were attempted and save that result so that it can be picked up
		// in the RENDER_PHASE.
		SuppressedPrintWriter suppressedPrintWriter = (SuppressedPrintWriter) headerResponse.getWriter();
		headerRequest.setAttribute(HEADER_PHASE_ATTEMPTED_WRITE, suppressedPrintWriter.isAttemptedWrite());

		// Determine whether or not the markup was captured.
		Object renderResponseOutput = headerRequest.getAttribute(Bridge.RENDER_RESPONSE_OUTPUT);
		headerRequest.setAttribute(MARKUP_CAPTURED, (renderResponseOutput != null));
	}
}
