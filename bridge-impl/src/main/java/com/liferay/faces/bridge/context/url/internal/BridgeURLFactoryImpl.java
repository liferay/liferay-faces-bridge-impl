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
package com.liferay.faces.bridge.context.url.internal;

import java.util.List;
import java.util.Map;

import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.url.BridgeResourceURL;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.bridge.context.url.BridgeURL;
import com.liferay.faces.bridge.context.url.BridgeURLFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLFactoryImpl extends BridgeURLFactory {

	@Override
	public BridgeURL getBridgeActionURL(BridgeContext bridgeContext, BridgeURI bridgeURI, String viewId) {
		return new BridgeActionURLImpl(bridgeContext, bridgeURI, viewId);
	}

	@Override
	public BridgeURL getBridgeBookmarkableURL(BridgeContext bridgeContext, BridgeURI bridgeURI,
		Map<String, List<String>> parameters, String viewId) {
		return new BridgeBookmarkableURLImpl(bridgeContext, bridgeURI, parameters, viewId);
	}

	@Override
	public BridgeURL getBridgePartialActionURL(BridgeContext bridgeContext, BridgeURI bridgeURI, String viewId) {
		return new BridgePartialActionURLImpl(bridgeContext, bridgeURI, viewId);
	}

	@Override
	public BridgeURL getBridgeRedirectURL(BridgeContext bridgeContext, BridgeURI bridgeURI,
		Map<String, List<String>> parameters, String viewId) {

		return new BridgeRedirectURLImpl(bridgeContext, bridgeURI, parameters, viewId);
	}

	@Override
	public BridgeResourceURL getBridgeResourceURL(BridgeContext bridgeContext, BridgeURI bridgeURI, String viewId) {
		return new BridgeResourceURLImpl(bridgeContext, bridgeURI, viewId);
	}

	public BridgeURLFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
