/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.faces.Bridge;

import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.helper.IntegerHelper;
import com.liferay.faces.util.helper.LongHelper;


/**
 * This is a utility class that provides static utility methods for getting values from {@link PortletConfig} init-param
 * values.
 *
 * @author  Neil Griffin
 */
public final class PortletConfigParamUtil {

	// Package-Private Final Data Members
	/* package-private */ static final String[] ALTERNATE_NAMES_EMPTY_ARRAY = new String[] {};

	private PortletConfigParamUtil() {
		throw new AssertionError();
	}

	public static boolean getBooleanValue(PortletConfig portletConfig, PortletConfigParam portletConfigParam) {

		boolean booleanValue = portletConfigParam.getDefaultBooleanValue();
		String name = portletConfigParam.getName();

		if (name.startsWith(Bridge.BRIDGE_PACKAGE_PREFIX)) {

			String portletName = portletConfig.getPortletName();

			if (portletName != null) {
				String namespacedContextAttributeName = Bridge.BRIDGE_PACKAGE_PREFIX + portletName + "." +
					name.substring(Bridge.BRIDGE_PACKAGE_PREFIX.length());
				PortletContext portletContext = portletConfig.getPortletContext();
				Object namespacedContextAttributeValue = portletContext.getAttribute(namespacedContextAttributeName);

				if (namespacedContextAttributeValue != null) {

					if (namespacedContextAttributeValue instanceof Boolean) {
						booleanValue = (Boolean) namespacedContextAttributeValue;
					}
					else {
						booleanValue = BooleanHelper.isTrueToken(namespacedContextAttributeValue.toString());
					}

					return booleanValue;
				}
			}
		}

		String configuredValue = getConfiguredValue(portletConfig, portletConfigParam);

		if (configuredValue != null) {
			booleanValue = BooleanHelper.isTrueToken(configuredValue);
		}

		return booleanValue;
	}

	public static String getConfiguredValue(PortletConfig portletConfig, PortletConfigParam portletConfigParam) {
		return getConfiguredValue((Object) portletConfig, portletConfigParam);
	}

	public static int getIntegerValue(PortletConfig portletConfig, PortletConfigParam portletConfigParam) {

		int integerValue = portletConfigParam.getDefaultIntegerValue();
		String configuredValue = getConfiguredValue(portletConfig, portletConfigParam);

		if (configuredValue != null) {
			integerValue = IntegerHelper.toInteger(configuredValue);
		}

		return integerValue;
	}

	public static long getLongValue(PortletConfig portletConfig, PortletConfigParam portletConfigParam) {

		long longValue = portletConfigParam.getDefaultLongValue();
		String configuredValue = getConfiguredValue(portletConfig, portletConfigParam);

		if (configuredValue != null) {
			longValue = LongHelper.toLong(configuredValue);
		}

		return longValue;
	}

	public static String getStringValue(ExternalContext externalContext, PortletConfigParam portletConfigParam) {
		return getStringValue((Object) externalContext, portletConfigParam);
	}

	public static String getStringValue(PortletConfig portletConfig, PortletConfigParam portletConfigParam) {
		return getStringValue((Object) portletConfig, portletConfigParam);
	}

	public static boolean isSpecified(PortletConfig portletConfig, PortletConfigParam portletConfigParam) {
		return (getConfiguredValue(portletConfig, portletConfigParam) != null);
	}

	/* package-private */ static Set<String> asInsertionOrderedSet(String... names) {
		return Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(names)));
	}

	/* package-private */ static String getAlternateName(String... names) {

		if (names.length > 1) {
			return names[1];
		}
		else {
			return null;
		}
	}

	private static String getConfiguredValue(Object initParamContainer, PortletConfigParam portletConfigParam) {

		ExternalContext externalContext = null;
		PortletConfig portletConfig = null;
		PortletContext portletContext = null;

		if (initParamContainer instanceof ExternalContext) {
			externalContext = (ExternalContext) initParamContainer;
		}
		else if (initParamContainer instanceof PortletConfig) {

			portletConfig = (PortletConfig) initParamContainer;
			portletContext = portletConfig.getPortletContext();
		}
		else {
			throw new UnsupportedOperationException(
				"This method only supports ExternalContext and PortletConfig init params.");
		}

		String configuredValue = null;
		Set<String> names = portletConfigParam.getNames();
		Iterator<String> iterator = names.iterator();

		while (iterator.hasNext() && (configuredValue == null)) {

			String name = iterator.next();

			if (externalContext != null) {
				configuredValue = externalContext.getInitParameter(name);
			}
			else {

				configuredValue = portletConfig.getInitParameter(name);

				if (configuredValue == null) {
					configuredValue = portletContext.getInitParameter(name);
				}
			}
		}

		return configuredValue;
	}

	private static String getStringValue(Object initParamContainer, PortletConfigParam portletConfigParam) {

		String stringValue = portletConfigParam.getDefaultStringValue();
		String configuredValue = getConfiguredValue(initParamContainer, portletConfigParam);

		if (configuredValue != null) {
			stringValue = configuredValue;
		}

		return stringValue;
	}
}
