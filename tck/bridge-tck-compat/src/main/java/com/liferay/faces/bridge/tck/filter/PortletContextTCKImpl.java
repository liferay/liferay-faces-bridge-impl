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
import javax.portlet.filter.PortletContextWrapper;


/**
 * @author  Neil Griffin
 */
public class PortletContextTCKImpl extends PortletContextWrapper {

	public PortletContextTCKImpl(PortletContext portletContext) {
		super(portletContext);
	}

	@Override
	public Object getAttribute(String name) {
		return getPortletContext().getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return getPortletContext().getAttributeNames();
	}

	@Override
	public ClassLoader getClassLoader() {
		return getPortletContext().getClassLoader();
	}

	@Override
	public Enumeration<String> getContainerRuntimeOptions() {
		return getPortletContext().getContainerRuntimeOptions();
	}

	@Override
	public String getContextPath() {
		return getPortletContext().getContextPath();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return getPortletContext().getEffectiveMajorVersion();
	}

	@Override
	public int getEffectiveMinorVersion() {
		return getPortletContext().getEffectiveMinorVersion();
	}

	@Override
	public String getInitParameter(String name) {
		return getPortletContext().getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return getPortletContext().getInitParameterNames();
	}

	@Override
	public int getMajorVersion() {
		return getPortletContext().getMajorVersion();
	}

	@Override
	public String getMimeType(String file) {
		return getPortletContext().getMimeType(file);
	}

	@Override
	public int getMinorVersion() {
		return getPortletContext().getMinorVersion();
	}

	@Override
	public PortletRequestDispatcher getNamedDispatcher(String name) {
		return getPortletContext().getNamedDispatcher(name);
	}

	@Override
	public String getPortletContextName() {
		return getPortletContext().getPortletContextName();
	}

	@Override
	public String getRealPath(String path) {
		return getPortletContext().getRealPath(path);
	}

	@Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		return new PortletRequestDispatcherTCKImpl(getPortletContext().getRequestDispatcher(path));
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return getPortletContext().getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return getPortletContext().getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return getPortletContext().getResourcePaths(path);
	}

	@Override
	public String getServerInfo() {
		return getPortletContext().getServerInfo();
	}

	@Override
	public void log(String msg) {
		getPortletContext().log(msg);
	}

	@Override
	public void log(String message, Throwable throwable) {
		getPortletContext().log(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		getPortletContext().removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		getPortletContext().setAttribute(name, object);
	}
}
