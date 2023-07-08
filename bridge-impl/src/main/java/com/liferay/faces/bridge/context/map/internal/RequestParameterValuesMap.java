/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * This class provides a {@link Map<String,String[]>} abstraction over request parameter values. Since it is designed to
 * exist and be used within the scope of a request, it is not thread-safe.
 *
 * @author  Neil Griffin
 */
public class RequestParameterValuesMap extends AbstractImmutablePropertyMap<String[]> implements Map<String, String[]> {

	// Private Data Members
	private FacesRequestParameterMap facesRequestParameterMap;

	public RequestParameterValuesMap(FacesRequestParameterMap facesRequestParameterMap) {
		this.facesRequestParameterMap = facesRequestParameterMap;
	}

	@Override
	public boolean containsKey(Object key) {
		return facesRequestParameterMap.containsKey(key);
	}

	@Override
	protected AbstractPropertyMapEntry<String[]> createPropertyMapEntry(String name) {
		return new RequestParameterValuesMapEntry(name, this);
	}

	@Override
	protected Enumeration<String> getImmutablePropertyNames() {

		Set<String> parameterNames = facesRequestParameterMap.keySet();

		return Collections.enumeration(parameterNames);
	}

	@Override
	protected String[] getProperty(String name) {
		return facesRequestParameterMap.get(name);
	}
}
