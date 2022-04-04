/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.servlet.http.HttpSession;

import com.liferay.faces.bridge.servlet.BridgeSessionListener;
import com.liferay.faces.util.cache.Cache;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeRequestScopeManagerImpl implements BridgeRequestScopeManager {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestScopeManagerImpl.class);

	// Private Final Data Members
	private final Cache<String, BridgeRequestScope> bridgeRequestScopeCache;

	public BridgeRequestScopeManagerImpl(Cache<String, BridgeRequestScope> bridgeRequestScopeCache) {
		this.bridgeRequestScopeCache = bridgeRequestScopeCache;
	}

	@Override
	public Cache<String, BridgeRequestScope> getBridgeRequestScopeCache() {
		return bridgeRequestScopeCache;
	}

	@Override
	public void removeBridgeRequestScopesByPortlet(PortletConfig portletConfig) {

		String portletNameToRemove = portletConfig.getPortletName();
		removeBridgeRequestScopes(true, portletNameToRemove);
	}

	/**
	 * This method is designed to be invoked from a {@link javax.servlet.http.HttpSessionListener} like {@link
	 * BridgeSessionListener} when a session timeout/expiration occurs.
	 */
	@Override
	public void removeBridgeRequestScopesBySession(HttpSession httpSession) {

		String sessionId = httpSession.getId();
		removeBridgeRequestScopes(false, sessionId);
	}

	private void removeBridgeRequestScopes(boolean removeByPortletId, String portletOrSessionId) {

		// Iterate over the map entries, and build up a list of BridgeRequestScope keys that are to be
		// removed. Doing it this way prevents ConcurrentModificationExceptions from being thrown.
		List<String> keysToRemove = new ArrayList<String>();
		Set<String> keySet = (Set<String>) bridgeRequestScopeCache.getKeys();
		String portletOrSessionIdWithSeparatorSuffix = portletOrSessionId + ":::";

		for (String bridgeRequestScopeId : keySet) {

			if (removeByPortletId) {

				if (bridgeRequestScopeId.startsWith(portletOrSessionIdWithSeparatorSuffix)) {
					keysToRemove.add(bridgeRequestScopeId);
				}
			}
			else {

				int indexOfSessionIdSection = bridgeRequestScopeId.indexOf(":::") + ":::".length();
				String idWithoutPortletNamePrefix = bridgeRequestScopeId.substring(indexOfSessionIdSection);

				if (idWithoutPortletNamePrefix.startsWith(portletOrSessionIdWithSeparatorSuffix)) {
					keysToRemove.add(bridgeRequestScopeId);
				}
			}
		}

		for (String keyToRemove : keysToRemove) {

			Object bridgeRequestScope = bridgeRequestScopeCache.removeValue(keyToRemove);

			if (!removeByPortletId) {
				logger.debug(
					"Removed bridgeRequestScopeId=[{0}] bridgeRequestScope=[{1}] from cache due to session timeout",
					keyToRemove, bridgeRequestScope);
			}
		}
	}
}
