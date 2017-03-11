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
package com.liferay.faces.bridge.context.map.internal;

import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class RequestParameterValuesMapEntry extends AbstractPropertyMapEntry<String[]> {

	// Private Data Members
	RequestParameterValuesMap requestParameterValuesMap;

	public RequestParameterValuesMapEntry(String key, RequestParameterValuesMap requestParameterValuesMap) {
		super(key);
		this.requestParameterValuesMap = requestParameterValuesMap;
	}

	public String[] getValue() {
		return requestParameterValuesMap.getProperty(getKey());
	}

	public String[] setValue(String[] value) {
		throw new UnsupportedOperationException();
	}
}
