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
package com.liferay.faces.bridge.filter.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.portlet.PortletRequest;
import javax.portlet.filter.PortletRequestWrapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Provides a way to decorate a {@link PortletRequest} as an {@link HttpServletRequest}. The methods signatures that are
 * unique to {@link HttpServletRequest} throw {@link UnsupportedOperationException} since they are never called during
 * the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more information,
 * see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class PortletRequestHttpServletAdapter extends PortletRequestWrapper implements HttpServletRequest {

	// Private Data Members
	private String characterEncoding;

	public PortletRequestHttpServletAdapter(PortletRequest portletRequest, String characterEncoding) {
		super(portletRequest);
		this.characterEncoding = characterEncoding;
	}

	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public int getContentLength() {
		throw new UnsupportedOperationException();
	}

	public String getContentType() {
		throw new UnsupportedOperationException();
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

	public int getIntHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public String getLocalAddr() {
		throw new UnsupportedOperationException();
	}

	public String getLocalName() {
		throw new UnsupportedOperationException();
	}

	public int getLocalPort() {
		throw new UnsupportedOperationException();
	}

	public String getMethod() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
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

	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	public String getRequestURI() {
		throw new UnsupportedOperationException();
	}

	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();
	}

	public ServletContext getServletContext() {
		throw new UnsupportedOperationException();
	}

	public String getServletPath() {
		throw new UnsupportedOperationException();
	}

	public HttpSession getSession() {
		return new PortletSessionHttpAdapter(super.getPortletSession());
	}

	public HttpSession getSession(boolean create) {
		return new PortletSessionHttpAdapter(super.getPortletSession(create));
	}

	public boolean isAsyncStarted() {
		throw new UnsupportedOperationException();
	}

	public boolean isAsyncSupported() {
		throw new UnsupportedOperationException();
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

	public void login(String username, String password) throws ServletException {
		throw new UnsupportedOperationException();
	}

	public void logout() throws ServletException {
		throw new UnsupportedOperationException();
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		throw new UnsupportedOperationException();
	}
}
