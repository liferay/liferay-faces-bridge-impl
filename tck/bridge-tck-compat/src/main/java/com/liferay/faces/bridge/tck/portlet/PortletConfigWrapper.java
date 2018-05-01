/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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


/**
 * NOTE: This class intentionally does NOT implement FacesWrapper&lt;PortletConfig&gt; in order to prevent
 * ELResolverImpl.resolveVariable(ELContext,String) from unwrapping the PortletConfig too much, which would cause a TCK
 * failure in TestPage201 (JSF_ELTest). This will not be necessary with JSR 378. For more information, see:
 * https://issues.liferay.com/browse/FACES-3108
 *
 * @author  Neil Griffin
 */
public abstract class PortletConfigWrapper implements PortletConfig {

	protected PortletConfig wrappedPortletConfig;

	public PortletConfigWrapper(PortletConfig portletConfig) {
		this.wrappedPortletConfig = portletConfig;
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		return wrappedPortletConfig.getContainerRuntimeOptions();
	}

	@Override
	public String getDefaultNamespace() {
		return wrappedPortletConfig.getDefaultNamespace();
	}

	@Override
	public String getInitParameter(String arg0) {
		return wrappedPortletConfig.getInitParameter(arg0);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return wrappedPortletConfig.getInitParameterNames();
	}

	@Override
	public PortletContext getPortletContext() {
		return wrappedPortletConfig.getPortletContext();
	}

	@Override
	public Enumeration<QName> getProcessingEventQNames() {
		return wrappedPortletConfig.getProcessingEventQNames();
	}

	@Override
	public Enumeration<String> getPublicRenderParameterNames() {
		return wrappedPortletConfig.getPublicRenderParameterNames();
	}

	@Override
	public Enumeration<QName> getPublishingEventQNames() {
		return wrappedPortletConfig.getPublishingEventQNames();
	}

	@Override
	public ResourceBundle getResourceBundle(Locale arg0) {
		return wrappedPortletConfig.getResourceBundle(arg0);
	}

	@Override
	public Enumeration<Locale> getSupportedLocales() {
		return wrappedPortletConfig.getSupportedLocales();
	}
}
