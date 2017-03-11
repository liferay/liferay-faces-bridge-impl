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

import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.faces.preference.Preference;


/**
 * @author  Neil Griffin
 */
public class PreferenceImpl implements Preference {

	private String name;
	private PortletPreferences portletPreferences;
	private PreferenceValuesList preferenceValuesList;

	public PreferenceImpl(PortletPreferences portletPreferences, String name) {
		this.portletPreferences = portletPreferences;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return portletPreferences.getValue(name, null);
	}

	public List<String> getValues() {

		if (preferenceValuesList == null) {
			preferenceValuesList = new PreferenceValuesList(portletPreferences, name);
		}

		return preferenceValuesList;
	}

	public boolean isReadOnly() {
		return portletPreferences.isReadOnly(name);
	}

	public void reset() throws ReadOnlyException {
		portletPreferences.reset(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) throws ReadOnlyException {
		portletPreferences.setValue(name, value);
	}

	public void setValues(String[] values) throws ReadOnlyException {
		portletPreferences.setValues(name, values);
	}
}
