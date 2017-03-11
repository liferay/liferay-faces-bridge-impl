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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.faces.Bridge;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class provides a {@link java.util.Map} style interface for managing cache of {@link BridgeRequestScope}.
 *
 * @author  Neil Griffin
 */
public class BridgeRequestScopeCacheImpl extends ConcurrentHashMap<String, BridgeRequestScope>
	implements BridgeRequestScopeCache {

	// serialVersionUID
	private static final long serialVersionUID = 4546189667853367660L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestScopeCacheImpl.class);

	// Private Data Members
	private int maxSize;

	public BridgeRequestScopeCacheImpl(int maxSize) {
		super();
		this.maxSize = maxSize;
	}

	@Override
	public BridgeRequestScope put(String bridgeRequestScopeId, BridgeRequestScope bridgeRequestScope) {

		// If there is a maximum size threshold for the cache, then
		if (maxSize != -1) {

			// If the threshold has been exceeded, then remove the eldest entry.
			if (super.size() > maxSize) {

				// Note: Iterating through the entire map is not the most performant algorithm for determining the
				// eldest entry, but there don't seem to be any good options for implementing an LRU feature for
				// ConcurrentHashMap without an external dependency. See:
				// http://stackoverflow.com/questions/221525/how-would-you-implement-an-lru-cache-in-java-6
				BridgeRequestScope eldestBridgeRequestScope = null;
				long oldestDate = bridgeRequestScope.getDateCreated();
				Set<Map.Entry<String, BridgeRequestScope>> entrySet = super.entrySet();

				for (Map.Entry<String, BridgeRequestScope> mapEntry : entrySet) {
					BridgeRequestScope currentBridgeRequestScope = mapEntry.getValue();

					if (currentBridgeRequestScope.getDateCreated() < oldestDate) {
						eldestBridgeRequestScope = currentBridgeRequestScope;
					}
				}

				if (eldestBridgeRequestScope != null) {
					String eldestBridgeRequestScopeId = eldestBridgeRequestScope.getId();
					super.remove(eldestBridgeRequestScopeId);
					logger.debug("Exceeded threshold of [{0}] for [{1}], removed eldest bridgeRequestScope id=[{2}]",
						maxSize, Bridge.MAX_MANAGED_REQUEST_SCOPES, eldestBridgeRequestScopeId);
				}
			}

		}

		return super.put(bridgeRequestScopeId, bridgeRequestScope);
	}
}
