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
package com.liferay.faces.bridge.tck.filter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.PortletRequestWrapper;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletRequestFactoryTCKImpl extends BridgePortletRequestFactory {

	// Private Constants
	private static final boolean RESIN_DETECTED = ProductFactory.getProduct(Product.Name.RESIN).isDetected();

	// Private Data Members
	private BridgePortletRequestFactory wrappedBridgePortletRequestFactory;

	public BridgePortletRequestFactoryTCKImpl(BridgePortletRequestFactory bridgePortletRequestFactory) {
		this.wrappedBridgePortletRequestFactory = bridgePortletRequestFactory;
	}

	@Override
	public ActionRequest getActionRequest(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getActionRequest(actionRequest, actionResponse, portletConfig, bridgeConfig);
	}

	@Override
	public EventRequest getEventRequest(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getEventRequest(eventRequest, eventResponse, portletConfig, bridgeConfig);
	}

	@Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		renderRequest = getWrapped().getRenderRequest(renderRequest, renderResponse, portletConfig, bridgeConfig);

		if (RESIN_DETECTED) {

			// Workaround for FACES-1629
			renderRequest = new RenderRequestResinImpl(renderRequest);
		}

		return renderRequest;
	}

	@Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		if (isPlutoPortletRequest(resourceRequest)) {
			resourceRequest = getWrapped().getResourceRequest(resourceRequest, resourceResponse, portletConfig,
					bridgeConfig);

			return new ResourceRequestPlutoTCKImpl(resourceRequest);
		}
		else {
			resourceRequest = getWrapped().getResourceRequest(resourceRequest, resourceResponse, portletConfig,
					bridgeConfig);
		}

		return resourceRequest;
	}

	@Override
	public BridgePortletRequestFactory getWrapped() {
		return wrappedBridgePortletRequestFactory;
	}

	/**
	 * Determines whether or not the specified {@link javax.portlet.PortletRequest} is one created by Liferay Portal. If
	 * the specified {@link javax.portlet.PortletRequest} is an instance of {@link
	 * javax.portlet.filter.PortletRequestWrapper} then it will work with the wrapped {@link
	 * javax.portlet.PortletRequest}.
	 *
	 * @param   portletRequest  The current {@link javax.portlet.PortletRequest}.
	 *
	 * @return  true if the specified portletRequest was created by Pluto.
	 */
	protected boolean isPlutoPortletRequest(PortletRequest portletRequest) {

		if (portletRequest != null) {

			while (portletRequest instanceof PortletRequestWrapper) {
				PortletRequestWrapper portletRequestWrapper = (PortletRequestWrapper) portletRequest;
				portletRequest = portletRequestWrapper.getRequest();
			}

			return portletRequest.getClass().getName().startsWith("org.apache.pluto");
		}
		else {
			return false;
		}
	}
}
