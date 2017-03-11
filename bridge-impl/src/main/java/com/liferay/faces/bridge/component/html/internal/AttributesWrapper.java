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
package com.liferay.faces.bridge.component.html.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesWrapper;


/**
 * @author  Neil Griffin
 */
public abstract class AttributesWrapper implements Map<String, Object>, FacesWrapper<Map<String, Object>> {

	public void clear() {
		getWrapped().clear();
	}

	public boolean containsKey(Object key) {
		return getWrapped().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return getWrapped().containsValue(value);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return getWrapped().entrySet();
	}

	public Object get(Object key) {
		return getWrapped().get(key);
	}

	public boolean isEmpty() {
		return getWrapped().isEmpty();
	}

	public Set<String> keySet() {
		return getWrapped().keySet();
	}

	public Object put(String key, Object value) {
		return getWrapped().put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> values) {
		getWrapped().putAll(values);
	}

	public Object remove(Object key) {
		return getWrapped().remove(key);
	}

	public int size() {
		return getWrapped().size();
	}

	public Collection<Object> values() {
		return getWrapped().values();
	}

}
