/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.filter.internal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.internal.BridgePortalContextImpl;
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletRequestFactoryImpl extends BridgePortletRequestFactory {

	@Override
	public ActionRequest getActionRequest(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = actionRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new ActionRequestBridgeImpl(actionRequest, bridgePortalContext);
	}

	@Override
	public EventRequest getEventRequest(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = eventRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new EventRequestBridgeImpl(eventRequest, bridgePortalContext);
	}

	@Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = renderRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new RenderRequestBridgeImpl(renderRequest, bridgePortalContext);
	}

	@Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = resourceRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new ResourceRequestBridgeImpl(resourceRequest, bridgePortalContext);
	}

	@Override
	public BridgePortletRequestFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
