/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletConfig;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.filter.BridgePortletRequestFactory;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public abstract class BridgePortletRequestFactoryTCKCompatImpl extends BridgePortletRequestFactory {

	// Private Constants
	protected static final boolean RESIN_DETECTED = ProductFactory.getProduct(Product.Name.RESIN).isDetected();

	@Override
	public HeaderRequest getHeaderRequest(HeaderRequest headerRequest, HeaderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		headerRequest = getWrapped().getHeaderRequest(headerRequest, headerResponse, portletConfig, bridgeConfig);

		if (RESIN_DETECTED) {

			// Workaround for FACES-1629
			headerRequest = new HeaderRequestResinImpl(headerRequest);
		}

		return headerRequest;
	}
}
