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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * @author  Neil Griffin
 */
public abstract class IncongruityContextBaseImpl extends IncongruityContext {

	// Private Constants
	private static final String INCONGRUOUS_ACTIONS = "incongruousActions";
	private static final String REQUEST_CHARACTER_ENCODING = "requestCharacterEncoding";
	private static final String REQUEST_CONTENT_TYPE = "requestContentType";
	private static final String RESPONSE_CHARACTER_ENCODING = "responseCharacterEncoding";

	// Protected Enumerations
	protected enum IncongruousAction {
		RESPONSE_FLUSH_BUFFER, RESPONSE_RESET, SET_REQUEST_CHARACTER_ENCODING, SET_RESPONSE_BUFFER_SIZE,
		SET_RESPONSE_CHARACTER_ENCODING, SET_RESPONSE_CONTENT_LENGTH, SET_RESPONSE_CONTENT_TYPE, SET_RESPONSE_STATUS,
		WRITE_RESPONSE_OUTPUT_WRITER, WRITE_RESPONSE_OUTPUT_STREAM
	}

	// Protected Data Members
	protected Map<String, Object> attributeMap;

	public IncongruityContextBaseImpl() {
		this.attributeMap = new HashMap<String, Object>();

		Set<IncongruousAction> incongruousActions = new HashSet<IncongruousAction>();
		this.attributeMap.put(INCONGRUOUS_ACTIONS, incongruousActions);
	}

	@Override
	public void dispatch(String path) throws IOException {
		throw new IllegalStateException();
	}

	@Override
	public String encodeActionURL(String url) {
		throw new IllegalStateException();
	}

	@Override
	public String encodeNamespace(String name) {
		throw new IllegalStateException();
	}

	@Override
	public String encodeResourceURL(String url) {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, Object> getApplicationMap() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributeMap;
	}

	@Override
	public String getAuthType() {
		throw new IllegalStateException();
	}

	@Override
	public Object getContext() {
		throw new IllegalStateException();
	}

	@Override
	public String getInitParameter(String name) {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, String> getInitParameterMap() {
		throw new IllegalStateException();
	}

	@Override
	public String getRemoteUser() {
		throw new IllegalStateException();
	}

	@Override
	public Object getRequest() {
		throw new IllegalStateException();
	}

	@Override
	public String getRequestCharacterEncoding() {
		return (String) attributeMap.get(REQUEST_CHARACTER_ENCODING);
	}

	@Override
	public String getRequestContentType() {
		return (String) attributeMap.get(REQUEST_CONTENT_TYPE);
	}

	@Override
	public String getRequestContextPath() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, Object> getRequestCookieMap() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		throw new IllegalStateException();
	}

	@Override
	public Locale getRequestLocale() {
		throw new IllegalStateException();
	}

	@Override
	public Iterator<Locale> getRequestLocales() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, Object> getRequestMap() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, String> getRequestParameterMap() {
		throw new IllegalStateException();
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		throw new IllegalStateException();
	}

	@Override
	public String getRequestPathInfo() {
		throw new IllegalStateException();
	}

	@Override
	public String getRequestServletPath() {
		throw new IllegalStateException();
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		throw new IllegalStateException();
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		throw new IllegalStateException();
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		throw new IllegalStateException();
	}

	@Override
	public Object getResponse() {
		throw new IllegalStateException();
	}

	@Override
	public String getResponseCharacterEncoding() {
		return (String) attributeMap.get(RESPONSE_CHARACTER_ENCODING);
	}

	@Override
	public Object getSession(boolean create) {
		throw new IllegalStateException();
	}

	@Override
	public Map<String, Object> getSessionMap() {
		throw new IllegalStateException();
	}

	@Override
	public Principal getUserPrincipal() {
		throw new IllegalStateException();
	}

	@Override
	public boolean isUserInRole(String role) {
		throw new IllegalStateException();
	}

	@Override
	public void log(String message) {
		throw new IllegalStateException();
	}

	@Override
	public void log(String message, Throwable exception) {
		throw new IllegalStateException();
	}

	@Override
	public void redirect(String url) throws IOException {
		throw new IllegalStateException();
	}

	@Override
	public void setRequest(Object request) {
	}

	@Override
	public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		attributeMap.put(REQUEST_CHARACTER_ENCODING, encoding);
		getIncongruousActions().add(IncongruousAction.SET_REQUEST_CHARACTER_ENCODING);
	}

	@Override
	public void setRequestContentType(String contentType) {
		attributeMap.put(REQUEST_CONTENT_TYPE, contentType);
	}

	@Override
	public void setResponse(Object response) {
		throw new IllegalStateException();
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		attributeMap.put(RESPONSE_CHARACTER_ENCODING, encoding);
		getIncongruousActions().add(IncongruousAction.SET_RESPONSE_CHARACTER_ENCODING);
	}

	@SuppressWarnings("unchecked")
	protected Set<IncongruousAction> getIncongruousActions() {
		return (Set<IncongruousAction>) attributeMap.get(INCONGRUOUS_ACTIONS);
	}
}
