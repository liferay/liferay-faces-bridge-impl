/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.factories.filter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.filter.BridgePortletResponseFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletResponseFactoryTCKImpl extends BridgePortletResponseFactory {

	// Private Data Members
	private BridgePortletResponseFactory wrappedFactory;

	public BridgePortletResponseFactoryTCKImpl(BridgePortletResponseFactory bridgePortletResponseFactory) {
		this.wrappedFactory = bridgePortletResponseFactory;
	}

	@Override
	public ActionResponse getActionResponse(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new ActionResponseTCKImpl(wrappedFactory.getActionResponse(actionRequest, actionResponse, portletConfig,
					bridgeConfig));
	}

	@Override
	public EventResponse getEventResponse(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new EventResponseTCKImpl(wrappedFactory.getEventResponse(eventRequest, eventResponse, portletConfig,
					bridgeConfig));
	}

	@Override
	public HeaderResponse getHeaderResponse(HeaderRequest headerRequest, HeaderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new HeaderResponseTCKImpl(wrappedFactory.getHeaderResponse(headerRequest, headerResponse, portletConfig,
					bridgeConfig), portletConfig.getPortletName());
	}

	@Override
	public RenderResponse getRenderResponse(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new RenderResponseTCKImpl(wrappedFactory.getRenderResponse(renderRequest, renderResponse, portletConfig,
					bridgeConfig));
	}

	@Override
	public ResourceResponse getResourceResponse(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new ResourceResponseTCKImpl(wrappedFactory.getResourceResponse(resourceRequest, resourceResponse,
					portletConfig, bridgeConfig));
	}

	@Override
	public BridgePortletResponseFactory getWrapped() {
		return wrappedFactory;
	}
}
