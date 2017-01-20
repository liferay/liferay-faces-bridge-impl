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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import org.w3c.dom.Element;

import com.liferay.faces.bridge.util.internal.URLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class HeadResponseWriterBase extends ResponseWriterWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadResponseWriterBase.class);

	protected static final String ADDED_RESOURCE_TO_HEAD =
		"Added resource to {0}'s <head>...</head> section, element=[{1}]";

	// Private Data Members
	private ResponseWriter wrappedResponseWriter;
	private ElementWriterStack elementWriterStack;
	private Stack<UIComponent> componentResourceStack;
	private boolean titleElement = false;

	public HeadResponseWriterBase(ResponseWriter wrappedResponseWriter) {

		this.wrappedResponseWriter = wrappedResponseWriter;
		this.elementWriterStack = new ElementWriterStack();
		this.componentResourceStack = new Stack<UIComponent>();
	}

	public abstract Element createElement(String name);

	@Override
	public Writer append(CharSequence csq) throws IOException {

		try {
			elementWriterStack.safePeek().append(csq);
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}

		return this;
	}

	@Override
	public Writer append(char c) throws IOException {

		try {
			elementWriterStack.safePeek().append(c);
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}

		return this;
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {

		try {
			elementWriterStack.safePeek().append(csq, start, end);
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}

		return this;
	}

	@Override
	public void close() throws IOException {

		try {
			elementWriterStack.safePeek().close();
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}
	}

	@Override
	public void endCDATA() throws IOException {

		try {
			elementWriterStack.safePeek().write("]]>");
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}
	}

	@Override
	public void endDocument() throws IOException {
		// no-op
	}

	@Override
	public void endElement(String name) throws IOException {

		if ("title".equals(name)) {
			titleElement = false;
		}
		else {

			try {
				ElementWriter elementWriter = elementWriterStack.pop();
				Element element = elementWriter.getElement();
				String nodeName = element.getNodeName();
				logger.trace("POPPED element name=[{0}]", nodeName);

				UIComponent componentResource = componentResourceStack.pop();

				if (!"head".equals(nodeName)) {
					addResourceToHeadSection(element, nodeName, componentResource);
				}
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void flush() throws IOException {

		try {
			elementWriterStack.safePeek().flush();
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public void startCDATA() throws IOException {

		try {
			elementWriterStack.safePeek().write("<![CDATA[");
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}
	}

	@Override
	public void startDocument() throws IOException {
		// no-op
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {

		if ("title".equals(name)) {

			logger.warn(
				"Title removed because multiple <title> elements are invalid and the portlet container controls the <title>.");
			titleElement = true;
		}
		else {
			Element element = createElement(name);
			ElementWriter elementWriter = new ElementWriter(element);
			elementWriterStack.push(elementWriter);
			componentResourceStack.push(component);
			logger.trace("PUSHED element name=[{0}]", name);
		}
	}

	@Override
	public void write(int c) throws IOException {

		try {
			elementWriterStack.safePeek().write(c);
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}
	}

	@Override
	public void write(char[] cbuf) throws IOException {

		if (cbuf != null) {

			try {
				elementWriterStack.safePeek().write(cbuf);
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void write(String str) throws IOException {

		if (str != null) {

			try {
				elementWriterStack.safePeek().write(str);
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		if (cbuf != null) {

			try {
				elementWriterStack.safePeek().write(cbuf, off, len);
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException {

		if (str != null) {

			try {
				elementWriterStack.safePeek().write(str, off, len);
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		try {

			if (value == null) {
				elementWriterStack.safePeek().getElement().setAttribute(name, null);
			}
			else {
				elementWriterStack.safePeek().getElement().setAttribute(name, value.toString());
			}
		}
		catch (EmptyStackException e) {
			throw new IOException(EmptyStackException.class.getSimpleName());
		}
	}

	@Override
	public void writeComment(Object comment) throws IOException {

		if (comment != null) {

			try {
				elementWriterStack.safePeek().write(comment.toString());
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void writeText(Object text, String property) throws IOException {

		if ((text != null) && !titleElement) {

			try {
				elementWriterStack.safePeek().write(text.toString());
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void writeText(Object text, UIComponent component, String property) throws IOException {

		if (text != null) {

			try {
				elementWriterStack.safePeek().write(text.toString());
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void writeText(char[] text, int off, int len) throws IOException {

		if (text != null) {

			try {
				elementWriterStack.safePeek().write(text, off, len);
			}
			catch (EmptyStackException e) {
				throw new IOException(EmptyStackException.class.getSimpleName());
			}
		}
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {

		if (value != null) {
			value = URLUtil.escapeXML(value.toString());
		}

		writeAttribute(name, value, property);
	}

	protected abstract void addResourceToHeadSection(Element element, String nodeName, UIComponent componentResource)
		throws IOException;
}
