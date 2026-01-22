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
import jakarta.portlet.faces.filter.BridgePortletRequestFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletRequestFactoryTCKCommonImpl extends BridgePortletRequestFactory {

	private BridgePortletRequestFactory wrappedBridgePortletRequestFactory;

	public BridgePortletRequestFactoryTCKCommonImpl(BridgePortletRequestFactory bridgePortletRequestFactory) {
		wrappedBridgePortletRequestFactory = bridgePortletRequestFactory;
	}

	@Override
	public ActionRequest getActionRequest(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new ActionRequestTCKCommonImpl(wrappedBridgePortletRequestFactory.getActionRequest(actionRequest,
					actionResponse, portletConfig, bridgeConfig));
	}

	@Override
	public EventRequest getEventRequest(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedBridgePortletRequestFactory.getEventRequest(eventRequest, eventResponse, portletConfig,
				bridgeConfig);
	}

	@Override
	public HeaderRequest getHeaderRequest(HeaderRequest headerRequest, HeaderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new HeaderRequestTCKCommonImpl(wrappedBridgePortletRequestFactory.getHeaderRequest(headerRequest,
					headerResponse, portletConfig, bridgeConfig));
	}

	@Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedBridgePortletRequestFactory.getRenderRequest(renderRequest, renderResponse, portletConfig,
				bridgeConfig);
	}

	@Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedBridgePortletRequestFactory.getResourceRequest(resourceRequest, resourceResponse, portletConfig,
				bridgeConfig);
	}

	@Override
	public BridgePortletRequestFactory getWrapped() {
		return wrappedBridgePortletRequestFactory;
	}
}
