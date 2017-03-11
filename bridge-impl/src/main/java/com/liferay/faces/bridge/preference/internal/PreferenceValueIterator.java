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
package com.liferay.faces.bridge.preference.internal;

import java.util.Enumeration;
import java.util.Iterator;


/**
 * @author  Neil Griffin
 */
public class PreferenceValueIterator implements Iterator<String> {

	private Enumeration<String> preferenceValues;

	public PreferenceValueIterator(Enumeration<String> preferenceValues) {
		this.preferenceValues = preferenceValues;
	}

	public boolean hasNext() {
		return preferenceValues.hasMoreElements();
	}

	public String next() {
		return preferenceValues.nextElement();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
