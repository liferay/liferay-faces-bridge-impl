/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public abstract class BridgePortletRequestFactoryTCKCompatImpl extends BridgePortletRequestFactory {

	// Private Constants
	protected static final boolean RESIN_DETECTED = ProductFactory.getProduct(Product.Name.RESIN).isDetected();

	@Override
	public RenderRequest getRenderRequest(RenderRequest headerRequest, RenderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		headerRequest = getWrapped().getRenderRequest(headerRequest, headerResponse, portletConfig, bridgeConfig);

		if (RESIN_DETECTED) {

			// Workaround for FACES-1629
			headerRequest = new RenderRequestResinImpl(headerRequest);
		}

		return headerRequest;
	}
}
