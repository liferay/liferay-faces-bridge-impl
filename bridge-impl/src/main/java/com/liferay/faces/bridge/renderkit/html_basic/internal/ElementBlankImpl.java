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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

/**
 * This implementation is a special case that is meant to be used when JSF component renderers do not properly call
 * startElement() first. It represents a pseudo-element that has has a blank (empty string) node name.
 *
 * @author  Neil Griffin
 */
public class ElementBlankImpl extends ElementImpl {

	public ElementBlankImpl() {
		super("");
	}

	@Override
	public String toString() {
		return getTextContent();
	}

}
