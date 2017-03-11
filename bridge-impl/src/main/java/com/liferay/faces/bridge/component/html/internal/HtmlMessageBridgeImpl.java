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

import javax.faces.component.html.HtmlMessage;


/**
 * @author  Neil Griffin
 */
public class HtmlMessageBridgeImpl extends HtmlMessage {

	// Private Data Members
	private Map<String, Object> attributes;

	@Override
	public Map<String, Object> getAttributes() {

		if (attributes == null) {
			attributes = new MessageAttributes(super.getAttributes());
		}

		return attributes;
	}
}
