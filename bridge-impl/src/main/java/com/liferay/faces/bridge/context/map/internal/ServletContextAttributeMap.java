/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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

import javax.servlet.ServletContext;

import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class provides a {@link Map <String,Object>} abstraction over the {@link ServletContext} attributes. Since it is
 * designed to exist and be used within the scope of a request, it is not thread-safe.
 *
 * @author  Neil Griffin
 */
public class ServletContextAttributeMap extends AbstractMutablePropertyMap<Object> {

	// Private Data Members
	private ServletContext servletContext;

	public ServletContextAttributeMap(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	protected AbstractPropertyMapEntry<Object> createPropertyMapEntry(String name) {
		return new ServletContextAttributeMapEntry(servletContext, name);
	}

	@Override
	protected Object getMutableProperty(String name) {
		return servletContext.getAttribute(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Enumeration<String> getMutablePropertyNames() {
		return servletContext.getAttributeNames();
	}

	@Override
	protected void removeMutableProperty(String name) {
		servletContext.removeAttribute(name);
	}

	@Override
	protected void setMutableProperty(String name, Object value) {
		servletContext.setAttribute(name, value);
	}
}
