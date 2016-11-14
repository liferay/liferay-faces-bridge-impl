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

import com.liferay.faces.bridge.application.internal.MissingResourceImpl;
import com.liferay.faces.bridge.application.internal.ResourceHandlerInnerImpl;


/**
 * @author  Kyle Stiemann
 */
public class PortletResourceURLTest {

	@Test
	public void testIsPortletResourceURL() {

		ResourceHandlerInnerImpl bridgeResourceHandler = new ResourceHandlerInnerImpl(new ResourceHandlerMockImpl());
		Assert.assertTrue(bridgeResourceHandler.isResourceURL("javax.faces.resource="));
		Assert.assertFalse(bridgeResourceHandler.isResourceURL("javax.faces.resource"));
		Assert.assertTrue(bridgeResourceHandler.isResourceURL(
				"http://liferay.com?javax.faces.resource=example.js&ln=example"));
		Assert.assertFalse(bridgeResourceHandler.isResourceURL(
				"http://liferay.com/javax.faces.resource/example.js&ln=example"));
		Assert.assertTrue(bridgeResourceHandler.isResourceURL(MissingResourceImpl.RES_NOT_FOUND));
	}
}
