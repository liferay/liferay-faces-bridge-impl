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
package com.liferay.faces.bridge.preference.internal;

import java.util.Enumeration;

import javax.portlet.PortletPreferences;
import javax.portlet.faces.preference.Preference;

import com.liferay.faces.util.map.AbstractPropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class MutablePreferenceMap extends AbstractPropertyMap<Preference> {

	private PortletPreferences portletPreferences;

	public MutablePreferenceMap(PortletPreferences portletPreferences) {
		this.portletPreferences = portletPreferences;
	}

	@Override
	protected AbstractPropertyMapEntry<Preference> createPropertyMapEntry(String name) {
		Preference preference = new PreferenceImpl(portletPreferences, name);

		return new MutablePreferenceMapEntry(preference, name);
	}

	@Override
	protected Preference getProperty(String name) {
		Preference preference = new PreferenceImpl(portletPreferences, name);

		return preference;
	}

	@Override
	protected Enumeration<String> getPropertyNames() {
		return portletPreferences.getNames();
	}

	@Override
	protected void removeProperty(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setProperty(String name, Preference value) {
		throw new UnsupportedOperationException();
	}
}
