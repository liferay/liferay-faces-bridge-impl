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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.liferay.faces.bridge.util.internal.XMLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class HeadResponseWriterBase extends ResponseWriterWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadResponseWriterBase.class);

	// Private Data Members
	private ResponseWriter wrappedResponseWriter;
	private Node currentNode;
	private Stack<UIComponent> componentResourceStack;
	private boolean titleElement = false;

	public HeadResponseWriterBase(ResponseWriter wrappedResponseWriter) {

		this.wrappedResponseWriter = wrappedResponseWriter;
		this.componentResourceStack = new Stack<UIComponent>();
	}

	@Override
	public Writer append(char c) throws IOException {
		return append(Character.toString(c));
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		return append(toString(csq, start, end));
	}

	@Override
	public void close() throws IOException {
		// no-op
	}

	@Override
	public void endDocument() throws IOException {
		// no-op
	}

	@Override
	public void endElement(String name) throws IOException {

		if ("head".equals(name)) {
			// no-op
		}
		else if ("title".equals(name)) {
			titleElement = false;
		}
		else {

			if (currentNode == null) {
				throw new IllegalStateException("ResponseWriter.endElement(\"" + name +
					"\") called before startElement(\"" + name + "\", uiComponent)");
			}

			if (!isElement(currentNode)) {
				throw new IllegalStateException(
					"ResponseWriter.endElement() called, but current node is not an element.");
			}

			UIComponent componentResource = componentResourceStack.pop();

			if (isDirectDescendantOfHead(currentNode)) {

				String nodeName = currentNode.getNodeName();
				logger.trace("POPPED element name=[{0}]", nodeName);

				if (!name.equals(nodeName)) {
					throw new IllegalStateException("Current element node name [\"" + nodeName +
						"\"] does not match name passed to endElement() [\"" + name + "\"].");
				}

				writeNodeToHeadSection(currentNode, componentResource);
				currentNode = null;
			}
			else {
				currentNode = currentNode.getParentNode();
			}
		}
	}

	@Override
	public void flush() throws IOException {
		// no-op
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public void startDocument() throws IOException {
		// no-op
	}

	@Override
	public void startElement(String name, UIComponent uiComponent) throws IOException {

		if ("head".equals(name)) {
			// no-op
		}
		else if ("title".equals(name)) {

			logger.warn(
				"Title removed because multiple <title> elements are invalid and the portlet container controls the <title>.");
			titleElement = true;
		}
		else {

			if (currentNode == null) {
				currentNode = createElement(name);
			}
			else {

				if (!isElement(currentNode)) {
					throw new IllegalStateException(
						"ResponseWriter.startElement() called, but parent node is not an element.");
				}

				Node element = createElement(name);
				currentNode.appendChild(element);
				currentNode = element;
			}

			componentResourceStack.push(uiComponent);

			logger.trace("PUSHED element name=[{0}]", name);
		}
	}

	@Override
	public void write(int c) throws IOException {
		write(Character.toChars(c));
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		write(String.valueOf(cbuf));
	}

	@Override
	public void write(String str) throws IOException {
		append(str);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		write(toString(cbuf, off, len));
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		write(toString(str, off, len));
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		if (isElement(currentNode)) {

			Element currentElement = (Element) currentNode;

			if (value != null) {
				currentElement.setAttribute(name, value.toString());
			}
			else {
				currentElement.setAttribute(name, null);
			}
		}
		else {
			throw new IllegalStateException("ResponseWriter.writeAttribute() called before startElement().");
		}
	}

	@Override
	public void writeText(Object text, String property) throws IOException {

		if ((text != null) && !titleElement) {

			if (isEscapeTextXML(currentNode)) {
				text = XMLUtil.escapeXML(text.toString());
			}

			write(text.toString());
		}
	}

	@Override
	public void writeText(Object text, UIComponent uiComponent, String property) throws IOException {
		writeText(text, property);
	}

	@Override
	public void writeText(char[] textArray, int off, int len) throws IOException {

		if (textArray != null) {
			writeText(toString(textArray, off, len), null);
		}
	}

	@Override
	public void writeURIAttribute(String name, Object value, String property) throws IOException {
		writeAttribute(name, value, property);
	}

	protected abstract Node createElement(String nodeName);

	protected abstract void writeNodeToHeadSection(Node node, UIComponent componentResource) throws IOException;

	protected Node getCurrentNode() {
		return currentNode;
	}

	protected String getNodeInfo(Node node) {

		String nodeInfo = "";
		short nodeType = node.getNodeType();

		if (isElement(node)) {
			nodeInfo = node.getNodeName();
		}
		else if (nodeType == Node.CDATA_SECTION_NODE) {
			nodeInfo = "CDATA_SECTION_NODE";
		}
		else if (nodeType == Node.COMMENT_NODE) {
			nodeInfo = "COMMENT_NODE";
		}
		else if (nodeType == Node.TEXT_NODE) {
			nodeInfo = "TEXT_NODE";
		}

		return nodeInfo;
	}

	protected boolean isElement(Node node) {
		return (node != null) && (node.getNodeType() == Node.ELEMENT_NODE);
	}

	protected void setCurrentNode(Node newCurrentNode) {
		this.currentNode = newCurrentNode;
	}

	private boolean isDirectDescendantOfHead(Node node) {

		Node parentNode = node.getParentNode();

		return (parentNode == null) || (parentNode.getNodeType() != Node.ELEMENT_NODE);
	}

	private boolean isEscapeTextXML(Node node) {

		boolean cdataNode = false;
		boolean scriptNode = false;
		boolean styleNode = false;

		if (node != null) {

			if (isElement(node)) {

				String nodeName = node.getNodeName();
				String lowerCaseNodeName = nodeName.toLowerCase(Locale.ENGLISH);
				scriptNode = lowerCaseNodeName.equals("script");
				styleNode = lowerCaseNodeName.equals("style");
			}
			else {

				short nodeType = node.getNodeType();
				cdataNode = (nodeType == Node.CDATA_SECTION_NODE);
			}
		}

		return !(cdataNode || scriptNode || styleNode);
	}

	private String toString(CharSequence csq, int start, int end) {
		return new StringBuilder(1).append(csq, start, end).toString();
	}

	private String toString(char[] cbuf, int off, int len) {
		return String.valueOf(Arrays.copyOfRange(cbuf, off, len));
	}
}
