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
package com.liferay.faces.bridge.scope.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.RequestAttributeInspector;
import javax.portlet.faces.annotation.ExcludeFromManagedRequestScope;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class RequestAttributeInspectorImpl implements RequestAttributeInspector, Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 1876589389345663517L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(RequestAttributeInspectorImpl.class);

	// Private Constants for EXCLUDED namespaces listed in Section 5.1.2 of the JSR 329 Spec
	private static final String EXCLUDED_NAMESPACE_JAVAX_FACES = "javax.faces";
	private static final String EXCLUDED_NAMESPACE_JAVAX_PORTLET = "javax.portlet";
	private static final String EXCLUDED_NAMESPACE_JAVAX_PORTLET_FACES = "javax.portlet.faces";
	private static final String EXCLUCED_NAMESPACE_JAVAX_SERVLET = "javax.servlet";
	private static final String EXCLUCED_NAMESPACE_JAVAX_SERVLET_INCLUDE = "javax.servlet.include";

	// Private Data Members
	private List<String> excludedAttributeNames;
	private Set<String> preExistingAttributeNames;

	public RequestAttributeInspectorImpl(PortletRequest portletRequest, PortletConfig portletConfig,
		BridgeConfig bridgeConfig) {

		this.excludedAttributeNames = new ArrayList<String>();

		// Get the list of excluded BridgeRequestScope attributes from the WEB-INF/portlet.xml descriptor.
		PortletContext portletContext = portletConfig.getPortletContext();
		String portletName = portletConfig.getPortletName();
		@SuppressWarnings("unchecked")
		List<String> portletContextExcludedAttributeNames = (List<String>) portletContext.getAttribute(
				Bridge.BRIDGE_PACKAGE_PREFIX + portletName + "." + Bridge.EXCLUDED_REQUEST_ATTRIBUTES);

		// Combine the two lists into a single list of excluded BridgeRequestScope attributes.
		Set<String> facesConfigExcludedAttributeNames = bridgeConfig.getExcludedRequestAttributes();

		if (facesConfigExcludedAttributeNames != null) {
			this.excludedAttributeNames.addAll(facesConfigExcludedAttributeNames);
		}

		if (portletContextExcludedAttributeNames != null) {
			this.excludedAttributeNames.addAll(portletContextExcludedAttributeNames);
		}

		this.preExistingAttributeNames = getPreExistingRequestAttributeNames(portletRequest);
	}

	@Override
	public boolean containsExcludedNamespace(String name) {

		if (isNamespaceMatch(name, EXCLUDED_NAMESPACE_JAVAX_FACES) ||
				isNamespaceMatch(name, EXCLUDED_NAMESPACE_JAVAX_PORTLET) ||
				isNamespaceMatch(name, EXCLUDED_NAMESPACE_JAVAX_PORTLET_FACES) ||
				isNamespaceMatch(name, EXCLUCED_NAMESPACE_JAVAX_SERVLET) ||
				isNamespaceMatch(name, EXCLUCED_NAMESPACE_JAVAX_SERVLET_INCLUDE)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isExcludedByAnnotation(String name, Object value) {

		boolean excluded = false;

		if ((value != null) && (value.getClass().getAnnotation(ExcludeFromManagedRequestScope.class) != null)) {
			excluded = true;
		}

		return excluded;
	}

	@Override
	public boolean isExcludedByConfig(String name, Object value) {

		boolean excluded = false;

		if (excludedAttributeNames != null) {

			for (String excludedAttribute : excludedAttributeNames) {

				if (name.equals(excludedAttribute)) {
					excluded = true;

					break;
				}
				else if (excludedAttribute.endsWith("*")) {

					String wildcardNamespace = excludedAttribute;
					int dotPos = wildcardNamespace.lastIndexOf(".");

					if (dotPos > 0) {
						wildcardNamespace = wildcardNamespace.substring(0, dotPos);
					}

					if (isNamespaceMatch(name, wildcardNamespace)) {
						excluded = true;

						break;
					}
				}
			}
		}

		return excluded;
	}

	@Override
	public boolean isExcludedByPreExisting(String name, Object value) {
		return preExistingAttributeNames.contains(name);
	}

	@Override
	public boolean isExcludedByType(String name, Object value) {

		// EXCLUDED attributes listed in Section 5.1.2 of the JSR 329 Spec
		return ((value != null) &&
				((value instanceof ExternalContext) || (value instanceof FacesContext) ||
					(value instanceof HttpSession) || (value instanceof PortalContext) ||
					(value instanceof PortletConfig) || (value instanceof PortletContext) ||
					(value instanceof PortletPreferences) || (value instanceof PortletRequest) ||
					(value instanceof PortletResponse) || (value instanceof PortletSession) ||
					(value instanceof ServletConfig) || (value instanceof ServletContext) ||
					(value instanceof ServletRequest) || (value instanceof ServletResponse)));
	}

	/**
	 * According to section 5.1.2 of the JSR 329 spec, the request attributes that exist before the bridge acquires the
	 * FacesContext must not be part of the bridge request scope. Having noted that, we have to save-off a list of names
	 * of these pre-existing request attributes, so that we know to NOT restore them.
	 */
	protected Set<String> getPreExistingRequestAttributeNames(PortletRequest portletRequest) {
		Set<String> attributeNames = null;
		Enumeration<String> requestAttributeNames = portletRequest.getAttributeNames();

		if (requestAttributeNames != null) {
			attributeNames = new HashSet<String>();

			while (requestAttributeNames.hasMoreElements()) {
				String attributeName = requestAttributeNames.nextElement();
				attributeNames.add(attributeName);
				logger.trace("Saving name of pre-existing request attribute [{0}]", attributeName);
			}
		}

		return attributeNames;
	}

	protected boolean isNamespaceMatch(String attributeName, String namespace) {

		boolean match = false;

		String attributeNamespace = attributeName;
		int dotPos = attributeNamespace.lastIndexOf(".");

		if (dotPos > 0) {
			attributeNamespace = attributeNamespace.substring(0, dotPos);
		}

		if (namespace.equals(attributeNamespace)) {
			match = true;
		}

		return match;
	}
}
