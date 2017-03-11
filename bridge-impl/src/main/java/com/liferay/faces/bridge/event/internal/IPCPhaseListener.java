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
package com.liferay.faces.bridge.event.internal;

import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgePublicRenderParameterHandler;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This inner-class is a phase listener that listens to the RESTORE_VIEW phase of the JSF lifecycle. It satisfies
 * multiple requirements in the Spec related to Portlet 2.0 Inter-Portlet Communication (IPC), including JSF lifecycle
 * handling of Public Render Parameters and Events. See the inline comments for more details about specific requirements
 * that are satisfied.
 *
 * @author  Neil Griffin
 */
public class IPCPhaseListener implements PhaseListener {

	// serialVersionUID
	private static final long serialVersionUID = 454155161145961729L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(IPCPhaseListener.class);

	@Override
	public void afterPhase(PhaseEvent phaseEvent) {

		FacesContext facesContext = phaseEvent.getFacesContext();

		PhaseId phaseId = phaseEvent.getPhaseId();

		if (phaseId == PhaseId.RESTORE_VIEW) {

			// Sections 5.2.4, 5.2.5, 5.2.6, and 5.2.7 require that there be a phase listener registered  that processes
			// incoming Public Render Parameters. This is to happen for all phases of the Portlet 2.0 lifecycle. The
			// phase listener is to execute after the RESTORE_VIEW phase of the JSF lifecycle completes, in accordance
			// with Section 5.3.2.
			processIncomingPublicRenderParameters(facesContext);

			// Section 5.2.5 and 6.4 of the JSR 329 Spec require that the phase listener short-circuit the JSF lifecycle
			// after the RESTORE_VIEW phase completes during the EVENT_PHASE of the Portlet 2.0 lifecycle.
			Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

			if (portletPhase == Bridge.PortletPhase.EVENT_PHASE) {
				facesContext.renderResponse();
			}
		}
		else if (phaseId == PhaseId.INVOKE_APPLICATION) {

			// Sections 5.2.4 and 5.3.3 require that there be a phase listener registered that processes outgoing Public
			// Render Parameters during the ACTION_PHASE and RENDER_PHASE of the Portlet 2.0 lifecycle. It is
			// appropriate to do this after the INVOKE_APPLICATION phase of the JSF lifecycle because that's where a JSF
			// backing bean action (or action listener) would have been called in order to populate the model.
			Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

			if ((portletPhase == Bridge.PortletPhase.ACTION_PHASE) ||
					(portletPhase == Bridge.PortletPhase.EVENT_PHASE)) {
				processOutgoingPublicRenderParameters(facesContext);
			}
		}
	}

	@Override
	public void beforePhase(PhaseEvent phaseEvent) {
		// This method is required by the PhaseListener interface but is not used.
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/**
	 * This method processes the "incoming" Public Render Parameters in accordance with Section 5.3.2 of the Spec.
	 */
	public void processIncomingPublicRenderParameters(FacesContext facesContext) {

		try {

			// If the specified context is valid, then proceed with processing incoming public render parameters
			// since this phase listener is being executed within the portlet lifecycle.
			if (facesContext != null) {

				// Section 5.3.2 requires the phase listener to inject the public render parameters into the
				// Model concern of the MVC design pattern (as in JSF model managed-beans) after RESTORE_VIEW
				// phase completes. This is accomplished below by evaluating the EL expressions found in the
				// <model-el>...</model-el> section of the WEB-INF/faces-config.xml file.
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				BridgeConfig bridgeConfig = RequestMapUtil.getBridgeConfig(portletRequest);
				Map<String, String[]> publicParameterMappings = bridgeConfig.getPublicParameterMappings();

				if (publicParameterMappings != null) {

					boolean invokeHandler = false;
					PortletConfig portletConfig = RequestMapUtil.getPortletConfig(portletRequest);
					String portletName = portletConfig.getPortletName();
					Map<String, String[]> publicParameterMap = portletRequest.getPublicParameterMap();
					Set<String> publicRenderParameterNames = publicParameterMappings.keySet();

					// For each of the public render parameters found in the WEB-INF/faces-config.xml file:
					for (String prefixedParameterName : publicRenderParameterNames) {

						String[] modelExpressions = publicParameterMappings.get(prefixedParameterName);

						if (modelExpressions != null) {

							String parameterPrefix;
							String nonPrefixedParameterName;

							int colonPos = prefixedParameterName.indexOf(":");

							if (colonPos > 0) {
								parameterPrefix = prefixedParameterName.substring(0, colonPos);
								nonPrefixedParameterName = prefixedParameterName.substring(colonPos + 1);
							}
							else {
								parameterPrefix = null;
								nonPrefixedParameterName = prefixedParameterName;
							}

							if (publicParameterMap.containsKey(nonPrefixedParameterName)) {

								for (String originalModelEL : modelExpressions) {

									String[] parameterValues = publicParameterMap.get(nonPrefixedParameterName);
									String parameterValue = null;

									if ((parameterValues != null) && (parameterValues.length > 0)) {
										parameterValue = parameterValues[0];
									}

									PublicRenderParameter publicRenderParameter = new PublicRenderParameterImpl(
											parameterPrefix, parameterValue, originalModelEL, portletName);

									if (logger.isTraceEnabled()) {
										logger.trace(
											"portletName=[{0}] public render parameter=[{1}] originalModelEL=[{2}] modifiedModelEL=[{3}] isForThisPortlet=[{4}]",
											portletName, nonPrefixedParameterName, originalModelEL,
											publicRenderParameter.getModifiedModelEL(),
											publicRenderParameter.isForThisPortlet());
									}

									if (publicRenderParameter.isForThisPortlet()) {

										logger.debug(
											"Injecting render parameter=[{0}] value=[{1}] into expression=[{2}]",
											nonPrefixedParameterName, parameterValue,
											publicRenderParameter.getModifiedModelEL());
										invokeHandler = publicRenderParameter.injectIntoModel(facesContext);
									}
									else {
										logger.debug(
											"NOT injecting render parameter=[{0}] value=[{1}] into expression=[{2}] because it is NOT for this portletName=[{3}]",
											nonPrefixedParameterName, parameterValue,
											publicRenderParameter.getModifiedModelEL(), portletName);
									}
								}
							}
							else {
								logger.debug(
									"NOT injecting render parameter=[{0}] because it is not found in the public parameter map",
									nonPrefixedParameterName);
							}
						}
					}

					// Section 5.3.2 also requires that if a bridgePublicRenderParameterHandler has been registered
					// in WEB-INF/portlet.xml, then the handler must be invoked so that it can perform any
					// processing that might be necessary.
					if (invokeHandler) {
						String bridgePublicRenderParameterHandlerAttributeName = Bridge.BRIDGE_PACKAGE_PREFIX +
							portletName + "." + Bridge.BRIDGE_PUBLIC_RENDER_PARAMETER_HANDLER;

						logger.trace("bridgePublicRenderParameterHandlerAttributeName=[{0}]",
							bridgePublicRenderParameterHandlerAttributeName);

						BridgePublicRenderParameterHandler bridgePublicRenderParameterHandler =
							(BridgePublicRenderParameterHandler) externalContext.getApplicationMap().get(
								bridgePublicRenderParameterHandlerAttributeName);

						if (bridgePublicRenderParameterHandler != null) {
							logger.debug("Invoking {0} for class=[{1}]", bridgePublicRenderParameterHandler,
								bridgePublicRenderParameterHandler.getClass());

							bridgePublicRenderParameterHandler.processUpdates(facesContext);
						}
					}
				}
			}
		}
		catch (Exception e) {

			// There's no point in throwing a RuntimeException of any kind like FacesException because the Faces
			// runtime will swallow it. So the best we can do is log the exception.
			logger.error(e);
		}
	}

	/**
	 * This method processes the "outgoing" Public Render Parameters in accordance with Section 5.3.3 of the Spec.
	 */
	public void processOutgoingPublicRenderParameters(FacesContext facesContext) {

		try {

			// If the specified context is valid, then proceed with processing incoming public render parameters
			// since this phase listener is being executed within the portlet lifecycle.
			if (facesContext != null) {

				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				StateAwareResponse stateAwareResponse = (StateAwareResponse) externalContext.getResponse();

				// Section 5.3.3 requires the phase listener to re-examine the public render parameters. For each one
				// that has been changed in the model, its new value must be set in the response, so that when the
				// RENDER_PHASE of the Portlet 2.0 lifecycle executes, this phase listener will be able to inject the
				// new value into the model of other portlets that are participating in the IPC.
				BridgeConfig bridgeConfig = RequestMapUtil.getBridgeConfig(portletRequest);
				Map<String, String[]> publicParameterMappings = bridgeConfig.getPublicParameterMappings();

				if (publicParameterMappings != null) {

					PortletConfig portletConfig = RequestMapUtil.getPortletConfig(portletRequest);
					String portletName = portletConfig.getPortletName();
					Map<String, String[]> publicParameterMap = portletRequest.getPublicParameterMap();
					Set<String> publicRenderParameterNames = publicParameterMappings.keySet();

					// For each of the public render parameters found in the WEB-INF/faces-config.xml file:
					for (String prefixedParameterName : publicRenderParameterNames) {
						String[] modelExpressions = publicParameterMappings.get(prefixedParameterName);

						if (modelExpressions != null) {

							String parameterPrefix;
							String nonPrefixedParameterName;

							int colonPos = prefixedParameterName.indexOf(":");

							if (colonPos > 0) {
								parameterPrefix = prefixedParameterName.substring(0, colonPos);
								nonPrefixedParameterName = prefixedParameterName.substring(colonPos + 1);
							}
							else {
								parameterPrefix = null;
								nonPrefixedParameterName = prefixedParameterName;
							}

							for (String originalModelEL : modelExpressions) {

								String[] parameterValues = publicParameterMap.get(nonPrefixedParameterName);
								String parameterValue = null;

								if ((parameterValues != null) && (parameterValues.length > 0)) {
									parameterValue = parameterValues[0];
								}

								PublicRenderParameter publicRenderParameter = new PublicRenderParameterImpl(
										parameterPrefix, parameterValue, originalModelEL, portletName);

								if (publicRenderParameter.isForThisPortlet()) {

									String modelValue = publicRenderParameter.getModelValue(facesContext);
									boolean modelValueHasChanged = publicRenderParameter.isModelValueChanged(
											facesContext);

									if (logger.isTraceEnabled()) {
										logger.trace(
											"portletName=[{0}] public render parameter=[{1}] parameterValue=[{2}] modelValue=[{3}] modelValueHasChanged=[{4}]",
											portletName, nonPrefixedParameterName, parameterValue, modelValue,
											modelValueHasChanged);
									}

									if (modelValueHasChanged) {
										logger.debug(
											"Setting render parameter=[{0}] in response because modelValue=[{1}] has changed",
											nonPrefixedParameterName, modelValue);
										stateAwareResponse.setRenderParameter(nonPrefixedParameterName, modelValue);
									}
									else {
										logger.debug(
											"NOT setting render parameter=[{0}] in response because modelValue=[{1}] has NOT changed",
											nonPrefixedParameterName, modelValue);
									}
								}
								else {
									logger.debug(
										"NOT setting render parameter=[{0}] in response because it is NOT for this portletName=[{1}]",
										nonPrefixedParameterName, portletName);
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {

			// There's no point in throwing a RuntimeException of any kind like FacesException because the Faces
			// runtime will swallow it. So the best we can do is log the exception.
			logger.error(e);
		}
	}
}
