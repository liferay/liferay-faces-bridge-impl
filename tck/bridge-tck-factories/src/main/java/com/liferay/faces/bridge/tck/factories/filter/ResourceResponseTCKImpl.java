/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.factories.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.portlet.ResourceResponse;
import javax.portlet.filter.ResourceResponseWrapper;


/**
 * @author  Neil Griffin
 */
public class ResourceResponseTCKImpl extends ResourceResponseWrapper {

	private PrintWriter printWriter;
	private int status;

	public ResourceResponseTCKImpl(ResourceResponse resourceResponse) {
		super(resourceResponse);
	}

	@Override
	public PrintWriter getWriter() throws IOException {

		if (printWriter == null) {
			printWriter = new CapturingPrintWriter(super.getWriter());
		}

		return printWriter;
	}

	/**
	 * @see {@link com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_3.Tests#responseResetPreRenderEventHandler(javax.faces.event.ComponentSystemEvent)}
	 */
	private class CapturingPrintWriter extends PrintWriter {

		private StringWriter stringWriter = new StringWriter();

		public CapturingPrintWriter(Writer out) {
			super(out);
		}

		@Override
		public String toString() {
			return stringWriter.toString();
		}

		@Override
		public void write(int c) {
			stringWriter.write(c);
			super.write(c);
		}

		@Override
		public void write(String s) {
			stringWriter.write(s);
			super.write(s);
		}

		@Override
		public void write(char[] buf) {

			try {
				stringWriter.write(buf);
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}

			super.write(buf);
		}

		@Override
		public void write(char[] buf, int off, int len) {
			stringWriter.write(buf, off, len);
			super.write(buf, off, len);
		}
	}

	@Override
	public void setProperty(String key, String value) {

		if (ResourceResponse.HTTP_STATUS_CODE.equals(key)) {
			status = Integer.valueOf(key);
		}

		super.setProperty(key, value);
	}

	public int getStatus() {
		return status;
	}
}
