/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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

	// Private Constants
	private static final String DEFAULT_VIEWID_PREFIX = GenericFacesPortlet.DEFAULT_VIEWID + ".";

	// Private Final Data Members
	private final PortletConfig portletConfig;

	public DefaultViewIdMap(PortletConfig portletConfig) {
		this.portletConfig = portletConfig;
	}

	@Override
	protected AbstractPropertyMapEntry<String> createPropertyMapEntry(String name) {
		return new DefaultViewIdMapEntry(portletConfig, name);
	}

	@Override
	protected Enumeration<String> getImmutablePropertyNames() {

		Enumeration<String> immutablePropertyNames = portletConfig.getInitParameterNames();
		List<String> immutablePropertyNamesList = new ArrayList<String>();

		while (immutablePropertyNames.hasMoreElements()) {

			String immutablePropertyName = immutablePropertyNames.nextElement();

			if (immutablePropertyName.startsWith(DEFAULT_VIEWID_PREFIX)) {
				immutablePropertyNamesList.add(immutablePropertyName);
			}
		}

		return Collections.enumeration(immutablePropertyNamesList);
	}

	@Override
	protected String getProperty(String defaultViewIdInitParamName) {

		if ((defaultViewIdInitParamName != null) && !defaultViewIdInitParamName.startsWith(DEFAULT_VIEWID_PREFIX)) {
			defaultViewIdInitParamName = DEFAULT_VIEWID_PREFIX + defaultViewIdInitParamName;
		}

		String defaultViewId = portletConfig.getInitParameter(defaultViewIdInitParamName);

		if ((defaultViewId != null) && !defaultViewId.startsWith("/")) {
			defaultViewId = "/" + defaultViewId;
		}

		return defaultViewId;
	}
}
