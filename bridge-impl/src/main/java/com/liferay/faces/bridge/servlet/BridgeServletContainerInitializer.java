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
package com.liferay.faces.bridge.servlet;

import java.util.Set;

import javax.faces.annotation.FacesConfig;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;


/**
 * @author  Kyle Stiemann
 * @author  Neil Griffin
 */
@HandlesTypes(FacesConfig.class)
public final class BridgeServletContainerInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> annotatedClasses, ServletContext servletContext) throws ServletException {

		if ((annotatedClasses != null) && !annotatedClasses.isEmpty()) {
			servletContext.setAttribute(FacesConfig.class.getName(), Boolean.TRUE);
		}

		servletContext.addListener(BridgeSessionListener.class);
	}
}
