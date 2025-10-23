/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.faces.application.NavigationHandler;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgeEventHandlerFactory;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.event.EventNavigationResult;
import javax.portlet.faces.filter.BridgePortletRequestFactory;
import javax.portlet.faces.filter.BridgePortletResponseFactory;

import com.liferay.faces.bridge.event.EventPayloadWrapper;
import com.liferay.faces.bridge.event.internal.IPCPhaseListener;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePhaseEventImpl extends BridgePhaseCompat_2_2_Impl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseEventImpl.class);

	// Private Data Members
	private EventRequest eventRequest;
	private EventResponse eventResponse;

	public BridgePhaseEventImpl(EventRequest eventRequest, EventResponse eventResponse, PortletConfig portletConfig,
		BridgeConfig bridgeConfig) {

		super(portletConfig, bridgeConfig);

		this.eventRequest = BridgePortletRequestFactory.getEventRequestInstance(eventRequest, eventResponse,
				portletConfig, bridgeConfig);

		this.eventResponse = BridgePortletResponseFactory.getEventResponseInstance(eventRequest, eventResponse,
				portletConfig, bridgeConfig);
	}

	// Java 1.6+ @Override
	@Override
	public void execute() throws BridgeDefaultViewNotSpecifiedException, BridgeException {

		logger.debug(Logger.SEPARATOR);
		logger.debug("execute(EventRequest, EventResponse) portletName=[{0}]", portletName);

		String bridgeEventHandlerAttributeName = Bridge.BRIDGE_PACKAGE_PREFIX + portletName + "." +
			Bridge.BRIDGE_EVENT_HANDLER;
		BridgeEventHandler bridgeEventHandler = (BridgeEventHandler) portletContext.getAttribute(
				bridgeEventHandlerAttributeName);

		if (bridgeEventHandler == null) {
			bridgeEventHandler = BridgeEventHandlerFactory.getBridgeEventHandlerInstance(portletConfig);
		}

		try {

			// If there is a bridgeEventHandler registered in portlet.xml, then
			if (bridgeEventHandler != null) {

				init(eventRequest, eventResponse, Bridge.PortletPhase.EVENT_PHASE);

				// Restore the BridgeRequestScope that may have started during the ACTION_PHASE.
				bridgeRequestScope.restoreState(facesContext);

				// PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-202
				bridgeRequestScope.setPortletMode(eventRequest.getPortletMode());

				// Execute the JSF lifecycle so that ONLY the RESTORE_VIEW phase executes (note that this this is
				// accomplished by the IPCPhaseListener).
				facesLifecycle.execute(facesContext);
				throwQueuedExceptionIfNecessary(facesContext);

				// Set a flag on the bridge request scope indicating that the Faces Lifecycle has executed.
				bridgeRequestScope.setFacesLifecycleExecuted(true);

				// If there is a bridgeEventHandler registered in portlet.xml, then invoke the handler so that it
				// can process the event payload.

				logger.debug("Invoking {0} for class=[{1}]", bridgeEventHandlerAttributeName,
					bridgeEventHandler.getClass());

				Event event = eventRequest.getEvent();
				EventNavigationResult eventNavigationResult = bridgeEventHandler.handleEvent(facesContext, event);

				if (eventNavigationResult != null) {
					String oldViewId = facesContext.getViewRoot().getViewId();
					String fromAction = eventNavigationResult.getFromAction();
					String outcome = eventNavigationResult.getOutcome();
					logger.debug("Invoking navigationHandler fromAction=[{0}] outcome=[{1}]", fromAction, outcome);

					NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
					navigationHandler.handleNavigation(facesContext, fromAction, outcome);

					String newViewId = facesContext.getViewRoot().getViewId();
					bridgeRequestScope.setNavigationOccurred(!oldViewId.equals(newViewId));
				}

				// Process the outgoing public render parameters.
				// TCK (eventControllerTest)
				processOutgoingPublicRenderParameters(facesLifecycle);

				// Save the faces view root and any messages in the faces context so that they can be restored during
				// the RENDER_PHASE of the portlet lifecycle.
				bridgeRequestScope.saveState(facesContext);

				// In accordance with Section 5.2.5 of the Spec, if a portlet mode change has occurred, then the
				// bridge request scope must not be maintained.
				//
				// NOTE: THIS REQUIREMENT IS HANDLED BY THE RENDER PHASE since the current design relies on
				// BridgeRequestScope.restoreState(FacesContext) to detect portlet mode changes in some cases.
				//
				// PROPOSE-FOR-BRIDGE3-SPEC: Although the spec does not mention the redirect case, the bridge request
				// scope must not be maintained if a redirect has occurred.
				//
				// REDIRECT:
				// ---------
				// TCK (eventScopeNotRestoredRedirectTest)
				// TCK (redirectEventTest)
				//
				// PORTLET MODE CHANGED:
				// ---------------------
				// TCK (eventScopeNotRestoredModeChangedTest)
				// TCK (encodeActionURLWithModeEventTest)
				//
				// NON-REDIRECT / SAME PORTLET MODE:
				// ---------------------------------
				// TCK (eventPhaseListenerTest)
				// TCK (eventScopeRestoredTest)
				// TCK (eventControllerTest)
				// TCK (encodeActionURLJSFViewEventTest)
				// TCK (encodeActionURLWithParamEventTest)
				// TCK (encodeActionURLWithInvalidModeEventTest),
				// TCK (encodeActionURLWithWindowStateEventTest)
				// TCK (encodeActionURLWithInvalidWindowStateEventTest)
				// TCK (getRequestHeaderMapEventTest)
				// TCK (getRequestHeaderValuesMapEventTest)
				// TCK (getRequestCharacterEncodingEventTest)
				// TCK (getRequestContentTypeEventTest)
				// TCK (getResponseCharacterEncodingEventTest)
				// TCK (getResponseContentTypeEventTest)

				// Assume that the bridge request scope should be maintained from the EVENT_PHASE into the
				// RENDER_PHASE by utilizing render parameters.
				BridgeRequestScope.Transport bridgeRequestScopeTransport =
					BridgeRequestScope.Transport.RENDER_PARAMETER;
				Serializable eventPayload = event.getValue();

				// FACES-1465: If the portlet developer intentionally wrapped the event payload is with an
				// EventPayloadWrapper, then determine whether or not this is happening during a redirect. If this is
				// the case, then the bridge request scope must be maintained from the EVENT_PHASE into the RENDER_PHASE
				// by utilizing a portlet session attribute. This is because render parameters will not survive a
				// redirect.
				if ((eventPayload != null) && (eventPayload instanceof EventPayloadWrapper)) {
					EventPayloadWrapper eventPayloadWrapper = (EventPayloadWrapper) eventPayload;

					if (eventPayloadWrapper.isRedirect()) {

						bridgeRequestScopeTransport = BridgeRequestScope.Transport.PORTLET_SESSION_ATTRIBUTE;
					}
				}

				maintainBridgeRequestScope(eventRequest, eventResponse, bridgeRequestScopeTransport);
			}

			// Maintain the render parameters set in the ACTION_PHASE so that they carry over to the RENDER_PHASE.
			maintainRenderParameters(eventRequest, eventResponse);

			// Spec 6.6 (Namespacing)
			if (bridgeEventHandler != null) {
				indicateNamespacingToConsumers(facesContext.getViewRoot(), eventResponse);
			}
		}
		catch (Throwable t) {
			throw new BridgeException(t);
		}
		finally {

			if (bridgeEventHandler != null) {
				cleanup(eventRequest);
			}
		}
	}

	/**
	 * Maintains (copies) the render parameters found in the specified EventRequest to the specified EventResponse.
	 * FACES-1453: Since {@link EventResponse#setRenderParameters(EventRequest)} would end up clobbering existing
	 * public/private render parameters, it is necessary to iterate through all of them and only maintain the ones that
	 * don't already exist in {@link EventResponse#getRenderParameterMap()}.
	 */
	protected void maintainRenderParameters(EventRequest eventRequest, EventResponse eventResponse) {

		Map<String, String[]> existingResponseRenderParameterMap = eventResponse.getRenderParameterMap();

		// Maintain the public render parameters.
		Map<String, String[]> publicParameterMap = eventRequest.getPublicParameterMap();

		if (publicParameterMap != null) {

			Set<Map.Entry<String, String[]>> entrySet = publicParameterMap.entrySet();

			for (Map.Entry<String, String[]> mapEntry : entrySet) {
				String key = mapEntry.getKey();

				boolean alreadyExists = false;

				if (existingResponseRenderParameterMap != null) {
					alreadyExists = existingResponseRenderParameterMap.containsKey(key);
				}

				if (alreadyExists) {

					// FACES-1453: Avoid clobbering existing public render parameters set on the EventResponse.
					if (logger.isTraceEnabled()) {
						String[] existingValues = existingResponseRenderParameterMap.get(key);

						logger.trace(
							"Not maintaining public render parameter name=[{0}] values=[{1}] because it already exists",
							key, existingValues);
					}
				}
				else {
					String[] values = mapEntry.getValue();
					eventResponse.setRenderParameter(key, values);
					logger.trace("Maintaining public render parameter name=[{0}] values=[{1}]", key, values);
				}
			}
		}

		// Maintain the private render parameters.
		Map<String, String[]> privateParameterMap = eventRequest.getPrivateParameterMap();

		if (privateParameterMap != null) {
			Set<Map.Entry<String, String[]>> entrySet = privateParameterMap.entrySet();

			for (Map.Entry<String, String[]> mapEntry : entrySet) {
				String key = mapEntry.getKey();
				boolean alreadyExists = false;

				if (existingResponseRenderParameterMap != null) {
					alreadyExists = existingResponseRenderParameterMap.containsKey(key);
				}

				if (alreadyExists) {

					// FACES-1453: Avoid clobbering existing private render parameters set on the EventResponse.
					if (logger.isTraceEnabled()) {
						String[] existingValues = existingResponseRenderParameterMap.get(key);

						logger.trace(
							"Not maintaining private render parameter name=[{0}] values=[{1}] because it already exists",
							key, existingValues);
					}
				}
				else {
					String[] values = mapEntry.getValue();
					eventResponse.setRenderParameter(key, values);
					logger.trace("Maintaining private render parameter name=[{0}] values=[{1}]", key, values);
				}
			}
		}
	}

	protected void processOutgoingPublicRenderParameters(Lifecycle lifecycle) {

		PhaseListener[] phaseListeners = lifecycle.getPhaseListeners();

		for (PhaseListener phaseListener : phaseListeners) {

			if (phaseListener instanceof IPCPhaseListener) {
				IPCPhaseListener ipcPhaseListener = (IPCPhaseListener) phaseListener;
				ipcPhaseListener.processOutgoingPublicRenderParameters(facesContext);

				break;
			}
		}
	}
}
