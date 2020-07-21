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
package com.liferay.faces.bridge.context;

import javax.portlet.PortalContext;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.faces.bridge.context.internal.PortalContextBridgeImpl;


/**
 * @author  Kyle Stiemann
 */
public class BridgePortalContextTest extends BridgePortalContextTestCompat {

	@Test
	public void testBridgePortalContext() {

		// PortalContextBridgeImpl should delegate to the portlet container to determine if the optional
		// MARKUP_HEAD_ELEMENT feature is supported.
		boolean markupHeadElementSupport = true;
		PortalContext portalContextBridgeImpl = new PortalContextBridgeImpl(new PortletRequestMockImpl(
					markupHeadElementSupport));
		Assert.assertEquals("true", portalContextBridgeImpl.getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT));
		testBridgePortalContextAddToHeadSupport(portalContextBridgeImpl, markupHeadElementSupport);

		// PortalContextBridgeImpl should delegate to the portlet container to determine if the optional
		// MARKUP_HEAD_ELEMENT feature is supported.
		markupHeadElementSupport = false;
		portalContextBridgeImpl = new PortalContextBridgeImpl(new PortletRequestMockImpl(markupHeadElementSupport));
		Assert.assertNull(portalContextBridgeImpl.getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT));
		testBridgePortalContextAddToHeadSupport(portalContextBridgeImpl, markupHeadElementSupport);
	}
}
