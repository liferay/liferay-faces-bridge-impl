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
package com.liferay.faces.demos.spring;

import java.util.Map;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;


/**
 * This class serves as a custom Spring scope that decorates the map of view-scoped managed-beans behind the JSF2 {@link
 * ViewScoped} annotation.
 *
 * @author  Neil Griffin
 */
public class ViewScope implements Scope {

	public Object get(String name, ObjectFactory<?> objectFactory) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> viewMap = facesContext.getViewRoot().getViewMap();
		Object viewScopedBean = viewMap.get(name);

		if (viewScopedBean == null) {
			viewScopedBean = objectFactory.getObject();
			viewMap.put(name, viewScopedBean);
		}

		return viewScopedBean;
	}

	public String getConversationId() {

		// Unsupported feature
		return null;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		// Unsupported feature
	}

	public Object remove(String name) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> viewMap = facesContext.getViewRoot().getViewMap();

		return viewMap.remove(name);
	}

	public Object resolveContextualObject(String key) {

		// Unsupported feature
		return null;
	}

}
