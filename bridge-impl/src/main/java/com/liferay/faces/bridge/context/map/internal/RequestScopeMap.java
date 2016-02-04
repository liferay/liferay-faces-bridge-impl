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
package com.liferay.faces.bridge.context.map.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.bean.internal.BeanManager;
import com.liferay.faces.bridge.bean.internal.BeanManagerFactory;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvoker;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvokerFactory;
import com.liferay.faces.bridge.config.internal.PortletConfigParam;
import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.ContextMapFactory;
import com.liferay.faces.bridge.scope.BridgeRequestScope;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.map.AbstractPropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class RequestScopeMap extends AbstractPropertyMap<Object> {

	// Private Data Members
	private BeanManager beanManager;
	private PortletRequest portletRequest;
	private PreDestroyInvoker preDestroyInvoker;
	private boolean preferPreDestroy;
	private Set<String> removedAttributeNames;

	public RequestScopeMap(BridgeContext bridgeContext) {

		String appConfigAttrName = ApplicationConfig.class.getName();
		PortletContext portletContext = bridgeContext.getPortletContext();
		ApplicationConfig applicationConfig = (ApplicationConfig) portletContext.getAttribute(appConfigAttrName);
		BeanManagerFactory beanManagerFactory = (BeanManagerFactory) BridgeFactoryFinder.getFactory(
				BeanManagerFactory.class);
		this.beanManager = beanManagerFactory.getBeanManager(applicationConfig.getFacesConfig());
		this.portletRequest = bridgeContext.getPortletRequest();

		// Determines whether or not methods annotated with the @PreDestroy annotation are preferably invoked
		// over the @BridgePreDestroy annotation.
		PortletConfig portletConfig = bridgeContext.getPortletConfig();
		this.preferPreDestroy = PortletConfigParam.PreferPreDestroy.getBooleanValue(portletConfig);

		ContextMapFactory contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(
				ContextMapFactory.class);
		Map<String, Object> applicationScopeMap = contextMapFactory.getApplicationScopeMap(bridgeContext);
		PreDestroyInvokerFactory preDestroyInvokerFactory = (PreDestroyInvokerFactory) BridgeFactoryFinder.getFactory(
				PreDestroyInvokerFactory.class);
		this.preDestroyInvoker = preDestroyInvokerFactory.getPreDestroyInvoker(applicationScopeMap);

		BridgeRequestScope bridgeRequestScope = bridgeContext.getBridgeRequestScope();

		if (bridgeRequestScope != null) {
			this.removedAttributeNames = bridgeRequestScope.getRemovedAttributeNames();
		}
		else {
			this.removedAttributeNames = new HashSet<String>();
		}
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext#getRequestMap}, before a managed-bean is removed
	 * from the map, any public no-argument void return methods annotated with javax.annotation.PreDestroy must be
	 * called first.
	 */
	@Override
	public Object remove(Object key) {

		String keyAsString = (String) key;
		Object potentialManagedBeanValue = super.remove(key);

		if (beanManager.isManagedBean(keyAsString, potentialManagedBeanValue)) {
			preDestroyInvoker.invokeAnnotatedMethods(potentialManagedBeanValue, preferPreDestroy);
		}

		return potentialManagedBeanValue;
	}

	@Override
	protected AbstractPropertyMapEntry<Object> createPropertyMapEntry(String name) {
		return new RequestScopeMapEntry(portletRequest, name);
	}

	@Override
	protected void removeProperty(String name) {
		removedAttributeNames.add(name);
		portletRequest.removeAttribute(name);
	}

	@Override
	protected Object getProperty(String name) {

		// If the specified name is a Servlet-API reserved attribute name, then the JSF runtime is attempting to
		// determine the viewId for a webapp environment. Because of this, it is necessary to return null so that the
		// JSF runtime will attempt to determine the viewId a different way, namely by calling
		// ExternalContext#getRequestPathInfo() or ExternalContext#getRequestServletPath().
		if ("javax.servlet.include.path_info".equals(name) || "javax.servlet.include.servlet_path".equals(name)) {
			return null;
		}

		// Otherwise, return the portlet request attribute value.
		else {
			return portletRequest.getAttribute(name);
		}
	}

	@Override
	protected void setProperty(String name, Object value) {

		// If the specified attribute name is regarded as previously removed, then no longer regard it as removed since
		// it is being added back now.
		if (removedAttributeNames.contains(name)) {
			removedAttributeNames.remove(name);
		}

		// Set the attribute value on the underlying request.
		portletRequest.setAttribute(name, value);
	}

	@Override
	protected Enumeration<String> getPropertyNames() {

		List<String> attributeNames = new ArrayList<String>();

		Enumeration<String> portletRequestAttributeNames = portletRequest.getAttributeNames();

		if (portletRequestAttributeNames != null) {

			while (portletRequestAttributeNames.hasMoreElements()) {
				String attributeName = portletRequestAttributeNames.nextElement();

				if (!removedAttributeNames.contains(attributeName)) {
					attributeNames.add(attributeName);
				}
			}
		}

		return Collections.enumeration(attributeNames);
	}
}
