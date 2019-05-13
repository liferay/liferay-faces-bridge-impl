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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;


/**
 * @author  Kyle Stiemann
 */
public class ResponseWriterMockImpl extends ResponseWriter {

	@Override
	public ResponseWriter cloneWithWriter(Writer writer) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void endDocument() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void endElement(String name) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void flush() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void startDocument() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void writeComment(Object comment) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void writeText(Object text, String property) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {
		throw new UnsupportedOperationException("");
	}
}
