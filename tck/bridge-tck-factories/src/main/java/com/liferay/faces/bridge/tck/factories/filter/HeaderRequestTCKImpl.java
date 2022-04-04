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
package com.liferay.faces.bridge.tck.factories.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.portlet.HeaderRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.WindowState;
import javax.portlet.filter.HeaderRequestWrapper;
import javax.servlet.http.Cookie;


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
	public Cookie[] getCookies() {

		Cookie[] cookies = super.getCookies();

		if (portletName.contains("cookiesAlternativeTest")) {

			List<Cookie> cookieList = Collections.emptyList();

			if ((cookies != null) && (cookies.length > 0)) {
				cookieList = new ArrayList(Arrays.asList(cookies));
				cookieList.add(new CookieTCKImpl("foo", "1234"));
			}

			cookies = cookieList.toArray(new Cookie[0]);
		}

		return cookies;
	}

	@Override
	public Enumeration<Locale> getLocales() {

		if (portletName.contains("localesAlternativeTest")) {
			ArrayList<Locale> locales = new ArrayList<>(Collections.list(super.getLocales()));
			locales.add(new Locale("en_BW", "BWA"));

			return Collections.enumeration(locales);
		}

		return super.getLocales();
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

	@Override
	public WindowState getWindowState() {

		WindowState windowState = super.getWindowState();

		if (portletName.contains("windowStateAlternativeTest")) {
			return new WindowStateTCKFooImpl();
		}

		return windowState;
	}

	@Override
	public boolean isSecure() {

		if (portletName.contains("isSecureTest")) {
			return true;
		}

		return super.isSecure();
	}
}
