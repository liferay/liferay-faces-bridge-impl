/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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

import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.map.AbstractPropertyMap;
import com.liferay.faces.util.map.AbstractPropertyMapEntry;


/**
 * @author  Neil Griffin
 */
public class RequestParameterValuesMap extends AbstractPropertyMap<String[]> implements Map<String, String[]> {

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
	protected void removeProperty(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method returns the value of the specified parameter name according to the current portlet request.
	 */
	@Override
	protected String[] getProperty(String name) {
		return facesRequestParameterMap.get(name);
	}

	@Override
	protected void setProperty(String name, String[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Enumeration<String> getPropertyNames() {
		return Collections.enumeration(facesRequestParameterMap.keySet());
	}
}
