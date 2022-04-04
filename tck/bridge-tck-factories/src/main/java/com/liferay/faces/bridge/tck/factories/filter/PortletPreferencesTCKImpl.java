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
package com.liferay.faces.bridge.tck.factories.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;


/**
 * @author  Neil Griffin
 */
public class PortletPreferencesTCKImpl implements PortletPreferences {

	private PortletPreferences wrappedPortletPreferences;

	public PortletPreferencesTCKImpl(PortletPreferences portletPreferences) {
		this.wrappedPortletPreferences = portletPreferences;
	}

	@Override
	public Map<String, String[]> getMap() {
		return wrappedPortletPreferences.getMap();
	}

	@Override
	public Enumeration<String> getNames() {
		return wrappedPortletPreferences.getNames();
	}

	@Override
	public String getValue(String s, String s1) {
		return wrappedPortletPreferences.getValue(s, s1);
	}

	@Override
	public String[] getValues(String s, String[] strings) {
		return wrappedPortletPreferences.getValues(s, strings);
	}

	@Override
	public boolean isReadOnly(String s) {
		return wrappedPortletPreferences.isReadOnly(s);
	}

	@Override
	public void reset(String s) throws ReadOnlyException {
		wrappedPortletPreferences.reset(s);
	}

	@Override
	public void setValue(String s, String s1) throws ReadOnlyException {
		wrappedPortletPreferences.setValue(s, s1);
	}

	@Override
	public void setValues(String s, String... strings) throws ReadOnlyException {
		wrappedPortletPreferences.setValues(s, strings);
	}

	@Override
	public void store() throws IOException, ValidatorException {
		wrappedPortletPreferences.store();
	}
}
