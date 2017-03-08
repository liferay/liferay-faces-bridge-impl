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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * @author  Kyle Stiemann
 */
public class PortletMarkupRenderedInRenderPhaseTestImpl extends GenericFacesTestSuitePortlet {

	// Private Constants
	private static final String HEADER_PHASE_WRITE_METHOD_CALLS = PortletMarkupRenderedInRenderPhaseTestImpl.class
		.getName() + "_HEADER_WRITE_METHOD_CALLS";

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		int headerWriteMethodCalls = (Integer) renderRequest.getAttribute(HEADER_PHASE_WRITE_METHOD_CALLS);
		String testResultStatus;
		String testResultDetail;

		if (headerWriteMethodCalls == 0) {

			RenderResponseMarkupRenderedInRenderPhaseTestImpl renderResponseMarkupRenderedInRenderPhaseTestImpl =
				new RenderResponseMarkupRenderedInRenderPhaseTestImpl(renderResponse);
			super.render(renderRequest, renderResponseMarkupRenderedInRenderPhaseTestImpl);

			int renderWriteMethodCalls = renderResponseMarkupRenderedInRenderPhaseTestImpl.getWriteMethodCalls();

			if (renderWriteMethodCalls > 0) {

				testResultStatus = "SUCCESS";
				testResultDetail =
					"The bridge generated the response markup in the header phase and rendered the markup in the render phase. The bridge called the portlet PrintWriter's write method " +
					renderWriteMethodCalls + " times.";
			}
			else {

				testResultStatus = "FAILURE";
				testResultDetail = "The bridge never wrote to the response during the render phase.";
			}
		}
		else {

			testResultStatus = "FAILURE";
			testResultDetail = "The bridge incorrectly attempted to write to the response during the header phase.";
		}

		PrintWriter writer = renderResponse.getWriter();

		//J-
		writer
			.append("<p>Test: <span id=\"markupRenderedInRenderPhaseTest-test-name\">markupRenderedInRenderPhaseTest</span></p>")
			.append("<p>Status: <span id=\"markupRenderedInRenderPhaseTest-result-status\">").append(testResultStatus).append("</span></p>")
			.append("<p>Detail: <span id=\"markupRenderedInRenderPhaseTest-result-detail\">").append(testResultDetail).append("</p>");
		//J+
	}

	@Override
	public void renderHeaders(HeaderRequest headerRequest, HeaderResponse headerResponse) throws PortletException,
		IOException {

		HeaderResponseMarkupRenderedInRenderPhaseTestImpl headerResponseMarkupRenderedInRenderPhaseTestImpl =
			new HeaderResponseMarkupRenderedInRenderPhaseTestImpl(headerResponse);
		super.renderHeaders(headerRequest, headerResponseMarkupRenderedInRenderPhaseTestImpl);

		int writeMethodCalls = headerResponseMarkupRenderedInRenderPhaseTestImpl.getWriteMethodCalls();
		headerRequest.setAttribute(HEADER_PHASE_WRITE_METHOD_CALLS, writeMethodCalls);
	}

}
