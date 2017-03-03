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
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class replaces the &lt;body&gt; elements with &lt;div&gt; elements and modifies other markup output by the
 * default body renderer in order to make h:body's markup compatible with portlets.
 *
 * @author  Kyle Stiemann
 */
public class ResponseWriterBridgeBodyImpl extends ResponseWriterWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResponseWriterBridgeBodyImpl.class);

	// Private Data Members
	private Stack<String> elementsStack = new Stack<String>();
	private ResponseWriter wrapppedResponseWriter;

	public ResponseWriterBridgeBodyImpl(ResponseWriter wrapppedResponseWriter) {
		this.wrapppedResponseWriter = wrapppedResponseWriter;
	}

	@Override
	public void endElement(String name) throws IOException {

		if (!elementsStack.empty()) {
			elementsStack.pop();
		}

		// Supress illegal elements that have been migrated from the <head> section.
		if ("title".equals(name) || "base".equals(name) || "meta".equals(name)) {
			logger.warn("Suppressed writing of illegal element <{0}> in the document <body>.", name);
		}
		else {

			// It is forbidden for a portlet to render the <body> element, so instead, render a <div> element.
			if ("body".equals(name)) {
				name = "div";
			}

			super.endElement(name);
		}
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrapppedResponseWriter;
	}

	@Override
	public void startElement(String name, UIComponent component) throws IOException {

		elementsStack.push(name);

		// Supress illegal elements that have been migrated from the <head> section.
		if ("title".equals(name) || "base".equals(name) || "meta".equals(name)) {
			logger.warn("Suppressed writing of illegal element <{0}> in the document <body>.", name);
		}
		else {

			// It is forbidden for a portlet to render the <body> element, so instead, render a <div> element.
			if ("body".equals(name)) {
				name = "div";
			}

			super.startElement(name, component);
		}
	}

	@Override
	public void writeAttribute(String name, Object value, String property) throws IOException {

		String currentElement = null;

		if (!elementsStack.empty()) {
			currentElement = elementsStack.peek();
		}

		if ("body".equals(currentElement) &&
				("onload".equals(name) || "onunload".equals(name) || "role".equals(name) || "xmlns".equals(name))) {
			logger.warn("Suppressed writing of illegal attribute \"{0}\" on portlet body <div>.", name);
		}

		// Supress illegal elements that have been migrated from the <head> section.
		else if ("title".equals(currentElement) || "base".equals(currentElement) || "meta".equals(currentElement)) {
			logger.warn("Suppressed writing of illegal element <{0}> in the document <body>.", currentElement);
		}
		else {

			if ("body".equals(currentElement) && "class".equals(name)) {

				String valueAsString = "";

				if (value != null) {
					valueAsString = value.toString();
				}

				// Add a special CSS class name, BodyRendererBridgeImpl.STYLE_CLASS_PORTLET_BODY, in the rendered markup
				// in order to clue-in the developer that a <div> was rendered instead of <body> if the styleClass does
				// not already contain BodyRendererBridgeImpl.STYLE_CLASS_PORTLET_BODY.
				if (!valueAsString.contains(RenderKitUtil.STYLE_CLASS_PORTLET_BODY)) {
					valueAsString = valueAsString.concat(" ").concat(RenderKitUtil.STYLE_CLASS_PORTLET_BODY);
				}

				value = valueAsString;
			}

			super.writeAttribute(name, value, property);
		}
	}
}
