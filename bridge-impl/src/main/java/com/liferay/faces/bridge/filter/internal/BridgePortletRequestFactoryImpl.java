/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
import javax.portlet.EventRequest;
import javax.portlet.PortalContext;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.internal.BridgePortalContextImpl;
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletRequestFactoryImpl extends BridgePortletRequestFactory {

	@Override
	public ActionRequest getActionRequest(ActionRequest actionRequest) {

		PortalContext portalContext = actionRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new ActionRequestBridgeImpl(actionRequest, bridgePortalContext);
	}

	@Override
	public EventRequest getEventRequest(EventRequest eventRequest) {

		PortalContext portalContext = eventRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new EventRequestBridgeImpl(eventRequest, bridgePortalContext);
	}

	@Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest) {

		PortalContext portalContext = renderRequest.getPortalContext();
		BridgePortalContext bridgePortalContext = new BridgePortalContextImpl(portalContext);

		return new RenderRequestBridgeImpl(renderRequest, bridgePortalContext);
	}

	@Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest) {

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
