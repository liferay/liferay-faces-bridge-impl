/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.cdi.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.annotation.BridgeRequestScoped;


/**
 * @author  Neil Griffin
 */
public class BridgeRequestBeanContext implements Context {

	@Override
	public <T> T get(Contextual<T> contextual) {
		return get(contextual, null);
	}

	@Override
	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {

		Bean<T> bean = (Bean<T>) contextual;

		String attributeName = bean.getName();

		if ((attributeName == null) || attributeName.isEmpty()) {
			Class<?> beanClass = bean.getBeanClass();

			attributeName = beanClass.getName();
		}

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		Map<String, Object> requestMap = externalContext.getRequestMap();

		T bridgeRequestScopedBean = (T) requestMap.get(attributeName);

		if (bridgeRequestScopedBean == null) {

			if (creationalContext == null) {
				return null;
			}

			bridgeRequestScopedBean = bean.create(creationalContext);

			requestMap.put(attributeName, bridgeRequestScopedBean);
		}

		return bridgeRequestScopedBean;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return BridgeRequestScoped.class;
	}

	@Override
	public boolean isActive() {
		return FacesContext.getCurrentInstance() != null;
	}
}
