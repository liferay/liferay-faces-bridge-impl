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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.util.helper.BooleanHelper;


/**
 * @author  Neil Griffin
 */
public class BridgeURLActionImpl extends BridgeURLBase {

	public BridgeURLActionImpl(String uri, String contextPath, String namespace, String currentViewId,
		BridgeConfig bridgeConfig) throws URISyntaxException {

		super(uri, contextPath, namespace, currentViewId, bridgeConfig);

		// If the URI starts with "portlet:", then
		if (bridgeURI.isPortletScheme()) {

			// If the "_jsfBridgeViewId" parameter is equal to "_jsfBridgeCurrentView" then the URI is self-referencing
			// and the "_jsfBridgeViewId" parameter must be removed from the query-string.
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

		// Otherwise,
		else {

			// If the URI has a "javax.portlet.faces.DirectLink" parameter with a value of "false", then the
			// parameter must be removed from the URI's query-string.
			String directLinkParam = bridgeURI.getParameter(Bridge.DIRECT_LINK);

			if (BooleanHelper.isFalseToken(directLinkParam)) {
				bridgeURI.removeParameter(Bridge.DIRECT_LINK);
			}
		}
	}

	@Override
	public BaseURL toBaseURL() throws MalformedURLException {

		BaseURL baseURL;
		String uri = bridgeURI.toString();

		// If this is executing during the ACTION_PHASE of the portlet lifecycle, then
		PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if (portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) {

			// The Mojarra MultiViewHandler.getResourceURL(String) method is implemented in such a way that it calls
			// ExternalContext.encodeActionURL(ExternalContext.encodeResourceURL(url)). The return value of those calls
			// will ultimately be passed to the ExternalContext.redirect(String) method. For this reason, need to return
			// a simple string-based representation of the URL.
			baseURL = new BaseURLNonEncodedImpl(uri);
		}

		// Otherwise,
		else {

			// Otherwise, if the URL string starts with a "#" character, or it's an absolute URL that is external to
			// this portlet, then simply return the URL string as required by the Bridge Spec.
			if (uri.startsWith("#") || (bridgeURI.isAbsolute() && bridgeURI.isExternal(contextPath))) {

				// TCK TestPage084: encodeActionURLPoundCharTest
				baseURL = new BaseURLNonEncodedImpl(uri, getParameterMap());
			}

			// Otherwise, if the URL string has a "javax.portlet.faces.DirectLink" parameter with a value of "true",
			// then return an absolute path (to the path in the URL string) as required by the Bridge Spec.
			else if (bridgeURI.isExternal(contextPath) || BooleanHelper.isTrueToken(getParameter(Bridge.DIRECT_LINK))) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				baseURL = new BaseURLDirectImpl(uri, getParameterMap(), bridgeURI.getPath(), portletRequest);
			}

			// Otherwise,
			else {

				// Determine whether or not the portlet mode is to be changed by examining the
				// "javax.portlet.faces.PortletMode" parameter.
				boolean modeChanged = false;
				String portletMode = getParameter(Bridge.PORTLET_MODE_PARAMETER);

				if ((portletMode != null) && (portletMode.length() > 0)) {
					modeChanged = true;
				}

				// Note: If the URL string starts with "portlet:", then the type of URL the portlet container creates is
				// determined by what follows the scheme, such as "portlet:action" "portlet:render" and
				// "portlet:resource".
				FacesContext facesContext = FacesContext.getCurrentInstance();

				if (bridgeURI.isPortletScheme()) {
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
				}
				else {

					String bridgeAjaxRedirectValue = removeParameter("_bridgeAjaxRedirect");
					boolean bridgeAjaxRedirect = BooleanHelper.isTrueToken(bridgeAjaxRedirectValue);

					if (portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE) {
						baseURL = new BaseURLNonEncodedImpl(uri);
					}
					else if ((portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE) && bridgeAjaxRedirect) {
						baseURL = createRenderURL(facesContext, modeChanged);
					}
					else {
						baseURL = createActionURL(facesContext, modeChanged);
					}
				}

				// If the URL string is self-referencing, meaning, it targets the current Faces view, then copy the
				// render parameters from the current PortletRequest to the BaseURL.
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

					// According to the Bridge Spec, the "javax.portlet.faces.PortletMode"" parameter must not be
					// "carried forward to the generated reference." According to a clarification in the Portlet 3.0
					// JavaDoc for BaseURL#setProperty(String,String), setting the parameter to null will remove it.
					portletURL.setParameter(Bridge.PORTLET_MODE_PARAMETER, (String) null);

					String windowState = getParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
					PortletURLHelper.setWindowState(portletURL, windowState, portletRequest);

					// According to the Bridge Spec, the "javax.portlet.faces.WindowState" parameter must not be
					// "carried forward to the generated reference." According to a clarification in the Portlet 3.0
					// JavaDoc for BaseURL#setProperty(String,String), setting the parameter to null will remove it.
					portletURL.setParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER, (String) null);
				}

				// Apply the security.
				String secure = getParameter(Bridge.PORTLET_SECURE_PARAMETER);
				PortletURLHelper.setSecure(baseURL, secure);

				// According to the Bridge Spec, the "javax.portlet.faces.Secure" parameter must not be "carried
				// forward to the generated reference." According to a clarification in the Portlet 3.0 JavaDoc for
				// BaseURL#setProperty(String,String), setting the parameter to null will remove it.
				baseURL.setParameter(Bridge.PORTLET_SECURE_PARAMETER, (String) null);
			}
		}

		return baseURL;
	}
}
