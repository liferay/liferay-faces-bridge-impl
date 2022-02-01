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
package com.liferay.faces.bridge.tck.factories.filter;

import javax.portlet.HeaderRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.filter.HeaderRequestWrapper;


/**
 * @author  Neil Griffin
 */
public class HeaderRequestTCKImpl extends HeaderRequestWrapper {

	private String portletName;

	public HeaderRequestTCKImpl(HeaderRequest headerRequest, String portletName) {
		super(headerRequest);

		this.portletName = portletName;
	}

	@Override
	public PortletMode getPortletMode() {
		PortletMode portletMode = super.getPortletMode();

		if (PortletMode.VIEW.equals(portletMode)) {
			return new PortletModeTCKViewImpl();
		}

		return portletMode;
	}

	@Override
	public PortletSession getPortletSession() {
		return new PortletSessionTCKImpl(super.getPortletSession());
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		return new PortletSessionTCKImpl(super.getPortletSession(create));
	}

	@Override
	public PortletPreferences getPreferences() {
		return new PortletPreferencesTCKImpl(super.getPreferences());
	}

	@Override
	public RenderParameters getRenderParameters() {
		return new RenderParametersTCKImpl(super.getRenderParameters());
	}

	@Override
	public String getWindowID() {

		if (portletName.contains("windowIdAlternativeTest")) {
			return "tckWindowId";
		}

		return super.getWindowID();
	}
}
