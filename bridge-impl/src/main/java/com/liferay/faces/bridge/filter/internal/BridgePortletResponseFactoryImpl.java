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

import javax.portlet.ActionResponse;
import javax.portlet.EventResponse;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.filter.BridgePortletResponseFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePortletResponseFactoryImpl extends BridgePortletResponseFactory {

	@Override
	public ActionResponse getActionResponse(ActionResponse actionResponse) {
		return new ActionResponseBridgeImpl(actionResponse);
	}

	@Override
	public EventResponse getEventResponse(EventResponse eventResponse) {
		return new EventResponseBridgeImpl(eventResponse);
	}

	@Override
	public RenderResponse getRenderResponse(RenderResponse renderResponse) {

		if (PortletContainerDetector.isPlutoPortletResponse(renderResponse)) {
			return new RenderResponseBridgePlutoImpl(renderResponse);
		}
		else {
			return new RenderResponseBridgeImpl(renderResponse);
		}
	}

	@Override
	public ResourceResponse getResourceResponse(ResourceResponse resourceResponse) {

		if (PortletContainerDetector.isPlutoPortletResponse(resourceResponse)) {
			return new ResourceResponseBridgePlutoImpl(resourceResponse);
		}
		else {
			return new ResourceResponseBridgeImpl(resourceResponse);
		}
	}

	// Java 1.6+ @Override
	public BridgePortletResponseFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
