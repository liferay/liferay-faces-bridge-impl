/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.util.internal.PortletResourceUtil;
import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLResourceImpl extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLResourceImpl.class);

	// Private Constants
	private static final Set<String> EXCLUDED_PARAMETER_NAMES;

	static {

		Set<String> excludedParameterNames = new HashSet<String>();
		excludedParameterNames.add(Bridge.PORTLET_MODE_PARAMETER);
		excludedParameterNames.add(Bridge.PORTLET_SECURE_PARAMETER);
		excludedParameterNames.add(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
		EXCLUDED_PARAMETER_NAMES = Collections.unmodifiableSet(excludedParameterNames);
	}

	// Private Data Members
	private boolean inProtocol;
	private boolean viewLink;

	public BridgeURLResourceImpl(FacesContext facesContext, String uri, String contextPath, String namespace,
		String currentViewId, BridgeConfig bridgeConfig) throws URISyntaxException {

		super(uri, contextPath, namespace, currentViewId, bridgeConfig);

		this.inProtocol = (bridgeURI.getParameter(Bridge.IN_PROTOCOL_RESOURCE_LINK) != null);
		this.viewLink = BooleanHelper.isTrueToken(bridgeURI.getParameter(Bridge.VIEW_LINK));

		// If the "javax.portlet.faces.ViewLink" parameter is found and set to "true", then
		String viewLinkParam = bridgeURI.getParameter(Bridge.VIEW_LINK);

		if (BooleanHelper.isTrueToken(viewLinkParam)) {

			// Since this is going to be a URL that represents navigation to a different viewId, the
			// "javax.portlet.faces.ViewLink" parameter must be removed from the URI's query-string.
			bridgeURI.removeParameter(Bridge.VIEW_LINK);

			// If the "javax.portlet.faces.BackLink" parameter is found in the URI query-string, then replace it's value
			// with an encoded URL that can cause navigation back to the current view.
			if (bridgeURI.getParameter(Bridge.BACK_LINK) != null) {
				String newParamName = bridgeURI.removeParameter(Bridge.BACK_LINK);
				bridgeURI.setParameter(newParamName, getEncodedBackLinkURL(facesContext));
			}
		}

		// If the URI is opaque, meaning it starts with something like "portlet:" or "mailto:" and doesn't
		// have the double-forward-slash like "http://" does, then
		if (bridgeURI.isOpaque()) {

			// If the URI starts with "portlet:", then
			if (bridgeURI.isPortletScheme()) {

				// If the "_jsfBridgeViewId" parameter is equal to "_jsfBridgeCurrentView" then the URI is
				// self-referencing and the "_jsfBridgeViewId" parameter must be removed from the query-string.
				String facesViewIdParameter = bridgeURI.getParameter(Bridge.FACES_VIEW_ID_PARAMETER);

				if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewIdParameter)) {
					selfReferencing = true;
					bridgeURI.removeParameter(Bridge.FACES_VIEW_ID_PARAMETER);
				}

				// If the "_jsfBridgeViewPath" parameter is equal to "_jsfBridgeCurrentView" then the URI is
				// self-referencing and the "_jsfBridgeViewPath" parameter must be removed from the query-string.
				String facesViewPathParameter = bridgeURI.getParameter(Bridge.FACES_VIEW_PATH_PARAMETER);

				if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewPathParameter)) {
					selfReferencing = true;
					bridgeURI.removeParameter(Bridge.FACES_VIEW_PATH_PARAMETER);
				}
			}
		}

		// Otherwise, if the URI is hierarchical and targets an external resource, then
		else if (bridgeURI.isHierarchical() && bridgeURI.isExternal(contextPath)) {

			// If the "javax.portlet.faces.BackLink" parameter is found, then replace it's value with an encoded URL
			// that can cause navigation back to the current view.
			if (bridgeURI.getParameter(Bridge.BACK_LINK) != null) {
				String newParamName = bridgeURI.removeParameter(Bridge.BACK_LINK);
				bridgeURI.setParameter(newParamName, getEncodedBackLinkURL(facesContext));
			}
		}

		// Otherwise, if the URI is hierarchical and targets a resource internal to this application, then
		else if (bridgeURI.isHierarchical() && !bridgeURI.isExternal(contextPath)) {

			// If the "javax.portlet.faces.BackLink" parameter is found, then replace it's value with an encoded URL
			// that can cause navigation back to the current view.
			if (bridgeURI.getParameter(Bridge.BACK_LINK) != null) {
				String newParamName = bridgeURI.removeParameter(Bridge.BACK_LINK);
				bridgeURI.setParameter(newParamName, getEncodedBackLinkURL(facesContext));
			}

			// If the "javax.portlet.faces.InProtocolResourceLink" parameter is found, then
			if ((bridgeURI.getParameter(Bridge.IN_PROTOCOL_RESOURCE_LINK) != null)) {

				// Since an in-protocol-resource URL must be a ResourceURL, the "javax.portlet.faces.PortletMode" and
				// "javax.portlet.faces.WindowState" parameters must be removed from the URI query-string (if present)
				// because it is not possible to change a PortletMode or WindowState in a ResourceRequest.
				bridgeURI.removeParameter(Bridge.PORTLET_MODE_PARAMETER);
				bridgeURI.removeParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
			}
		}
	}

	@Override
	public BaseURL toBaseURL() throws MalformedURLException {

		BaseURL baseURL;
		String uri = bridgeURI.toString();

		// If the URL is opaque, meaning it starts with something like "portlet:" or "mailto:" and
		// doesn't have the double-forward-slash like "http://" does, then
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (bridgeURI.isOpaque()) {

			// If the URI starts with "portlet:", then return a BaseURL that contains the modified
			// parameters. This will be a URL that represents navigation to a different viewId.
			if (bridgeURI.isPortletScheme()) {

				// TCK TestPage005: modeViewIDTest
				// TCK TestPage042: requestRenderIgnoresScopeViaCreateViewTest
				// TCK TestPage043: requestRenderRedisplayTest
				// TCK TestPage044: requestRedisplayOutOfScopeTest
				// TCK TestPage049: renderRedirectTest
				// TCK TestPage050: ignoreCurrentViewIdModeChangeTest
				// TCK TestPage051: exceptionThrownWhenNoDefaultViewIdTest
				String portletMode = getParameter(Bridge.PORTLET_MODE_PARAMETER);
				boolean modeChanged = ((portletMode != null) && (portletMode.length() > 0));
				Bridge.PortletPhase urlPortletPhase = bridgeURI.getPortletPhase();

				if (urlPortletPhase == Bridge.PortletPhase.ACTION_PHASE) {
					baseURL = createActionURL(facesContext, modeChanged);
				}
				else if (urlPortletPhase == Bridge.PortletPhase.RENDER_PHASE) {
					baseURL = createRenderURL(facesContext, modeChanged);
				}
				else {
					baseURL = createResourceURL(facesContext, modeChanged);
				}

				// If the URI is self-referencing, meaning, it targets the current Faces view, then copy the render
				// parameters from the current PortletRequest to the BaseURL. NOTE: This has the added benefit of
				// copying the bridgeRequestScopeId render parameter, which will preserve the BridgeRequestScope if the
				// user clicks on the link (invokes the BaseURL).
				if (selfReferencing) {
					ExternalContext externalContext = facesContext.getExternalContext();
					PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
					copyRenderParameters(portletRequest, baseURL);
				}

				// If the portlet container created a PortletURL, then apply the PortletMode and WindowState to the
				// PortletURL.
				if (baseURL instanceof PortletURL) {

					PortletURL portletURL = (PortletURL) baseURL;
					ExternalContext externalContext = facesContext.getExternalContext();
					PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
					PortletURLHelper.setPortletMode(portletURL, portletMode, portletRequest);

					String windowState = getParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
					PortletURLHelper.setWindowState(portletURL, windowState, portletRequest);
				}

				// Apply the security.
				String secure = getParameter(Bridge.PORTLET_SECURE_PARAMETER);
				PortletURLHelper.setSecure(baseURL, secure);

				// According to the Bridge Spec, the "javax.portlet.faces.Secure" parameter must not be "carried
				// forward to the generated reference." According to a clarification in the Portlet 3.0 JavaDoc for
				// BaseURL#setProperty(String,String), setting the parameter to null will remove it.
				baseURL.setParameter(Bridge.PORTLET_SECURE_PARAMETER, (String) null);
			}

			// Otherwise, return the a BaseURL string representation (unmodified value) as required by the Bridge Spec.
			else {

				// TCK TestPage128: encodeResourceURLOpaqueTest
				baseURL = new BaseURLNonEncodedImpl(bridgeURI);
			}
		}

		// Otherwise, if the URL is identified by the ResourceHandler as a JSF2 resource URL, then
		else if ((uri != null) && (uri.contains("javax.faces.resource"))) {

			// If the URL has already been encoded, then return the URI unmodified.
			if (PortletResourceUtil.isPortletResourceURL(uri)) {

				// FACES-63: Prevent double-encoding of resource URLs
				baseURL = new BaseURLNonEncodedImpl(bridgeURI);
			}

			// Otherwise, return a ResourceURL that can retrieve the JSF2 resource.
			else {
				baseURL = createResourceURL(facesContext, bridgeURI.getParameterMap());
			}
		}

		// Otherwise, if the URL is external, then return an encoded BaseURL string representation of the URL.
		else if (bridgeURI.isExternal(contextPath)) {

			// TCK TestPage130: encodeResourceURLForeignExternalURLBackLinkTest
			ExternalContext externalContext = facesContext.getExternalContext();
			PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
			baseURL = new BaseURLEncodedImpl(bridgeURI, portletResponse);
		}

		// Otherwise, if the URL is relative, in that it starts with "../", then return a BaseURL string representation
		// of the URL that contains the context-path.
		else if (bridgeURI.isPathRelative()) {

			// TCK TestPage131: encodeResourceURLRelativeURLTest
			// TCK TestPage132: encodeResourceURLRelativeURLBackLinkTest
			ExternalContext externalContext = facesContext.getExternalContext();
			String contextPath = externalContext.getRequestContextPath();
			baseURL = new BaseURLNonEncodedRelativeImpl(bridgeURI, contextPath);
		}

		// Otherwise, if the URL originally contained the "javax.portlet.faces.ViewLink" which represents navigation
		// to a different Faces view, then
		else if (viewLink) {

			String portletMode = getParameter(Bridge.PORTLET_MODE_PARAMETER);
			String windowState = getParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
			boolean secure = BooleanHelper.toBoolean(getParameter(Bridge.PORTLET_SECURE_PARAMETER));

			// If the URL targets a Faces viewId, then return a PortletURL (Action URL) that targets the view with the
			// appropriate PortletMode, WindowState, and Security settings built into the URL. For more info, see
			// JavaDoc comments for {@link Bridge#VIEW_LINK}.
			if (getViewId() != null) {

				// TCK TestPage135: encodeResourceURLViewLinkTest
				// TCK TestPage136: encodeResourceURLViewLinkWithBackLinkTest
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				PortletURL actionURL = createActionURL(facesContext, EXCLUDED_PARAMETER_NAMES);
				PortletURLHelper.setPortletMode(actionURL, portletMode, portletRequest);
				PortletURLHelper.setWindowState(actionURL, windowState, portletRequest);
				PortletURLHelper.setSecure(actionURL, secure);

				// According to the Bridge Spec, the "javax.portlet.faces.Secure" parameter must not be "carried
				// forward to the generated reference." According to a clarification in the Portlet 3.0 JavaDoc for
				// BaseURL#setProperty(String,String), setting the parameter to null will remove it.
				actionURL.setParameter(Bridge.PORTLET_SECURE_PARAMETER, (String) null);

				baseURL = actionURL;
			}

			// Otherwise, return a PortletURL (Render URL) that contains the "_jsfBridgeNonFacesView" render parameter,
			// which is a signal to the GenericFacesPortlet to dispatch to this non-Faces target when the URL is
			// requested. Note that this seems to be a use-case that is contradictory with the JavaDoc for
			// Brige#VIEW_LINK which claims navigation to a different view. But there are a number of tests in the TCK
			// that utilize this (see below).
			else {

				Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if ((portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) ||
						(portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE)) {

					// TCK TestPage097: encodeActionURLNonJSFViewRenderTest
					// TCK TestPage098: encodeActionURLNonJSFViewWithParamRenderTest
					// TCK TestPage099: encodeActionURLNonJSFViewWithModeRenderTest
					// TCK TestPage100: encodeActionURLNonJSFViewWithInvalidModeRenderTest
					// TCK TestPage101: encodeActionURLNonJSFViewWithWindowStateRenderTest
					// TCK TestPage102: encodeActionURLNonJSFViewWithInvalidWindowStateRenderTest
					// TCK TestPage103: encodeActionURLNonJSFViewResourceTest
					// TCK TestPage104: encodeActionURLNonJSFViewWithParamResourceTest
					// TCK TestPage105: encodeActionURLNonJSFViewWithModeResourceTest
					// TCK TestPage106: encodeActionURLNonJSFViewWithInvalidModeResourceTest
					// TCK TestPage107: encodeActionURLNonJSFViewWithWindowStateResourceTest
					// TCK TestPage108: encodeActionURLNonJSFViewWithInvalidWindowStateResourceTest
					ExternalContext externalContext = facesContext.getExternalContext();
					PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
					PortletURL renderURL = createRenderURL(facesContext, EXCLUDED_PARAMETER_NAMES);
					renderURL.setParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER, bridgeURI.getPath());
					PortletURLHelper.setPortletMode(renderURL, portletMode, portletRequest);
					PortletURLHelper.setWindowState(renderURL, windowState, portletRequest);
					PortletURLHelper.setSecure(renderURL, secure);

					// According to the Bridge Spec, the "javax.portlet.faces.Secure" parameter must not be "carried
					// forward to the generated reference." According to a clarification in the Portlet 3.0 JavaDoc for
					// BaseURL#setProperty(String,String), setting the parameter to null will remove it.
					renderURL.setParameter(Bridge.PORTLET_SECURE_PARAMETER, (String) null);

					baseURL = renderURL;
				}
				else {
					throw new IllegalStateException("Unable to encode a URL for a non-Faces view in the " +
						portletRequestPhase + " of the portlet lifecycle.");
				}
			}
		}

		// Otherwise, if the URL targets a Faces viewId, then return a ResourceURL that targets the view.
		else if (getViewId() != null) {

			// TCK TestPage073: scopeAfterRedisplayResourcePPRTest
			// TCK TestPage121: encodeActionURLJSFViewResourceTest
			// TCK TestPage122: encodeActionURLWithParamResourceTest
			// TCK TestPage123: encodeActionURLWithModeResourceTest
			// TCK TestPage124: encodeActionURLWithInvalidModeResourceTest
			// TCK TestPage125: encodeActionURLWithWindowStateResourceTest
			// TCK TestPage126: encodeActionURLWithInvalidWindowStateResourceTest
			// TCK TestPage127: encodeURLEscapingTest
			// TCK TestPage137: encodeResourceURLWithModeTest
			baseURL = createResourceURL(facesContext, EXCLUDED_PARAMETER_NAMES);
		}

		// Otherwise, if the bridge must encode the URL to satisfy "in-protocol" resource serving, then return a
		// an appropriate ResourceURL.
		else if (inProtocol) {

			// TCK TestPage071: nonFacesResourceTest
			ResourceURL resourceURL = createResourceURL(facesContext);
			resourceURL.setResourceID(bridgeURI.getContextRelativePath(contextPath));
			baseURL = resourceURL;
		}

		// Otherwise, assume that the URL is for an resource external to the portlet context like
		// "/portalcontext/resources/foo.png" and return a BaseURL string representation of it.
		else {

			// TCK TestPage133: encodeResourceURLTest
			// TCK TestPage134: encodeResourceURLBackLinkTest
			baseURL = new BaseURLNonEncodedImpl(bridgeURI);
		}

		return baseURL;
	}

	private String getEncodedBackLinkURL(FacesContext facesContext) {

		String encodedBackLinkURL = "";
		Application application = facesContext.getApplication();
		ViewHandler viewHandler = application.getViewHandler();
		String backLinkURL = viewHandler.getActionURL(facesContext, currentViewId);

		try {
			ExternalContext externalContext = facesContext.getExternalContext();
			encodedBackLinkURL = URLEncoder.encode(externalContext.encodeActionURL(backLinkURL), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}

		return encodedBackLinkURL;
	}
}
