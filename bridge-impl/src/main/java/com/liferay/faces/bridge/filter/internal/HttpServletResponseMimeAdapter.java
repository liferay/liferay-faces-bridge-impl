/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import javax.portlet.CacheControl;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;


/**
 * Provides a way to decorate an {@link HttpServletResponse} as a portlet {@link MimeResponse}. The methods signatures
 * that are unique to {@link MimeResponse} throw {@link UnsupportedOperationException} since they are never called
 * during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more
 * information, see {@link com.liferay.faces.bridge.context.internal.ExternalContextImpl#setResponse(Object)}.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseMimeAdapter extends HttpServletResponseWrapper implements MimeResponse {

	// Private Data Members
	private String namespace;

	public HttpServletResponseMimeAdapter(HttpServletResponse httpServletResponse, String namespace) {
		super(httpServletResponse);
		this.namespace = namespace;
	}

	@Override
	public void addProperty(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String key, Element element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletURL createActionURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletURL createRenderURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResourceURL createResourceURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheControl getCacheControl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}
}
