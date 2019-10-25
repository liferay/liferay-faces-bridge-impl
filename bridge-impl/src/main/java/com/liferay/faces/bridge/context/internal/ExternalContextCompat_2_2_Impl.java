/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.ClientWindow;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.servlet.http.Cookie;

import com.liferay.faces.util.config.ApplicationConfig;


/**
 * This class provides a compatibility layer that isolates differences between JSF 2.1 and JSF 2.2.
 *
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_2_2_Impl extends ExternalContextCompat_2_1_Impl {

	// Private Constants
	private static final String COOKIE_PROPERTY_HTTP_ONLY = "httpOnly";

	// Private Data Members
	private String applicationContextPath;
	private ClientWindow clientWindow;

	public ExternalContextCompat_2_2_Impl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {
		super(portletContext, portletRequest, portletResponse);
	}

	/**
	 * @see    ExternalContext#getApplicationContextPath()
	 * @since  JSF 2.2
	 */
	@Override
	public String getApplicationContextPath() {

		if (applicationContextPath == null) {
			String appConfigAttrName = ApplicationConfig.class.getName();
			ApplicationConfig applicationConfig = (ApplicationConfig) getApplicationMap().get(appConfigAttrName);
			applicationContextPath = applicationConfig.getContextPath();
		}

		return applicationContextPath;
	}

	/**
	 * @see    ExternalContext#getClientWindow()
	 * @since  JSF 2.2
	 */
	@Override
	public ClientWindow getClientWindow() {
		return clientWindow;
	}

	/**
	 * @since  JSF 2.2
	 */
	@Override
	public String getSessionId(boolean create) {

		String sessionId = null;

		PortletSession portletSession = (PortletSession) getSession(create);

		if ((portletSession == null) && (!create)) {

			// JSF 2.2 requires the empty string to be returned in this case.
			sessionId = "";
		}
		else {
			sessionId = portletSession.getId();
		}

		return sessionId;
	}

	/**
	 * @see    ExternalContext#setClientWindow(ClientWindow)
	 * @since  JSF 2.2
	 */
	@Override
	public void setClientWindow(ClientWindow clientWindow) {
		this.clientWindow = clientWindow;
	}

	@Override
	protected Cookie createCookie(String name, String value, Map<String, Object> properties) {

		Cookie cookie = super.createCookie(name, value, properties);

		if ((properties != null) && !properties.isEmpty()) {

			Boolean httpOnly = (Boolean) properties.get(COOKIE_PROPERTY_HTTP_ONLY);

			if (httpOnly != null) {
				cookie.setHttpOnly(httpOnly);
			}

		}

		return cookie;
	}
}
