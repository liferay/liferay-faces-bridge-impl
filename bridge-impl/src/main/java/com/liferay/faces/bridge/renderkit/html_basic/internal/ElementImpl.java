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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;


/**
 * @author  Kyle Stiemann
 */
public class ElementImpl extends NodeImpl implements Element {

	// Private Data Members
	private NamedNodeMapImpl attributes;
	private String nodeName;

	public ElementImpl(String nodeName, Node parentNode) {

		super(Node.ELEMENT_NODE, parentNode);
		this.attributes = new NamedNodeMapImpl();
		this.nodeName = nodeName;
	}

	@Override
	public String getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Attr getAttributeNode(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamedNodeMap getAttributes() {
		return attributes;
	}

	@Override
	public NodeList getElementsByTagName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNodeName() {
		return nodeName;
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTagName() {
		return getNodeName();
	}

	@Override
	public boolean hasAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String name) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, String value) throws DOMException {
		attributes.add(new AttrImpl(name, value, this));
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIdAttribute(String name, boolean isId) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		throw new UnsupportedOperationException();
	}
}
