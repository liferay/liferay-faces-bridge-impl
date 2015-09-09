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
package com.liferay.faces.bridge.context.url;

import java.net.MalformedURLException;
import java.util.HashMap;

import javax.portlet.BaseURL;

import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.url.internal.BaseURLNonEncodedStringImpl;
import com.liferay.faces.bridge.context.url.internal.BridgeURLInternalBase;


/**
 * @author  Neil Griffin
 */
public class BridgeURLMockImpl extends BridgeURLInternalBase {

	// Private Data Members
	private BridgeURI bridgeURI;

	public BridgeURLMockImpl(BridgeContext bridgeContext, BridgeURI bridgeURI, String viewId) {
		super(bridgeContext, bridgeURI, viewId);
		this.bridgeURI = bridgeURI;
	}

	@Override
	public BaseURL toBaseURL() throws MalformedURLException {
		return new BaseURLNonEncodedStringImpl(bridgeURI.toString(), new HashMap<String, String[]>());
	}

}
