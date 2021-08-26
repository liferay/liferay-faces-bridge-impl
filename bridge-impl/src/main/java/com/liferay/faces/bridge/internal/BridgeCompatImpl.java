/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeConfigFactory;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeUninitializedException;
import javax.portlet.faces.filter.BridgePortletConfigFactory;


/**
 * This class provides a compatibility layer that isolates differences related to Portlet 2.0/3.0 and helps to minimize
 * diffs across branches.
 *
 * @author  Kyle Stiemann
 */
public abstract class BridgeCompatImpl implements Bridge {

	@Override
	public void doFacesRequest(HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws BridgeDefaultViewNotSpecifiedException, BridgeUninitializedException, BridgeException {

		checkNull(headerRequest, headerResponse);

		if (isInitialized()) {
			PortletConfig wrappedPortletConfig = BridgePortletConfigFactory.getPortletConfigInstance(
					getPortletConfig());
			BridgeConfig bridgeConfig = BridgeConfigFactory.getBridgeConfigInstance(wrappedPortletConfig);
			BridgePhase bridgePhase = new BridgePhaseHeaderImpl(headerRequest, headerResponse, wrappedPortletConfig,
					bridgeConfig);
			bridgePhase.execute();
		}
		else {
			throw new BridgeUninitializedException();
		}
	}

	protected abstract void checkNull(PortletRequest portletRequest, PortletResponse portletResponse);

	protected abstract PortletConfig getPortletConfig();

	protected abstract boolean isInitialized();
}
