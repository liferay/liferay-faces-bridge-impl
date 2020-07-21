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

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 * @author  Kyle Stiemann
 */
public class ElementTest {

	@Test
	public void testOverwriteElementAttribute() {

		Element element = new ElementImpl("script", null);
		element.setAttribute("id", "my_id");

		NamedNodeMap attributes = element.getAttributes();
		Assert.assertEquals("my_id", attributes.item(0).getNodeValue());
		element.setAttribute("id", "my_id_again");
		Assert.assertEquals(1, attributes.getLength());
		Assert.assertEquals("my_id_again", attributes.item(0).getNodeValue());
	}
}
