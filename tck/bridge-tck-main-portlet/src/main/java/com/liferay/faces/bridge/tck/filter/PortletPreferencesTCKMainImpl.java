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
package com.liferay.faces.bridge.tck.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;


/**
 * This class exists simply to make sure that the chain-of-delegation pattern is working for factories. See JSF_ELTest
 * for more details.
 *
 * @author  Neil Griffin
 */
public class PortletPreferencesTCKMainImpl extends PortletPreferencesWrapper {

	private Map<String, String[]> map;

	public PortletPreferencesTCKMainImpl(PortletPreferences portletPreferences) {
		super(portletPreferences);
	}

	@Override
	public Map<String, String[]> getMap() {

		if (map == null) {
			map = new HashMap<>(super.getMap());

			map.put("portletPreferencesTCKMainImpl", new String[] { "true" });
		}

		return map;
	}

	@Override
	public Enumeration<String> getNames() {
		Set<String> nameSet = new HashSet<>();
		Enumeration<String> names = super.getNames();

		while (names.hasMoreElements()) {
			nameSet.add(names.nextElement());
		}

		nameSet.add("portletPreferencesTCKMainImpl");

		return Collections.enumeration(nameSet);
	}

	@Override
	public String getValue(String key, String defaultValue) {

		if (key.equals("portletPreferencesTCKMainImpl")) {
			return "true";
		}

		return super.getValue(key, defaultValue);
	}

	@Override
	public String[] getValues(String key, String[] defaultValues) {

		if (key.equals("portletPreferencesTCKMainImpl")) {
			return new String[] { "true" };
		}

		return super.getValues(key, defaultValues);
	}
}
