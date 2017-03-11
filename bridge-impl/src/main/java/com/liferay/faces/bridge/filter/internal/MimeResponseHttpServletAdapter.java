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
package com.liferay.faces.bridge.filter.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.portlet.filter.MimeResponseWrapper;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate a {@link MimeResponse} as an {@link HttpServletResponse}. The methods signatures that are
 * unique to {@link HttpServletResponse} throw {@link UnsupportedOperationException} since they are never called during
 * the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more information,
 * see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class MimeResponseHttpServletAdapter extends MimeResponseWrapper implements HttpServletResponse {

	public MimeResponseHttpServletAdapter(MimeResponse mimeResponse) {
		super(mimeResponse);
	}

	/**
	 * See {@link HttpServletResponse#addCookie(Cookie)}
	 */
	@Override
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#addDateHeader(String, long)}
	 */
	@Override
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#addHeader(String, String)}
	 */
	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#addIntHeader(String, int)}
	 */
	@Override
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#containsHeader(String)}
	 */
	@Override
	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#encodeRedirectURL(String)}
	 */
	@Override
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#encodeRedirectUrl(String)}
	 */
	@Override
	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#encodeUrl(String)}
	 */
	@Override
	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#getHeader(String)}
	 */
	@Override
	public String getHeader(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#getHeaderNames()}
	 */
	@Override
	public Collection<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#getHeaders(String)}
	 */
	@Override
	public Collection<String> getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#getOutputStream()}
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#getStatus()}
	 */
	@Override
	public int getStatus() {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#sendError(int)}
	 */
	@Override
	public void sendError(int sc) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#sendError(int)}
	 */
	@Override
	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#sendRedirect(String)}
	 */
	@Override
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setCharacterEncoding(String)}
	 */
	@Override
	public void setCharacterEncoding(String charset) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setContentLength(int)}
	 */
	@Override
	public void setContentLength(int len) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setDateHeader(String, long)}
	 */
	@Override
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setHeader(String, String)}
	 */
	@Override
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setIntHeader(String, int)}
	 */
	@Override
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setLocale(Locale)}
	 */
	@Override
	public void setLocale(Locale loc) {
		// no-op: called by org.apache.myfaces.view.jsp.JspViewDeclarationLanguage.buildView(FacesContext,UIViewRoot)
	}

	/**
	 * See {@link HttpServletResponse#setStatus(int)}
	 */
	@Override
	public void setStatus(int sc) {
		throw new UnsupportedOperationException();
	}

	/**
	 * See {@link HttpServletResponse#setStatus(int, String)}
	 */
	@Override
	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException();
	}
}
