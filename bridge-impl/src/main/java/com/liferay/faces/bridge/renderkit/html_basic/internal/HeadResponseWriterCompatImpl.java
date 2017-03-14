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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.portlet.HeaderResponse;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.liferay.faces.util.application.ResourceUtil;


/**
 * Custom {@link ResponseWriter} that has the ability to write to the &lt;head&gt;...&lt;/head&gt; section of the portal
 * page.
 *
 * @author  Neil Griffin
 */
public class HeadResponseWriterCompatImpl extends HeadResponseWriterBase {

	// Private Data Members
	private HeaderResponse headerResponse;

	public HeadResponseWriterCompatImpl(ResponseWriter wrappedResponseWriter, HeaderResponse headerResponse) {
		super(wrappedResponseWriter);
		this.headerResponse = headerResponse;
	}

	@Override
	public Element createElement(String name) {
		return headerResponse.createElement(name);
	}

	@Override
	protected void addResourceToHeadSection(Element element, String nodeName, UIComponent componentResource)
		throws IOException {

		String name;
		String scope = null;
		String version = null;
		String elementString = elementToString(nodeName, element);

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
		else {

			// Generate a unique id for each element that is not a JSF resource.
			name = Integer.toString(element.hashCode()) + Integer.toString(headerResponse.hashCode());
		}

		headerResponse.addDependency(name, scope, version, elementString);
	}

	private String elementToString(String nodeName, Element element) {

		StringBuilder buf = new StringBuilder("<");
		buf.append(nodeName);

		NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {

			Node attribute = attributes.item(i);
			buf.append(" ");
			buf.append(attribute.getNodeName());
			buf.append("=\"");
			buf.append(attribute.getNodeValue());
			buf.append("\"");
		}

		buf.append(">");

		if (!nodeName.equals("link")) {

			String textContent = element.getTextContent();

			if (textContent != null) {
				buf.append(textContent);
			}

			buf.append("</");
			buf.append(nodeName);
			buf.append(">");
		}

		return buf.toString();
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
