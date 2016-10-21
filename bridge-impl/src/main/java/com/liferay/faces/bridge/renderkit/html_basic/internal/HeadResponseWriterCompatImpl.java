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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Custom {@link ResponseWriter} that has the ability to write to the &lt;head&gt;...&lt;/head&gt; section of the portal
 * page.
 *
 * @author  Neil Griffin
 */
public class HeadResponseWriterCompatImpl extends HeadResponseWriterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadResponseWriterCompatImpl.class);

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

		if (HeadRendererBridgeImpl.isScriptResource(componentResource) ||
				HeadRendererBridgeImpl.isStyleSheetResource(componentResource)) {

			String resourceId = ResourceUtil.getResourceId(componentResource);

			// TODO consider support for portlet:scope attribute via TagDecorator.
			Map<String, Object> attributes = componentResource.getAttributes();
			String scope = (String) attributes.get("portlet:scope");

			// TODO add option to configure this boolean on a portlet wide basis.
			boolean scopeComponentResourcesToPortlet = false;

			if ((scope == null) && scopeComponentResourcesToPortlet) {
				scope = headerResponse.getNamespace();
			}

			// TODO consider support for portlet:version attribute via TagDecorator.
			String version = (String) attributes.get("portlet:version");

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

			String elementString = toString(nodeName, element);
			headerResponse.addDependency(resourceId, scope, version, elementString);
		}
		else {

			// NOTE: The Portlet 2.0 Javadocs for the addProperty method indicate that if the key already exists,
			// then the element will be added to any existing elements under that key name. There is a risk that
			// multiple portlet instances on the same portal page could cause multiple <script /> elements to be
			// added to the <head>...</head> section of the rendered portal page. See:
			// http://portals.apache.org/pluto/portlet-2.0-apidocs/javax/portlet/PortletResponse.html#addProperty(java.lang.String,%20org.w3c.dom.Element)
			headerResponse.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
			logger.debug(ADDED_RESOURCE_TO_HEAD, "portal", nodeName);
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

	private String toString(String nodeName, Element scriptOrCSSResourceElement) {

		String elementString = "<" + nodeName;
		NamedNodeMap attributes = scriptOrCSSResourceElement.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {

			Node attribute = attributes.item(i);
			elementString += " " + attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\"";
		}

		if (nodeName.equals("script")) {
			elementString += "></script>";
		}
		else {
			elementString += ">";
		}

		return elementString;
	}
}
