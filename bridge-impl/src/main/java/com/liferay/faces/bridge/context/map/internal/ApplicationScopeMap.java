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

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.bean.internal.BeanManager;
import com.liferay.faces.bridge.bean.internal.BeanManagerFactory;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvoker;
import com.liferay.faces.bridge.bean.internal.PreDestroyInvokerFactory;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class provides a {@link Map<String,Object>} abstraction over the {@link PortletContext} attributes. Since it is
 * designed to exist and be used within the scope of a request, it is not thread-safe.
 *
 * @author  Neil Griffin
 */
public class ApplicationScopeMap extends AbstractMutablePropertyMap<Object> {

	// Private Data Members
	private BeanManager beanManager;
	private PortletContext portletContext;
	private PreDestroyInvoker preDestroyInvoker;
	private boolean preferPreDestroy;

	public ApplicationScopeMap(PortletContext portletContext, boolean preferPreDestroy) {

		BeanManagerFactory beanManagerFactory = (BeanManagerFactory) BridgeFactoryFinder.getFactory(portletContext,
				BeanManagerFactory.class);
		this.portletContext = portletContext;

		String appConfigAttrName = ApplicationConfig.class.getName();
		ApplicationConfig applicationConfig = (ApplicationConfig) this.portletContext.getAttribute(appConfigAttrName);
		this.beanManager = beanManagerFactory.getBeanManager(applicationConfig.getFacesConfig());
		this.preferPreDestroy = preferPreDestroy;

		PreDestroyInvokerFactory preDestroyInvokerFactory = (PreDestroyInvokerFactory) BridgeFactoryFinder.getFactory(
				portletContext, PreDestroyInvokerFactory.class);
		this.preDestroyInvoker = preDestroyInvokerFactory.getPreDestroyInvoker(portletContext);
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext#getApplicationMap}, before a managed-bean is removed
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
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext#getApplicationMap}, before a managed-bean is removed
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
		return new ApplicationScopeMapEntry(portletContext, name);
	}

	@Override
	protected Object getMutableProperty(String name) {
		return portletContext.getAttribute(name);
	}

	@Override
	protected Enumeration<String> getMutablePropertyNames() {
		return portletContext.getAttributeNames();
	}

	@Override
	protected void removeMutableProperty(String name) {
		portletContext.removeAttribute(name);
	}

	@Override
	protected void setMutableProperty(String name, Object value) {
		portletContext.setAttribute(name, value);
	}
}
