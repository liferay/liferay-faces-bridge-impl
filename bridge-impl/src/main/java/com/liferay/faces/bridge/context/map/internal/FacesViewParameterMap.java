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
package com.liferay.faces.bridge.context.map.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.internal.PortletURLHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class represents a {@link Map} of URL parameters that are found in the query-string of a Faces view.
 *
 * @author  Neil Griffin
 */
public class FacesViewParameterMap extends HashMap<String, String> implements Map<String, String> {

	// serialVersionUID
	private static final long serialVersionUID = 3213871316191406286L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FacesViewParameterMap.class);

	public FacesViewParameterMap(String facesViewQueryString) {

		if ((facesViewQueryString != null) && (facesViewQueryString.length() > 0)) {

			String[] queryParameters = facesViewQueryString.split("[&]");

			for (String queryParameter : queryParameters) {
				String[] nameValueArray = queryParameter.split("[=]");

				if (nameValueArray != null) {

					if (nameValueArray.length == 1) {

						String parameterName = nameValueArray[0].trim();

						if (parameterName.length() == 0) {
							logger.error(
								"Invalid name=value pair=[{0}] in facesViewQueryString=[{1}]: name cannot be empty",
								nameValueArray, facesViewQueryString);
						}
						else {
							put(parameterName, "");
						}
					}
					else if (nameValueArray.length == 2) {

						String parameterName = nameValueArray[0].trim();

						if (parameterName.length() == 0) {
							logger.error(
								"Invalid name=value pair=[{0}] in facesViewQueryString=[{1}]: name cannot be empty",
								nameValueArray, facesViewQueryString);
						}
						else {

							String parameterValue = nameValueArray[1];

							if (PortletURLHelper.EXCLUDED_PARAMETER_NAMES.contains(parameterName)) {
								logger.debug("Excluding parameterName=[{0}]", parameterName);
							}
							else {

								logger.debug("Adding parameterName=[{0}] parameterValue=[{1}]", parameterName,
									parameterValue);
								put(parameterName, parameterValue);
							}
						}
					}
					else {
						logger.error("Invalid name=value pair=[{0}] in facesViewQueryString=[{1}]", nameValueArray,
							facesViewQueryString);
					}
				}
			}
		}
	}

}
