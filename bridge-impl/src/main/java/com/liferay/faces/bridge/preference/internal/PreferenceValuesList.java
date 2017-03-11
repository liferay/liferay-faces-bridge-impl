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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.portlet.PortletPreferences;


/**
 * @author  Neil Griffin
 */
public class PreferenceValuesList implements List<String> {

	private PortletPreferences portletPreferences;
	private String name;

	public PreferenceValuesList(PortletPreferences portletPreferences, String name) {
		this.portletPreferences = portletPreferences;
		this.name = name;
	}

	public boolean add(String value) {
		throw new UnsupportedOperationException();
	}

	public void add(int index, String value) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends String> values) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends String> values) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object value) {

		if (value != null) {
			String[] values = portletPreferences.getValues(name, null);

			if (values != null) {

				for (String currentValue : values) {

					if (value.equals(currentValue)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean containsAll(Collection<?> values) {
		boolean allFound = true;

		if (values != null) {
			Collection<String> stringCollection = (Collection<String>) values;

			for (String value : stringCollection) {
				allFound = (allFound && contains(value));

				if (!allFound) {
					break;
				}
			}
		}

		return allFound;
	}

	@Override
	public boolean equals(Object obj) {

		boolean flag = false;

		if ((obj != null) && (obj instanceof List<?>)) {

			List<?> objList = (List<?>) obj;

			if (objList.size() == this.size()) {
				flag = true;

				int index = 0;

				for (Object listEntry : objList) {

					if (listEntry instanceof String) {
						String listEntryAsString = (String) listEntry;
						String thisEntry = this.get(index);

						if (thisEntry.equals(listEntryAsString)) {
							index++;
						}
						else {
							flag = false;

							break;
						}
					}
					else {
						flag = false;

						break;
					}
				}
			}
		}

		return flag;
	}

	public String get(int index) {
		String value = null;
		String[] values = portletPreferences.getValues(name, null);

		if ((values != null) && (values.length > index)) {
			value = values[index];
		}

		return value;
	}

	public int indexOf(Object value) {
		int index = 0;
		String[] values = portletPreferences.getValues(name, null);

		if ((values != null)) {

			for (int i = 0; i < values.length; i++) {
				String currentValue = values[i];

				if ((currentValue != null) && (currentValue.equals(value))) {
					index = i;

					break;
				}
			}
		}

		return index;
	}

	public boolean isEmpty() {
		String[] values = portletPreferences.getValues(name, null);

		return ((values == null) || (values.length == 0));
	}

	public Iterator<String> iterator() {
		return new PreferenceValueIterator(portletPreferences.getNames());
	}

	public int lastIndexOf(Object value) {
		throw new UnsupportedOperationException();
	}

	public ListIterator<String> listIterator() {
		throw new UnsupportedOperationException();
	}

	public ListIterator<String> listIterator(int arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public String remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	public String set(int arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		int size = 0;
		String[] values = portletPreferences.getValues(name, null);

		if (values != null) {
			size = values.length;
		}

		return size;
	}

	public List<String> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public Object[] toArray() {
		return portletPreferences.getValues(name, null);
	}

	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}
}
