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

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.bridge.bean.internal.BeanManager;
import com.liferay.faces.bridge.bean.internal.BeanManagerFactory;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvoker;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvokerFactory;
import com.liferay.faces.bridge.config.internal.PortletConfigParam;
import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.map.AbstractPropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class SessionScopeMap extends AbstractPropertyMap<Object> {

	// Private Data Members
	private BeanManager beanManager;
	private PortletSession portletSession;
	private PreDestroyInvoker preDestroyInvoker;
	private boolean preferPreDestroy;
	private int scope;

	/**
	 * Constructs a new SessionMap object instance.
	 *
	 * @param  bridgeContext  The current bridge context.
	 * @param  scope          The scope of the session map, which can be PortletSession.PORTLET_SCOPE or
	 *                        PortletSession.APPLICATION_SCOPE
	 */
	public SessionScopeMap(BridgeContext bridgeContext, int scope) {

		String appConfigAttrName = ApplicationConfig.class.getName();
		PortletContext portletContext = bridgeContext.getPortletContext();
		ApplicationConfig applicationConfig = (ApplicationConfig) portletContext.getAttribute(appConfigAttrName);
		BeanManagerFactory beanManagerFactory = (BeanManagerFactory) BridgeFactoryFinder.getFactory(
				BeanManagerFactory.class);
		this.beanManager = beanManagerFactory.getBeanManager(applicationConfig.getFacesConfig());

		ContextMapFactory contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(
				ContextMapFactory.class);
		Map<String, Object> applicationScopeMap = contextMapFactory.getApplicationScopeMap(bridgeContext);
		PreDestroyInvokerFactory preDestroyInvokerFactory = (PreDestroyInvokerFactory) BridgeFactoryFinder.getFactory(
				PreDestroyInvokerFactory.class);
		this.preDestroyInvoker = preDestroyInvokerFactory.getPreDestroyInvoker(applicationScopeMap);

		PortletRequest portletRequest = bridgeContext.getPortletRequest();
		this.portletSession = portletRequest.getPortletSession();

		// Determines whether or not methods annotated with the @PreDestroy annotation are preferably invoked
		// over the @BridgePreDestroy annotation.
		PortletConfig portletConfig = bridgeContext.getPortletConfig();
		this.preferPreDestroy = PortletConfigParam.PreferPreDestroy.getBooleanValue(portletConfig);

		this.scope = scope;
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext#getSessionMap}, before a managed-bean is removed
	 * from the map, any public no-argument void return methods annotated with javax.annotation.PreDestroy must be
	 * called first.
	 */
	@Override
	public void clear() {
		Set<Map.Entry<String, Object>> mapEntries = entrySet();

		if (mapEntries != null) {

			for (Map.Entry<String, Object> mapEntry : mapEntries) {

				String potentialManagedBeanName = mapEntry.getKey();

				Object potentialManagedBeanValue = mapEntry.getValue();

				if (beanManager.isManagedBean(potentialManagedBeanName, potentialManagedBeanValue)) {
					preDestroyInvoker.invokeAnnotatedMethods(potentialManagedBeanValue, preferPreDestroy);
				}
			}
		}

		super.clear();
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext#getSessionMap}, before a managed-bean is removed
	 * from the map, any public no-argument void return methods annotated with javax.annotation.PreDestroy must be
	 * called first.
	 */
	@Override
	public Object remove(Object key) {

		String potentialManagedBeanName = (String) key;
		Object potentialManagedBeanValue = super.remove(key);

		if (beanManager.isManagedBean(potentialManagedBeanName, potentialManagedBeanValue)) {
			preDestroyInvoker.invokeAnnotatedMethods(potentialManagedBeanValue, preferPreDestroy);
		}

		return potentialManagedBeanValue;
	}

	@Override
	protected AbstractPropertyMapEntry<Object> createPropertyMapEntry(String name) {
		return new SessionScopeMapEntry(portletSession, name, scope);
	}

	@Override
	protected void removeProperty(String name) {
		portletSession.removeAttribute(name, scope);
	}

	@Override
	protected Object getProperty(String name) {
		return portletSession.getAttribute(name, scope);
	}

	@Override
	protected void setProperty(String name, Object value) {
		portletSession.setAttribute(name, value, scope);
	}

	@Override
	protected Enumeration<String> getPropertyNames() {
		return portletSession.getAttributeNames(scope);
	}
}
