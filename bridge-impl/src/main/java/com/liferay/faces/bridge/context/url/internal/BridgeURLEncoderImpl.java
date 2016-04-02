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
package com.liferay.faces.bridge.context.url.internal;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeFactoryFinder;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.config.internal.BridgeConfigAttributeMap;
import com.liferay.faces.bridge.context.internal.FacesView;
import com.liferay.faces.bridge.context.internal.FacesViewImpl;
import com.liferay.faces.bridge.context.url.BridgeResourceURL;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.bridge.context.url.BridgeURIFactory;
import com.liferay.faces.bridge.context.url.BridgeURL;
import com.liferay.faces.bridge.context.url.BridgeURLEncoder;
import com.liferay.faces.bridge.context.url.BridgeURLFactory;
import com.liferay.faces.bridge.internal.BridgeExt;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLEncoderImpl extends BridgeURLEncoderCompatImpl implements BridgeURLEncoder {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLEncoderImpl.class);

	// Private Data Members
	private BridgeURIFactory bridgeURIFactory;
	private BridgeURLFactory bridgeURLFactory;
	private List<ConfiguredServletMapping> configuredFacesServletMappings;
	private List<String> configuredSuffixes;

	@SuppressWarnings("unchecked")
	public BridgeURLEncoderImpl(BridgeConfig bridgeConfig) {
		this.bridgeURIFactory = (BridgeURIFactory) BridgeFactoryFinder.getFactory(BridgeURIFactory.class);
		this.bridgeURLFactory = (BridgeURLFactory) BridgeFactoryFinder.getFactory(BridgeURLFactory.class);
		this.configuredFacesServletMappings = (List<ConfiguredServletMapping>) bridgeConfig.getAttributes().get(
				BridgeConfigAttributeMap.CONFIGURED_FACES_SERVLET_MAPPINGS);
		this.configuredSuffixes = (List<String>) bridgeConfig.getAttributes().get(
				BridgeConfigAttributeMap.CONFIGURED_SUFFIXES);
	}

	@Override
	public BridgeURL encodeActionURL(FacesContext facesContext, String url) throws URISyntaxException {

		logger.debug("encodeActionURL fromURL=[{0}]", url);

		// Determine the target of the specified URL, which could be a Faces-View or a Non-Faces-View.
		BridgeURI bridgeURI = bridgeURIFactory.getBridgeURI(url);
		UIViewRoot viewRoot = facesContext.getViewRoot();
		String currentFacesViewId = viewRoot.getViewId();
		BridgeURL bridgeActionURL = bridgeURLFactory.getBridgeActionURL(facesContext, bridgeURI, currentFacesViewId);

		// If the specified URL starts with "portlet:", then
		Map<String, String[]> parameterMap = bridgeActionURL.getParameterMap();

		if (bridgeURI.isPortletScheme()) {

			// If the "_jsfBridgeViewId" URL parameter is equal to "_jsfBridgeCurrentView" then the URL is
			// self-referencing and the "_jsfBridgeViewId" parameter muse be removed from the URL.
			String facesViewIdParameter = bridgeActionURL.getParameter(Bridge.FACES_VIEW_ID_PARAMETER);

			if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewIdParameter)) {
				bridgeActionURL.setSelfReferencing(true);
				parameterMap.remove(Bridge.FACES_VIEW_ID_PARAMETER);
			}

			// If the "_jsfBridgeViewPath" URL parameter is equal to "_jsfBridgeCurrentView" then the URL is
			// self-referencing and the "_jsfBridgeViewPath" parameter muse be removed from the URL.
			String facesViewPathParameter = bridgeActionURL.getParameter(Bridge.FACES_VIEW_PATH_PARAMETER);

			if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewPathParameter)) {
				bridgeActionURL.setSelfReferencing(true);
				parameterMap.remove(Bridge.FACES_VIEW_PATH_PARAMETER);
			}
		}

		// Otherwise, the specified URL must be for a path-encoded URL (either a Faces-View or Non-Faces-View)
		else {

			// If the specified URL has a "javax.portlet.faces.DirectLink" parameter with a value of "false", then
			// remove it from the map of parameters as required by the Bridge Spec.
			String directLinkParam = bridgeActionURL.getParameter(Bridge.DIRECT_LINK);

			if (BooleanHelper.isFalseToken(directLinkParam)) {
				parameterMap.remove(Bridge.DIRECT_LINK);
			}

			String contextRelativeViewPath = null;
			ExternalContext externalContext = facesContext.getExternalContext();
			String contextPath = externalContext.getRequestContextPath();

			if (!bridgeURI.isExternal(contextPath)) {

				contextRelativeViewPath = bridgeURI.getContextRelativePath(contextPath);

				if (contextRelativeViewPath == null) {
					contextRelativeViewPath = viewRoot.getViewId();
				}
			}

			FacesView targetFacesView = new FacesViewImpl(contextRelativeViewPath, configuredSuffixes,
					configuredFacesServletMappings);

			if (!bridgeURI.isAbsolute() && !targetFacesView.isExtensionMapped() && !targetFacesView.isPathMapped() &&
					!url.startsWith("#") && (contextRelativeViewPath != null)) {
				bridgeActionURL.setParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER, contextRelativeViewPath);
			}
		}

		return bridgeActionURL;
	}

	@Override
	public BridgeURL encodeBookmarkableURL(FacesContext facesContext, String url, Map<String, List<String>> parameters)
		throws URISyntaxException {

		logger.debug("encodeBookmarkableURL fromURL=[{0}]", url);

		String viewId = url;
		ExternalContext externalContext = facesContext.getExternalContext();
		String requestContextPath = externalContext.getRequestContextPath();

		if (url.startsWith(requestContextPath)) {
			viewId = url.substring(requestContextPath.length());
		}

		BridgeURI bridgeURI = bridgeURIFactory.getBridgeURI(url);

		return bridgeURLFactory.getBridgeBookmarkableURL(facesContext, bridgeURI, parameters, viewId);
	}

	@Override
	public BridgeURL encodePartialActionURL(FacesContext facesContext, String url) throws URISyntaxException {

		logger.debug("encodePartialActionURL fromURL=[{0}]", url);

		UIViewRoot viewRoot = facesContext.getViewRoot();
		String currentFacesViewId = viewRoot.getViewId();
		BridgeURI bridgeURI = bridgeURIFactory.getBridgeURI(url);
		BridgeURL bridgePartialActionURL = bridgeURLFactory.getBridgePartialActionURL(facesContext, bridgeURI,
				currentFacesViewId);
		bridgePartialActionURL.setParameter(BridgeExt.FACES_AJAX_PARAMETER, Boolean.TRUE.toString());

		return bridgePartialActionURL;
	}

	@Override
	public BridgeURL encodeRedirectURL(FacesContext facesContext, String url, Map<String, List<String>> parameters)
		throws URISyntaxException {

		logger.debug("encodeRedirectURL fromURL=[{0}]", url);

		BridgeURI bridgeURI = bridgeURIFactory.getBridgeURI(url);
		String redirectViewId = null;

		ExternalContext externalContext = facesContext.getExternalContext();
		String contextPath = externalContext.getRequestContextPath();

		if (!bridgeURI.isExternal(contextPath)) {
			redirectViewId = bridgeURI.getContextRelativePath(contextPath);
		}

		BridgeURL bridgeRedirectURL = bridgeURLFactory.getBridgeRedirectURL(facesContext, bridgeURI, parameters,
				redirectViewId);

		if (isJSF2PartialRequest(facesContext)) {
			bridgeRedirectURL.setParameter("_bridgeAjaxRedirect", "true");
		}

		return bridgeRedirectURL;
	}

	@Override
	public BridgeURL encodeResourceURL(FacesContext facesContext, String url) throws URISyntaxException {

		logger.debug("encodeResourceURL fromURL=[{0}]", url);

		String currentFacesViewId = null;
		UIViewRoot uiViewRoot = facesContext.getViewRoot();

		if (uiViewRoot != null) {
			currentFacesViewId = uiViewRoot.getViewId();
		}

		BridgeURI bridgeURI = bridgeURIFactory.getBridgeURI(url);
		BridgeResourceURL bridgeResourceURL = bridgeURLFactory.getBridgeResourceURL(facesContext, bridgeURI,
				currentFacesViewId);

		// If the "javax.portlet.faces.ViewLink" parameter is found and set to "true", then
		String viewLinkParam = bridgeResourceURL.getParameter(Bridge.VIEW_LINK);
		Map<String, String[]> parameterMap = bridgeResourceURL.getParameterMap();
		ExternalContext externalContext = facesContext.getExternalContext();
		String contextPath = externalContext.getRequestContextPath();

		if (BooleanHelper.isTrueToken(viewLinkParam)) {

			// Since this is going to be a URL that represents navigation to a different viewId, need to remove the
			// "javax.portlet.faces.ViewLink" parameter as required by the Bridge Spec.
			parameterMap.remove(Bridge.VIEW_LINK);

			// Set a flag indicating that this is a view-link type of navigation.
			bridgeResourceURL.setViewLink(true);

			// If the "javax.portlet.faces.BackLink" parameter is found, then replace it's value with a URL that can
			// cause navigation back to the current view.
			if (bridgeResourceURL.getParameter(Bridge.BACK_LINK) != null) {
				bridgeResourceURL.replaceBackLinkParameter(facesContext);
			}
		}

		// If the specified URL is opaque, meaning it starts with something like "portlet:" or "mailto:" and doesn't
		// have the double-forward-slash like "http://" does, then
		if (bridgeURI.isOpaque()) {

			// If the specified URL starts with "portlet:", then
			if (bridgeURI.isPortletScheme()) {

				// If the "_jsfBridgeViewId" URL parameter is equal to "_jsfBridgeCurrentView" then the URL is
				// self-referencing and the "_jsfBridgeViewId" parameter muse be removed from the URL.
				String facesViewIdParameter = bridgeResourceURL.getParameter(Bridge.FACES_VIEW_ID_PARAMETER);

				if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewIdParameter)) {
					bridgeResourceURL.setSelfReferencing(true);
					parameterMap.remove(Bridge.FACES_VIEW_ID_PARAMETER);
				}

				// If the "_jsfBridgeViewPath" URL parameter is equal to "_jsfBridgeCurrentView" then the URL is
				// self-referencing and the "_jsfBridgeViewPath" parameter muse be removed from the URL.
				String facesViewPathParameter = bridgeResourceURL.getParameter(Bridge.FACES_VIEW_PATH_PARAMETER);

				if (Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewPathParameter)) {
					bridgeResourceURL.setSelfReferencing(true);
					parameterMap.remove(Bridge.FACES_VIEW_PATH_PARAMETER);
				}
			}
		}

		// Otherwise, if the specified URL is hierarchical and targets an external resource, then
		else if (bridgeURI.isHierarchical() && bridgeURI.isExternal(contextPath)) {

			// If the "javax.portlet.faces.BackLink" parameter is found, then replace it's value with a URL that can
			// cause navigation back to the current view.
			if (bridgeResourceURL.getParameter(Bridge.BACK_LINK) != null) {
				bridgeResourceURL.replaceBackLinkParameter(facesContext);
			}
		}

		// Otherwise, if the specified URL is hierarchical and targets a resource internal to this application, then
		else if (bridgeURI.isHierarchical() && !bridgeURI.isExternal(contextPath)) {

			// If the "javax.portlet.faces.BackLink" parameter is found, then replace it's value with a URL that can
			// cause navigation back to the current view.
			if (bridgeResourceURL.getParameter(Bridge.BACK_LINK) != null) {
				bridgeResourceURL.replaceBackLinkParameter(facesContext);
			}

			// If the "javax.portlet.faces.InProtocolResourceLink" parameter is found, then
			if ((bridgeResourceURL.getParameter(Bridge.IN_PROTOCOL_RESOURCE_LINK) != null)) {
				bridgeResourceURL.setInProtocol(true);

				// Since an in-protocol-resource URL must be a ResourceURL, the "javax.portlet.faces.PortletMode" and
				// "javax.portlet.faces.WindowState" parameters must be removed from the URL (if present) because you
				// can change a PortletMode or WindowState in a ResourceRequest.
				parameterMap.remove(Bridge.PORTLET_MODE_PARAMETER);
				parameterMap.remove(Bridge.PORTLET_WINDOWSTATE_PARAMETER);

				// The Bridge Spec indicates that the "javax.portlet.faces.Secure" parameter must be removed but must
				// also be used to set the security of the ResourceURL below.
				String secureParam = bridgeResourceURL.getParameter(Bridge.PORTLET_SECURE_PARAMETER);
				bridgeResourceURL.setSecure(BooleanHelper.isTrueToken(secureParam));
				parameterMap.remove(Bridge.PORTLET_SECURE_PARAMETER);
			}
		}

		return bridgeResourceURL;
	}
}
