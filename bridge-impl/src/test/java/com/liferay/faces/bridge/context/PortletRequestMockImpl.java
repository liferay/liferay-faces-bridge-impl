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
package com.liferay.faces.bridge.context;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;


/**
 * @author  Kyle Stiemann
 */
public class PortletRequestMockImpl extends PortletRequestMockCompatImpl implements PortletRequest {

	// Private Data Members
	private final Map<String, String[]> parameters;
	private final PortalContext portalContext;

	public PortletRequestMockImpl(boolean markupHeadElementSupport) {
		this(markupHeadElementSupport, false);
	}

	public PortletRequestMockImpl(boolean markupHeadElementSupport, boolean ajaxRequest) {

		this.portalContext = new PortalContextMockImpl(markupHeadElementSupport);
		this.parameters = initParameters(ajaxRequest);
	}

	@Override
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getAuthType() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getContextPath() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Cookie[] getCookies() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getParameter(String name) {

		String parameterValue = null;
		String[] parameterValues = getParameterValues(name);

		if ((parameterValues != null) && (parameterValues.length > 0)) {
			parameterValue = parameterValues[0];
		}

		return parameterValue;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return parameters;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return parameters.get(name);
	}

	@Override
	public PortalContext getPortalContext() {
		return portalContext;
	}

	@Override
	public PortletMode getPortletMode() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public PortletSession getPortletSession() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public PortletPreferences getPreferences() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Map<String, String[]> getPrivateParameterMap() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<String> getProperties(String name) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getProperty(String name) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Map<String, String[]> getPublicParameterMap() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getResponseContentType() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getScheme() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getServerName() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public int getServerPort() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getWindowID() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public WindowState getWindowState() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode mode) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean isUserInRole(String role) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean isWindowStateAllowed(WindowState state) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void removeAttribute(String name) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void setAttribute(String name, Object o) {
		throw new UnsupportedOperationException("");
	}
}
