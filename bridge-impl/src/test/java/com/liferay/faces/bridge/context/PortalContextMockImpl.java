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
package com.liferay.faces.bridge.context;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;


/**
 * @author  Kyle Stiemann
 */
public class PortalContextMockImpl implements PortalContext {

	// Private Data Members
	private final Map<String, String> properties;

	public PortalContextMockImpl(boolean markupHeadElementSupport) {

		if (markupHeadElementSupport) {

			Map<String, String> properties = new HashMap<String, String>();
			properties.put(MARKUP_HEAD_ELEMENT_SUPPORT, "true");
			this.properties = Collections.unmodifiableMap(properties);
		}
		else {
			this.properties = Collections.emptyMap();
		}
	}

	@Override
	public String getPortalInfo() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.enumeration(properties.keySet());
	}

	@Override
	public Enumeration<PortletMode> getSupportedPortletModes() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<WindowState> getSupportedWindowStates() {
		throw new UnsupportedOperationException("");
	}
}
