/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context.map;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletSession;

import com.liferay.faces.bridge.util.ManagedBeanUtil;
import com.liferay.faces.util.map.AbstractPropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class SessionMap extends AbstractPropertyMap<Object> {

	// Private Data Members
	private PortletSession portletSession;
	private boolean preferPreDestroy;
	private int scope;

	/**
	 * Constructs a new SessionMap object instance.
	 *
	 * @param  portletSession  The portlet session.
	 * @param  scope           The scope of the session map, which can be PortletSession.PORTLET_SCOPE or
	 *                         PortletSession.APPLICATION_SCOPE
	 */
	public SessionMap(PortletSession portletSession, int scope, boolean preferPreDestroy) {
		this.portletSession = portletSession;
		this.scope = scope;
		this.preferPreDestroy = preferPreDestroy;
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext.getSessionMap}, before a managed-bean is removed
	 * from the map, any public no-argument void return methods annotated with javax.annotation.PreDestroy must be
	 * called first.
	 */
	@Override
	public void clear() {
		Set<Map.Entry<String, Object>> mapEntries = entrySet();

		if (mapEntries != null) {

			for (Map.Entry<String, Object> mapEntry : mapEntries) {
				Object potentialManagedBean = mapEntry.getValue();
				ManagedBeanUtil.invokePreDestroyMethods(potentialManagedBean, preferPreDestroy);
			}
		}

		super.clear();
	}

	/**
	 * According to the JSF 2.0 JavaDocs for {@link ExternalContext.getSessionMap}, before a managed-bean is removed
	 * from the map, any public no-argument void return methods annotated with javax.annotation.PreDestroy must be
	 * called first.
	 */
	@Override
	public Object remove(Object key) {
		Object potentialManagedBean = super.remove(key);
		ManagedBeanUtil.invokePreDestroyMethods(potentialManagedBean, preferPreDestroy);

		return potentialManagedBean;
	}

	@Override
	protected AbstractPropertyMapEntry<Object> createPropertyMapEntry(String name) {
		return new SessionMapEntry(portletSession, name, scope);
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
