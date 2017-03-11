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

import java.util.Map;


/**
 * @author  Neil Griffin
 */
public class MessageAttributes extends AttributesWrapper {

	// Private Data Members
	private Map<String, Object> wrappedAttributes;

	public MessageAttributes(Map<String, Object> attributes) {
		this.wrappedAttributes = attributes;
	}

	@Override
	public Object get(Object key) {

		Object value = super.get(key);

		if (key != null) {

			String styleClass = (String) super.get("styleClass");

			if ("errorClass".equals(key) || "fatalClass".equals(key)) {
				value = getClassAttributeValue(value, "portlet-msg-error", styleClass);
			}
			else if ("infoClass".equals(key)) {
				value = getClassAttributeValue(value, "portlet-msg-info", styleClass);
			}
			else if ("warnClass".equals(key)) {
				value = getClassAttributeValue(value, "portlet-msg-warn", styleClass);
			}
		}

		return value;
	}

	public Map<String, Object> getWrapped() {
		return wrappedAttributes;
	}

	protected String getClassAttributeValue(Object value, String defaultValue, String styleClass) {

		String classAttributeValue = null;

		if (value == null) {

			if (styleClass == null) {
				classAttributeValue = defaultValue;
			}
			else {
				classAttributeValue = defaultValue.concat(" ").concat(styleClass);
			}
		}
		else {

			String valueAsString = value.toString();

			if (valueAsString.contains(defaultValue)) {
				classAttributeValue = valueAsString;
			}
			else {
				classAttributeValue = defaultValue.concat(" ").concat(valueAsString);
			}
		}

		return classAttributeValue;
	}
}
