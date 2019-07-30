/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.filter.internal;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.filter.PortletRequestWrapper;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ final class RequestParameterUtil {

	private RequestParameterUtil() {
		throw new AssertionError();
	}

	/* package-private */ static String getPortlet2_0Parameter(String parameterName, PortletRequestWrapper portletRequestWrapper) {

		PortletRequest portletRequest = portletRequestWrapper.getRequest();
		String parameterValue = portletRequest.getParameter(parameterName);

		if (parameterValue == null) {

			Map<String, String[]> parameterMap = portletRequest.getParameterMap();

			if (parameterMap.containsKey(parameterName)) {
				parameterValue = "";
			}
		}

		return parameterValue;
	}

	/* package-private */ static Map<String, String[]> getPortlet2_0ParameterMap(Map<String, String[]> parameterMap) {

		for (String[] parameterValues : parameterMap.values()) {
			RequestParameterUtil.getPortlet2_0ParameterValues(parameterValues);
		}

		return parameterMap;
	}

	/* package-private */ static String[] getPortlet2_0ParameterValues(String name, PortletRequestWrapper portletRequestWrapper) {

		PortletRequest portletRequest = portletRequestWrapper.getRequest();
		String[] parameterValues = portletRequest.getParameterValues(name);

		return getPortlet2_0ParameterValues(parameterValues);
	}

	private static String[] getPortlet2_0ParameterValues(String[] potentialPortlet3_0ParameterValues) {

		if (potentialPortlet3_0ParameterValues != null) {

			for (int i = 0; i < potentialPortlet3_0ParameterValues.length; i++) {

				if (potentialPortlet3_0ParameterValues[i] == null) {
					potentialPortlet3_0ParameterValues[i] = "";
				}
			}
		}

		return potentialPortlet3_0ParameterValues;
	}
}
