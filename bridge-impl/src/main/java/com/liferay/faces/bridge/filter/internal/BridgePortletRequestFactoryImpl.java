/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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

import java.io.Serializable;

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
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.filter.BridgePortletRequestFactory;

import com.liferay.faces.bridge.context.internal.PortalContextBridgeImpl;


/**
 * @author  Neil Griffin
 */
public class BridgePortletRequestFactoryImpl extends BridgePortletRequestFactoryCompatImpl implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 1165668363125044029L;

	@Override
	public ActionRequest getActionRequest(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = new PortalContextBridgeImpl(actionRequest);

		return new ActionRequestBridgeImpl(actionRequest, portalContext);
	}

	@Override
	public EventRequest getEventRequest(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = new PortalContextBridgeImpl(eventRequest);

		return new EventRequestBridgeImpl(eventRequest, portalContext);
	}

	@Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = new PortalContextBridgeImpl(renderRequest);

		return new RenderRequestBridgeImpl(renderRequest, portalContext);
	}

	@Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		PortalContext portalContext = new PortalContextBridgeImpl(resourceRequest);

		return new ResourceRequestBridgeImpl(resourceRequest, portalContext);
	}

	@Override
	public BridgePortletRequestFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
