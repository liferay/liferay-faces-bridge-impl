/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.el.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.util.helper.Wrapper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class provides an {@link HttpServletResponse} adapter/wrapper around the current {@link PortletResponse}.
 * Typical usage is to hack-around Servlet-API dependencies in JSF implementations.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseAdapter implements HttpServletResponse, Wrapper<PortletResponse> {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HttpServletResponseAdapter.class);

	// Private Data Members
	private PortletResponse wrappedPortletResponse;
	private Locale requestLocale;

	public HttpServletResponseAdapter(PortletResponse portletResponse, Locale requestLocale) {
		this.wrappedPortletResponse = portletResponse;
	}

	public void addCookie(Cookie cookie) {
		getWrapped().addProperty(cookie);
	}

	public void addDateHeader(String name, long value) {
		// ignore / no-op
	}

	public void addHeader(String name, String value) {
		// ignore / no-op
	}

	public void addIntHeader(String name, int value) {
		// ignore / no-op
	}

	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeURL(String url) {
		return getWrapped().encodeURL(url);
	}

	public String encodeUrl(String url) {
		return getWrapped().encodeURL(url);
	}

	public void flushBuffer() throws IOException {
		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.flushBuffer();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public int getBufferSize() {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getBufferSize();
		}
		else {
			return 0;
		}
	}

	public String getCharacterEncoding() {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getCharacterEncoding();
		}
		else {
			return null;
		}
	}

	public String getContentType() {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getContentType();
		}
		else {
			return null;
		}
	}

	public Locale getLocale() {
		return requestLocale;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStreamAdapter(getWrapped());
	}

	public PortletResponse getWrapped() {
		return wrappedPortletResponse;
	}

	public PrintWriter getWriter() throws IOException {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getWriter();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public boolean isCommitted() {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.isCommitted();
		}
		else {
			return true;
		}
	}

	public void reset() {
		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.reset();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public void resetBuffer() {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.resetBuffer();
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public void sendError(int sc) throws IOException {
		logger.warn("No equivalent for HttpServletResponse.sendError(int=[{0}]) for PortletResponse", sc);
	}

	public void sendError(int sc, String message) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	public void setCharacterEncoding(String charset) {
		throw new UnsupportedOperationException();
	}

	public void setContentLength(int len) {
		throw new UnsupportedOperationException();
	}

	public void setContentType(String type) {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof MimeResponse) {

			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.setContentType(type);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	public void setLocale(Locale loc) {

		PortletResponse portletResponse = getWrapped();

		if (portletResponse instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse) portletResponse;

			resourceResponse.setLocale(loc);
		}
		else {
			// ignore / no-op
		}
	}

	public void setStatus(int sc) {
		logger.warn("No equivalent for HttpServletResponse.setStatus(int=[{0}]) for PortletResponse", sc);
	}

	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException();
	}

}
