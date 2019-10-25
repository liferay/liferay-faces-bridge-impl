/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ class AttrImpl extends NodeImpl implements Attr {

	// Private Data Members
	private String name;
	private String value;

	public AttrImpl(String name, String value, Node parentNode) {

		super(Node.ATTRIBUTE_NODE, parentNode);
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNodeName() {
		return getName();
	}

	@Override
	public String getNodeValue() throws DOMException {
		return getValue();
	}

	@Override
	public Element getOwnerElement() {
		return (Element) getParentNode();
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getSpecified() {
		return true;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(String value) throws DOMException {
		this.value = value;
	}
}
