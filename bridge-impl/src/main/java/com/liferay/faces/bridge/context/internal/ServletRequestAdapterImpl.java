/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;


/**
 * @author  Neil Griffin
 */
public class ServletRequestAdapterImpl implements ServletRequest {

	// Private Data Members
	private PortletRequest portletRequest;

	public ServletRequestAdapterImpl(PortletRequest portletRequest) {
		this.portletRequest = portletRequest;
	}

	public Object getAttribute(String name) {
		return portletRequest.getAttribute(name);
	}

	public Enumeration<?> getAttributeNames() {
		return portletRequest.getAttributeNames();
	}

	public String getCharacterEncoding() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getCharacterEncoding();
		}
		else {
			return null;
		}
	}

	public int getContentLength() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getContentLength();
		}
		else {
			return 0;
		}
	}

	public String getContentType() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getContentType();
		}
		else {
			return null;
		}
	}

	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	public String getLocalAddr() {
		return null;
	}

	public Locale getLocale() {
		return portletRequest.getLocale();
	}

	public Enumeration<?> getLocales() {
		return portletRequest.getLocales();
	}

	public String getLocalName() {
		return portletRequest.getLocale().toString();
	}

	public int getLocalPort() {
		return 0;
	}

	public String getParameter(String name) {
		return portletRequest.getParameter(name);
	}

	public Map<?, ?> getParameterMap() {
		return portletRequest.getParameterMap();
	}

	public Enumeration<?> getParameterNames() {
		return portletRequest.getParameterNames();
	}

	public String[] getParameterValues(String name) {
		return portletRequest.getParameterValues(name);
	}

	public String getProtocol() {
		return null;
	}

	public BufferedReader getReader() throws IOException {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getReader();
		}
		else {
			return null;
		}
	}

	public String getRealPath(String path) {
		return null;
	}

	public String getRemoteAddr() {
		return null;
	}

	public String getRemoteHost() {
		return null;
	}

	public int getRemotePort() {
		return 0;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	public String getScheme() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getScheme();
		}
		else {
			return null;
		}
	}

	public String getServerName() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getServerName();
		}
		else {
			return null;
		}
	}

	public int getServerPort() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest.getServerPort();
		}
		else {
			return 0;
		}
	}

	public boolean isSecure() {
		return portletRequest.isSecure();
	}

	public void removeAttribute(String name) {
		portletRequest.removeAttribute(name);
	}

	public void setAttribute(String name, Object value) {
		portletRequest.setAttribute(name, value);
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		throw new UnsupportedOperationException();
	}
}
