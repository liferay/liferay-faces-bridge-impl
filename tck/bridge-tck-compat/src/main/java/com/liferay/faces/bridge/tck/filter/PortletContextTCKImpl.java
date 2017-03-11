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
package com.liferay.faces.bridge.tck.filter;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;


/**
 * @author  Neil Griffin
 */
public class PortletContextTCKImpl implements PortletContext {

	// Private Data Members
	private PortletContext wrappedPortletContext;

	public PortletContextTCKImpl(PortletContext portletContext) {
		this.wrappedPortletContext = portletContext;
	}

	@Override
	public Object getAttribute(String name) {
		return wrappedPortletContext.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return wrappedPortletContext.getAttributeNames();
	}

	@Override
	public Enumeration<String> getContainerRuntimeOptions() {
		return wrappedPortletContext.getContainerRuntimeOptions();
	}

	@Override
	public String getInitParameter(String name) {
		return wrappedPortletContext.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return wrappedPortletContext.getInitParameterNames();
	}

	@Override
	public int getMajorVersion() {
		return wrappedPortletContext.getMajorVersion();
	}

	@Override
	public String getMimeType(String file) {
		return wrappedPortletContext.getMimeType(file);
	}

	@Override
	public int getMinorVersion() {
		return wrappedPortletContext.getMinorVersion();
	}

	@Override
	public PortletRequestDispatcher getNamedDispatcher(String name) {
		return wrappedPortletContext.getNamedDispatcher(name);
	}

	@Override
	public String getPortletContextName() {
		return wrappedPortletContext.getPortletContextName();
	}

	@Override
	public String getRealPath(String path) {
		return wrappedPortletContext.getRealPath(path);
	}

	@Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		return new PortletRequestDispatcherTCKImpl(wrappedPortletContext.getRequestDispatcher(path));
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return wrappedPortletContext.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return wrappedPortletContext.getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return wrappedPortletContext.getResourcePaths(path);
	}

	@Override
	public String getServerInfo() {
		return wrappedPortletContext.getServerInfo();
	}

	@Override
	public void log(String msg) {
		wrappedPortletContext.log(msg);
	}

	@Override
	public void log(String message, Throwable throwable) {
		wrappedPortletContext.log(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		wrappedPortletContext.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		wrappedPortletContext.setAttribute(name, object);
	}
}
