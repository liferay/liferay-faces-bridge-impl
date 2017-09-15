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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.liferay.faces.bridge.util.internal.ElementUtil;


/**
 * @author  Kyle Stiemann
 */
public class RenderResponseMockImpl implements RenderResponse {

	// Private Data Members
	private Document document;
	private Element element;

	public RenderResponseMockImpl() {

		try {

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			document = documentBuilder.newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addProperty(Cookie cookie) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void addProperty(String key, String value) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void addProperty(String key, Element element) {
		this.element = element;
	}

	@Override
	public PortletURL createActionURL() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		return document.createElement(tagName);
	}

	@Override
	public PortletURL createRenderURL() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public ResourceURL createResourceURL() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String encodeURL(String path) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public CacheControl getCacheControl() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException("");
	}

	public String getLastElementPropertyAsString() {
		return ElementUtil.elementToString(element);
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getNamespace() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void setContentType(String type) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void setProperty(String key, String value) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public void setTitle(String title) {
		throw new UnsupportedOperationException("");
	}
}
