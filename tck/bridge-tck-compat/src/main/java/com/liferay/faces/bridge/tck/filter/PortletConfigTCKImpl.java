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
package com.liferay.faces.bridge.tck.filter;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.xml.namespace.QName;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public class PortletConfigTCKImpl implements PortletConfig, Wrapper<PortletConfig> {

	// Private Data Members
	private PortletConfig wrappedPortletConfig;

	public PortletConfigTCKImpl(PortletConfig portletConfig) {
		this.wrappedPortletConfig = portletConfig;
	}

	// Java 1.6+ @Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		return wrappedPortletConfig.getContainerRuntimeOptions();
	}

	// Java 1.6+ @Override
	public String getDefaultNamespace() {
		return wrappedPortletConfig.getDefaultNamespace();
	}

	// Java 1.6+ @Override
	public String getInitParameter(String name) {
		return wrappedPortletConfig.getInitParameter(name);
	}

	// Java 1.6+ @Override
	public Enumeration<String> getInitParameterNames() {
		return wrappedPortletConfig.getInitParameterNames();
	}

	// Java 1.6+ @Override
	public PortletContext getPortletContext() {

		PortletContext wrappedPortletContext = wrappedPortletConfig.getPortletContext();

		return new PortletContextTCKImpl(wrappedPortletContext);
	}

	// Java 1.6+ @Override
	public String getPortletName() {
		return wrappedPortletConfig.getPortletName();
	}

	// Java 1.6+ @Override
	public Enumeration<QName> getProcessingEventQNames() {
		return wrappedPortletConfig.getProcessingEventQNames();
	}

	// Java 1.6+ @Override
	public Enumeration<String> getPublicRenderParameterNames() {
		return wrappedPortletConfig.getPublicRenderParameterNames();
	}

	// Java 1.6+ @Override
	public Enumeration<QName> getPublishingEventQNames() {
		return wrappedPortletConfig.getPublishingEventQNames();
	}

	// Java 1.6+ @Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return wrappedPortletConfig.getResourceBundle(locale);
	}

	// Java 1.6+ @Override
	public Enumeration<Locale> getSupportedLocales() {
		return wrappedPortletConfig.getSupportedLocales();
	}

	// Java 1.6+ @Override
	public PortletConfig getWrapped() {
		return wrappedPortletConfig;
	}
}
