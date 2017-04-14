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
package com.liferay.faces.bridge.internal;

import java.util.Enumeration;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.context.internal.ExternalContextWrapper;
import com.liferay.faces.bridge.context.map.internal.AbstractMutablePropertyMap;
import com.liferay.faces.bridge.context.map.internal.ApplicationScopeMapEntry;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class BridgeFactoryFinderImpl extends BridgeFactoryFinder {

	@Override
	@Deprecated
	public Object getFactoryInstance(Class<?> factoryClass) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletContext portletContext = (PortletContext) externalContext.getContext();

		return getFactoryInstance(portletContext, factoryClass);
	}

	@Override
	public Object getFactoryInstance(PortletContext portletContext, Class<?> factoryClass) {

		ExternalContext factoryExternalContext = new FactoryExternalContext(portletContext);

		return FactoryExtensionFinder.getFactory(factoryExternalContext, factoryClass);
	}

	@Override
	public void releaseFactories(PortletContext portletContext) {
		ExternalContext factoryExternalContext = new FactoryExternalContext(portletContext);
		FactoryExtensionFinder.getInstance().releaseFactories(factoryExternalContext);
	}

	private static class FactoryApplicationScopeMap extends AbstractMutablePropertyMap<Object> {

		// Private Data Members
		private PortletContext portletContext;

		private FactoryApplicationScopeMap(PortletContext portletContext) {
			this.portletContext = portletContext;
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

	private static class FactoryExternalContext extends ExternalContextWrapper {

		// Private Data Members
		private Map<String, Object> applicationMap;

		public FactoryExternalContext(PortletContext portletContext) {
			this.applicationMap = new FactoryApplicationScopeMap(portletContext);
		}

		@Override
		public Map<String, Object> getApplicationMap() {
			return applicationMap;
		}

		@Override
		public ExternalContext getWrapped() {
			return null;
		}
	}
}
