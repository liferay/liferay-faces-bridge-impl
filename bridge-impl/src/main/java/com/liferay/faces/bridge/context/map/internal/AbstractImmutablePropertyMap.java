/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import com.liferay.faces.util.map.AbstractPropertyMap;


/**
 * This class extends {@link AbstractPropertyMap} in order to provide a non-thread-safe performance optimization for the
 * {@link #containsKey(Object)} and {@link #getPropertyNames()} methods. The {@link #removeProperty(String)} and {@link
 * #setProperty(String, Object)} methods throw {@link UnsupportedOperationException} in order to enforce immutability.
 *
 * @author  Neil Griffin
 */
public abstract class AbstractImmutablePropertyMap<V> extends AbstractPropertyMap<V> {

	// Private Data Members
	private Set<String> propertyNames;

	@Override
	public boolean containsKey(Object key) {

		initPropertyNames();

		return propertyNames.contains(key);
	}

	protected abstract Enumeration<String> getImmutablePropertyNames();

	@Override
	protected Enumeration<String> getPropertyNames() {

		initPropertyNames();

		return Collections.enumeration(propertyNames);
	}

	@Override
	protected void removeProperty(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setProperty(String name, V value) {
		throw new UnsupportedOperationException();
	}

	private void initPropertyNames() {

		if (propertyNames == null) {
			Enumeration<String> attributeNames = getImmutablePropertyNames();
			ArrayList<String> attributeNameList = Collections.list(attributeNames);
			propertyNames = new LinkedHashSet<String>(attributeNameList);
		}
	}
}
