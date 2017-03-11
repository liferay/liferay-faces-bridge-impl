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

import java.util.Map;

import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.filter.BridgePortletContextFactory;

import com.liferay.faces.bridge.context.internal.IncongruityContext;
import com.liferay.faces.bridge.context.internal.IncongruityContextFactory;
import com.liferay.faces.bridge.helper.internal.PortletModeHelper;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScopeCache;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScopeCacheFactory;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScopeFactory;
import com.liferay.faces.bridge.util.internal.ViewUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class BridgePhaseBaseImpl implements BridgePhase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseBaseImpl.class);

	// Private Constants
	private static final String PARAM_BRIDGE_REQUEST_SCOPE_ID = "com.liferay.faces.bridge.bridgeRequestScopeId";

	// Protected Data Members
	protected BridgeConfig bridgeConfig;
	protected BridgeRequestScope bridgeRequestScope;
	protected BridgeRequestScopeCache bridgeRequestScopeCache;
	protected boolean bridgeRequestScopePreserved;
	protected FacesContext facesContext;
	protected IncongruityContext incongruityContext;
	protected Lifecycle facesLifecycle;
	protected PortletConfig portletConfig;
	protected PortletContext portletContext;
	protected String portletName;

	// Private Data Members
	private FacesContextFactory facesContextFactory;

	public BridgePhaseBaseImpl(PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		this.portletConfig = portletConfig;
		this.bridgeConfig = bridgeConfig;
		this.portletName = portletConfig.getPortletName();
		this.bridgeRequestScopePreserved = PortletConfigParam.BridgeRequestScopePreserved.getBooleanValue(
				portletConfig);

		this.portletContext = BridgePortletContextFactory.getPortletContextInstance(portletConfig.getPortletContext());

		// Initialize the incongruity context implementation.
		this.incongruityContext = IncongruityContextFactory.getIncongruityContextInstance();

		// Get the bridge request scope cache from the factory.
		this.bridgeRequestScopeCache = BridgeRequestScopeCacheFactory.getBridgeRequestScopeCacheInstance(
				portletContext);

		// Get the default lifecycle instance from the factory.
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(
				FactoryFinder.LIFECYCLE_FACTORY);
		String lifecycleId = this.portletContext.getInitParameter(Bridge.LIFECYCLE_ID);

		if (lifecycleId == null) {
			lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
		}

		this.facesLifecycle = lifecycleFactory.getLifecycle(lifecycleId);
	}

	protected abstract void removeBridgeContextAttribute(PortletRequest portletRequest);

	protected abstract void setBridgeContextAttribute(PortletRequest portletRequest);

	protected void cleanup(PortletRequest portletRequest) {

		if (facesContext != null) {
			facesContext.release();
		}

		// Cleanup request attributes.
		if (portletRequest != null) {
			removeBridgeContextAttribute(portletRequest);
			portletRequest.removeAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);
			portletRequest.removeAttribute(PortletConfig.class.getName());
			portletRequest.removeAttribute(BridgeConfig.class.getName());
			portletRequest.removeAttribute(BridgeRequestScope.class.getName());
			portletRequest.removeAttribute(IncongruityContext.class.getName());
		}
	}

	protected FacesContext getFacesContext(PortletRequest portletRequest, PortletResponse portletResponse,
		Lifecycle lifecycle) {

		FacesContext newFacesContext = getFacesContextFactory().getFacesContext(portletContext, portletRequest,
				portletResponse, lifecycle);

		// TCK TestPage203 (JSF_ELTest) ensure that the #{facesContext} implicit object is set to the current instance.
		ELContext elContext = newFacesContext.getELContext();
		elContext.putContext(FacesContext.class, newFacesContext);

		return newFacesContext;
	}

	protected FacesContextFactory getFacesContextFactory() {

		if (facesContextFactory == null) {
			facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		}

		return facesContextFactory;
	}

	protected String getFacesViewId(ExternalContext externalContext) {

		String viewId = externalContext.getRequestPathInfo();

		if (viewId == null) {
			viewId = externalContext.getRequestServletPath();
		}

		return viewId;
	}

	protected void indicateNamespacingToConsumers(UIViewRoot uiViewRoot, PortletResponse portletResponse) {

		if (uiViewRoot != null) {

			// This method helps satisfy the namespacing requirements of Section 6.6 of the specification. It might be
			// the case that the consumer (portal engine / portlet container) needs to know if all of the form fields
			// have been namespaced properly. If that's the case, then it can check for the existence of the
			// "X-JAVAX-PORTLET-FACES-NAMESPACED-RESPONSE" property on the response, which will be set to "true" if the
			// UIViewRoot is annotated with {@link PortletNamingContainer}.
			if (uiViewRoot.getClass().getAnnotation(PortletNamingContainer.class) != null) {
				portletResponse.addProperty(Bridge.PORTLET_NAMESPACED_RESPONSE_PROPERTY, Boolean.TRUE.toString());
			}
		}
		else {

			// http://issues.liferay.com/browse/FACES-267 Sometimes there are requests that the bridge may see as valid
			// ResourceRequests (e.g. related to Ajax Push) where a ViewRoot might not be available -- this is not an
			// error.
			logger.debug("UIViewRoot is null -- might be push related");
		}
	}

	protected void init(PortletRequest portletRequest, PortletResponse portletResponse,
		Bridge.PortletPhase portletPhase) {

		// Save the Bridge.PortletPhase as a request attribute so that it can be picked up by the
		// BridgeRequestAttributeListener.
		portletRequest.setAttribute(Bridge.PORTLET_LIFECYCLE_PHASE, portletPhase);

		// Save the PortletConfig as a request attribute.
		portletRequest.setAttribute(PortletConfig.class.getName(), portletConfig);

		// Save the BridgeConfig as a request attribute.
		portletRequest.setAttribute(BridgeConfig.class.getName(), bridgeConfig);

		// Initialize the bridge request scope.
		initBridgeRequestScope(portletRequest, portletResponse, portletPhase);

		// Save the BridgeRequestScope as a request attribute.
		portletRequest.setAttribute(BridgeRequestScope.class.getName(), bridgeRequestScope);

		// Save the IncongruityContext as a request attribute.
		portletRequest.setAttribute(IncongruityContext.class.getName(), incongruityContext);

		// Save the BridgeContext as a request attribute for legacy versions of ICEfaces.
		setBridgeContextAttribute(portletRequest);

		// Get the FacesContext.
		facesContext = getFacesContext(portletRequest, portletResponse, facesLifecycle);

		// If not set by a previous request, then set the default viewIdHistory for the portlet modes.
		for (String portletMode : PortletModeHelper.PORTLET_MODE_NAMES) {

			String attributeName = Bridge.VIEWID_HISTORY + "." + portletMode;
			PortletSession portletSession = portletRequest.getPortletSession();

			if (portletSession.getAttribute(attributeName) == null) {
				Map<String, String> defaultViewIdMap = ViewUtil.getDefaultViewIdMap(portletConfig);
				portletSession.setAttribute(attributeName, defaultViewIdMap.get(portletMode));
			}
		}
	}

	protected void initBridgeRequestScope(PortletRequest portletRequest, PortletResponse portletResponse,
		Bridge.PortletPhase portletPhase) {

		boolean bridgeRequestScopeEnabled = true;

		if (portletPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
			bridgeRequestScopeEnabled = PortletConfigParam.BridgeRequestScopeAjaxEnabled.getBooleanValue(portletConfig);
		}

		if (bridgeRequestScopeEnabled) {

			// Determine if there is a bridge request scope "id" saved as a render parameter. Note that in order to
			// avoid collisions with bridge request scopes for other portlets, the render parameter name has to be
			// namespaced with the portlet name.
			String portletName = portletConfig.getPortletName();
			String bridgeRequestScopeKey = portletName + PARAM_BRIDGE_REQUEST_SCOPE_ID;

			// If there is a render parameter value found for the "id", then return the cached bridge request scope
			// associated with the "id".
			String bridgeRequestScopeId = portletRequest.getParameter(bridgeRequestScopeKey);

			if (bridgeRequestScopeId != null) {

				bridgeRequestScope = bridgeRequestScopeCache.get(bridgeRequestScopeId);

				if (bridgeRequestScope != null) {
					logger.debug("Found render parameter name=[{0}] value=[{1}] and cached bridgeRequestScope=[{2}]",
						bridgeRequestScopeKey, bridgeRequestScopeId, bridgeRequestScope);
				}
				else {

					if (bridgeRequestScopePreserved) {
						logger.error(
							"Found render parameter name=[{0}] value=[{1}] BUT bridgeRequestScope is NOT in the cache",
							bridgeRequestScopeKey, bridgeRequestScopeId);
					}
				}
			}

			// Otherwise, if there is a portlet session attribute found for the "id", then return the cached bridge
			// request scope associated with the "id". Note: This occurs after an Ajax-based ResourceRequest so that
			// non-excluded request attributes can be picked up by a subsequent RenderRequest.
			if (bridgeRequestScope == null) {

				// TCK TestPage071: nonFacesResourceTest
				// TCK TestPage073: scopeAfterRedisplayResourcePPRTest
				PortletSession portletSession = portletRequest.getPortletSession();
				bridgeRequestScopeId = (String) portletSession.getAttribute(bridgeRequestScopeKey);

				if (bridgeRequestScopeId != null) {

					portletSession.removeAttribute(bridgeRequestScopeKey);

					bridgeRequestScope = bridgeRequestScopeCache.get(bridgeRequestScopeId);

					if (bridgeRequestScope != null) {

						logger.debug(
							"Found (and removed) session-attribute name=[{0}] value=[{1}] and cached bridgeRequestScope=[{2}]",
							bridgeRequestScopeKey, bridgeRequestScopeId, bridgeRequestScope);

						if (portletResponse instanceof StateAwareResponse) {
							logger.debug("Setting former session-attribute as render parameter name=[{0}] value=[{1}]",
								bridgeRequestScopeKey, bridgeRequestScopeId);

							StateAwareResponse stateAwareResponse = (StateAwareResponse) portletResponse;
							stateAwareResponse.setRenderParameter(bridgeRequestScopeKey, bridgeRequestScopeId);
						}
					}
					else {

						logger.error(
							"Found session attribute name=[{0}] value=[{1}] but bridgeRequestScope is not in the cache",
							bridgeRequestScopeKey, bridgeRequestScopeId);
					}
				}
			}

			// Otherwise, return a new factory created instance.
			if (bridgeRequestScope == null) {
				bridgeRequestScope = BridgeRequestScopeFactory.getBridgeRequestScopeInstance(portletRequest,
						portletConfig, bridgeConfig);
			}
		}
	}

	protected void maintainBridgeRequestScope(PortletRequest portletRequest, PortletResponse portletResponse,
		BridgeRequestScope.Transport bridgeRequestScopeTransport) {

		String bridgeRequestScopeId = bridgeRequestScope.getId();

		bridgeRequestScopeCache.put(bridgeRequestScopeId, bridgeRequestScope);

		String bridgeRequestScopeKey = portletName + PARAM_BRIDGE_REQUEST_SCOPE_ID;

		if (bridgeRequestScopeTransport == BridgeRequestScope.Transport.PORTLET_SESSION_ATTRIBUTE) {

			// TCK TestPage071: nonFacesResourceTest
			// TCK TestPage073: scopeAfterRedisplayResourcePPRTest
			PortletSession portletSession = portletRequest.getPortletSession(true);
			portletSession.setAttribute(bridgeRequestScopeKey, bridgeRequestScopeId);
		}
		else {

			if (portletResponse instanceof StateAwareResponse) {
				logger.debug("Setting render parameter name=[{0}] value=[{1}]", bridgeRequestScopeKey,
					bridgeRequestScopeId);

				try {
					StateAwareResponse stateAwareResponse = (StateAwareResponse) portletResponse;
					stateAwareResponse.setRenderParameter(bridgeRequestScopeKey, bridgeRequestScopeId);
				}
				catch (IllegalStateException e) {

					// If a redirect occurred, then swallow/ignore the IllegalStateException
					if (bridgeRequestScope.isRedirectOccurred()) {

						// The Portlet API JavaDocs indicate that StateAwareResponse.setRenderParameter(String, String)
						// must throw an IllegalStateException if ActionResponse.sendRedirect(String) was previously
						// called. The JSR 329 TCK TestPage039 (requestNoScopeOnRedirectTest) and TestPage176
						// (redirectActionTest) both perform pseudo-redirects (effectively treated like navigation-rules
						// from one JSF viewId to another). Since the tests don't actually call
						// ActionResponse.sendRedirect(String), this condition is never reached by the TCK. However,
						// this condition is a real-world use-case and so the IllegalStateException must be
						// swallowed/ignored here so that portlet lifecycle processing is able to continue. For more
						// information, see: http://issues.liferay.com/browse/FACES-1367
					}

					// Otherwise throw the IllegalStateException.
					else {
						throw e;
					}
				}
			}
		}
	}
}
