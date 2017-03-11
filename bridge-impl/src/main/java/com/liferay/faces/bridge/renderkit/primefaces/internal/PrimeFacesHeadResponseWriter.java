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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import com.liferay.faces.bridge.renderkit.html_basic.internal.InlineScript;


/**
 * This class is part of a workaround for FACES-2061 and is responsible for capturing inline scripts that are encoded by
 * the PrimeFaces HeadRenderer.
 *
 * @author  Neil Griffin
 */
public class PrimeFacesHeadResponseWriter extends ResponseWriter {

	// Private Data Members
	private List<String> externalResourceURLs;
	private boolean inlineScript;
	private List<InlineScript> inlineScripts;
	private StringWriter stringWriter;
	private boolean writingScript;
	private boolean writingCss;
	private boolean writingLink;
	private String potentialExternalCssURL;

	public PrimeFacesHeadResponseWriter() {

		this.externalResourceURLs = new ArrayList<String>();
		this.inlineScript = true;
		this.inlineScripts = new ArrayList<InlineScript>();
		this.stringWriter = new StringWriter();
	}

	@Override
	public ResponseWriter cloneWithWriter(Writer writer) {
		return null;
	}

	@Override
	public void close() throws IOException {
		stringWriter.close();
	}

	@Override
	public void endDocument() throws IOException {
	}

	@Override
	public void endElement(String name) throws IOException {

		if ("script".equals(name)) {

			writingScript = false;

			if (inlineScript) {

				InlineScript inlineScript = new InlineScript(stringWriter.toString(), "primefaces");
				inlineScripts.add(inlineScript);
				stringWriter.getBuffer().setLength(0);
			}

			inlineScript = true;
		}
		else if ("link".equals(name)) {

			potentialExternalCssURL = null;
			writingCss = false;
			writingLink = false;
		}
	}

	@Override
	public void flush() throws IOException {
		stringWriter.flush();
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	public List<String> getExternalResourceURLs() {
		return externalResourceURLs;
	}

	public List<InlineScript> getInlineScripts() {
		return inlineScripts;
	}

	@Override
	public void startDocument() throws IOException {
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {

		if ("script".equals(name)) {
			writingScript = true;
		}
		else if ("link".equals(name)) {
			writingLink = true;
		}
	}

	@Override
	public void write(char[] text, int off, int len) throws IOException {

		if (writingScript && inlineScript) {
			stringWriter.write(text, off, len);
		}
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		if (writingScript && "src".equals(name)) {

			inlineScript = false;

			if (value != null) {

				String externalScriptURL = value.toString();
				externalResourceURLs.add(externalScriptURL);
			}
		}
		else if (writingLink && "type".equals(name) && "text/css".equals(value)) {

			// If a resource URL has been saved while writing this link, then it is a CSS resource URL.
			if (potentialExternalCssURL != null) {
				externalResourceURLs.add(potentialExternalCssURL);
			}
			else {
				writingCss = true;
			}
		}
		else if (writingLink && "href".equals(name) && (value != null)) {

			String externalResourceURL = value.toString();

			if (writingCss) {
				externalResourceURLs.add(externalResourceURL);
			}

			// Otherwise, the resource may or may not be a CSS resource URL, so save it until it's type can be
			// determined.
			else {
				potentialExternalCssURL = externalResourceURL;
			}
		}
	}

	@Override
	public void writeComment(Object comment) throws IOException {

		if (writingScript && inlineScript && (comment != null)) {
			stringWriter.write(comment.toString());
		}
	}

	@Override
	public void writeText(Object text, String property) throws IOException {

		if (writingScript && inlineScript && (text != null)) {
			stringWriter.write(text.toString());
		}
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {

		if (writingScript && inlineScript && (text != null)) {
			stringWriter.write(text, off, len);
		}
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {
		writeAttribute(name, value, property);
	}
}
