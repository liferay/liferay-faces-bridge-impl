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

import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;


/**
 * @author  Neil Griffin
 */
public abstract class IncongruityContextWrapper extends IncongruityContext implements FacesWrapper<IncongruityContext> {

	public abstract IncongruityContext getWrapped();

	@Override
	public void dispatch(String path) throws IOException {
		getWrapped().dispatch(path);
	}

	@Override
	public String encodeActionURL(String url) {
		return getWrapped().encodeActionURL(url);
	}

	@Override
	public String encodeNamespace(String name) {
		return getWrapped().encodeNamespace(name);
	}

	@Override
	public String encodeResourceURL(String url) {
		return getWrapped().encodeResourceURL(url);
	}

	@Override
	public Map<String, Object> getApplicationMap() {
		return getWrapped().getApplicationMap();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return getWrapped().getAttributes();
	}

	@Override
	public String getAuthType() {
		return getWrapped().getAuthType();
	}

	@Override
	public Object getContext() {
		return getWrapped().getContext();
	}

	@Override
	public String getInitParameter(String name) {
		return getWrapped().getInitParameter(name);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map getInitParameterMap() {
		return getWrapped().getInitParameterMap();
	}

	@Override
	public String getRemoteUser() {
		return getWrapped().getRemoteUser();
	}

	@Override
	public Object getRequest() {
		return getWrapped().getRequest();
	}

	@Override
	public String getRequestContextPath() {
		return getWrapped().getRequestContextPath();
	}

	@Override
	public Map<String, Object> getRequestCookieMap() {
		return getWrapped().getRequestCookieMap();
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		return getWrapped().getRequestHeaderMap();
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		return getWrapped().getRequestHeaderValuesMap();
	}

	@Override
	public Locale getRequestLocale() {
		return getWrapped().getRequestLocale();
	}

	@Override
	public Iterator<Locale> getRequestLocales() {
		return getWrapped().getRequestLocales();
	}

	@Override
	public Map<String, Object> getRequestMap() {
		return getWrapped().getRequestMap();
	}

	@Override
	public Map<String, String> getRequestParameterMap() {
		return getWrapped().getRequestParameterMap();
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		return getWrapped().getRequestParameterNames();
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		return getWrapped().getRequestParameterValuesMap();
	}

	@Override
	public String getRequestPathInfo() {
		return getWrapped().getRequestPathInfo();
	}

	@Override
	public String getRequestServletPath() {
		return getWrapped().getRequestServletPath();
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return getWrapped().getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return getWrapped().getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return getWrapped().getResourcePaths(path);
	}

	@Override
	public Object getResponse() {
		return getWrapped().getResponse();
	}

	@Override
	public int getResponseContentLength() {
		return getWrapped().getResponseContentLength();
	}

	@Override
	public Object getSession(boolean create) {
		return getWrapped().getSession(create);
	}

	@Override
	public Map<String, Object> getSessionMap() {
		return getWrapped().getSessionMap();
	}

	@Override
	public Principal getUserPrincipal() {
		return getWrapped().getUserPrincipal();
	}

	@Override
	public boolean isUserInRole(String role) {
		return getWrapped().isUserInRole(role);
	}

	@Override
	public void log(String message) {
		getWrapped().log(message);
	}

	@Override
	public void log(String message, Throwable exception) {
		getWrapped().log(message, exception);
	}

	@Override
	public void makeCongruous(FacesContext facesContext) throws IOException {
		getWrapped().makeCongruous(facesContext);
	}

	@Override
	public void redirect(String url) throws IOException {
		getWrapped().redirect(url);
	}

	@Override
	public void setRequestContentLength(int length) {
		getWrapped().setRequestContentLength(length);
	}

	@Override
	public void setRequestContentType(String contentType) {
		getWrapped().setRequestContentType(contentType);
	}

	@Override
	public void setResponseCommitted(boolean committed) {
		getWrapped().setResponseCommitted(committed);
	}
}
