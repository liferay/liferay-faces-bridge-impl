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
package com.liferay.faces.bridge.context.internal;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.ServletContext;


/**
 * @author  Neil Griffin
 */
public class PortletContextAdapter implements PortletContext {

	// Private Data Members
	private ServletContext servletContext;

	public PortletContextAdapter(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	// Java 1.6+ @Override
	public Object getAttribute(String name) {
		return servletContext.getAttribute(name);
	}

	// Java 1.6+ @Override
	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return servletContext.getAttributeNames();
	}

	// Java 1.6+ @Override
	public Enumeration<String> getContainerRuntimeOptions() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getContextPath() {
		return servletContext.getContextPath();
	}

	// Java 1.6+ @Override
	public String getInitParameter(String name) {
		return servletContext.getInitParameter(name);
	}

	// Java 1.6+ @Override
	@SuppressWarnings("unchecked")
	public Enumeration<String> getInitParameterNames() {
		return servletContext.getInitParameterNames();
	}

	// Java 1.6+ @Override
	public int getMajorVersion() {
		return servletContext.getMajorVersion();
	}

	// Java 1.6+ @Override
	public String getMimeType(String file) {
		return servletContext.getMimeType(file);
	}

	// Java 1.6+ @Override
	public int getMinorVersion() {
		return servletContext.getMinorVersion();
	}

	// Java 1.6+ @Override
	public PortletRequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getPortletContextName() {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public String getRealPath(String path) {
		return servletContext.getRealPath(path);
	}

	// Java 1.6+ @Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	// Java 1.6+ @Override
	public URL getResource(String path) throws MalformedURLException {
		return servletContext.getResource(path);
	}

	// Java 1.6+ @Override
	public InputStream getResourceAsStream(String path) {
		return servletContext.getResourceAsStream(path);
	}

	// Java 1.6+ @Override
	@SuppressWarnings("unchecked")
	public Set<String> getResourcePaths(String path) {
		return servletContext.getResourcePaths(path);
	}

	// Java 1.6+ @Override
	public String getServerInfo() {
		return servletContext.getServerInfo();
	}

	// Java 1.6+ @Override
	public void log(String msg) {
		servletContext.log(msg);
	}

	// Java 1.6+ @Override
	public void log(String message, Throwable throwable) {
		servletContext.log(message, throwable);
	}

	// Java 1.6+ @Override
	public void removeAttribute(String name) {
		servletContext.removeAttribute(name);
	}

	// Java 1.6+ @Override
	public void setAttribute(String name, Object object) {
		servletContext.setAttribute(name, object);
	}
}
