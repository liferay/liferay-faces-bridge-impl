/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.scope.internal;

import javax.portlet.PortletContext;
import javax.portlet.faces.Bridge;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeRequestScopeCacheFactoryImpl extends BridgeRequestScopeCacheFactory {

	// Private Constants
	private static final int DEFAULT_MAX_MANAGED_REQUEST_SCOPES = -1; // Unlimited
	private static final String ATTR_BRIDGE_REQUEST_SCOPE_CACHE = "com.liferay.faces.bridge.bridgeRequestScopeCache";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestScopeCacheFactoryImpl.class);

	@Override
	public BridgeRequestScopeCache getBridgeRequestScopeCache(PortletContext portletContext) {

		BridgeRequestScopeCache bridgeRequestScopeCache = null;

		synchronized (portletContext) {
			bridgeRequestScopeCache = (BridgeRequestScopeCache) portletContext.getAttribute(
					ATTR_BRIDGE_REQUEST_SCOPE_CACHE);

			if (bridgeRequestScopeCache == null) {

				// Spec Section 3.2: Support for configuration of maximum number of bridge request scopes.
				int maxSize = DEFAULT_MAX_MANAGED_REQUEST_SCOPES;
				String maxManagedRequestScopes = portletContext.getInitParameter(Bridge.MAX_MANAGED_REQUEST_SCOPES);

				if (maxManagedRequestScopes != null) {

					try {
						maxSize = Integer.parseInt(maxManagedRequestScopes);
					}
					catch (NumberFormatException e) {
						logger.error("Unable to parse portlet.xml init-param name=[{0}] error=[{1}]",
							Bridge.MAX_MANAGED_REQUEST_SCOPES, e.getMessage());
					}
				}

				bridgeRequestScopeCache = new BridgeRequestScopeCacheImpl(maxSize);

				portletContext.setAttribute(ATTR_BRIDGE_REQUEST_SCOPE_CACHE, bridgeRequestScopeCache);
			}
		}

		return bridgeRequestScopeCache;
	}

	public BridgeRequestScopeCacheFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
