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
package com.liferay.faces.bridge.tck.filter;

import javax.portlet.ActionResponse;
import javax.portlet.EventResponse;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.filter.BridgePortletResponseFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletResponseFactoryTCKImpl extends BridgePortletResponseFactory {

	// Private Data Members
	private BridgePortletResponseFactory wrappedBridgePortletResponseFactory;

	public BridgePortletResponseFactoryTCKImpl(BridgePortletResponseFactory bridgePortletResponseFactory) {
		this.wrappedBridgePortletResponseFactory = bridgePortletResponseFactory;
	}

	@Override
	public ActionResponse getActionResponse(ActionResponse actionResponse) {

		actionResponse = getWrapped().getActionResponse(actionResponse);

		return new ActionResponseTrinidadImpl(actionResponse);
	}

	@Override
	public EventResponse getEventResponse(EventResponse eventResponse) {
		return getWrapped().getEventResponse(eventResponse);
	}

	@Override
	public RenderResponse getRenderResponse(RenderResponse renderResponse) {
		return getWrapped().getRenderResponse(renderResponse);
	}

	@Override
	public ResourceResponse getResourceResponse(ResourceResponse resourceResponse) {
		return getWrapped().getResourceResponse(resourceResponse);
	}

	@Override
	public BridgePortletResponseFactory getWrapped() {
		return wrappedBridgePortletResponseFactory;
	}
}
