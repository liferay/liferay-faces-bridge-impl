/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import javax.portlet.HeaderResponse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.liferay.faces.bridge.util.internal.XMLUtil;
import com.liferay.faces.util.lang.ThreadSafeAccessor;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * Custom {@link ResponseWriter} that has the ability to write to the &lt;head&gt;...&lt;/head&gt; section of the portal
 * page.
 *
 * @author  Neil Griffin
 */
public class HeadResponseWriterCompatImpl extends HeadResponseWriterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadResponseWriterCompatImpl.class);

	// Private Final Data Members
	private final HeaderResponse headerResponse;

	public HeadResponseWriterCompatImpl(ResponseWriter wrappedResponseWriter, HeaderResponse headerResponse) {
		super(wrappedResponseWriter);
		this.headerResponse = headerResponse;
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
				addNodeToHeadSection(Node.TEXT_NODE, csq);
			}
		}

		return this;
	}

	@Override
	public void endCDATA() throws IOException {

		Node currentNode = getCurrentNode();

		if (currentNode.getNodeType() != Node.CDATA_SECTION_NODE) {
			throw new IllegalArgumentException("ResponseWriter.endCDATA() called before startCDATA().");
		}

		Node parentNode = currentNode.getParentNode();

		if (parentNode != null) {
			setCurrentNode(parentNode);
		}
		else {

			writeNodeToHeadSection(currentNode, null);
			setCurrentNode(null);
		}
	}

	@Override
	public void startCDATA() throws IOException {

		Node currentNode = getCurrentNode();
		Node cdataNode = new NodeImpl(Node.CDATA_SECTION_NODE, currentNode);

		if (currentNode != null) {

			if (Node.CDATA_SECTION_NODE == currentNode.getNodeType()) {
				throw new IllegalStateException("CDATA cannot be nested.");
			}

			currentNode.appendChild(cdataNode);
		}

		setCurrentNode(cdataNode);
	}

	@Override
	public void writeComment(Object comment) throws IOException {

		if (comment != null) {
			addNodeToHeadSection(Node.COMMENT_NODE, XMLUtil.escapeXML(comment.toString()));
		}
	}

	@Override
	protected Node createElement(String nodeName) {
		return new ElementImpl(nodeName, getCurrentNode());
	}

	@Override
	protected void writeNodeToHeadSection(Node node, UIComponent componentResource) throws IOException {

		String name = null;
		String scope = null;
		String version = null;
		String nodeString;

		if (isElement(node)) {

			Element element = (Element) node;

			if ((componentResource != null) &&
					(RenderKitUtil.isScriptResource(componentResource) ||
						RenderKitUtil.isStyleSheetResource(componentResource))) {

				Map<String, Object> attributes = componentResource.getAttributes();
				name = (String) attributes.get("name");
				scope = (String) attributes.get("library");

				// TODO consider support for portlet:version attribute via TagDecorator.
				version = (String) attributes.get("portlet:version");

				// TODO add option to configure this boolean on a portlet wide basis.
				boolean obtainComponentResourceVersionFromURL = false;

				if ((version == null) && obtainComponentResourceVersionFromURL) {

					String url = element.getAttribute("src");

					if ((url == null) || url.equals("")) {
						url = element.getAttribute("href");
					}

					int queryIndex = url.indexOf("?");

					if (queryIndex > 0) {

						String queryString = url.substring(queryIndex + 1);
						int versionStartIndex = getParameterValueStartIndex(queryString, "v");

						if (versionStartIndex > -1) {
							versionStartIndex = getParameterValueStartIndex(queryString, "version");
						}

						if (versionStartIndex > -1) {

							version = queryString.substring(versionStartIndex);

							int indexOfAmpersand = version.indexOf("&");

							if (indexOfAmpersand > -1) {
								version = version.substring(0, indexOfAmpersand);
							}
						}
					}
				}
			}

			nodeString = XMLUtil.elementToString(node, true);
		}
		else {
			nodeString = XMLUtil.nodeToString(node);
		}

		if (name == null) {

			// Generate a unique id for each element that is not a JSF resource.
			name = Integer.toString(node.hashCode()) + Integer.toString(headerResponse.hashCode());
		}

		headerResponse.addDependency(name, scope, version, nodeString);

		if (logger.isDebugEnabled()) {
			logger.debug("Added resource to Liferay's <head>...</head> section, node=[{0}]", getNodeInfo(node));
		}
	}

	private void addNodeToHeadSection(short nodeType, Object text) throws IOException {

		Node currentNode = getCurrentNode();

		if (currentNode != null) {
			currentNode.appendChild(new NodeImpl(nodeType, text.toString(), currentNode));
		}
		else {
			writeNodeToHeadSection(new NodeImpl(nodeType, text.toString(), null), null);
		}
	}

	private int getParameterValueStartIndex(String queryString, String parameterName) {

		int parameterValueStartIndex = -1;
		String parameterEqual = parameterName + "=";
		String andParameterEqual = "&" + parameterEqual;

		if (queryString.startsWith(parameterEqual)) {
			parameterValueStartIndex = queryString.indexOf(parameterEqual) + parameterEqual.length();
		}
		else if (queryString.contains(andParameterEqual)) {

			parameterValueStartIndex = queryString.indexOf(andParameterEqual);

			if (parameterValueStartIndex > -1) {
				parameterValueStartIndex += andParameterEqual.length();
			}
		}

		return parameterValueStartIndex;
	}
}
