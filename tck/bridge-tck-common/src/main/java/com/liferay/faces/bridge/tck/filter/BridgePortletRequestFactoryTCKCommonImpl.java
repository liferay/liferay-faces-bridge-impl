/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;


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
	public RenderRequest getRenderRequest(RenderRequest headerRequest, RenderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new RenderRequestTCKCommonImpl(wrappedBridgePortletRequestFactory.getRenderRequest(headerRequest,
					headerResponse, portletConfig, bridgeConfig));
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
