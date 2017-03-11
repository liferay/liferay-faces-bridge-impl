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

import javax.portlet.PortletSession;

import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class SessionScopeMapEntry extends AbstractPropertyMapEntry<Object> {

	private PortletSession portletSession;
	private int scope;

	/**
	 * Constructs a new SessionMapEntry object instance.
	 *
	 * @param  portletSession  The portlet session.
	 * @param  key             The session map key name.
	 * @param  scope           The scope of the session map, which can be PortletSession.PORTLET_SCOPE or
	 *                         PortletSession.APPLICATION_SCOPE
	 */
	public SessionScopeMapEntry(PortletSession portletSession, String key, int scope) {
		super(key);
		this.portletSession = portletSession;
		this.scope = scope;
	}

	public Object getValue() {
		return portletSession.getAttribute(getKey(), scope);
	}

	public Object setValue(Object value) {
		Object oldValue = getValue();
		portletSession.setAttribute(getKey(), value, scope);

		return oldValue;
	}
}
