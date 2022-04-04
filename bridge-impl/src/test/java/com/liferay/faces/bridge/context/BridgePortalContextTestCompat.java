/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context;

import javax.portlet.PortalContext;

import org.junit.Assert;


/**
 * @author  Kyle Stiemann
 */
public class BridgePortalContextTestCompat {

	protected void testBridgePortalContextAddToHeadSupport(PortalContext portalContextBridgeImpl,
		boolean markupHeadElementSupport) {

		// The Portlet 3.0 HeaderResponse.addDependency() API allows the portlet to add any valid element to the
		// <head> section, so all BridgePortalContext.ADD_*_TO_HEAD_SUPPORT properties
		// (such as ADD_ELEMENT_TO_HEAD_SUPPORT) should return "true".
		Assert.assertEquals("true",
			portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT));
		Assert.assertEquals("true",
			portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT));
		Assert.assertEquals("true",
			portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT));
		Assert.assertEquals("true",
			portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT));
		Assert.assertEquals("true",
			portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_ELEMENT_TO_HEAD_SUPPORT));
	}
}
