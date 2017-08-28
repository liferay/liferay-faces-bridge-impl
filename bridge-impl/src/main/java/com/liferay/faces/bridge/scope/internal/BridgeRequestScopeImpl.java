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
package com.liferay.faces.bridge.scope.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.ActionResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.RequestAttributeInspector;
import com.liferay.faces.bridge.RequestAttributeInspectorFactory;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.internal.IncongruityContext;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.util.internal.FacesMessageWrapper;
import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeRequestScopeImpl extends BridgeRequestScopeCompat_2_2_Impl implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7113251688518329851L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestScopeImpl.class);

	// Private Constants for Bridge Request Scope Attributes
	private static final String BRIDGE_REQ_SCOPE_ATTR_ACTION_PARAMS = "com.liferay.faces.bridge.actionParams";
	private static final String BRIDGE_REQ_SCOPE_ATTR_FACES_MESSAGES = "com.liferay.faces.bridge.faces.messages";
	private static final String BRIDGE_REQ_SCOPE_ATTR_FACES_VIEW_ROOT = "com.liferay.faces.bridge.faces.view.root";
	private static final String BRIDGE_REQ_SCOPE_ATTR_INCONGRUITY_CONTEXT_ATTRIBUTES =
		"com.liferay.faces.bridge.incongruitycontext.attributes";
	private static final String BRIDGE_REQ_SCOPE_ATTR_REQUEST_ATTRIBUTES =
		"com.liferay.faces.bridge.faces.request.attributes";

	// Other Private Constants
	private static final String JAVAX_FACES_ENCODED_URL_PARAM = "javax.faces.encodedURL";

	// Private Data Members
	private Bridge.PortletPhase beganInPhase;
	private boolean facesLifecycleExecuted;
	private String id;
	private boolean navigationOccurred;
	private Set<String> nonExcludedAttributeNames;
	private PortletMode portletMode;
	private boolean portletModeChanged;
	private boolean postRedirectGetSupported;
	private boolean preserveActionParams;
	private boolean redirectOcurred;
	private RequestAttributeInspector requestAttributeInspector;

	public BridgeRequestScopeImpl(PortletRequest portletRequest, PortletConfig portletConfig,
		BridgeConfig bridgeConfig) {

		String portletName = portletConfig.getPortletName();
		PortletSession portletSession = portletRequest.getPortletSession();
		String sessionId = portletSession.getId();
		Calendar calendar = new GregorianCalendar();
		this.id = portletName + ":::" + sessionId + ":::" + calendar.getTimeInMillis();
		this.portletMode = PortletMode.VIEW;
		this.beganInPhase = (Bridge.PortletPhase) portletRequest.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);
		this.preserveActionParams = PortletConfigParam.PreserveActionParams.getBooleanValue(portletConfig);
		this.requestAttributeInspector = RequestAttributeInspectorFactory.getRequestAttributeInspectorInstance(
				portletRequest, portletConfig, bridgeConfig);

		PortalContext portalContext = portletRequest.getPortalContext();
		String postRedirectGetSupport = portalContext.getProperty(BridgePortalContext.POST_REDIRECT_GET_SUPPORT);
		this.postRedirectGetSupported = BooleanHelper.isTrueToken(postRedirectGetSupport);
	}

	@Override
	public Bridge.PortletPhase getBeganInPhase() {
		return beganInPhase;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public PortletMode getPortletMode() {
		return portletMode;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getPreservedActionParameterMap() {
		return (Map<String, String>) getAttribute(BRIDGE_REQ_SCOPE_ATTR_ACTION_PARAMS);
	}

	@Override
	public String getPreservedViewStateParam() {
		return (String) getAttribute(ResponseStateManager.VIEW_STATE_PARAM);
	}

	// The overrides for {@link #toString()} and {@link #hashCode()} are necessary because the {@link ConcurrentHashMap}
	// parent class overrides them and causes debug logs to be difficult to interpret.
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean isFacesLifecycleExecuted() {
		return facesLifecycleExecuted;
	}

	@Override
	public boolean isNavigationOccurred() {
		return navigationOccurred;
	}

	@Override
	public boolean isPortletModeChanged() {
		return portletModeChanged;
	}

	@Override
	public boolean isRedirectOccurred() {
		return redirectOcurred;
	}

	@Override
	public void release(FacesContext facesContext) {

		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if (!postRedirectGetSupported &&
				((portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) ||
					(portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE))) {
			ExternalContext externalContext = facesContext.getExternalContext();
			Set<String> nonExcludedAttributeNames = getNonExcludedRequestAttributes(externalContext.getRequestMap());
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			simulatePostRedirectGet(portletRequest, nonExcludedAttributeNames);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void restoreState(FacesContext facesContext) {

		logger.debug("restoreState(facesContext)");

		boolean restoreNonExcludedRequestAttributes = ((beganInPhase == Bridge.PortletPhase.ACTION_PHASE) ||
				(beganInPhase == Bridge.PortletPhase.EVENT_PHASE) ||
				(beganInPhase == Bridge.PortletPhase.RESOURCE_PHASE));

		PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if (portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) {

			ExternalContext externalContext = facesContext.getExternalContext();
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

			if (!portletMode.equals(portletRequest.getPortletMode())) {

				if (!portletModeChanged) {

					// TCK TestPage040 (requestNoScopeOnModeChangeTest) - In this test, a navigation-rule fires in the
					// ACTION_PHASE of the portlet lifecycle that contains a portlet mode change from VIEW to EDIT.
					// Since the BridgeRequestScope instance created in the ACTION_PHASE is not maintained, "this" will
					// be a new instance and the mode change that took place in the ACTION_PHASE will not be known. In
					// this case, the BridgeRequestScope was not maintained from the ACTION_PHASE to the RENDER_PHASE
					// but a navigation-rule. Detecting a mode change is possible though by checking the request to see
					// if it differs from VIEW mode (the default).
					portletModeChanged = true;
				}

				restoreNonExcludedRequestAttributes = false;
			}
		}

		if (((beganInPhase == Bridge.PortletPhase.ACTION_PHASE) || (beganInPhase == Bridge.PortletPhase.EVENT_PHASE) ||
					(beganInPhase == Bridge.PortletPhase.RESOURCE_PHASE))) {

			// Restore the view root that may have been saved during the action/event/render phase of the portlet
			// lifecycle.
			UIViewRoot uiViewRoot = (UIViewRoot) getAttribute(BRIDGE_REQ_SCOPE_ATTR_FACES_VIEW_ROOT);

			if (uiViewRoot != null) {
				facesContext.setViewRoot(uiViewRoot);
				logger.debug("Restored viewId=[{0}] uiViewRoot=[{1}]", uiViewRoot.getViewId(), uiViewRoot);
			}
			else {
				logger.debug("Did not restore uiViewRoot");
			}

			if (!redirectOcurred) {

				// Restore the faces messages that may have been saved during the action/event/render phase of the
				// portlet lifecycle.
				List<FacesMessageWrapper> facesMessages = (List<FacesMessageWrapper>) getAttribute(
						BRIDGE_REQ_SCOPE_ATTR_FACES_MESSAGES);

				boolean restoredFacesMessages = false;

				if (facesMessages != null) {

					for (FacesMessageWrapper facesMessageWrapper : facesMessages) {
						String clientId = facesMessageWrapper.getClientId();
						FacesMessage facesMessage = facesMessageWrapper.getFacesMessage();
						facesContext.addMessage(clientId, facesMessage);
						logger.trace("Restored facesMessage=[{0}]", facesMessage.getSummary());
						restoredFacesMessages = true;
					}
				}

				if (restoredFacesMessages) {
					logger.debug("Restored facesMessages");
				}
				else {
					logger.debug("Did not restore any facesMessages");
				}

				// NOTE: PROPOSE-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-203 Restore the
				// FacesContext attributes that may have been saved during the ACTION_PHASE of the portlet lifecycle.
				restoreJSF2FacesContextAttributes(facesContext);
			}
		}

		if (restoreNonExcludedRequestAttributes) {

			// Restore the non-excluded request attributes.
			List<RequestAttribute> savedRequestAttributes = (List<RequestAttribute>) getAttribute(
					BRIDGE_REQ_SCOPE_ATTR_REQUEST_ATTRIBUTES);

			boolean restoredNonExcludedRequestAttributes = false;

			if (savedRequestAttributes != null) {
				ExternalContext externalContext = facesContext.getExternalContext();
				Map<String, Object> currentRequestAttributes = externalContext.getRequestMap();

				// If a redirect did not occur, then restore the non-excluded request attributes.
				if (!redirectOcurred) {

					for (RequestAttribute requestAttribute : savedRequestAttributes) {
						String name = requestAttribute.getName();
						Object value = requestAttribute.getValue();
						logger.trace("Restoring non-excluded request attribute name=[{0}] value=[{1}]", name, value);
						currentRequestAttributes.put(name, value);
						restoredNonExcludedRequestAttributes = true;
					}
				}
			}

			if (restoredNonExcludedRequestAttributes) {
				logger.debug("Restored non-excluded request attributes");
			}
			else {
				logger.debug("Did not restore any non-excluded request attributes");
			}
		}

		// If running in the EVENT_PHASE or RENDER_PHASE, then the client window must be restored.
		if ((portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE)) {

			// PROPOSE-FOR-BRIDGE3-API
			restoreClientWindow(facesContext.getExternalContext());
		}

		// If running in the RENDER_PHASE, then the Flash scope must be restored.
		if (portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) {

			// NOTE: PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-201
			// Restore the flash scope.
			ExternalContext externalContext = facesContext.getExternalContext();
			restoreFlashState(externalContext);
		}

		// If running in the RENDER_PHASE, then the incongruity context must be restored.
		if (((beganInPhase == Bridge.PortletPhase.ACTION_PHASE) || (beganInPhase == Bridge.PortletPhase.EVENT_PHASE)) &&
				(portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE)) {

			List<IncongruityAttribute> savedIncongruityAttributes = (List<IncongruityAttribute>) getAttribute(
					BRIDGE_REQ_SCOPE_ATTR_INCONGRUITY_CONTEXT_ATTRIBUTES);

			if (savedIncongruityAttributes != null) {

				ExternalContext externalContext = facesContext.getExternalContext();
				IncongruityContext incongruityContext = (IncongruityContext) externalContext.getRequestMap().get(
						IncongruityContext.class.getName());
				Map<String, Object> incongruityContextAttributes = incongruityContext.getAttributes();

				for (IncongruityAttribute incongruityAttribute : savedIncongruityAttributes) {
					String key = incongruityAttribute.getName();
					Object value = incongruityAttribute.getValue();
					incongruityContextAttributes.put(key, value);
				}
			}
		}
	}

	/**
	 * Saves the state of the FacesContext as required by section 5.1.2 of the JSR 329 spec. This method is designed to
	 * be called during the ACTION_PHASE of the portlet lifecycle.
	 *
	 * @param  facesContext  The current faces context.
	 */
	@Override
	public void saveState(FacesContext facesContext) {

		logger.debug("saveState(facesContext)");

		// If the bridge request scope began in the ACTION_PHASE, EVENT_PHASE, or RESOURCE_PHASE of the portlet
		// lifecycle, then
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		if ((beganInPhase == Bridge.PortletPhase.ACTION_PHASE) || (beganInPhase == Bridge.PortletPhase.EVENT_PHASE) ||
				(beganInPhase == Bridge.PortletPhase.RESOURCE_PHASE)) {

			// Save the view root.
			setAttribute(BRIDGE_REQ_SCOPE_ATTR_FACES_VIEW_ROOT, facesContext.getViewRoot());

			// If the PortletMode hasn't changed, then preserve the "javax.faces.ViewState" request parameter value.
			if (!portletModeChanged) {

				if (portletResponse instanceof ActionResponse) {
					String viewState = externalContext.getRequestParameterMap().get(
							ResponseStateManager.VIEW_STATE_PARAM);

					if (viewState != null) {

						// NOTE: Although it is possible to save this as a render parameter, can't use that approach
						// because portlet containers like Pluto will add the "javax.faces.ViewState" parameter to any
						// ResourceURLs that are created during the RENDER_PHASE of the portlet lifecycle.
						setAttribute(ResponseStateManager.VIEW_STATE_PARAM, viewState);
					}
				}
			}

			// If specified in the WEB-INF/portlet.xml descriptor, then preserve the action parameters.
			if (preserveActionParams) {
				Map<String, String> actionRequestParameterMap = new HashMap<String, String>(
						externalContext.getRequestParameterMap());
				actionRequestParameterMap.remove(ResponseStateManager.VIEW_STATE_PARAM);
				actionRequestParameterMap.remove(JAVAX_FACES_ENCODED_URL_PARAM);
				setAttribute(BRIDGE_REQ_SCOPE_ATTR_ACTION_PARAMS, actionRequestParameterMap);
			}

			// Save the list of faces messages.
			List<FacesMessageWrapper> facesMessageWrappers = new ArrayList<FacesMessageWrapper>();
			Iterator<String> clientIds = facesContext.getClientIdsWithMessages();

			while (clientIds.hasNext()) {
				String clientId = clientIds.next();
				Iterator<FacesMessage> facesMessages = facesContext.getMessages(clientId);

				while (facesMessages.hasNext()) {
					FacesMessage facesMessage = facesMessages.next();
					FacesMessageWrapper facesMessageWrapper = new FacesMessageWrapper(clientId, facesMessage);
					facesMessageWrappers.add(facesMessageWrapper);
				}
			}

			if (facesMessageWrappers.size() > 0) {
				setAttribute(BRIDGE_REQ_SCOPE_ATTR_FACES_MESSAGES, facesMessageWrappers);
			}
			else {
				logger.trace("Not saving any faces messages");
			}

			// NOTE: PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-203 Build up a list
			// of attributes found in the FacesContext attribute map and save them. It has to be copied in this manner
			// because the Faces implementation likely calls the clear() method during the call to its
			// FacesContextImpl.release() method.
			saveJSF2FacesContextAttributes(facesContext);

			boolean saveNonExcludedAttributes = true;

			// If a redirect occurred, then indicate that the non-excluded request attributes are not to be preserved.
			if (redirectOcurred) {

				// TCK TestPage062: eventScopeNotRestoredRedirectTest
				logger.trace("Due to redirect, not saving any non-excluded request attributes");
				saveNonExcludedAttributes = false;
			}

			// Otherwise, if the portlet mode has changed, then indicate that the non-excluded request attributes are
			// not to be preserved.
			else if (portletModeChanged) {
				logger.trace("Due to PortletMode change, not saving any non-excluded request attributes");
				saveNonExcludedAttributes = false;
			}

			// If running in the ACTION_PHASE or EVENT_PHASE, then the client window must be saved as well so that it
			// can be restored.
			Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

			if ((portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) ||
					(portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE)) {

				// PROPOSE-FOR-BRIDGE3-API
				saveClientWindow(externalContext);
			}

			// If appropriate, save the non-excluded request attributes. This would include, for example, managed-bean
			// instances that may have been created during the ACTION_PHASE that need to survive to the RENDER_PHASE.
			if (saveNonExcludedAttributes) {
				Map<String, Object> requestMap = externalContext.getRequestMap();
				Set<String> nonExcludedAttributeNames = getNonExcludedRequestAttributes(requestMap);
				List<RequestAttribute> savedRequestAttributes = new ArrayList<RequestAttribute>();

				for (Map.Entry<String, Object> mapEntry : requestMap.entrySet()) {
					String attributeName = mapEntry.getKey();

					if (nonExcludedAttributeNames.contains(attributeName)) {
						Object attributeValue = mapEntry.getValue();
						logger.trace("SAVING non-excluded request attribute name=[{0}] value=[{1}]", attributeName,
							attributeValue);
						savedRequestAttributes.add(new RequestAttribute(attributeName, attributeValue));
					}
				}

				if (savedRequestAttributes.size() > 0) {
					setAttribute(BRIDGE_REQ_SCOPE_ATTR_REQUEST_ATTRIBUTES, savedRequestAttributes);
				}
				else {
					logger.trace("Not saving any non-excluded request attributes");
				}
			}
		}

		// If running in the ACTION_PHASE or EVENT_PHASE, then the Flash scope must be saved as well so that it can be
		// restored.
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if ((portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE)) {

			// PROPOSED-FOR-JSR344-API: http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-1070
			// PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-201
			saveFlashState(externalContext);
		}

		// If running in the ACTION_PHASE or EVENT_PHASE, then the incongruity context must be saved as well so that it
		// can be restored.
		if ((portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE)) {

			IncongruityContext incongruityContext = (IncongruityContext) externalContext.getRequestMap().get(
					IncongruityContext.class.getName());
			Map<String, Object> incongruityAttributeMap = incongruityContext.getAttributes();
			int mapSize = incongruityAttributeMap.size();
			List<IncongruityAttribute> savedIncongruityAttributes = new ArrayList<IncongruityAttribute>(mapSize);

			for (Map.Entry<String, Object> mapEntry : incongruityAttributeMap.entrySet()) {
				String name = mapEntry.getKey();
				Object value = mapEntry.getValue();
				logger.trace("Saving IncongruityContext attribute name=[{0}] value=[{1}]", name, value);
				savedIncongruityAttributes.add(new IncongruityAttribute(name, value));
			}

			setAttribute(BRIDGE_REQ_SCOPE_ATTR_INCONGRUITY_CONTEXT_ATTRIBUTES, savedIncongruityAttributes);
		}

		if (!postRedirectGetSupported &&
				((portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) ||
					(portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE))) {
			Map<String, Object> requestMap = externalContext.getRequestMap();
			Set<String> nonExcludedAttributeNames = getNonExcludedRequestAttributes(requestMap);
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			simulatePostRedirectGet(portletRequest, nonExcludedAttributeNames);
		}
	}

	@Override
	public void setFacesLifecycleExecuted(boolean facesLifecycleExecuted) {
		this.facesLifecycleExecuted = facesLifecycleExecuted;
	}

	@Override
	public void setNavigationOccurred(boolean navigationOccurred) {
		this.navigationOccurred = navigationOccurred;
	}

	@Override
	public void setPortletMode(PortletMode portletMode) {
		this.portletMode = portletMode;
	}

	@Override
	public void setPortletModeChanged(boolean portletModeChanged) {
		this.portletModeChanged = portletModeChanged;
	}

	@Override
	public void setRedirectOccurred(boolean redirectOcurred) {
		this.redirectOcurred = redirectOcurred;
	}

	// The overrides for {@link #toString()} and {@link #hashCode()} are necessary because the {@link ConcurrentHashMap}
	// parent class overrides them and causes debug logs to be difficult to interpret.
	@Override
	public String toString() {
		return getClass().getName().concat("@").concat(Integer.toHexString(hashCode()));
	}

	private Set<String> getNonExcludedRequestAttributes(Map<String, Object> requestMap) {

		if (nonExcludedAttributeNames == null) {

			nonExcludedAttributeNames = new HashSet<String>();

			for (Map.Entry<String, Object> mapEntry : requestMap.entrySet()) {

				String attributeName = mapEntry.getKey();
				Object attributeValue = mapEntry.getValue();

				boolean excluded = requestAttributeInspector.isExcludedByConfig(attributeName, attributeValue) ||
					requestAttributeInspector.isExcludedByAnnotation(attributeName, attributeValue) ||
					requestAttributeInspector.containsExcludedNamespace(attributeName) ||
					requestAttributeInspector.isExcludedByType(attributeName, attributeValue) ||
					requestAttributeInspector.isExcludedByPreExisting(attributeName, attributeValue);

				if (!excluded) {
					nonExcludedAttributeNames.add(attributeName);
				}
			}
		}

		return nonExcludedAttributeNames;
	}

	/**
	 * If the portlet container does not support the POST-REDIRECT-GET design pattern, then the ACTION_PHASE and
	 * RENDER_PHASE are both part of a single HTTP POST request. In such cases, the excluded request attributes must be
	 * pro-actively removed. Note that this must take place prior to the FacesContext getting constructed because the
	 * FacesContextFactory delegation chain might consult a request attribute that is supposed to be excluded. This is
	 * indeed the case with Apache Trinidad
	 * org.apache.myfaces.trinidadinternal.context.FacesContextFactoryImpl.CacheRenderKit constructor, which consults a
	 * request attribute named "org.apache.myfaces.trinidad.util.RequestStateMap" that must first be excluded.
	 */
	private void simulatePostRedirectGet(PortletRequest portletRequest, Set<String> nonExcludedAttributeNames) {

		// TCK TestPage062: eventScopeNotRestoredRedirectTest
		// TCK TestPage063: eventScopeNotRestoredModeChangedTest
		for (String nonExcludedAttributeName : nonExcludedAttributeNames) {

			portletRequest.removeAttribute(nonExcludedAttributeName);

			if (logger.isTraceEnabled()) {

				if (redirectOcurred) {
					logger.trace(
						"Due to redirect, removed request attribute name=[{0}] that had been preserved in the ACTION_PHASE or EVENT_PHASE",
						nonExcludedAttributeName);
				}
				else {
					logger.trace(
						"Due to PortletMode change, removed request attribute name=[{0}] that had been preserved in the ACTION_PHASE or EVENT_PHASE",
						nonExcludedAttributeName);
				}
			}
		}

		// Iterate through all of the request attributes and build up a list of those that are to be removed.
		Enumeration<String> attributeNames = portletRequest.getAttributeNames();

		// TCK TestPage037: requestScopeContentsTest
		// TCK TestPage045: excludedAttributesTest
		// TCK TestPage151: requestMapRequestScopeTest
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			Object attributeValue = portletRequest.getAttribute(attributeName);

			if (requestAttributeInspector.isExcludedByPreExisting(attributeName, attributeValue)) {

				if (requestAttributeInspector.isExcludedByConfig(attributeName, attributeValue)) {

					// TCK TestPage151 (requestMapRequestScopeTest) remove "verifyPreBridgeExclusion"
					portletRequest.removeAttribute(attributeName);
					logger.debug("Removed request attribute name=[{0}] since it was specified for removal.",
						attributeName);
				}
				else {
					logger.debug(
						"Kept request attribute name=[{0}] since it existed prior to the FacesContext being created.",
						attributeName);
				}
			}
			else {

				if (requestAttributeInspector.isExcludedByConfig(attributeName, attributeValue) ||
						requestAttributeInspector.isExcludedByAnnotation(attributeName, attributeValue) ||
						requestAttributeInspector.isExcludedByType(attributeName, attributeValue) ||
						requestAttributeInspector.containsExcludedNamespace(attributeName)) {

					portletRequest.removeAttribute(attributeName);

					logger.debug(
						"Removed request attribute name=[{0}] that had been preserved in the ACTION_PHASE or EVENT_PHASE",
						attributeName);
				}
			}
		}
	}
}
