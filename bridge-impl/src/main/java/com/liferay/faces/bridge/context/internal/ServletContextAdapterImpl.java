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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * @author  Neil Griffin
 */
public class ServletContextAdapterImpl implements ServletContext {

	private PortletContext portletContext;
	private String requestContextPath;

	public ServletContextAdapterImpl(PortletContext portletContext, String requestContextPath) {
		this.portletContext = portletContext;
		this.requestContextPath = requestContextPath;
	}

	public Object getAttribute(String name) {
		return portletContext.getAttribute(name);
	}

	public Enumeration<?> getAttributeNames() {
		return portletContext.getAttributeNames();
	}

	public ServletContext getContext(String uripath) {
		return new ServletContextAdapterImpl(portletContext, uripath);
	}

	public String getContextPath() {
		return requestContextPath;
	}

	public String getInitParameter(String name) {
		return portletContext.getInitParameter(name);
	}

	public Enumeration<?> getInitParameterNames() {
		return portletContext.getInitParameterNames();
	}

	public int getMajorVersion() {
		return 2;
	}

	public String getMimeType(String file) {
		return portletContext.getMimeType(file);
	}

	public int getMinorVersion() {
		return 5;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	public String getRealPath(String path) {
		return portletContext.getRealPath(path);
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	public URL getResource(String path) throws MalformedURLException {
		return portletContext.getResource(path);
	}

	public InputStream getResourceAsStream(String path) {
		return portletContext.getResourceAsStream(path);
	}

	public Set<?> getResourcePaths(String path) {
		return portletContext.getResourcePaths(path);
	}

	public String getServerInfo() {
		return portletContext.getServerInfo();
	}

	public Servlet getServlet(String name) throws ServletException {
		throw new UnsupportedOperationException();
	}

	public String getServletContextName() {
		return portletContext.getPortletContextName();
	}

	public Enumeration<?> getServletNames() {
		throw new UnsupportedOperationException();
	}

	public Enumeration<?> getServlets() {
		throw new UnsupportedOperationException();
	}

	public void log(String msg) {
		portletContext.log(msg);
	}

	public void log(Exception exception, String message) {
		portletContext.log(message, exception);
	}

	public void log(String message, Throwable throwable) {
		portletContext.log(message, throwable);
	}

	public void removeAttribute(String name) {
		portletContext.removeAttribute(name);
	}

	public void setAttribute(String name, Object object) {
		portletContext.setAttribute(name, object);
	}
}
