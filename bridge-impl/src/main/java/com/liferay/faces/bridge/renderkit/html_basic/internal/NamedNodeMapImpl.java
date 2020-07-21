/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ class NamedNodeMapImpl extends LinkedHashMap<String, Attr> implements NamedNodeMap {

	// serialVersionUID
	private static final long serialVersionUID = 8229596543405771392L;

	private Iterator<Attr> iterator;
	private int iteratorIndex = 0;

	@Override
	public int getLength() {

		if (iterator == null) {

			Collection<Attr> values = values();
			iterator = values.iterator();
			iteratorIndex = 0;
		}

		return size();
	}

	@Override
	public Node getNamedItem(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node item(int index) {

		if (index < 0) {
			throw new IndexOutOfBoundsException("Invalid negative index: " + index);
		}

		if (index != iteratorIndex) {
			throw new UnsupportedOperationException("Random access is not supported. Please iterate in order.");
		}

		if ((index == 0) && (iterator == null)) {

			Collection<Attr> values = values();
			iterator = values.iterator();
		}

		if (!iterator.hasNext()) {
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		}

		Attr node = iterator.next();

		if (iterator.hasNext()) {
			iteratorIndex++;
		}
		else {

			iterator = null;
			iteratorIndex = 0;
		}

		return node;
	}

	@Override
	public Node removeNamedItem(String name) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node setNamedItem(Node arg) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new UnsupportedOperationException();
	}
}
