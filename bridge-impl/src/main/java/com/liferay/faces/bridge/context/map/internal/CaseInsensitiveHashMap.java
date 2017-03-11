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

import java.util.HashMap;


/**
 * @author  Neil Griffin
 */
public class CaseInsensitiveHashMap<V> extends HashMap<String, V> {

	// serialVersionUID
	private static final long serialVersionUID = 7989809010272499963L;

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(getLowerCaseKey(key));
	}

	@Override
	public V get(Object key) {

		return super.get(getLowerCaseKey(key));
	}

	@Override
	public V put(String key, V value) {
		return super.put(getLowerCaseKey(key), value);
	}

	protected String getLowerCaseKey(Object key) {
		String lowerCaseKey = null;

		if (key != null) {
			lowerCaseKey = key.toString().toLowerCase();
		}

		return lowerCaseKey;
	}
}
