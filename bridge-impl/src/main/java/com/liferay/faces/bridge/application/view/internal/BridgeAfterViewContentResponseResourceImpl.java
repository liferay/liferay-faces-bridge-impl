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
package com.liferay.faces.bridge.application.view.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;


/**
 * @author  Neil Griffin
 */
public class BridgeAfterViewContentResponseResourceImpl extends BridgeAfterViewContentResponse
	implements ResourceResponse {

	// Private Data Members
	private ResourceResponse wrappedResourceResponse;

	public BridgeAfterViewContentResponseResourceImpl(ResourceResponse resourceResponse, Locale requestLocale) {
		super(resourceResponse, requestLocale);
		this.wrappedResourceResponse = resourceResponse;
	}

	public void addProperty(Cookie cookie) {
		wrappedResourceResponse.addProperty(cookie);
	}

	public void addProperty(String name, String value) {
		wrappedResourceResponse.addProperty(name, value);
	}

	public void addProperty(String name, Element value) {
		wrappedResourceResponse.addProperty(name, value);
	}

	public PortletURL createActionURL() {
		return wrappedResourceResponse.createActionURL();
	}

	public Element createElement(String name) throws DOMException {
		return wrappedResourceResponse.createElement(name);
	}

	public PortletURL createRenderURL() {
		return wrappedResourceResponse.createRenderURL();
	}

	public ResourceURL createResourceURL() {
		return wrappedResourceResponse.createResourceURL();
	}

	@Override
	public String encodeURL(String url) {
		return wrappedResourceResponse.encodeURL(url);
	}

	@Override
	public void flushBuffer() throws IOException {
		wrappedResourceResponse.flushBuffer();
	}

	@Override
	public void reset() {
		wrappedResourceResponse.reset();
	}

	@Override
	public void resetBuffer() {
		wrappedResourceResponse.resetBuffer();
	}

	@Override
	public int getBufferSize() {
		return wrappedResourceResponse.getBufferSize();
	}

	@Override
	public void setBufferSize(int size) {
		wrappedResourceResponse.setBufferSize(size);
	}

	public CacheControl getCacheControl() {
		return wrappedResourceResponse.getCacheControl();
	}

	@Override
	public String getCharacterEncoding() {
		return wrappedResourceResponse.getCharacterEncoding();
	}

	@Override
	public void setCharacterEncoding(String encoding) {
		wrappedResourceResponse.setCharacterEncoding(encoding);
	}

	@Override
	public void setContentLength(int length) {
		wrappedResourceResponse.setContentLength(length);
	}

	@Override
	public String getContentType() {
		return wrappedResourceResponse.getContentType();
	}

	@Override
	public void setContentType(String contentType) {
		wrappedResourceResponse.setContentType(contentType);
	}

	@Override
	public boolean isCommitted() {
		return wrappedResourceResponse.isCommitted();
	}

	@Override
	public Locale getLocale() {
		return wrappedResourceResponse.getLocale();
	}

	@Override
	public void setLocale(Locale locale) {
		wrappedResourceResponse.setLocale(locale);
	}

	public String getNamespace() {
		return wrappedResourceResponse.getNamespace();
	}

	public OutputStream getPortletOutputStream() throws IOException {
		return wrappedResourceResponse.getPortletOutputStream();
	}

	public void setProperty(String name, String value) {
		wrappedResourceResponse.setProperty(name, value);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return wrappedResourceResponse.getWriter();
	}
}
