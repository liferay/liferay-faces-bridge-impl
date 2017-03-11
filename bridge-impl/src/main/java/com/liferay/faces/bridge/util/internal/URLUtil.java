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
package com.liferay.faces.bridge.util.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class URLUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(URLUtil.class);

	/**
	 * Escapes XML characters so that a URL can be safely written to the response. This method's functionality was
	 * copied from {@link com.liferay.portal.util.HtmlImpl#escape(java.lang.String)}
	 * (https://github.com/liferay/liferay-portal/blob/7.0.1-ga2/portal-impl/src/com/liferay/portal/util/HtmlImpl.java#L112-L120).
	 * This method follows recommendations from
	 * http://www.owasp.org/index.php/Cross_Site_Scripting#How_to_Protect_Yourself.
	 */
	public static String escapeXML(String url) {

		// "&" must be replaced before the others to avoid double escaping them.
		char[] tokens = new char[] { '&', '<', '>', '"', '\'', '\u00bb', '\u2013', '\u2014', '\u2028' };
		String[] replacements = new String[] {
				"&amp;", "&lt;", "&gt;", "&#034;", "&#039;", "&#187;", "&#x2013;", "&#x2014;", "&#x2028;"
			};

		for (int i = 0; i < tokens.length; i++) {

			String token = Character.toString(tokens[i]);
			url = url.replace(token, replacements[i]);
		}

		return url;
	}

	public static Map<String, String[]> parseParameterMapValuesArray(String url) {
		Map<String, String[]> parameterMapValuesArray = new LinkedHashMap<String, String[]>();

		if (url != null) {
			int pos = url.indexOf("?");

			if (pos > 0) {
				String queryString = url.substring(pos + 1);
				queryString = queryString.replaceAll("&amp;", "&");

				if ((queryString != null) && (queryString.length() > 0)) {

					pos = queryString.indexOf("#");

					if (pos > 0) {
						queryString = queryString.substring(0, pos);
					}

					String[] queryParameters = queryString.split("[&]");

					for (String queryParameter : queryParameters) {

						String[] nameValueArray = queryParameter.split("[=]");

						String name = nameValueArray[0].trim();
						String[] existingValues = parameterMapValuesArray.get(name);

						if (nameValueArray.length == 1) {

							String[] newValues;

							if (existingValues == null) {
								newValues = new String[] { "" };
							}
							else {
								newValues = Arrays.copyOf(existingValues, existingValues.length + 1);
								newValues[existingValues.length] = "";
							}

							parameterMapValuesArray.put(name, newValues);
						}
						else if (nameValueArray.length == 2) {

							if (name.length() == 0) {
								logger.error("Invalid name=value pair=[{0}] in URL=[{1}]: name cannot be empty",
									nameValueArray, url);
							}
							else {

								String[] newValues;

								if (existingValues == null) {
									newValues = new String[] { nameValueArray[1] };
								}
								else {
									newValues = Arrays.copyOf(existingValues, existingValues.length + 1);
									newValues[existingValues.length] = nameValueArray[1];
								}

								parameterMapValuesArray.put(name, newValues);
							}
						}
						else {
							logger.error("Invalid name=value pair=[{0}] in URL=[{1}]", nameValueArray, url);
						}
					}
				}
			}
		}

		return parameterMapValuesArray;
	}

	public static Map<String, List<String>> parseParameterMapValuesList(String url) {

		Map<String, List<String>> parameterMapValuesList = new LinkedHashMap<String, List<String>>();
		Map<String, String[]> parameterMapValuesArray = parseParameterMapValuesArray(url);
		Set<Entry<String, String[]>> entrySet = parameterMapValuesArray.entrySet();

		for (Map.Entry<String, String[]> mapEntry : entrySet) {
			String[] mapEntryValues = mapEntry.getValue();
			List<String> values = new ArrayList<String>(mapEntryValues.length);

			for (int i = 0; i < mapEntryValues.length; i++) {
				values.add(mapEntryValues[i]);
			}

			parameterMapValuesList.put(mapEntry.getKey(), values);
		}

		return parameterMapValuesList;
	}
}
