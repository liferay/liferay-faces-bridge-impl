/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import jakarta.portlet.BaseURL;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.WindowState;

import com.liferay.faces.bridge.util.internal.XMLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class wraps a {@link BridgeURI} so that it can be accessed as a {@link BaseURL}.
 *
 * @author  Neil Griffin
 */
public class BaseURLBridgeURIAdapterImpl implements BaseURL {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BaseURLBridgeURIAdapterImpl.class);

	// Protected Data Members
	protected BridgeURI bridgeURI;

	public BaseURLBridgeURIAdapterImpl(BridgeURI bridgeURI) {
		this.bridgeURI = bridgeURI;
	}

	@Override
	public void addProperty(String key, String value) {
		// no-op
	}

	@Override
	public Appendable append(Appendable out) throws IOException {

		// no-op
		return null;
	}

	@Override
	public Appendable append(Appendable out, boolean escapeXML) throws IOException {

		// no-op
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return bridgeURI.getParameterMap();
	}

	@Override
	public PortletMode getPortletMode() {

		// no-op
		return null;
	}

	@Override
	public RenderParameters getRenderParameters() {

		// no-op
		return null;
	}

	@Override
	public WindowState getWindowState() {

		// no-op
		return null;
	}

	@Override
	public void setParameter(String name, String value) {
		bridgeURI.setParameter(name, value);
	}

	@Override
	public void setParameter(String name, String[] values) {
		bridgeURI.setParameter(name, values);
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		bridgeURI.setParameters(parameters);
	}

	@Override
	public void setProperty(String key, String value) {
		// no-op
	}

	@Override
	public void setSecure(boolean secure) throws PortletSecurityException {
		// no-op
	}

	@Override
	public String toString() {
		return bridgeURI.toString();
	}

	@Override
	public void write(Writer out) throws IOException {
		write(out, true);
	}

	@Override
	public void write(Writer out, boolean escapeXML) throws IOException {

		String uri = toString();

		if (escapeXML) {
			uri = XMLUtil.escapeXML(uri);
		}

		out.write(uri);
	}
}
