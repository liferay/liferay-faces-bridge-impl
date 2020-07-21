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
package com.liferay.faces.bridge.scope.internal;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.bridge.internal.PortletConfigEmptyImpl;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.util.cache.Cache;
import com.liferay.faces.util.cache.CacheFactory;
import com.liferay.faces.util.lang.ThreadSafeAccessor;


/**
 * @author  Neil Griffin
 */
public class BridgeRequestScopeManagerFactoryImpl extends BridgeRequestScopeManagerFactory {

	// Private Final Data Members
	private final BridgeRequestScopeManagerAccessor bridgeRequestScopeManagerAccessor =
		new BridgeRequestScopeManagerAccessor();

	@Override
	public BridgeRequestScopeManager getBridgeRequestScopeManager(PortletContext portletContext) {
		return bridgeRequestScopeManagerAccessor.get(portletContext);
	}

	public BridgeRequestScopeManagerFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}

	private static final class BridgeRequestScopeManagerAccessor
		extends ThreadSafeAccessor<BridgeRequestScopeManagerImpl, PortletContext> {

		@Override
		protected BridgeRequestScopeManagerImpl computeValue(PortletContext portletContext) {

			CacheFactory cacheFactory = (CacheFactory) BridgeFactoryFinder.getFactory(portletContext,
					CacheFactory.class);

			PortletConfig emptyPortletConfig = new PortletConfigEmptyImpl(portletContext);
			int initialCacheCapacity = PortletConfigParam.BridgeRequestScopeInitialCacheCapacity.getIntegerValue(
					emptyPortletConfig);
			int maxCacheCapacity = PortletConfigParam.BridgeRequestScopeMaxCacheCapacity.getIntegerValue(
					emptyPortletConfig);
			Cache<String, BridgeRequestScope> bridgeRequestScopeCache;

			if (maxCacheCapacity > -1) {
				bridgeRequestScopeCache = cacheFactory.getConcurrentLRUCache(initialCacheCapacity, maxCacheCapacity);
			}
			else {
				bridgeRequestScopeCache = cacheFactory.getConcurrentCache(initialCacheCapacity);
			}

			return new BridgeRequestScopeManagerImpl(bridgeRequestScopeCache);
		}
	}
}
