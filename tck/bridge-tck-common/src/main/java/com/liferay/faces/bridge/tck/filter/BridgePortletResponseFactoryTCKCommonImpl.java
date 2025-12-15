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


/**
 * @author  Neil Griffin
 */
public class BridgePortletResponseFactoryTCKCommonImpl extends BridgePortletResponseFactory {

	private BridgePortletResponseFactory wrappedBridgePortletResponseFactory;

	public BridgePortletResponseFactoryTCKCommonImpl(BridgePortletResponseFactory bridgePortletResponseFactory) {
		this.wrappedBridgePortletResponseFactory = bridgePortletResponseFactory;
	}

	@Override
	public ActionResponse getActionResponse(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new ActionResponseTCKCommonImpl(wrappedBridgePortletResponseFactory.getActionResponse(actionRequest,
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
		return new HeaderResponseTCKCommonImpl(wrappedBridgePortletResponseFactory.getHeaderResponse(headerRequest,
					headerResponse, portletConfig, bridgeConfig));
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
