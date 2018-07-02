/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.bridge.bean.internal.BeanManager;
import com.liferay.faces.bridge.bean.internal.BeanManagerFactory;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvoker;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvokerFactory;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class provides a {@link Map<String,Object>} abstraction over the {@link PortletSession} attributes. Since it is
 * designed to exist and be used within the scope of a request, it is not thread-safe.
 *
 * @author  Neil Griffin
 */
public class SessionScopeMap extends AbstractMutablePropertyMap<Object> {

	// Private Data Members
	private BeanManager beanManager;
	private PortletSession portletSession;
	private PreDestroyInvoker preDestroyInvoker;
	private boolean preferPreDestroy;
	private int scope;

	public SessionScopeMap(PortletContext portletContext, PortletSession portletSession, int scope,
		boolean preferPreDestroy) {

		String appConfigAttrName = ApplicationConfig.class.getName();
		ApplicationConfig applicationConfig = (ApplicationConfig) portletContext.getAttribute(appConfigAttrName);
		BeanManagerFactory beanManagerFactory = (BeanManagerFactory) BridgeFactoryFinder.getFactory(portletContext,
				BeanManagerFactory.class);
		this.beanManager = beanManagerFactory.getBeanManager(applicationConfig.getFacesConfig());

		PreDestroyInvokerFactory preDestroyInvokerFactory = (PreDestroyInvokerFactory) BridgeFactoryFinder.getFactory(
				portletContext, PreDestroyInvokerFactory.class);
		this.preDestroyInvoker = preDestroyInvokerFactory.getPreDestroyInvoker(portletContext);
		this.portletSession = portletSession;
		this.preferPreDestroy = preferPreDestroy;
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
	protected Object getMutableProperty(String name) {
		return portletSession.getAttribute(name, scope);
	}

	@Override
	protected Enumeration<String> getMutablePropertyNames() {
		return portletSession.getAttributeNames(scope);
	}

	@Override
	protected void removeMutableProperty(String name) {
		portletSession.removeAttribute(name, scope);
	}

	@Override
	protected void setMutableProperty(String name, Object value) {
		portletSession.setAttribute(name, value, scope);
	}
}
