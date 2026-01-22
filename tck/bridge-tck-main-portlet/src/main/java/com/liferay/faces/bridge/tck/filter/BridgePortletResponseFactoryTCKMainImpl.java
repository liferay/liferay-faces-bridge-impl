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
package com.liferay.faces.bridge.tck.filter;

import java.io.Serializable;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.faces.BridgeConfig;
import jakarta.portlet.faces.filter.BridgePortletResponseFactory;

import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2.DependencyTrackingHeaderResponse;


/**
 * @author  Kyle Stiemann
 */
public class BridgePortletResponseFactoryTCKMainImpl extends BridgePortletResponseFactory implements Serializable {

	// Serial Version UID
	private static final long serialVersionUID = 2184921901586098823L;

	// Private Constants
	private static final String RESOURCES_RENDERED_IN_HEAD_TEST = "resourcesRenderedInHeadTest";

	// Private Data Members
	private BridgePortletResponseFactory wrappedBridgePortletResponseFactory;

	public BridgePortletResponseFactoryTCKMainImpl(BridgePortletResponseFactory wrappedBridgePortletResponseFactory) {
		this.wrappedBridgePortletResponseFactory = wrappedBridgePortletResponseFactory;
	}

	@Override
	public ActionResponse getActionResponse(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new ActionResponseTCKMainImpl(wrappedBridgePortletResponseFactory.getActionResponse(actionRequest,
					actionResponse, portletConfig, bridgeConfig));
	}

	@Override
	public EventResponse getEventResponse(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedBridgePortletResponseFactory.getEventResponse(eventRequest, eventResponse, portletConfig,
				bridgeConfig);
	}

	@Override
	public HeaderResponse getHeaderResponse(HeaderRequest headerRequest, HeaderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		HeaderResponse returnHeaderResponse = wrappedBridgePortletResponseFactory.getHeaderResponse(headerRequest,
				headerResponse, portletConfig, bridgeConfig);
		String testName = (String) headerRequest.getAttribute(Constants.TEST_NAME);

		if (RESOURCES_RENDERED_IN_HEAD_TEST.equals(testName)) {
			returnHeaderResponse = new DependencyTrackingHeaderResponse(returnHeaderResponse);
		}
		else {
			returnHeaderResponse = new HeaderResponseTCKMainImpl(returnHeaderResponse);
		}

		return returnHeaderResponse;
	}

	@Override
	public RenderResponse getRenderResponse(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedBridgePortletResponseFactory.getRenderResponse(renderRequest, renderResponse, portletConfig,
				bridgeConfig);
	}

	@Override
	public ResourceResponse getResourceResponse(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedBridgePortletResponseFactory.getResourceResponse(resourceRequest, resourceResponse, portletConfig,
				bridgeConfig);
	}

	@Override
	public BridgePortletResponseFactory getWrapped() {
		return wrappedBridgePortletResponseFactory;
	}
}
