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

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author  Kyle Stiemann
 */
public class NodeListImpl extends ArrayList<Node> implements NodeList {

	// serialVersionUID
	private static final long serialVersionUID = 5559419753981665137L;

	@Override
	public int getLength() {
		return size();
	}

	@Override
	public Node item(int index) {
		return get(index);
	}
}
