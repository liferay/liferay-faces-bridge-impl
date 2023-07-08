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
package com.liferay.faces.bridge.filter.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.filter.PortletResponseWrapper;
import javax.portlet.filter.RenderResponseWrapper;
import javax.portlet.filter.ResourceResponseWrapper;


/**
 * This is a wrapper style decorator class that is modeled after the {@link RenderResponseWrapper} and {@link
 * ResourceResponseWrapper} found in the Portlet API.
 *
 * @author  Neil Griffin
 */
public class MimeResponseWrapper extends PortletResponseWrapper implements MimeResponse {

	public MimeResponseWrapper(MimeResponse mimeResponse) {
		super(mimeResponse);
	}

	public PortletURL createActionURL() {
		return getResponse().createActionURL();
	}

	public PortletURL createRenderURL() {
		return getResponse().createRenderURL();
	}

	public ResourceURL createResourceURL() {
		return getResponse().createResourceURL();
	}

	public void flushBuffer() throws IOException {
		getResponse().flushBuffer();
	}

	public int getBufferSize() {
		return getResponse().getBufferSize();
	}

	public CacheControl getCacheControl() {
		return getResponse().getCacheControl();
	}

	public String getCharacterEncoding() {
		return getResponse().getCharacterEncoding();
	}

	public String getContentType() {
		return getResponse().getContentType();
	}

	public Locale getLocale() {
		return getResponse().getLocale();
	}

	public OutputStream getPortletOutputStream() throws IOException {
		return getResponse().getPortletOutputStream();
	}

	@Override
	public MimeResponse getResponse() {
		return (MimeResponse) super.getResponse();
	}

	public PrintWriter getWriter() throws IOException {
		return getResponse().getWriter();
	}

	public boolean isCommitted() {
		return getResponse().isCommitted();
	}

	public void reset() {
		getResponse().reset();
	}

	public void resetBuffer() {
		getResponse().resetBuffer();
	}

	public void setBufferSize(int size) {
		getResponse().setBufferSize(size);
	}

	public void setContentType(String type) {
		getResponse().setContentType(type);
	}

}
