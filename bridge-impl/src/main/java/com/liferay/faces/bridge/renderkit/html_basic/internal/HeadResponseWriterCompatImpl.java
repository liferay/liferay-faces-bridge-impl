/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.portlet.MimeResponse;
import javax.portlet.RenderResponse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Custom {@link ResponseWriter} that has the ability to write to the &lt;head&gt;...&lt;/head&gt; section of the portal
 * page. Because {@link RenderResponse} only supports the creation of {@link Element}s via {@link
 * RenderResponse#createElement(java.lang.String)}, many features of normal ResponseWriters such as CDATA, comments, and
 * child text nodes must be disabled since those node types cannot be created.
 *
 * @author  Neil Griffin
 */
public class HeadResponseWriterCompatImpl extends HeadResponseWriterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadResponseWriterCompatImpl.class);

	// Private Data Members
	private RenderResponse renderResponse;

	public HeadResponseWriterCompatImpl(ResponseWriter wrappedResponseWriter, RenderResponse renderResponse) {
		super(wrappedResponseWriter);
		this.renderResponse = renderResponse;
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {

		if (csq != null) {

			String text = csq.toString();

			if ("<![CDATA[".equalsIgnoreCase(text.toUpperCase(Locale.ENGLISH))) {
				startCDATA();
			}
			else if ("]]>".equalsIgnoreCase(text)) {
				endCDATA();
			}
			else {

				Node currentNode = getCurrentNode();

				if (!currentNode.hasChildNodes()) {

					String textContent = currentNode.getTextContent();

					if (textContent == null) {
						textContent = text;
					}
					else {
						textContent += text;
					}

					currentNode.setTextContent(textContent);
				}

				// Avoid overwriting child nodes with text.
				else {
					logger.warn(
						"Element text removed because adding both children and text to an element in the <head> section is not supported in Portlet 2.0.");
				}
			}
		}

		return this;
	}

	@Override
	public void endCDATA() throws IOException {
		logger.warn(
			"CDATA end tag removed because adding a CDATA tag to the <head> section is not supported in Portlet 2.0.");
	}

	@Override
	public void startCDATA() throws IOException {
		logger.warn(
			"CDATA start tag removed because adding a CDATA tag to the <head> section is not supported in Portlet 2.0.");
	}

	@Override
	public void writeComment(Object comment) throws IOException {
		logger.warn("Comment removed because adding a comment to the <head> section is not supported in Portlet 2.0.");
	}

	@Override
	protected Node createElement(String nodeName) {
		return renderResponse.createElement(nodeName);
	}

	@Override
	protected void writeNodeToHeadSection(Node node, UIComponent componentResource) throws IOException {

		if (!isElement(node)) {
			throw new IllegalArgumentException("Only elements may be added to the <head> section in Portlet 2.0");
		}

		// NOTE: The Portlet 2.0 Javadocs for the addProperty method indicate that if the key already exists,
		// then the element will be added to any existing elements under that key name. There is a risk that
		// multiple portlet instances on the same portal page could cause multiple <script /> elements to be
		// added to the <head>...</head> section of the rendered portal page. See:
		// http://portals.apache.org/pluto/portlet-2.0-apidocs/javax/portlet/PortletResponse.html#addProperty(java.lang.String,%20org.w3c.dom.Element)
		renderResponse.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, (Element) node);

		if (logger.isDebugEnabled()) {
			logger.debug("Added resource to Liferay's <head>...</head> section, node=[{0}]", getNodeInfo(node));
		}
	}
}
