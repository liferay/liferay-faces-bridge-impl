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
package com.liferay.faces.bridge.tck.portlet;

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

public class PortletConfigWrapper implements PortletConfig, Wrapper<PortletConfig> {

	private PortletConfig wrappedPortletConfig;

	public PortletConfigWrapper(PortletConfig portletConfig) {
		this.wrappedPortletConfig = portletConfig;
	}

	public Map<String, String[]> getContainerRuntimeOptions() {
		return wrappedPortletConfig.getContainerRuntimeOptions();
	}

	public String getDefaultNamespace() {
		return wrappedPortletConfig.getDefaultNamespace();
	}

	public String getInitParameter(String arg0) {
		return wrappedPortletConfig.getInitParameter(arg0);
	}

	public Enumeration<String> getInitParameterNames() {
		return wrappedPortletConfig.getInitParameterNames();
	}

	public PortletContext getPortletContext() {
		return wrappedPortletConfig.getPortletContext();
	}

	public String getPortletName() {

		String portletName = wrappedPortletConfig.getPortletName();

		// Example: Transform "chapter5_2TestsisPostbackTestportlet" to "chapter5_2Tests-isPostbackTest-portlet"
		portletName = portletName.replaceFirst("Tests", "Tests-");
		portletName = portletName.replaceAll("portlet$", "-portlet");

		return portletName;
	}

	public Enumeration<QName> getProcessingEventQNames() {
		return wrappedPortletConfig.getProcessingEventQNames();
	}

	public Enumeration<String> getPublicRenderParameterNames() {
		return wrappedPortletConfig.getPublicRenderParameterNames();
	}

	public Enumeration<QName> getPublishingEventQNames() {
		return wrappedPortletConfig.getPublishingEventQNames();
	}

	public ResourceBundle getResourceBundle(Locale arg0) {
		return wrappedPortletConfig.getResourceBundle(arg0);
	}

	public Enumeration<Locale> getSupportedLocales() {
		return wrappedPortletConfig.getSupportedLocales();
	}

	// Java 1.6+ @Override
	public PortletConfig getWrapped() {
		return wrappedPortletConfig;
	}
}
