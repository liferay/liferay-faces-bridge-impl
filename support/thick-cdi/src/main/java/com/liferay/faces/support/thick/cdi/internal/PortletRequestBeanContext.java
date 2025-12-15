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
package com.liferay.faces.support.thick.cdi.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.annotations.PortletRequestScoped;
import jakarta.portlet.filter.PortletRequestWrapper;


/**
 * Note: The source for class is mostly taken from the {@link
 * com.liferay.bean.portlet.cdi.extension.internal.scope.PortletRequestBeanContext} class.
 *
 * @author  Neil Griffin
 */
public class PortletRequestBeanContext implements Context {
	private static final String _ATTRIBUTE_NAME_PREFIX = "com.liferay.faces.thick.cdi.";

	@Override
	public <T> T get(Contextual<T> contextual) {
		return get(contextual, null);
	}

	@Override
	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
		return getPortletRequestScopedBean((Bean<T>) contextual, creationalContext);
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return PortletRequestScoped.class;
	}

	@Override
	public boolean isActive() {

		if (getPortletRequest() != null) {
			return true;
		}

		return false;
	}

	private String getAttributeName(Bean<?> bean) {
		String attributeName = bean.getName();

		if ((attributeName == null) || attributeName.isEmpty()) {
			Class<?> beanClass = bean.getBeanClass();

			attributeName = beanClass.getName();
		}

		// RequestAttributeInspectorImpl.isNamespaceMatch(String,String) is implemented such that it does not account
		// for the fact that the full FQCN of a class has a dot-delimited package name, so need to substitute the dots
		// for underscores in order for the "com.liferay.faces.thick.cdi." namespace wildcard specified in
		// META-INF/faces-config.xml to work properly.
		attributeName = attributeName.replaceAll("[.]", "_");

		return _ATTRIBUTE_NAME_PREFIX.concat(attributeName);
	}

	private PortletRequest getPortletRequest() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		while (portletRequest instanceof PortletRequestWrapper) {
			PortletRequestWrapper portletRequestWrapper = (PortletRequestWrapper) portletRequest;
			portletRequest = portletRequestWrapper.getRequest();
		}

		return portletRequest;
	}

	private <T> T getPortletRequestScopedBean(Bean<T> bean, CreationalContext<T> creationalContext) {

		String name = getAttributeName(bean);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		@SuppressWarnings("unchecked")
		CDIScopedBean<T> scopedBean = (CDIScopedBean<T>) requestMap.get(name);

		if (scopedBean == null) {

			if (creationalContext == null) {
				return null;
			}

			scopedBean = new CDIScopedBean<>(bean, creationalContext, name, PortletRequestScoped.class.getSimpleName());

			requestMap.put(name, scopedBean);
		}

		return scopedBean.getContainerCreatedInstance();
	}
}
