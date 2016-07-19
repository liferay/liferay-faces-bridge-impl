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
 * This class is part of a workaround for <a href="https://issues.liferay.com/browse/FACES-2638">FACES_2638</a> that
 * provides the renderer for rich:fileUpload with a ServletContext instead of a PortletContext.
 *
 * @author  Neil Griffin
 */
public class ServletContextRichFacesImpl implements ServletContext {

	// Private Data Members
	private PortletContext portletContext;

	public ServletContextRichFacesImpl(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	// Java 1.6+ @Override
	public Object getAttribute(String name) {
		return portletContext.getAttribute(name);
	}

	// Java 1.6+ @Override
	public Enumeration<String> getAttributeNames() {
		return portletContext.getAttributeNames();
	}

	// Java 1.6+ @Override
	public ServletContext getContext(String uripath) {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getContextPath() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getInitParameter(String name) {
		return portletContext.getInitParameter(name);
	}

	// Java 1.6+ @Override
	public Enumeration<String> getInitParameterNames() {
		return portletContext.getInitParameterNames();
	}

	// Java 1.6+ @Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getMimeType(String file) {
		return portletContext.getMimeType(file);
	}

	// Java 1.6+ @Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public RequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getRealPath(String path) {
		return portletContext.getRealPath(path);
	}

	// Java 1.6+ @Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public URL getResource(String path) throws MalformedURLException {
		return portletContext.getResource(path);
	}

	// Java 1.6+ @Override
	public InputStream getResourceAsStream(String path) {
		return portletContext.getResourceAsStream(path);
	}

	// Java 1.6+ @Override
	public Set<String> getResourcePaths(String path) {
		return portletContext.getResourcePaths(path);
	}

	// Java 1.6+ @Override
	public String getServerInfo() {
		return portletContext.getServerInfo();
	}

	// Java 1.6+ @Override
	public Servlet getServlet(String name) throws ServletException {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getServletContextName() {
		return portletContext.getPortletContextName();
	}

	// Java 1.6+ @Override
	public Enumeration<String> getServletNames() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public Enumeration<Servlet> getServlets() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public void log(String msg) {
		portletContext.log(msg);
	}

	// Java 1.6+ @Override
	public void log(Exception exception, String message) {
		portletContext.log(message, exception);
	}

	// Java 1.6+ @Override
	public void log(String message, Throwable throwable) {
		portletContext.log(message, throwable);
	}

	// Java 1.6+ @Override
	public void removeAttribute(String name) {
		portletContext.removeAttribute(name);
	}

	// Java 1.6+ @Override
	public void setAttribute(String name, Object object) {
		portletContext.setAttribute(name, object);
	}
}
