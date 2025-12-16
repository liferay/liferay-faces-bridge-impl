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
package com.liferay.faces.bridge.renderkit.bridge.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.faces.context.ExternalContext;
import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.filter.PortletRequestWrapper;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public final class HttpServletRequestFileUploadAdapter extends PortletRequestWrapper implements HttpServletRequest,
	ClientDataRequest {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HttpServletRequestFileUploadAdapter.class);

	// Private Constants
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String CONTENT_TYPE = "Content-Type";

	// Private Final Data Members
	private final ExternalContext externalContext;
	private final Map<String, List<UploadedFile>> uploadedFileMap;
	private final ServletContext servletContext;

	// Private Data Members
	private String queryString;
	private Collection<Part> parts;

	public HttpServletRequestFileUploadAdapter(ClientDataRequest clientDataRequest,
		Map<String, List<UploadedFile>> uploadedFileMap, ExternalContext externalContext) {

		super(clientDataRequest);
		this.uploadedFileMap = uploadedFileMap;

		PortletContext portletContext = (PortletContext) externalContext.getContext();
		this.servletContext = new ServletContextFileUploadAdapterImpl(portletContext);
		this.externalContext = externalContext;
	}

	private static void addHeader(Map requestHeaderMap, String headerName, Object headerValue, boolean arrayValue) {

		if (arrayValue) {
			headerValue = new String[] { (String) headerValue };
		}

		requestHeaderMap.put(headerName, headerValue);
	}

	private static Map getRequestHeaderMap(ExternalContext externalContext, boolean arrayValue) {

		Map originalRequestHeaderMap;

		if (arrayValue) {
			originalRequestHeaderMap = externalContext.getRequestHeaderValuesMap();
		}
		else {
			originalRequestHeaderMap = externalContext.getRequestHeaderMap();
		}

		boolean containsContentLength = originalRequestHeaderMap.containsKey(CONTENT_LENGTH);
		boolean containsContentType = originalRequestHeaderMap.containsKey(CONTENT_TYPE);

		if (containsContentLength && containsContentType) {
			return originalRequestHeaderMap;
		}

		Map requestHeaderMap = new HashMap(originalRequestHeaderMap);

		if (!containsContentLength) {

			Object requestContentLengthValue = Integer.toString(externalContext.getRequestContentLength());
			addHeader(requestHeaderMap, CONTENT_LENGTH, requestContentLengthValue, arrayValue);
		}

		if (!containsContentType) {

			Object requestContentTypeValue = externalContext.getRequestContentType();
			addHeader(requestHeaderMap, CONTENT_TYPE, requestContentTypeValue, arrayValue);
		}

		return Collections.unmodifiableMap(requestHeaderMap);
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext getAsyncContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCharacterEncoding() {
		return getClientDataRequest().getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return getClientDataRequest().getContentLength();
	}

	public long getContentLengthLong() {
		return getContentLength();
	}

	@Override
	public String getContentType() {
		return getClientDataRequest().getContentType();
	}

	@Override
	public long getDateHeader(String name) {
		throw new IllegalArgumentException();
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProtocolRequestId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletConnection getServletConnection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeader(String name) {

		Map<String, String> requestHeaderMap = getRequestHeaderMap(externalContext, false);

		return requestHeaderMap.get(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {

		Map<String, String[]> requestHeaderValuesMap = getRequestHeaderMap(externalContext, true);

		return Collections.enumeration(requestHeaderValuesMap.keySet());
	}

	@Override
	public Enumeration<String> getHeaders(String name) {

		Map<String, String[]> requestHeaderValuesMap = getRequestHeaderMap(externalContext, true);
		String[] headersArray = requestHeaderValuesMap.get(name);
		List<String> headers;

		if (headersArray != null) {
			headers = Arrays.asList(headersArray);
		}
		else {
			headers = Collections.emptyList();
		}

		return Collections.enumeration(headers);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getIntHeader(String name) {

		int intHeader = -1;
		String header = getHeader(name);

		if (header != null) {
			intHeader = Integer.parseInt(header);
		}

		return intHeader;
	}

	@Override
	public String getLocalAddr() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocalName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLocalPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMethod() {
		return getClientDataRequest().getMethod();
	}

	@Override
	public Part getPart(String name) throws IOException {

		Part part = null;
		List<UploadedFile> uploadedFiles = uploadedFileMap.get(name);

		if ((uploadedFiles != null) && !uploadedFiles.isEmpty()) {
			part = new PartFileUploadAdapterImpl(uploadedFiles.get(0), name);
		}

		return part;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Part> getParts() throws IOException {

		if (parts == null) {

			List<Part> parts = new ArrayList<Part>();
			Set<Map.Entry<String, List<UploadedFile>>> uploadedFileMapEntries = uploadedFileMap.entrySet();

			for (Map.Entry<String, List<UploadedFile>> uploadedFileMapEntry : uploadedFileMapEntries) {

				List<UploadedFile> uploadedFilesForName = uploadedFileMapEntry.getValue();

				if ((uploadedFilesForName != null) && !uploadedFilesForName.isEmpty()) {

					String partName = uploadedFileMapEntry.getKey();

					for (UploadedFile uploadedFile : uploadedFilesForName) {
						parts.add(new PartFileUploadAdapterImpl(uploadedFile, partName));
					}
				}
			}

			this.parts = Collections.unmodifiableList(parts);
		}

		return parts;
	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public InputStream getPortletInputStream() throws IOException {
		return getClientDataRequest().getPortletInputStream();
	}

	@Override
	public String getProtocol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQueryString() {

		if (queryString == null) {

			StringBuilder stringBuilder = new StringBuilder();
			Map<String, String[]> parameterMap = getParameterMap();

			for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {

				String[] values = parameter.getValue();
				String name = parameter.getKey();

				if (values.length > 0) {

					for (String value : values) {

						if (stringBuilder.length() > 0) {
							stringBuilder.append("&");
						}

						try {

							stringBuilder.append(URLEncoder.encode(name, "UTF-8"));
							stringBuilder.append("=");
							stringBuilder.append(URLEncoder.encode(value, "UTF-8"));
						}
						catch (UnsupportedEncodingException e) {

							logger.error("Unable to url encode name=[{0}], value=[{1}] pair with UTF-8 encoding:", name,
								value);
							logger.error(e);
						}
					}
				}
				else {

					if (stringBuilder.length() > 0) {
						stringBuilder.append("&");
					}

					stringBuilder.append(name);
				}
			}

			queryString = stringBuilder.toString();
		}

		return queryString;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteAddr() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteHost() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRemotePort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestURI() {
		return getContextPath();
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public String getServletPath() {
		return "";
	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String changeSessionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncStarted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncSupported() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void login(String username, String password) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
		getClientDataRequest().setCharacterEncoding(characterEncoding);
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
		throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	private ClientDataRequest getClientDataRequest() {
		return (ClientDataRequest) getRequest();
	}
}
