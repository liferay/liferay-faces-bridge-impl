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
public class BridgePortletRequestFactoryTCKImpl extends BridgePortletRequestFactory {

	// Private Data Members
	private BridgePortletRequestFactory wrappedFactory;

	public BridgePortletRequestFactoryTCKImpl(BridgePortletRequestFactory bridgePortletRequestFactory) {
		this.wrappedFactory = bridgePortletRequestFactory;
	}

	@Override
	public ActionRequest getActionRequest(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedFactory.getActionRequest(actionRequest, actionResponse, portletConfig, bridgeConfig);
	}

	@Override
	public EventRequest getEventRequest(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return wrappedFactory.getEventRequest(eventRequest, eventResponse, portletConfig, bridgeConfig);
	}

	@Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		return new RenderRequestTCKImpl(wrappedFactory.getRenderRequest(renderRequest, renderResponse, portletConfig,
					bridgeConfig), portletConfig.getPortletName());
	}

	@Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new ResourceRequestTCKImpl(wrappedFactory.getResourceRequest(resourceRequest, resourceResponse,
					portletConfig, bridgeConfig));
	}

	@Override
	public BridgePortletRequestFactory getWrapped() {
		return wrappedFactory;
	}
}
