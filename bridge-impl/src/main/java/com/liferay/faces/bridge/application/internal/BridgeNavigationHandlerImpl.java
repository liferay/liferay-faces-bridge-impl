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
package com.liferay.faces.bridge.application.internal;

import java.util.Iterator;
import java.util.Map;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationCase;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.faces.Bridge;
import jakarta.portlet.faces.BridgeFactoryFinder;
import jakarta.portlet.faces.BridgeURL;
import jakarta.portlet.faces.BridgeURLFactory;

import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.bridge.util.internal.ViewUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeNavigationHandlerImpl extends BridgeNavigationHandlerCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeNavigationHandlerImpl.class);

	public BridgeNavigationHandlerImpl(NavigationHandler navigationHandler) {
		super(navigationHandler);
	}

	@Override
	public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {

		logger.debug("fromAction=[{0}] outcome=[{1}]", fromAction, outcome);

		String queryString = null;
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		String viewId = uiViewRoot.getViewId();

		if (viewId != null) {

			int pos = viewId.indexOf("?");

			if (pos > 0) {
				queryString = viewId.substring(pos);
				viewId = viewId.substring(0, pos);
				uiViewRoot.setViewId(viewId);
			}
		}

		NavigationCase navigationCase = getNavigationCase(facesContext, fromAction, outcome);

		// Ask the wrapped NavigationHandler to perform the navigation.
		getWrappedNavigationHandler().handleNavigation(facesContext, fromAction, outcome);

		if (queryString != null) {
			uiViewRoot.setViewId(viewId.concat(queryString));
		}

		if (navigationCase != null) {

			// Hack for http://jira.icesoft.org/browse/ICE-7996
			Iterator<FacesMessage> itr = facesContext.getMessages();

			while (itr.hasNext()) {
				FacesMessage facesMessage = itr.next();

				if (facesMessage.getDetail().contains("Unable to find matching navigation case")) {
					logger.warn("Removed bogus FacesMessage caused by http://jira.icesoft.org/browse/ICE-7996");
					itr.remove();
				}
			}

			// If the navigation-case is NOT a redirect, then directly encode the {@link PortletMode} and {@link
			// WindowState} to the response. Don't need to worry about the redirect case here because that's handled in
			// the ExternalContext#redirect(String) method. It would be nice to handle the redirect case here but it
			// needs to stay in ExternalContext since it's possible for developers to call
			// ExternalContext.redirect(String) directly from their application.
			if (!navigationCase.isRedirect()) {

				String toViewId = navigationCase.getToViewId(facesContext);

				if (toViewId != null) {

					ExternalContext externalContext = facesContext.getExternalContext();
					PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

					if (portletResponse instanceof StateAwareResponse) {

						PortletContext portletContext = (PortletContext) externalContext.getContext();
						BridgeURLFactory bridgeURLFactory = (BridgeURLFactory) BridgeFactoryFinder.getFactory(
								portletContext, BridgeURLFactory.class);

						try {
							BridgeURL bridgeActionURL = bridgeURLFactory.getBridgeActionURL(facesContext, toViewId);

							BridgeNavigationCase bridgeNavigationCase = new BridgeNavigationCaseImpl(navigationCase);
							String portletMode = bridgeNavigationCase.getPortletMode();

							if (portletMode != null) {
								bridgeActionURL.setParameter(Bridge.PORTLET_MODE_PARAMETER, portletMode);
							}

							String windowState = bridgeNavigationCase.getWindowState();

							if (windowState != null) {
								bridgeActionURL.setParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER, windowState);
							}

							PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
							BridgeNavigationUtil.navigate(portletRequest, (StateAwareResponse) portletResponse,
								bridgeActionURL.getParameterMap());
						}
						catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
				}
			}
		}
	}

	@Override
	public void handleNavigation(FacesContext facesContext, PortletMode fromPortletMode, PortletMode toPortletMode) {

		if ((fromPortletMode != null) && !fromPortletMode.equals(toPortletMode)) {

			logger.debug("fromPortletMode=[{0}] toPortletMode=[{1}]", fromPortletMode, toPortletMode);

			String currentViewId = facesContext.getViewRoot().getViewId();
			PortletConfig portletConfig = RequestMapUtil.getPortletConfig(facesContext);
			Map<String, String> defaultViewIdMap = ViewUtil.getDefaultViewIdMap(portletConfig);
			String portletModeViewId = defaultViewIdMap.get(toPortletMode.toString());

			if ((currentViewId != null) && (portletModeViewId != null)) {

				if (!currentViewId.equals(portletModeViewId)) {

					logger.debug("Navigating to viewId=[{0}]", portletModeViewId);

					ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
					UIViewRoot viewRoot = viewHandler.createView(facesContext, portletModeViewId);

					if (viewRoot != null) {
						facesContext.setViewRoot(viewRoot);
						partialViewContextRenderAll(facesContext);
					}
				}
			}
		}
	}
}
