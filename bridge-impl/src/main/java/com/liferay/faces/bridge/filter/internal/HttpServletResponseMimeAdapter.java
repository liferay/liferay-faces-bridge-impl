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
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.filter.PortletResponseWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
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
	private MimeResponse wrappedMimeResponse;

	public HttpServletResponseMimeAdapter(HttpServletResponse httpServletResponse, String namespace) {
		super(httpServletResponse);
		this.namespace = namespace;
		this.wrappedMimeResponse = (MimeResponse) unwrapPortletResponse(this);
	}

	@Override
	public void addProperty(Cookie cookie) {
		wrappedMimeResponse.addProperty(cookie);
	}

	@Override
	public void addProperty(String key, String value) {
		wrappedMimeResponse.addProperty(key, value);
	}

	@Override
	public void addProperty(String key, Element element) {
		wrappedMimeResponse.addProperty(key, element);
	}

	@Override
	public PortletURL createActionURL() {
		return wrappedMimeResponse.createActionURL();
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		return wrappedMimeResponse.createElement(tagName);
	}

	@Override
	public PortletURL createRenderURL() {
		return wrappedMimeResponse.createRenderURL();
	}

	@Override
	public ResourceURL createResourceURL() {
		return wrappedMimeResponse.createResourceURL();
	}

	@Override
	public CacheControl getCacheControl() {
		return wrappedMimeResponse.getCacheControl();
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		return wrappedMimeResponse.getPortletOutputStream();
	}

	@Override
	public void setProperty(String key, String value) {
		wrappedMimeResponse.setProperty(key, value);
	}

	protected PortletResponse unwrapPortletResponse(PortletResponse portletResponse) {

		if (portletResponse instanceof ServletResponse) {

			PortletResponse unwrappedServletResponse = unwrapServletResponse((ServletResponse) portletResponse);

			if (unwrappedServletResponse != null) {
				return unwrappedServletResponse;
			}
			else {
				return portletResponse;
			}
		}
		else if (portletResponse instanceof PortletResponseWrapper) {

			PortletResponseWrapper portletResponseWrapper = (PortletResponseWrapper) portletResponse;
			portletResponse = portletResponseWrapper.getResponse();

			return unwrapPortletResponse(portletResponse);
		}
		else {
			return portletResponse;
		}
	}

	protected PortletResponse unwrapServletResponse(ServletResponse servletResponse) {

		if (servletResponse instanceof ServletResponseWrapper) {

			ServletResponseWrapper servletResponseWrapper = (ServletResponseWrapper) servletResponse;
			servletResponse = servletResponseWrapper.getResponse();

			return unwrapServletResponse(servletResponse);
		}
		else if (servletResponse instanceof PortletResponse) {
			return (PortletResponse) servletResponse;
		}
		else {
			return null;
		}
	}
}
