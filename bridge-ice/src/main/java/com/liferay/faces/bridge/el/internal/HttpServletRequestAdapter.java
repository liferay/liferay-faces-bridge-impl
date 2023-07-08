/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.el.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.portlet.filter.PortletRequestWrapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class provides an {@link HttpServletRequest} adapter/wrapper around the current {@link PortletRequest}. Typical
 * usage is to hack-around Servlet-API dependencies in JSF implementations.
 *
 * @author  Neil Griffin
 */

public class HttpServletRequestAdapter extends PortletRequestWrapper implements HttpServletRequest {

	// Private Data Members
	private PortletRequest wrappedPortletRequest;

	public HttpServletRequestAdapter(PortletRequest portletRequest) {

		super(portletRequest);
		this.wrappedPortletRequest = portletRequest;
	}

	@Override
	public Object getAttribute(String name) {
		return getWrapped().getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return getWrapped().getAttributeNames();
	}

	@Override
	public String getAuthType() {
		return getWrapped().getAuthType();
	}

	public String getCharacterEncoding() {

		PortletRequest portletRequest = getWrapped();

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getCharacterEncoding();
		}
		else {

			// Required for JSF 1.2 but JSF 2.0 is able to simply throw an UnsupportedOperationException
			FacesContext facesContext = FacesContext.getCurrentInstance();

			return facesContext.getExternalContext().getRequestCharacterEncoding();
		}
	}

	public int getContentLength() {

		PortletRequest portletRequest = getWrapped();

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getContentLength();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public String getContentType() {

		PortletRequest portletRequest = getWrapped();

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getContentType();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String getContextPath() {
		return getWrapped().getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return getWrapped().getCookies();
	}

	public long getDateHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public String getHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public Enumeration<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	public Enumeration<String> getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	public int getIntHeader(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String getLocalAddr() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		return getWrapped().getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return getWrapped().getLocales();
	}

	public String getLocalName() {
		return getWrapped().getLocale().getDisplayName();
	}

	public int getLocalPort() {
		return getWrapped().getServerPort();
	}

	public String getMethod() {

		PortletRequest portletRequest = getWrapped();

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getMethod();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String getParameter(String name) {
		return getWrapped().getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return getWrapped().getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return getWrapped().getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return getWrapped().getParameterValues(name);
	}

	public String getPathInfo() {
		throw new UnsupportedOperationException();
	}

	public String getPathTranslated() {
		throw new UnsupportedOperationException();
	}

	public String getProtocol() {
		throw new UnsupportedOperationException();
	}

	public String getQueryString() {
		throw new UnsupportedOperationException();
	}

	public BufferedReader getReader() throws IOException {

		PortletRequest portletRequest = getWrapped();

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getReader();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public String getRealPath(String path) {
		throw new UnsupportedOperationException();
	}

	public String getRemoteAddr() {
		throw new UnsupportedOperationException();
	}

	public String getRemoteHost() {
		throw new UnsupportedOperationException();
	}

	public int getRemotePort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteUser() {
		return getWrapped().getRemoteUser();
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestedSessionId() {
		return getWrapped().getRequestedSessionId();
	}

	public String getRequestURI() {
		throw new UnsupportedOperationException();
	}

	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScheme() {
		return getWrapped().getScheme();
	}

	@Override
	public String getServerName() {
		return getWrapped().getServerName();
	}

	@Override
	public int getServerPort() {
		return getWrapped().getServerPort();
	}

	public String getServletPath() {
		throw new UnsupportedOperationException();
	}

	public HttpSession getSession() {
		throw new UnsupportedOperationException();
	}

	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Principal getUserPrincipal() {
		return getWrapped().getUserPrincipal();
	}

	public PortletRequest getWrapped() {
		return wrappedPortletRequest;
	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();
	}

	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return getWrapped().isRequestedSessionIdValid();
	}

	@Override
	public boolean isSecure() {
		return getWrapped().isSecure();
	}

	@Override
	public boolean isUserInRole(String role) {
		return getWrapped().isUserInRole(role);
	}

	@Override
	public void removeAttribute(String name) {
		getWrapped().removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		getWrapped().setAttribute(name, value);
	}

	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
		PortletRequest portletRequest = getWrapped();

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			clientDataRequest.setCharacterEncoding(enc);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}
}
