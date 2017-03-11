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
package com.liferay.faces.bridge.context.internal;

import java.util.List;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeURL;
import javax.portlet.faces.BridgeURLFactory;

import com.liferay.faces.bridge.internal.BridgeConfigAttributeMap;
import com.liferay.faces.bridge.internal.BridgeURIFactory;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.util.config.ConfiguredServletMapping;


/**
 * @author  Neil Griffin
 */
public abstract class ExternalContextBridgeBase extends ExternalContext {

	// Private Data Members
	protected BridgeConfig bridgeConfig;
	protected BridgeRequestScope bridgeRequestScope;
	protected BridgeURIFactory bridgeURIFactory;
	protected BridgeURLFactory bridgeURLFactory;
	protected List<String> configuredSuffixes;
	protected List<ConfiguredServletMapping> configuredFacesServletMappings;
	protected IncongruityContext incongruityContext;
	protected boolean manageIncongruities;
	protected PortletConfig portletConfig;
	protected PortletContext portletContext;
	protected PortletRequest portletRequest;
	protected PortletResponse portletResponse;

	@SuppressWarnings("unchecked")
	public ExternalContextBridgeBase(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {
		this.portletContext = portletContext;
		this.portletRequest = portletRequest;
		this.portletResponse = portletResponse;
		this.bridgeConfig = RequestMapUtil.getBridgeConfig(portletRequest);
		this.incongruityContext = (IncongruityContext) portletRequest.getAttribute(IncongruityContext.class.getName());
		this.portletConfig = RequestMapUtil.getPortletConfig(portletRequest);
		this.manageIncongruities = PortletConfigParam.ManageIncongruities.getBooleanValue(portletConfig);
		this.bridgeRequestScope = (BridgeRequestScope) portletRequest.getAttribute(BridgeRequestScope.class.getName());
		this.bridgeURIFactory = (BridgeURIFactory) BridgeFactoryFinder.getFactory(BridgeURIFactory.class);
		this.bridgeURLFactory = (BridgeURLFactory) BridgeFactoryFinder.getFactory(BridgeURLFactory.class);
		this.configuredFacesServletMappings = (List<ConfiguredServletMapping>) bridgeConfig.getAttributes().get(
				BridgeConfigAttributeMap.CONFIGURED_FACES_SERVLET_MAPPINGS);
		this.configuredSuffixes = (List<String>) bridgeConfig.getAttributes().get(
				BridgeConfigAttributeMap.CONFIGURED_SUFFIXES);
	}

	@Override
	public String encodeActionURL(String url) {

		if (url == null) {
			throw new NullPointerException();
		}
		else {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			try {
				BridgeURL bridgeActionURL = bridgeURLFactory.getBridgeActionURL(facesContext, url);

				return bridgeActionURL.toString();
			}
			catch (BridgeException e) {
				throw new FacesException(e);
			}
		}
	}
}
