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
package com.liferay.faces.bridge.tck.filter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.filter.BridgePortletResponseFactory;


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
	public RenderResponse getRenderResponse(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new RenderResponseTCKCommonImpl(wrappedBridgePortletResponseFactory.getRenderResponse(renderRequest,
					renderResponse, portletConfig, bridgeConfig));
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
