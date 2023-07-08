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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
 * @author  Neil Griffin
 */
public class HttpServletResponseAdapterImpl implements HttpServletResponse {

	// Private Data Members
	private PortletResponse portletResponse;

	public HttpServletResponseAdapterImpl(PortletResponse portletResponse) {
		this.portletResponse = portletResponse;
	}

	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	public void addHeader(String name, String value) {
		// Ignore -- Mojarra will call this from the ExternalContextImpl constructor so can't throw
		// UnsupportedOperationException here.
	}

	public void addIntHeader(String arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public boolean containsHeader(String name) {
		return false;
	}

	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeURL(String url) {
		throw new UnsupportedOperationException();
	}

	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();
	}

	public void flushBuffer() throws IOException {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.flushBuffer();
		}
	}

	public int getBufferSize() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getBufferSize();
		}
		else {
			return 0;
		}
	}

	public String getCharacterEncoding() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getCharacterEncoding();
		}
		else {
			return null;
		}
	}

	public String getContentType() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getContentType();
		}
		else {
			return null;
		}
	}

	public Locale getLocale() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getLocale();
		}
		else {
			return null;
		}
	}

	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	public PrintWriter getWriter() throws IOException {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getWriter();
		}
		else {
			return null;
		}
	}

	public boolean isCommitted() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.isCommitted();
		}
		else {
			return false;
		}
	}

	public void reset() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.reset();
		}
	}

	public void resetBuffer() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.resetBuffer();
		}
	}

	public void sendError(int sc) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void setBufferSize(int size) {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.setBufferSize(size);
		}
	}

	public void setCharacterEncoding(String encoding) {
		throw new UnsupportedOperationException();
	}

	public void setContentLength(int length) {

		if (portletResponse instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
			resourceResponse.setContentLength(length);
		}
	}

	public void setContentType(String contentType) {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.setContentType(contentType);
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

	public void setLocale(Locale locale) {

		if (portletResponse instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
			resourceResponse.setLocale(locale);
		}
	}

	public void setStatus(int sc) {
		throw new UnsupportedOperationException();
	}

	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException();
	}

}
