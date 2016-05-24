/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import com.liferay.faces.util.map.AbstractPropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class InitParameterMap extends AbstractPropertyMap<String> {

	private PortletContext portletContext;

	public InitParameterMap(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	protected AbstractPropertyMapEntry<String> createPropertyMapEntry(String name) {
		return new InitParameterMapEntry(portletContext, name);
	}

	@Override
	protected String getProperty(String name) {
		return portletContext.getInitParameter(name);
	}

	@Override
	protected Enumeration<String> getPropertyNames() {
		return portletContext.getInitParameterNames();
	}

	@Override
	protected void removeProperty(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setProperty(String name, String value) {
		throw new UnsupportedOperationException();
	}
}
