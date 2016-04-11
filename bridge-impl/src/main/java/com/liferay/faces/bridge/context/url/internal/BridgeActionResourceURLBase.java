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

import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.BaseURL;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public abstract class BridgeActionResourceURLBase extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLBase.class);

	// Private Members
	private boolean selfReferencing;

	public BridgeActionResourceURLBase(BridgeURI bridgeURI, String contextPath, String namespace, String viewId,
		String viewIdParameterName, BridgeConfig bridgeConfig) {
		super(bridgeURI, contextPath, namespace, viewId, viewIdParameterName, bridgeConfig);
		this.selfReferencing = BridgeURLUtil.isViewIdSelfReferencing(bridgeURI, namespace) ||
			BridgeURLUtil.isViewPathSelfReferencing(bridgeURI, namespace);
	}

	protected static void setPortletModeParameter(FacesContext facesContext, PortletURL portletURL,
		String portletMode) {

		if (portletMode != null) {

			try {
				PortletMode candidatePortletMode = new PortletMode(portletMode);
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

				if (portletRequest.isPortletModeAllowed(candidatePortletMode)) {
					portletURL.setPortletMode(candidatePortletMode);
				}
				else {
					// TestPage118: encodeActionURLWithInvalidModeRenderTest
				}
			}
			catch (PortletModeException e) {
				logger.error(e);
			}
		}
	}

	protected static void setRenderParameters(FacesContext facesContext, Map<String, String[]> parameterMap,
		BaseURL baseURL) {

		// Copy the public render parameters of the current view to the BaseURL.
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		Map<String, String[]> publicParameterMap = portletRequest.getPublicParameterMap();

		if (publicParameterMap != null) {
			Set<Map.Entry<String, String[]>> publicParamterMapEntrySet = publicParameterMap.entrySet();

			for (Map.Entry<String, String[]> mapEntry : publicParamterMapEntrySet) {
				String publicParameterName = mapEntry.getKey();

				// Note that preserved action parameters, parameters that already exist in the URL string,
				// and "javax.faces.ViewState" must not be copied.
				if (!ResponseStateManager.VIEW_STATE_PARAM.equals(publicParameterName) &&
						!parameterMap.containsKey(publicParameterName)) {
					baseURL.setParameter(publicParameterName, mapEntry.getValue());
				}
			}
		}

		// Copy the private render parameters of the current view to the BaseURL.
		Map<String, String[]> privateParameterMap = portletRequest.getPrivateParameterMap();

		if (privateParameterMap != null) {
			Set<Map.Entry<String, String[]>> privateParameterMapEntrySet = privateParameterMap.entrySet();

			for (Map.Entry<String, String[]> mapEntry : privateParameterMapEntrySet) {
				String privateParameterName = mapEntry.getKey();

				// Note that preserved action parameters, parameters that already exist in the URL string,
				// and "javax.faces.ViewState" must not be copied.
				if (!ResponseStateManager.VIEW_STATE_PARAM.equals(privateParameterName) &&
						!parameterMap.containsKey(privateParameterName)) {
					baseURL.setParameter(privateParameterName, mapEntry.getValue());
				}
			}
		}
	}

	protected static void setWindowStateParameter(FacesContext facesContext, PortletURL portletURL,
		String windowState) {

		if (windowState != null) {

			try {
				WindowState candidateWindowState = new WindowState(windowState);
				ExternalContext externalContext = facesContext.getExternalContext();
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

				if (portletRequest.isWindowStateAllowed(candidateWindowState)) {
					portletURL.setWindowState(candidateWindowState);
				}
				else {
					// TestPage120: encodeActionURLWithInvalidWindowStateRenderTest
				}
			}
			catch (WindowStateException e) {
				logger.error(e);
			}
		}
	}

	protected final boolean isSelfReferencing() {
		return selfReferencing;
	}

	protected final void setSecureParameter(String secure, BaseURL baseURL) {

		if (secure != null) {

			try {
				baseURL.setSecure("true".equalsIgnoreCase(secure));
			}
			catch (PortletSecurityException e) {
				logger.error(e);
			}
		}
	}
}
