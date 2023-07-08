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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import com.liferay.faces.util.map.AbstractPropertyMap;


/**
 * This class extends {@link AbstractPropertyMap} in order to provide a non-thread-safe performance optimization for the
 * {@link #containsKey(Object)} and {@link #getPropertyNames()} methods.
 *
 * @author  Neil Griffin
 */
public abstract class AbstractMutablePropertyMap<V> extends AbstractPropertyMap<V> {

	// Private Data Members
	private Set<String> propertyNames;

	@Override
	public boolean containsKey(Object key) {

		initPropertyNames();

		return propertyNames.contains(key);
	}

	protected abstract V getMutableProperty(String name);

	protected abstract Enumeration<String> getMutablePropertyNames();

	protected abstract void removeMutableProperty(String name);

	protected abstract void setMutableProperty(String name, V value);

	@Override
	protected V getProperty(String name) {
		return getMutableProperty(name);
	}

	@Override
	protected Enumeration<String> getPropertyNames() {

		initPropertyNames();

		return Collections.enumeration(propertyNames);
	}

	@Override
	protected void removeProperty(String name) {

		if (propertyNames != null) {
			propertyNames.remove(name);
		}

		removeMutableProperty(name);
	}

	@Override
	protected void setProperty(String name, V value) {

		if (propertyNames != null) {

			if (value == null) {

				// Cause the set of property names to get re-initialized since setting a value to null might cause the
				// property to be removed from the underlying enumeration.
				propertyNames = null;
			}
			else {

				if (!propertyNames.contains(name)) {
					propertyNames.add(name);
				}
			}
		}

		setMutableProperty(name, value);
	}

	private void initPropertyNames() {

		if (propertyNames == null) {
			Enumeration<String> attributeNames = getMutablePropertyNames();
			ArrayList<String> attributeNameList = Collections.list(attributeNames);
			propertyNames = new LinkedHashSet<String>(attributeNameList);
		}
	}
}
