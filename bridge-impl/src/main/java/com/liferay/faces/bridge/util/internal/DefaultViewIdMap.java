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
package com.liferay.faces.bridge.util.internal;

import java.util.Enumeration;

import javax.portlet.PortletConfig;
import javax.portlet.faces.GenericFacesPortlet;

import com.liferay.faces.bridge.context.map.internal.AbstractImmutablePropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class serves as a {@link java.util.Map} facade over {@link PortletConfig} init-param values that makes it
 * convenient to retrieve a default viewId based on a portlet mode. It replaces the JSR 301/329 legacy behavior from
 * {@link GenericFacesPortlet#getDefaultViewIdMap()}.
 *
 * @author  Neil Griffin
 */
public class DefaultViewIdMap extends AbstractImmutablePropertyMap<String> {

	private PortletConfig portletConfig;

	public DefaultViewIdMap(PortletConfig portletConfig) {
		this.portletConfig = portletConfig;
	}

	@Override
	protected AbstractPropertyMapEntry<String> createPropertyMapEntry(String name) {
		return new DefaultViewIdMapEntry(portletConfig, name);
	}

	@Override
	protected Enumeration<String> getImmutablePropertyNames() {
		return portletConfig.getInitParameterNames();
	}

	@Override
	protected String getProperty(String portletMode) {
		return portletConfig.getInitParameter(GenericFacesPortlet.DEFAULT_VIEWID + "." + portletMode);
	}
}
