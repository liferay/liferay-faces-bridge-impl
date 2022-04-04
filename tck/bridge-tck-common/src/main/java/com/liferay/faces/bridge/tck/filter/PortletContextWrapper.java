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
package com.liferay.faces.bridge.tck.filter;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.faces.FacesWrapper;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;


/**
 * @author  Neil Griffin
 */
public abstract class PortletContextWrapper implements PortletContext, FacesWrapper<PortletContext> {

	public abstract PortletContext getWrapped();

	public Object getAttribute(String name) {
		return getWrapped().getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return getWrapped().getAttributeNames();
	}

	public Enumeration<String> getContainerRuntimeOptions() {
		return getWrapped().getContainerRuntimeOptions();
	}

	public String getInitParameter(String name) {
		return getWrapped().getInitParameter(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return getWrapped().getInitParameterNames();
	}

	public int getMajorVersion() {
		return getWrapped().getMajorVersion();
	}

	public String getMimeType(String file) {
		return getWrapped().getMimeType(file);
	}

	public int getMinorVersion() {
		return getWrapped().getMinorVersion();
	}

	public PortletRequestDispatcher getNamedDispatcher(String name) {
		return getWrapped().getNamedDispatcher(name);
	}

	public String getPortletContextName() {
		return getWrapped().getPortletContextName();
	}

	public String getRealPath(String path) {
		return getWrapped().getRealPath(path);
	}

	public PortletRequestDispatcher getRequestDispatcher(String path) {
		return getWrapped().getRequestDispatcher(path);
	}

	public URL getResource(String path) throws MalformedURLException {
		return getWrapped().getResource(path);
	}

	public InputStream getResourceAsStream(String path) {
		return getWrapped().getResourceAsStream(path);
	}

	public Set<String> getResourcePaths(String path) {
		return getWrapped().getResourcePaths(path);
	}

	public String getServerInfo() {
		return getWrapped().getServerInfo();
	}

	public void log(String msg) {
		getWrapped().log(msg);
	}

	public void log(String message, Throwable throwable) {
		getWrapped().log(message, throwable);
	}

	public void removeAttribute(String name) {
		getWrapped().removeAttribute(name);
	}

	public void setAttribute(String name, Object object) {
		getWrapped().setAttribute(name, object);
	}
}
