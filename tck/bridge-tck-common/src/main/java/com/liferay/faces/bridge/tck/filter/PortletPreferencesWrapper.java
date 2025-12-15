/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import jakarta.faces.FacesWrapper;
import javax.portlet.Portlet;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;


/**
 * This class is located in the TCK since the Portlet 3.0 API doesn't provide a PortletPreferencesWrapper.
 *
 * @author  Neil Griffin
 */
public class PortletPreferencesWrapper implements PortletPreferences, FacesWrapper<PortletPreferences> {

	private PortletPreferences portletPreferences;

	public PortletPreferencesWrapper(PortletPreferences portletPreferences) {
		this.portletPreferences = portletPreferences;
	}

	@Override
	public Map<String, String[]> getMap() {
		return portletPreferences.getMap();
	}

	@Override
	public Enumeration<String> getNames() {
		return portletPreferences.getNames();
	}

	@Override
	public String getValue(String key, String defaultValue) {
		return portletPreferences.getValue(key, defaultValue);
	}

	@Override
	public String[] getValues(String key, String[] defaultValues) {
		return portletPreferences.getValues(key, defaultValues);
	}

	@Override
	public PortletPreferences getWrapped() {
		return portletPreferences;
	}

	@Override
	public boolean isReadOnly(String key) {
		return portletPreferences.isReadOnly(key);
	}

	@Override
	public void reset(String key) throws ReadOnlyException {
		portletPreferences.reset(key);
	}

	@Override
	public void setValue(String key, String value) throws ReadOnlyException {
		portletPreferences.setValue(key, value);
	}

	@Override
	public void setValues(String key, String... values) throws ReadOnlyException {
		portletPreferences.setValues(key, values);
	}

	@Override
	public void store() throws IOException, ValidatorException {
		portletPreferences.store();
	}
}
