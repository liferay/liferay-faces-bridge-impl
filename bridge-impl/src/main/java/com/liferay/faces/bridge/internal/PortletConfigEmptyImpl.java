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

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;


/**
 * This class represents an "empty" {@link PortletConfig}, meaning one that does not contain any configuration. It is
 * designed to be used in conjunction with {@link PortletConfigParamUtil} when only portlet context initialization
 * parameter values (i.e. WEB-INF/web.xml context-param values) are to be considered.
 *
 * @author  Neil Griffin
 */
public class PortletConfigEmptyImpl implements PortletConfig {

	// Private Data Members
	private PortletContext portletContext;

	public PortletConfigEmptyImpl(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDefaultNamespace() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInitParameter(String s) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletContext getPortletContext() {
		return portletContext;
	}

	@Override
	public Enumeration<PortletMode> getPortletModes(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPortletName() {
		return "";
	}

	@Override
	public Enumeration<QName> getProcessingEventQNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, QName> getPublicRenderParameterDefinitions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getPublicRenderParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<QName> getPublishingEventQNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<Locale> getSupportedLocales() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<WindowState> getWindowStates(String s) {
		throw new UnsupportedOperationException();
	}
}
