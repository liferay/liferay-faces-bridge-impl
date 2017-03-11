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
package com.liferay.faces.bridge.context.map.internal;

import javax.portlet.PortletContext;

import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class ApplicationScopeMapEntry extends AbstractPropertyMapEntry<Object> {

	private PortletContext portletContext;

	public ApplicationScopeMapEntry(PortletContext portletContext, String key) {
		super(key);
		this.portletContext = portletContext;
	}

	public Object getValue() {
		return portletContext.getAttribute(getKey());
	}

	public Object setValue(Object value) {
		Object oldValue = getValue();
		portletContext.setAttribute(getKey(), value);

		return oldValue;
	}
}
