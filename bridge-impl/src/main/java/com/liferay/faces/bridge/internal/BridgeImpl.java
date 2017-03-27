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
package com.liferay.faces.bridge.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeConfigFactory;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeNotAFacesRequestException;
import javax.portlet.faces.BridgeUninitializedException;
import javax.portlet.faces.filter.BridgePortletConfigFactory;

import com.liferay.faces.bridge.scope.internal.BridgeRequestScopeManager;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScopeManagerFactory;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeImpl extends BridgeCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeImpl.class);

	// Private Data Members
	private boolean initialized = false;
	private PortletConfig portletConfig;

	@Override
	public void destroy() {
		initialized = false;

		// FACES-1450: Surround with try/catch block in order to prevent hot re-deploys from failing in Liferay Portal.
		try {

			BridgeRequestScopeManagerFactory bridgeRequestScopeManagerFactory = (BridgeRequestScopeManagerFactory)
				BridgeFactoryFinder.getFactory(portletConfig.getPortletContext(),
					BridgeRequestScopeManagerFactory.class);

			// Note: If the bridge request scope manager factory is null, that means that the servlet container that
			// underlies the portlet container already destroyed the context (including all of the context attributes
			// such as factory instances). This condition represents a design choice by the portal vendor rather than an
			// error.
			if (bridgeRequestScopeManagerFactory != null) {

				BridgeRequestScopeManager bridgeRequestScopeManager =
					bridgeRequestScopeManagerFactory.getBridgeRequestScopeManager();
				bridgeRequestScopeManager.removeBridgeRequestScopesByPortlet(portletConfig);
			}
			else {
				logger.debug(
					"The portlet container is designed to destroy the webapp context attributes prior to calling the portlet's destroy method.");
			}
		}
		catch (Throwable t) {
			logger.warn(t.getMessage());
		}
	}

	@Override
	public void doFacesRequest(ActionRequest actionRequest, ActionResponse actionResponse)
		throws BridgeDefaultViewNotSpecifiedException, BridgeUninitializedException, BridgeException {

		checkNull(actionRequest, actionResponse);

		if (initialized) {

			String nonFacesTargetPath = actionRequest.getParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER);

			if (nonFacesTargetPath != null) {
				throw new BridgeNotAFacesRequestException(nonFacesTargetPath);
			}

			PortletConfig wrappedPortletConfig = BridgePortletConfigFactory.getPortletConfigInstance(portletConfig);
			BridgeConfig bridgeConfig = BridgeConfigFactory.getBridgeConfigInstance(wrappedPortletConfig);
			BridgePhase bridgePhase = new BridgePhaseActionImpl(actionRequest, actionResponse, wrappedPortletConfig,
					bridgeConfig);
			bridgePhase.execute();
		}
		else {
			throw new BridgeUninitializedException();
		}
	}

	@Override
	public void doFacesRequest(EventRequest eventRequest, EventResponse eventResponse)
		throws BridgeUninitializedException, BridgeException {

		checkNull(eventRequest, eventResponse);

		if (initialized) {

			String nonFacesTargetPath = eventRequest.getParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER);

			if (nonFacesTargetPath != null) {
				throw new BridgeNotAFacesRequestException(nonFacesTargetPath);
			}

			PortletConfig wrappedPortletConfig = BridgePortletConfigFactory.getPortletConfigInstance(portletConfig);
			BridgeConfig bridgeConfig = BridgeConfigFactory.getBridgeConfigInstance(wrappedPortletConfig);
			BridgePhase bridgePhase = new BridgePhaseEventImpl(eventRequest, eventResponse, wrappedPortletConfig,
					bridgeConfig);
			bridgePhase.execute();
		}
		else {
			throw new BridgeUninitializedException();
		}
	}

	@Override
	public void doFacesRequest(RenderRequest renderRequest, RenderResponse renderResponse)
		throws BridgeDefaultViewNotSpecifiedException, BridgeUninitializedException, BridgeException {

		checkNull(renderRequest, renderResponse);

		if (initialized) {

			String nonFacesTargetPath = renderRequest.getParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER);

			if (nonFacesTargetPath != null) {
				throw new BridgeNotAFacesRequestException(nonFacesTargetPath);
			}

			PortletConfig wrappedPortletConfig = BridgePortletConfigFactory.getPortletConfigInstance(portletConfig);
			BridgeConfig bridgeConfig = BridgeConfigFactory.getBridgeConfigInstance(wrappedPortletConfig);
			BridgePhase bridgePhase = new BridgePhaseRenderImpl(renderRequest, renderResponse, wrappedPortletConfig,
					bridgeConfig);
			bridgePhase.execute();
		}
		else {
			throw new BridgeUninitializedException();
		}
	}

	@Override
	public void doFacesRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws BridgeUninitializedException, BridgeException {

		checkNull(resourceRequest, resourceResponse);

		if (initialized) {
			PortletConfig wrappedPortletConfig = BridgePortletConfigFactory.getPortletConfigInstance(portletConfig);
			BridgeConfig bridgeConfig = BridgeConfigFactory.getBridgeConfigInstance(wrappedPortletConfig);
			BridgePhase bridgePhase = new BridgePhaseResourceImpl(resourceRequest, resourceResponse,
					wrappedPortletConfig, bridgeConfig);
			bridgePhase.execute();
		}
		else {
			throw new BridgeUninitializedException();
		}
	}

	public String getTitle() {
		return BridgeImpl.class.getPackage().getImplementationTitle();
	}

	public String getVersion() {
		return BridgeImpl.class.getPackage().getImplementationVersion();
	}

	@Override
	public void init(PortletConfig portletConfig) throws BridgeException {
		StringBuilder logMessage = new StringBuilder();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
		Calendar calendar = new GregorianCalendar();
		String timestamp = dateFormat.format(calendar.getTime());
		logMessage.append(timestamp);
		logMessage.append(" INFO  [BridgeImpl] Initializing ");
		logMessage.append(getTitle());
		logMessage.append(" ");
		logMessage.append(getVersion());
		System.out.println(logMessage.toString());
		this.initialized = true;
		this.portletConfig = portletConfig;
	}

	@Override
	protected void checkNull(PortletRequest portletRequest, PortletResponse portletResponse) {

		// Null check required by the TCK.
		if (portletRequest == null) {
			throw new NullPointerException("portletRequest was null");
		}

		// Null check required by the TCK.
		if (portletResponse == null) {
			throw new NullPointerException("portletResponse was null");
		}
	}

	@Override
	protected PortletConfig getPortletConfig() {
		return portletConfig;
	}

	@Override
	protected boolean isInitialized() {
		return initialized;
	}
}
