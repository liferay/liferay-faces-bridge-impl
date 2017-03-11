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
package com.liferay.faces.bridge.filter.internal;

import java.util.Map;

import javax.portlet.BaseURL;


/**
 * @author  Neil Griffin
 */
public class PlutoBaseURLUtil {

	public static void removeParameter(BaseURL baseURL, String name) {

		// According to a clarification in the Portlet 3.0 JavaDoc for BaseURL#setProperty(String,String), setting
		// the parameter to null will remove it. However, Pluto 2.0 throws an IllegalArgumentException when
		// attempting to set a value of null. The only alternative is to replace the entire parameter map after
		// having removed the parameter with the specified name.
		Map<String, String[]> parameterMap = baseURL.getParameterMap();

		if (parameterMap.containsKey(name)) {
			parameterMap.remove(name);
			baseURL.setParameters(parameterMap);
		}
	}
}
