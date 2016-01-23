/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.util.Map;
import java.util.Set;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class RequestHeaderMap extends CaseInsensitiveHashMap<String> {

	// serialVersionUID
	private static final long serialVersionUID = 7916822183626170352L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(RequestHeaderMap.class);

	public RequestHeaderMap(Map<String, String[]> requestHeaderValuesMap) {
		Set<Map.Entry<String, String[]>> entrySet = requestHeaderValuesMap.entrySet();

		if (entrySet != null) {

			for (Map.Entry<String, String[]> mapEntry : entrySet) {
				String key = mapEntry.getKey();
				String[] value = mapEntry.getValue();

				if ((value != null) && (value.length > 0)) {
					put(key, value[0]);
					logger.debug("Adding {0}=[{1}] to header map", key, value);
				}
				else {
					put(key, null);
					logger.debug("Adding {0}=[null] to header map", key);
				}
			}
		}
	}

}
