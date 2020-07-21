/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate an {@link HttpServletResponse} as a portlet {@link MimeResponse}. The methods signatures
 * that are unique to {@link MimeResponse} throw {@link UnsupportedOperationException} since they are never called
 * during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more
 * information, see {@link com.liferay.faces.bridge.context.internal.ExternalContextImpl#setResponse(Object)}.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseMimeAdapter extends MimeResponseWrapper {

	// Private Data Members
	private HttpServletResponse httpServletResponse;

	public HttpServletResponseMimeAdapter(MimeResponse mimeResponse, HttpServletResponse httpServletResponse) {
		super(mimeResponse);
		this.httpServletResponse = httpServletResponse;
	}

	@Override
	public String encodeURL(String path) {
		return httpServletResponse.encodeURL(path);
	}

	@Override
	public void flushBuffer() throws IOException {
		httpServletResponse.flushBuffer();
	}

	@Override
	public int getBufferSize() {
		return httpServletResponse.getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		return httpServletResponse.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return httpServletResponse.getContentType();
	}

	@Override
	public Locale getLocale() {
		return httpServletResponse.getLocale();
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		return httpServletResponse.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return httpServletResponse.getWriter();
	}

	@Override
	public boolean isCommitted() {
		return httpServletResponse.isCommitted();
	}

	@Override
	public void reset() {
		httpServletResponse.reset();
	}

	@Override
	public void resetBuffer() {
		httpServletResponse.resetBuffer();
	}

	@Override
	public void setBufferSize(int size) {
		httpServletResponse.setBufferSize(size);
	}

	@Override
	public void setContentType(String type) {
		httpServletResponse.setContentType(type);
	}
}
