/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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

import javax.portlet.PortletContext;

import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class provides a {@link java.util.Map<String,String>} abstraction over the {@link PortletContext} init
 * parameters. Since it is designed to exist and be used within the scope of a request, it is not thread-safe.
 *
 * @author  Neil Griffin
 */
public class InitParameterMap extends AbstractImmutablePropertyMap<String> {

	// Private Data Members
	private PortletContext portletContext;

	public InitParameterMap(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	protected AbstractPropertyMapEntry<String> createPropertyMapEntry(String name) {
		return new InitParameterMapEntry(portletContext, name);
	}

	@Override
	protected Enumeration<String> getImmutablePropertyNames() {
		return portletContext.getInitParameterNames();
	}

	@Override
	protected String getProperty(String name) {
		return portletContext.getInitParameter(name);
	}
}
