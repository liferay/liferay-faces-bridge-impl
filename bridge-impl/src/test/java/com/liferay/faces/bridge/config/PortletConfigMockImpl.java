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
package com.liferay.faces.bridge.config;

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
 * @author  Neil Griffin
 */
public class PortletConfigMockImpl implements PortletConfig {

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		return null;
	}

	@Override
	public String getDefaultNamespace() {
		return null;
	}

	@Override
	public String getInitParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	@Override
	public PortletContext getPortletContext() {
		return null;
	}

	@Override
	public Enumeration<PortletMode> getPortletModes(String mimeType) {
		return null;
	}

	@Override
	public String getPortletName() {
		return null;
	}

	@Override
	public Enumeration<QName> getProcessingEventQNames() {
		return null;
	}

	@Override
	public Map<String, QName> getPublicRenderParameterDefinitions() {
		return null;
	}

	@Override
	public Enumeration<String> getPublicRenderParameterNames() {
		return null;
	}

	@Override
	public Enumeration<QName> getPublishingEventQNames() {
		return null;
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return null;
	}

	@Override
	public Enumeration<Locale> getSupportedLocales() {
		return null;
	}

	@Override
	public Enumeration<WindowState> getWindowStates(String mimeType) {
		return null;
	}
}
