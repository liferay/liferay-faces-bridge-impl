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
import java.util.Collection;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;


/**
 * @author  Neil Griffin
 */
public class BridgeAfterViewContentResponseRenderImpl extends BridgeAfterViewContentResponse implements RenderResponse {

	// Private Data Members
	private RenderResponse wrappedRenderResponse;

	public BridgeAfterViewContentResponseRenderImpl(RenderResponse renderResponse, Locale requestLocale) {
		super(renderResponse, requestLocale);
		this.wrappedRenderResponse = renderResponse;
	}

	public void addProperty(Cookie cookie) {
		wrappedRenderResponse.addProperty(cookie);
	}

	public void addProperty(String name, String value) {
		wrappedRenderResponse.addProperty(name, value);
	}

	public void addProperty(String name, Element value) {
		wrappedRenderResponse.addProperty(name, value);
	}

	public PortletURL createActionURL() {
		return wrappedRenderResponse.createActionURL();
	}

	public Element createElement(String name) throws DOMException {
		return wrappedRenderResponse.createElement(name);
	}

	public PortletURL createRenderURL() {
		return wrappedRenderResponse.createRenderURL();
	}

	public ResourceURL createResourceURL() {
		return wrappedRenderResponse.createResourceURL();
	}

	public CacheControl getCacheControl() {
		return wrappedRenderResponse.getCacheControl();
	}

	public String getNamespace() {
		return wrappedRenderResponse.getNamespace();
	}

	public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
		wrappedRenderResponse.setNextPossiblePortletModes(portletModes);
	}

	public OutputStream getPortletOutputStream() throws IOException {
		return wrappedRenderResponse.getPortletOutputStream();
	}

	public void setProperty(String name, String value) {
		wrappedRenderResponse.setProperty(name, value);
	}

	public void setTitle(String title) {
		wrappedRenderResponse.setTitle(title);
	}
}
