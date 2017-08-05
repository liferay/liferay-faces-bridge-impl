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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeException;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.application.internal.BridgeNavigationHandler;
import com.liferay.faces.bridge.application.internal.BridgeNavigationHandlerImpl;
import com.liferay.faces.bridge.context.internal.CapturingWriter;
import com.liferay.faces.bridge.context.internal.WriterOperation;
import com.liferay.faces.bridge.event.internal.IPCPhaseListener;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class provides functionality that is common to rendering the JSF lifecycle either in the Portlet 3.0
 * HEADER_PHASE or the Portlet 2.0 RENDER_PHASE.
 *
 * @author  Neil Griffin
 */
public abstract class BridgePhaseHeaderRenderCommon extends BridgePhaseCompat_2_2_Impl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseHeaderRenderCommon.class);

	public BridgePhaseHeaderRenderCommon(PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		super(portletConfig, bridgeConfig);
	}

	protected abstract MimeResponse getMimeResponse();

	protected abstract RenderRequest getRenderRequest();

	protected void executeRender(String renderRedirectViewId, Bridge.PortletPhase portletPhase) throws BridgeException,
		IOException {

		RenderRequest renderRequest = getRenderRequest();
		MimeResponse mimeResponse = getMimeResponse();

		init(renderRequest, mimeResponse, portletPhase);

		// Determine whether or not the Faces lifecycle was already executed.
		boolean facesLifecycleExecuted = bridgeRequestScope.isFacesLifecycleExecuted();

		// Restore the faces view root and messages that would have been saved during the ACTION_PHASE.
		bridgeRequestScope.restoreState(facesContext);

		// If a portlet mode change occurred, then the view associated with the new portlet mode is considered to be
		// a different view by the bridge (even if it has the same viewId as the previous mode). Therefore, the view
		// that caused the ACTION_PHASE is different than the view associated with the RENDER_PHASE. Since the bridge
		// request scope is an ACTION_PHASE -> ACTION_PHASE type of scope designed to help redisplay, the values
		// submitted in the ACTION_PHASE would be unrelated in the case of a redisplay. For that reason, the bridge
		// request scope must be removed from the cache so that a RENDER_PHASE caused by a redisplay will not use it.
		//
		// PROPOSE-FOR-BRIDGE3-SPEC: Although the spec does not mention the redirect case, the bridge request
		// scope must not be maintained if a redirect has occurred.
		if (bridgeRequestScope.isPortletModeChanged() || bridgeRequestScope.isRedirectOccurred()) {
			bridgeRequestScopeCache.remove(bridgeRequestScope.getId());
		}

		// If a render-redirect URL was specified, then it is necessary to create a new view from the URL and place it
		// in the FacesContext.
		if (renderRedirectViewId != null) {
			renderRequest.setAttribute(BridgeExt.RENDER_REDIRECT_AFTER_DISPATCH, Boolean.TRUE);

			ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
			UIViewRoot uiViewRoot = viewHandler.createView(facesContext, renderRedirectViewId);
			facesContext.setViewRoot(uiViewRoot);
			logger.debug("Performed render-redirect to viewId=[{0}]", renderRedirectViewId);
		}

		// Otherwise, if a redirect occurred in the ACTION_PHASE or the EVENT_PHASE (possibly due to a navigation-rule
		// firing with a <redirect/> element), then indicate that the JSF Lifecycle has not yet been executed. This
		// will cause the RESTORE_VIEW phase to get executed below and the target viewId will be the value of the
		// _facesViewIdRender request parameter.
		else if (bridgeRequestScope.isRedirectOccurred()) {

			// TCK TestPage177 (redirectEventTest)
			facesLifecycleExecuted = false;
		}

		// NOTE: PROPOSE-FOR-BRIDGE3-API Actually, the proposal would be to REMOVE
		// Bridge.IS_POSTBACK_ATTRIBUTE from the Bridge API, because JSF 2.0 introduced the
		// FacesContext#isPostBack() method.
		// http://javaserverfaces.java.net/nonav/docs/2.0/javadocs/javax/faces/context/FacesContext.html#isPostback()
		if (bridgeRequestScope.getBeganInPhase() == Bridge.PortletPhase.ACTION_PHASE) {

			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.getRequestMap().put(Bridge.IS_POSTBACK_ATTRIBUTE, Boolean.TRUE);
		}

		logger.debug("portletName=[{0}] facesLifecycleExecuted=[{1}]", portletName, facesLifecycleExecuted);

		// If the JSF lifecycle executed back in the ACTION_PHASE of the portlet lifecycle, then
		if (facesLifecycleExecuted) {

			// TCK TestPage054: prpUpdatedFromActionTest
			PhaseEvent restoreViewPhaseEvent = new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, facesLifecycle);
			PhaseListener[] phaseListeners = facesLifecycle.getPhaseListeners();

			for (PhaseListener phaseListener : phaseListeners) {

				if (phaseListener instanceof IPCPhaseListener) {
					phaseListener.afterPhase(restoreViewPhaseEvent);

					break;
				}
			}
		}

		// Otherwise, in accordance with Section 5.2.6 of the Spec, execute the JSF lifecycle so that ONLY the
		// RESTORE_VIEW phase executes. Note that this is accomplished by the RenderRequestPhaseListener.
		else {

			ExternalContext externalContext = facesContext.getExternalContext();

			try {
				String viewId = getFacesViewId(externalContext);
				logger.debug("Executing Faces lifecycle for viewId=[{0}]", viewId);
			}

			// In the case of a BridgeException being caught such as BridgeDefaultViewNotSpecifiedException or
			// BridgeInvalidViewPathException, even though there is no rendered markup, it is necessary to save the
			// empty list of captured writer operations as a clue to the RENDER_PHASE that an attempt to execute the JSF
			// lifecycle has already happened in the HEADER_PHASE.
			catch (BridgeException e) {

				// TCK TestPage048 (portletSetsInvalidViewPathTest)
				// TCK TestPage051 (exceptionThrownWhenNoDefaultViewIdTest)
				Writer responseOutputWriter = getResponseOutputWriter(externalContext);

				if (responseOutputWriter instanceof CapturingWriter) {

					CapturingWriter capturingWriter = (CapturingWriter) responseOutputWriter;
					renderRequest.setAttribute(BridgeExt.WRITER_OPERATIONS, capturingWriter.getWriterOperations());
				}

				logger.error("Unable to get viewId due to {0}", e.getClass().getSimpleName());
				throw e;
			}

			// Attach the JSF 2.2 client window to the JSF lifecycle so that Faces Flows can be utilized.
			attachClientWindowToLifecycle(facesContext, facesLifecycle);

			// Execute the JSF lifecycle.
			facesLifecycle.execute(facesContext);
		}

		// If there were any "handled" exceptions queued, then throw a BridgeException.
		Throwable handledException = getJSF2HandledException(facesContext);

		if (handledException != null) {
			throw new BridgeException(handledException);
		}

		// Otherwise, if there were any "unhandled" exceptions queued, then throw a BridgeException.
		Throwable unhandledException = getJSF2UnhandledException(facesContext);

		if (unhandledException != null) {
			throw new BridgeException(unhandledException);
		}

		// Otherwise, if the PortletMode has changed, and a navigation-rule hasn't yet fired (which could have happened
		// in the EVENT_PHASE), then switch to the appropriate PortletMode and navigate to the current viewId in the
		// UIViewRoot.
		if (bridgeRequestScope.isPortletModeChanged() && !bridgeRequestScope.isNavigationOccurred()) {
			BridgeNavigationHandler bridgeNavigationHandler = getBridgeNavigationHandler(facesContext);
			PortletMode fromPortletMode = bridgeRequestScope.getPortletMode();
			PortletMode toPortletMode = renderRequest.getPortletMode();
			bridgeNavigationHandler.handleNavigation(facesContext, fromPortletMode, toPortletMode);
		}

		// Determines whether or not lifecycle incongruities should be managed.
		boolean manageIncongruities = PortletConfigParam.ManageIncongruities.getBooleanValue(portletConfig);

		// Now that we're executing the RENDER_PHASE of the Portlet lifecycle, before the JSF
		// RENDER_RESPONSE phase is executed, we have to fix some incongruities between the Portlet
		// lifecycle and the JSF lifecycle that may have occurred during the ACTION_PHASE of the Portlet
		// lifecycle.
		if (manageIncongruities) {
			incongruityContext.makeCongruous(facesContext);
		}

		// Execute the RENDER_RESPONSE phase of the faces lifecycle.
		logger.debug("Executing Faces render");
		facesLifecycle.render(facesContext);

		// Set the view history according to Section 5.4.3 of the Bridge Spec.
		setViewHistory(facesContext.getViewRoot().getViewId());

		// Spec 6.6 (Namespacing)
		indicateNamespacingToConsumers(facesContext.getViewRoot(), mimeResponse);

		// If a render-redirect occurred, then
		ExternalContext externalContext = facesContext.getExternalContext();

		Map<String, Object> requestMap = externalContext.getRequestMap();
		Boolean renderRedirect = (Boolean) requestMap.remove(BridgeExt.RENDER_REDIRECT);
		Writer responseOutputWriter = getResponseOutputWriter(externalContext);

		if ((renderRedirect != null) && renderRedirect) {

			// Cleanup the old FacesContext since a new one will be created in the recursive method call below.
			facesContext.responseComplete();
			facesContext.release();

			// If the render-redirect standard feature is enabled in web.xml or portlet.xml, then the
			// ResponseOutputWriter has buffered up markup that must be discarded. This is because we don't want the
			// markup from the original Faces view to be included with the markup of Faces view found in the
			// redirect URL.
			if (responseOutputWriter instanceof CapturingWriter) {

				CapturingWriter capturingWriter = (CapturingWriter) responseOutputWriter;
				capturingWriter.getWriterOperations().clear();
			}

			// Recursively call this method with the render-redirect URL so that the RENDER_RESPONSE phase of the
			// JSF lifecycle will be re-executed according to the new Faces viewId found in the redirect URL.
			renderRedirectViewId = (String) requestMap.remove(BridgeExt.RENDER_REDIRECT_VIEW_ID);

			executeRender(renderRedirectViewId, portletPhase);
		}

		// Otherwise,
		else {

			// If there are captured writer operations, then that means either the JSF lifecycle executed in the
			// HEADER_PHASE or that a render-redirect executed in the HEADER_PHASE or RESOURCE_PHASE.
			if (responseOutputWriter instanceof CapturingWriter) {

				CapturingWriter capturingWriter = (CapturingWriter) responseOutputWriter;
				List<WriterOperation> writerOperations = capturingWriter.getWriterOperations();

				if (writerOperations != null) {

					// If running in the RENDER_PHASE of the portlet lifecycle, then invoke each writer operation so
					// that the markup will be written to the response.
					if (portletPhase == Bridge.PortletPhase.RENDER_PHASE) {

						for (WriterOperation writerOperation : writerOperations) {
							writerOperation.invoke(responseOutputWriter);
						}
					}

					// Otherwise, since running in the HEADER_PHASE, save the list of writer operations so that the
					// markup will be rendered in the subsequent RENDER_PHASE.
					else {
						renderRequest.setAttribute(BridgeExt.WRITER_OPERATIONS, writerOperations);
					}
				}
			}
		}
	}

	protected BridgeNavigationHandler getBridgeNavigationHandler(FacesContext facesContext) {
		BridgeNavigationHandler bridgeNavigationHandler;
		NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();

		if (navigationHandler instanceof BridgeNavigationHandler) {
			bridgeNavigationHandler = (BridgeNavigationHandler) navigationHandler;
		}
		else {
			bridgeNavigationHandler = new BridgeNavigationHandlerImpl(navigationHandler);
		}

		return bridgeNavigationHandler;
	}

	/**
	 * Sets the "javax.portlet.faces.viewIdHistory.<code>portletMode</code>" session attribute according to the
	 * requirements in Section 5.4.3 of the Bridge Spec. There is no corresponding getter method, because the value is
	 * meant to be retrieved by developers via an EL expression.
	 *
	 * @param  viewId  The current Faces viewId.
	 */
	protected void setViewHistory(String viewId) {

		RenderRequest renderRequest = getRenderRequest();
		String attributeName = Bridge.VIEWID_HISTORY.concat(".").concat(renderRequest.getPortletMode().toString());
		PortletSession portletSession = renderRequest.getPortletSession();
		portletSession.setAttribute(attributeName, viewId);
	}
}
