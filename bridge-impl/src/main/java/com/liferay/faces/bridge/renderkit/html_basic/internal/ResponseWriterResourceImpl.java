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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import com.liferay.faces.bridge.util.internal.URLUtil;
import com.liferay.faces.util.render.FacesURLEncoder;
import com.liferay.faces.util.render.FacesURLEncoderFactory;


/**
 * The purpose of this class is to bypass the Mojarra/MyFaces encoding of the URL found in the "src" attribute of a
 * &lt;script&gt; element or the "href" attribute of a &lt;link&gt; element. For more info, see: <a
 * href="http://issues.liferay.com/browse/FACES-1236">FACES-1236</a> and <a
 * href="https://github.com/javaserverfaces/mojarra/issues/4345">MOJARRA #4345</a>. This class also ensures that body
 * script resources are loaded in each Ajax request by rendering <code>type="text/javascript"</code> as the last
 * attribute. For more info, see: <a href="https://issues.liferay.com/browse/FACES-3252">FACES-3252</a> and <a
 * href="https://github.com/javaserverfaces/mojarra/issues/4340">MOJARRA #4340</a>.
 *
 * @author  Neil Griffin
 */
public class ResponseWriterResourceImpl extends ResponseWriterWrapper {

	// Private Final Data Members
	private final FacesURLEncoder facesURLEncoder;
	private final ResponseWriter wrappedResponseWriter;

	// Private Data Members
	private Attribute javaScriptType;
	private boolean writingLink;
	private boolean writingScript;
	private boolean closeStartTag;

	public ResponseWriterResourceImpl(FacesContext facesContext, ResponseWriter responseWriter) {
		ExternalContext externalContext = facesContext.getExternalContext();
		this.facesURLEncoder = FacesURLEncoderFactory.getFacesURLEncoderInstance(externalContext);
		this.wrappedResponseWriter = responseWriter;
	}

	@Override
	public Writer append(char c) throws IOException {

		closeStartTagIfNecessary();

		return super.append(c);
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {

		closeStartTagIfNecessary();

		return super.append(csq);
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {

		closeStartTagIfNecessary();

		return super.append(csq, start, end);
	}

	@Override
	public void close() throws IOException {

		closeStartTagIfNecessary();
		super.close();
	}

	@Override
	public void endCDATA() throws IOException {

		closeStartTagIfNecessary();
		super.endCDATA();
	}

	@Override
	public void endDocument() throws IOException {

		closeStartTagIfNecessary();
		super.endDocument();
	}

	@Override
	public void endElement(String name) throws IOException {

		boolean endingScript = "script".equalsIgnoreCase(name);
		boolean endingLink = "link".equalsIgnoreCase(name);

		if (endingScript || endingLink) {

			closeStartTagIfNecessary();
			writeWithoutClosingStartTag("</");
			writeWithoutClosingStartTag(name);
			writeWithoutClosingStartTag(">");

			if (endingScript) {
				writingScript = false;
			}
			else {
				writingLink = false;
			}
		}
		else {
			super.endElement(name);
		}
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public void startCDATA() throws IOException {

		closeStartTagIfNecessary();
		super.startCDATA();
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {

		writingScript = "script".equalsIgnoreCase(name);
		writingLink = "link".equalsIgnoreCase(name);

		if (writingScript || writingLink) {

			writeWithoutClosingStartTag("<");
			writeWithoutClosingStartTag(name);
			closeStartTag = true;
		}
		else {
			super.startElement(name, component);
		}
	}

	@Override
	public void write(String str) throws IOException {

		closeStartTagIfNecessary();
		super.write(str);
	}

	@Override
	public void write(char[] cbuf) throws IOException {

		closeStartTagIfNecessary();
		super.write(cbuf);
	}

	@Override
	public void write(int c) throws IOException {

		closeStartTagIfNecessary();
		super.write(c);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		closeStartTagIfNecessary();
		super.write(cbuf, off, len);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {

		closeStartTagIfNecessary();
		super.write(str, off, len);
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		if (writingScript && "type".equalsIgnoreCase(name) && "text/javascript".equalsIgnoreCase((String) value)) {
			javaScriptType = new Attribute(name, value);
		}
		else {
			super.writeAttribute(name, value, property);
		}
	}

	@Override
	public void writeComment(Object comment) throws IOException {

		closeStartTagIfNecessary();
		super.writeComment(comment);
	}

	@Override
	public void writeText(Object text, String property) throws IOException {

		closeStartTagIfNecessary();
		super.writeText(text, property);
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {

		closeStartTagIfNecessary();
		super.writeText(text, off, len);
	}

	@Override
	public void writeText(Object text, UIComponent component, String property) throws IOException {

		closeStartTagIfNecessary();
		super.writeText(text, component, property);
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {

		if (writingScript || writingLink) {

			// Workaround https://github.com/javaserverfaces/mojarra/issues/4345: JSF script and link resource urls
			// params are HTML escaped twice when added via Ajax. Also see https://issues.liferay.com/browse/FACES-1236.
			writeWithoutClosingStartTag(" ");
			writeWithoutClosingStartTag(name);
			writeWithoutClosingStartTag("=\"");

			if (value != null) {

				String uri = value.toString();
				String characterEncoding = wrappedResponseWriter.getCharacterEncoding();
				uri = URLUtil.encodeURL(uri, facesURLEncoder, characterEncoding);
				writeWithoutClosingStartTag(uri);
			}

			writeWithoutClosingStartTag("\"");
		}
	}

	private void closeStartTagIfNecessary() throws IOException {

		if (closeStartTag && (writingScript || writingLink)) {

			closeStartTag = false;

			if (javaScriptType != null) {

				// Workaround https://github.com/javaserverfaces/mojarra/issues/4340: Script resources in <body> are
				// never run during Ajax requests.
				writeWithoutClosingStartTag(" ");
				writeWithoutClosingStartTag(javaScriptType.name);
				writeWithoutClosingStartTag("=\"");

				if (javaScriptType.value != null) {
					writeWithoutClosingStartTag(javaScriptType.value.toString());
				}

				writeWithoutClosingStartTag("\"");
				javaScriptType = null;
			}

			writeWithoutClosingStartTag(">");
		}
	}

	private void writeWithoutClosingStartTag(String string) throws IOException {

		// Other methods in this class such as write() call closeElementStartTagIfNecessary(), so ensure that
		// closeElementStartTagIfNecessary() is not called recursively when super.write() calls other
		// methods from this class.
		boolean closeStartTag = this.closeStartTag;
		this.closeStartTag = false;
		super.write(string);
		this.closeStartTag = closeStartTag;
	}

	private static final class Attribute {

		private final String name;
		private final Object value;

		public Attribute(String name, Object value) {
			this.name = name;
			this.value = value;
		}
	}
}
