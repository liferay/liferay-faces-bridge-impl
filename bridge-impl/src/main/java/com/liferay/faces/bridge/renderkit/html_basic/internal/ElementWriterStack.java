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

import java.util.Stack;

import org.w3c.dom.Element;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ElementWriterStack extends Stack<ElementWriter> {

	// serialVersionUID
	private static final long serialVersionUID = 3761771658484098988L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ElementWriterStack.class);

	/**
	 * The safePeek() method may have pushed some blank elements, so this method takes that into account.
	 */
	@Override
	public synchronized ElementWriter pop() {

		// Pop the top element off the stack.
		ElementWriter topElementWriter = super.pop();
		Element topElement = topElementWriter.getElement();

		// For each element that remains on the top of the stack:
		boolean done = isEmpty();

		while (!done) {

			// If the top stack element is a blank element, then PREPEND its textContent to that of the topElement.
			Element peekedElement = peek().getElement();

			if (peekedElement instanceof ElementBlankImpl) {

				StringBuilder prependedTextContent = new StringBuilder();

				String peekedTextContent = peekedElement.getTextContent();

				if (peekedTextContent != null) {
					prependedTextContent.append(peekedTextContent);
				}

				String topTextContent = topElement.getTextContent();

				if (topTextContent != null) {
					prependedTextContent.append(topTextContent);
				}

				topElement.setTextContent(prependedTextContent.toString());
				super.pop();
				done = isEmpty();
			}
			else {
				done = true;
			}
		}

		return topElementWriter;
	}

	/**
	 * The purpose of safePeek() is to prevent an EmptyStackException from being thrown due to a JSF component renderer
	 * not playing by the rules, whereby startElement() should be called first before write().
	 */
	public synchronized ElementWriter safePeek() {

		if (isEmpty()) {
			logger.debug("Stack was empty so created blank element", (Object[]) null);
			push(new ElementWriter(new ElementBlankImpl()));
		}

		return peek();
	}

}
