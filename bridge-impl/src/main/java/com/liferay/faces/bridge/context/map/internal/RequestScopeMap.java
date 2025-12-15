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
package com.liferay.faces.bridge.context.map.internal;

import java.util.Enumeration;
import java.util.Map;

import jakarta.faces.context.ExternalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.bridge.bean.BeanManager;
import com.liferay.faces.bridge.bean.BeanManagerFactory;
import com.liferay.faces.bridge.bean.PreDestroyInvoker;
import com.liferay.faces.bridge.bean.PreDestroyInvokerFactory;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class provides a {@link Map <String,Object>} abstraction over the {@link PortletRequest} attributes. Since it is
 * designed to exist and be used within the scope of a request, it is not thread-safe.
 *
 * @author  Neil Griffin
 */
public class RequestScopeMap extends AbstractMutablePropertyMap<Object> {

	// Private Data Members
	private BeanManager beanManager;
	private PortletRequest portletRequest;
	private PreDestroyInvoker preDestroyInvoker;
	private boolean preferPreDestroy;

	public RequestScopeMap(PortletContext portletContext, PortletRequest portletRequest, boolean preferPreDestroy) {

		String appConfigAttrName = ApplicationConfig.class.getName();
		ApplicationConfig applicationConfig = (ApplicationConfig) portletContext.getAttribute(appConfigAttrName);
		BeanManagerFactory beanManagerFactory = (BeanManagerFactory) BridgeFactoryFinder.getFactory(portletContext,
				BeanManagerFactory.class);
		this.beanManager = beanManagerFactory.getBeanManager(applicationConfig.getFacesConfig());
		this.portletRequest = portletRequest;
		this.preferPreDestroy = preferPreDestroy;

		PreDestroyInvokerFactory preDestroyInvokerFactory = (PreDestroyInvokerFactory) BridgeFactoryFinder.getFactory(
				portletContext, PreDestroyInvokerFactory.class);
		this.preDestroyInvoker = preDestroyInvokerFactory.getPreDestroyInvoker(portletContext);
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext#getRequestMap}, before a managed-bean is removed
	 * from the map, any public no-argument void return methods annotated with jakarta.annotation.PreDestroy must be
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
	protected Object getMutableProperty(String name) {
		return portletRequest.getAttribute(name);
	}

	@Override
	protected Enumeration<String> getMutablePropertyNames() {
		return portletRequest.getAttributeNames();
	}

	@Override
	protected void removeMutableProperty(String name) {
		portletRequest.removeAttribute(name);
	}

	@Override
	protected void setMutableProperty(String name, Object value) {
		portletRequest.setAttribute(name, value);
	}
}
