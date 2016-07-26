/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.util;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.util.internal.PortletResourceUtil;


/**
 * @author  Kyle Stiemann
 */
public class PortletResourceUtilTest {

	@Test
	public void testIsPortletResourceURL() {

		Assert.assertTrue(PortletResourceUtil.isPortletResourceURL("javax.faces.resource="));
		Assert.assertFalse(PortletResourceUtil.isPortletResourceURL("javax.faces.resource"));
		Assert.assertTrue(PortletResourceUtil.isPortletResourceURL(
				"http://liferay.com?javax.faces.resource=example.js&ln=example"));
		Assert.assertFalse(PortletResourceUtil.isPortletResourceURL(
				"http://liferay.com/javax.faces.resource/example.js&ln=example"));
	}
}
