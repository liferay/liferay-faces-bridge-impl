/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import javax.portlet.RenderResponse;
import javax.portlet.filter.RenderResponseWrapper;


/**
 * This class decorates a {@link RenderResponse} in order to suppress write operations by the {@link PrintWriter}
 * returned by {@link #getWriter()}.
 *
 * @author  Kyle Stiemann
 */
public class SuppressedRenderResponse extends RenderResponseWrapper {

	// Private Data Members
	private SuppressedPrintWriter suppressedPrintWriter;

	public SuppressedRenderResponse(RenderResponse wrappedRenderResponse) {
		super(wrappedRenderResponse);
	}

	@Override
	public SuppressedPrintWriter getWriter() throws IOException {

		if (suppressedPrintWriter == null) {

			PrintWriter printWriter = super.getWriter();
			suppressedPrintWriter = new SuppressedPrintWriter(printWriter);
		}

		return suppressedPrintWriter;
	}
}
