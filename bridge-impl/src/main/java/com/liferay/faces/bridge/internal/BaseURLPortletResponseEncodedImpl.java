/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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

import javax.portlet.BaseURL;
import javax.portlet.PortletResponse;


/**
 * This class wraps a {@link BridgeURI} so that it can be accessed as a {@link BaseURL} which will be encoded by the
 * {@link PortletResponse} rather than the {@link com.liferay.faces.util.render.FacesURLEncoder}.
 *
 * @author  Neil Griffin
 */
public class BaseURLPortletResponseEncodedImpl extends BaseURLBridgeURIAdapterImpl {

	// Private Data Members
	private PortletResponse portletResponse;

	public BaseURLPortletResponseEncodedImpl(BridgeURI bridgeURI, PortletResponse portletResponse) {
		super(bridgeURI);
		this.portletResponse = portletResponse;
	}

	@Override
	public String toString() {
		return portletResponse.encodeURL(super.toString());
	}
}
