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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;

import com.icesoft.faces.webapp.http.portlet.PortletExternalContext;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.config.BridgeConfigImpl;
import com.liferay.faces.bridge.context.BridgeContext;


/**
 * @author  Neil Griffin
 */
public class BridgeContextImpl extends BridgeContext {

	// Private Data Members
	private BridgeConfig bridgeConfig;
	private PortletConfig portletConfig;

	public BridgeContextImpl() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		PortletExternalContext portletExternalContext = (PortletExternalContext) facesContext.getExternalContext();
		this.portletConfig = (PortletConfig) portletExternalContext.getConfig();
	}

	@Override
	public BridgeConfig getBridgeConfig() {

		if (bridgeConfig == null) {
			bridgeConfig = new BridgeConfigImpl();
		}

		return bridgeConfig;
	}

	@Override
	public PortletConfig getPortletConfig() {
		return portletConfig;
	}
}
