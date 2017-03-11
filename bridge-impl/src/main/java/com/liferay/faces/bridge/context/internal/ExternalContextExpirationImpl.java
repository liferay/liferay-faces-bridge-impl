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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.servlet.ServletContext;

import com.liferay.faces.bridge.context.map.internal.ContextMapFactory;


/**
 * This class is an implementation of {@link ExternalContext} that can be used during session expiration.
 *
 * @author  Neil Griffin
 */
public class ExternalContextExpirationImpl extends ExternalContextExpirationCompatImpl {

	// Private Data Members
	private Map<String, Object> applicationMap;
	private ServletContext servletContext;

	public ExternalContextExpirationImpl(ServletContext servletContext) {

		this.servletContext = servletContext;

		// Initialize the application map.
		ContextMapFactory contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(
				ContextMapFactory.class);
		this.applicationMap = contextMapFactory.getServletContextAttributeMap(servletContext);

	}

	@Override
	public void dispatch(String path) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeActionURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeNamespace(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeResourceURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getApplicationMap() {
		return applicationMap;
	}

	@Override
	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getContext() {
		return servletContext;
	}

	@Override
	public String getInitParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getInitParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getRequest() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestContextPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getRequestCookieMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getRequestLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Locale> getRequestLocales() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getRequestMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getRequestParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestPathInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestServletPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getResponse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getSessionMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUserInRole(String role) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String message, Throwable exception) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void redirect(String url) throws IOException {
		throw new UnsupportedOperationException();
	}
}
