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
package com.liferay.faces.bridge.internal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.BridgePhase;
import com.liferay.faces.bridge.BridgePhaseFactory;
import com.liferay.faces.bridge.BridgeConfig;


/**
 * @author  Neil Griffin
 */
public class BridgePhaseFactoryImpl extends BridgePhaseFactory {

	@Override
	public BridgePhase getBridgeActionPhase(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		return new BridgePhaseActionImpl(actionRequest, actionResponse, portletConfig, bridgeConfig);
	}

	@Override
	public BridgePhase getBridgeEventPhase(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new BridgePhaseEventImpl(eventRequest, eventResponse, portletConfig, bridgeConfig);
	}

	@Override
	public BridgePhase getBridgeRenderPhase(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new BridgePhaseRenderImpl(renderRequest, renderResponse, portletConfig, bridgeConfig);
	}

	@Override
	public BridgePhase getBridgeResourcePhase(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new BridgePhaseResourceImpl(resourceRequest, resourceResponse, portletConfig, bridgeConfig);
	}

	public BridgePhaseFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
