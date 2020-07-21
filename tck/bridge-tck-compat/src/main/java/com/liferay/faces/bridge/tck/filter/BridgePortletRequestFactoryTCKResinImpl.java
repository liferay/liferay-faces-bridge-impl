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
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletRequestFactoryTCKResinImpl extends BridgePortletRequestFactoryTCKCompatImpl {

	// Private Data Members
	private BridgePortletRequestFactory wrappedBridgePortletRequestFactory;

	public BridgePortletRequestFactoryTCKResinImpl(BridgePortletRequestFactory bridgePortletRequestFactory) {
		this.wrappedBridgePortletRequestFactory = bridgePortletRequestFactory;
	}

	// Java 1.6+ @Override
	public ActionRequest getActionRequest(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getActionRequest(actionRequest, actionResponse, portletConfig, bridgeConfig);
	}

	// Java 1.6+ @Override
	public EventRequest getEventRequest(EventRequest eventRequest, EventResponse eventResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getEventRequest(eventRequest, eventResponse, portletConfig, bridgeConfig);
	}

	// Java 1.6+ @Override
	public RenderRequest getRenderRequest(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		renderRequest = getWrapped().getRenderRequest(renderRequest, renderResponse, portletConfig, bridgeConfig);

		if (isResinDetected(portletConfig)) {

			// Workaround for FACES-1629
			renderRequest = new RenderRequestResinImpl(renderRequest);
		}

		return renderRequest;
	}

	// Java 1.6+ @Override
	public ResourceRequest getResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return getWrapped().getResourceRequest(resourceRequest, resourceResponse, portletConfig, bridgeConfig);
	}

	// Java 1.6+ @Override
	public BridgePortletRequestFactory getWrapped() {
		return wrappedBridgePortletRequestFactory;
	}

	@Override
	protected boolean isResinDetected(PortletConfig portletConfig) {

		PortletContext portletContext = portletConfig.getPortletContext();
		ProductFactory productFactory = (ProductFactory) BridgeFactoryFinder.getFactory(portletContext,
				ProductFactory.class);
		final Product RESIN = productFactory.getProductInfo(Product.Name.RESIN);

		return RESIN.isDetected();
	}
}
