/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
import org.junit.Test;

import com.liferay.faces.bridge.context.internal.PortalContextBridgeImpl;


/**
 * @author  Kyle Stiemann
 */
public class BridgePortalContextAjaxRequestTest {

	@Test
	public void testBridgePortalContextAjaxRequest() {

		boolean markupHeadElementSupport = true;
		boolean ajaxRequest = true;
		PortalContext portalContextBridgeImpl = new PortalContextBridgeImpl(new PortletRequestMockImpl(
					markupHeadElementSupport, ajaxRequest));
		testBridgePortalContextAjaxRequest(portalContextBridgeImpl);

		markupHeadElementSupport = false;
		portalContextBridgeImpl = new PortalContextBridgeImpl(new PortletRequestMockImpl(markupHeadElementSupport,
					ajaxRequest));
		testBridgePortalContextAjaxRequest(portalContextBridgeImpl);
	}

	private void testBridgePortalContextAjaxRequest(PortalContext portalContextBridgeImpl) {

		// During a Faces ajax request no elements can be added to the <head> section via the portlet API, so all
		// *_HEAD_*_SUPPORT properties should return null.
		Assert.assertNull(portalContextBridgeImpl.getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT));
		Assert.assertNull(portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT));
		Assert.assertNull(portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT));
		Assert.assertNull(portalContextBridgeImpl.getProperty(
				BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT));
		Assert.assertNull(portalContextBridgeImpl.getProperty(
				BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT));
		Assert.assertNull(portalContextBridgeImpl.getProperty(BridgePortalContext.ADD_ELEMENT_TO_HEAD_SUPPORT));
	}
}
