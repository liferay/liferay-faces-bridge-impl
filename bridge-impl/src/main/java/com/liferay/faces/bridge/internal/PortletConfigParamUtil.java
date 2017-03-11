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
package com.liferay.faces.bridge.internal;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.helper.IntegerHelper;
import com.liferay.faces.util.helper.LongHelper;


/**
 * This is a utility class that provides static utility methods for getting values from {@link PortletConfig} init-param
 * values.
 *
 * @author  Neil Griffin
 */
public class PortletConfigParamUtil {

	// Note: Performance is faster with a synchronized block around HashMap.put(String, Object) rather than using a
	// ConcurrentHashMap.
	private static final Map<String, Object> configParamCache = new HashMap<String, Object>();

	public static boolean getBooleanValue(PortletConfig portletConfig, String name, String alternateName,
		boolean defaultBooleanValue) {

		boolean booleanValue = defaultBooleanValue;

		String portletName = portletConfig.getPortletName();

		if (portletName == null) {
			String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

			if (configuredValue != null) {
				booleanValue = BooleanHelper.isTrueToken(configuredValue);
			}
		}
		else {
			String configParamName = portletName + name;
			Object cachedValue = configParamCache.get(configParamName);

			if ((cachedValue != null) && (cachedValue instanceof Boolean)) {
				booleanValue = (Boolean) cachedValue;
			}
			else {
				String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

				if (configuredValue != null) {
					booleanValue = BooleanHelper.isTrueToken(configuredValue);
				}

				synchronized (configParamCache) {
					configParamCache.put(configParamName, Boolean.valueOf(booleanValue));
				}
			}
		}

		return booleanValue;
	}

	public static String getConfiguredValue(PortletConfig portletConfig, String name, String alternateName) {

		String configuredValue = portletConfig.getInitParameter(name);

		PortletContext portletContext = null;

		if (configuredValue == null) {
			portletContext = portletConfig.getPortletContext();
			configuredValue = portletContext.getInitParameter(name);
		}

		if ((configuredValue == null) && (alternateName != null)) {
			configuredValue = portletConfig.getInitParameter(alternateName);

			if (configuredValue == null) {
				configuredValue = portletContext.getInitParameter(alternateName);
			}
		}

		return configuredValue;
	}

	public static int getIntegerValue(PortletConfig portletConfig, String name, String alternateName,
		int defaultIntegerValue) {

		int integerValue = defaultIntegerValue;

		String portletName = portletConfig.getPortletName();

		if (portletName == null) {
			String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

			if (configuredValue != null) {
				integerValue = IntegerHelper.toInteger(configuredValue);
			}
		}
		else {
			String configParamName = portletName + name;
			Object cachedValue = configParamCache.get(configParamName);

			if ((cachedValue != null) && (cachedValue instanceof Integer)) {
				integerValue = (Integer) cachedValue;
			}
			else {
				String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

				if (configuredValue != null) {
					integerValue = IntegerHelper.toInteger(configuredValue);
				}

				synchronized (configParamCache) {
					configParamCache.put(configParamName, Integer.valueOf(integerValue));
				}
			}
		}

		return integerValue;
	}

	public static long getLongValue(PortletConfig portletConfig, String name, String alternateName,
		long defaultLongValue) {

		long longValue = defaultLongValue;

		String portletName = portletConfig.getPortletName();

		if (portletName == null) {
			String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

			if (configuredValue != null) {
				longValue = LongHelper.toLong(configuredValue);
			}
		}
		else {
			String configParamName = portletName + name;
			Object cachedValue = configParamCache.get(configParamName);

			if ((cachedValue != null) && (cachedValue instanceof Long)) {
				longValue = (Long) cachedValue;
			}
			else {
				String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

				if (configuredValue != null) {
					longValue = LongHelper.toLong(configuredValue);
				}

				synchronized (configParamCache) {
					configParamCache.put(configParamName, Long.valueOf(longValue));
				}
			}
		}

		return longValue;
	}

	public static String getStringValue(PortletConfig portletConfig, String name, String alternateName,
		String defaultStringValue) {

		String stringValue = defaultStringValue;

		String portletName = portletConfig.getPortletName();

		if (portletName == null) {
			String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

			if (configuredValue != null) {
				stringValue = configuredValue;
			}
		}
		else {
			String configParamName = portletName + name;
			Object cachedValue = configParamCache.get(configParamName);

			if ((cachedValue != null) && (cachedValue instanceof String)) {
				stringValue = (String) cachedValue;
			}
			else {
				String configuredValue = getConfiguredValue(portletConfig, name, alternateName);

				if (configuredValue != null) {
					stringValue = configuredValue;
				}

				synchronized (configParamCache) {
					configParamCache.put(configParamName, stringValue);
				}
			}
		}

		return stringValue;
	}

	public static boolean isSpecified(PortletConfig portletConfig, String name, String alternateName) {
		return (getConfiguredValue(portletConfig, name, alternateName) != null);
	}
}
