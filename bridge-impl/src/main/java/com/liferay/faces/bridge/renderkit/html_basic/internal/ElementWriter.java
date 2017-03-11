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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Element;


/**
 * @author  Neil Griffin
 */
public class ElementWriter extends Writer {

	private Element element;

	public ElementWriter(Element element) {
		this.element = element;
	}

	public void append(String str) {

		if (str != null) {
			String textContent = element.getTextContent();

			if (textContent == null) {
				element.setTextContent(str);
			}
			else {
				element.setTextContent(textContent + str);
			}
		}
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		StringWriter stringWriter = new StringWriter();
		Writer writer = stringWriter.append(csq);
		append(stringWriter.getBuffer().toString());

		return writer;
	}

	@Override
	public Writer append(char c) throws IOException {
		StringWriter stringWriter = new StringWriter();
		Writer writer = stringWriter.append(c);
		append(stringWriter.getBuffer().toString());

		return writer;
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		StringWriter stringWriter = new StringWriter();
		Writer writer = stringWriter.append(csq, start, end);
		append(stringWriter.getBuffer().toString());

		return writer;
	}

	@Override
	public void close() throws IOException {
		// no-op
	}

	@Override
	public void flush() throws IOException {
		// no-op
	}

	public Element getElement() {
		return element;
	}

	@Override
	public void write(int c) throws IOException {
		StringWriter stringWriter = new StringWriter();
		stringWriter.write(c);
		append(stringWriter.getBuffer().toString());
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		StringWriter stringWriter = new StringWriter();
		stringWriter.write(cbuf);
		append(stringWriter.getBuffer().toString());
	}

	@Override
	public void write(String str) throws IOException {
		append(str);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		StringWriter stringWriter = new StringWriter();
		stringWriter.write(str, off, len);
		append(stringWriter.getBuffer().toString());
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		StringWriter stringWriter = new StringWriter();
		stringWriter.write(cbuf, off, len);
		append(stringWriter.getBuffer().toString());
	}

}
