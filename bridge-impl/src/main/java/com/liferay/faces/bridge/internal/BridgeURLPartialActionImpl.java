/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.BaseURL;
import javax.portlet.PortletConfig;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.render.FacesURLEncoder;


/**
 * @author  Neil Griffin
 */
public class BridgeURLPartialActionImpl extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLPartialActionImpl.class);

	public BridgeURLPartialActionImpl(String uri, String contextPath, String namespace, String encoding,
		FacesURLEncoder facesURLEncoder, String currentViewId, boolean clientWindowEnabled, String clientWindowId,
		Map<String, String> clientWindowParameters, PortletConfig portletConfig, BridgeConfig bridgeConfig)
		throws URISyntaxException, UnsupportedEncodingException {

		super(uri, contextPath, namespace, encoding, facesURLEncoder, currentViewId, portletConfig, bridgeConfig);

		// If the client window feature is enabled and the URI does not have a "jfwid" parameter then set the
		// "jfwid" parameter and any associated client window parameters on the URI.
		if (clientWindowEnabled && (clientWindowId != null) && (uri != null) &&
				!uri.contains(ResponseStateManager.CLIENT_WINDOW_URL_PARAM)) {

			bridgeURI.setParameter(ResponseStateManager.CLIENT_WINDOW_URL_PARAM, clientWindowId);

			if (clientWindowParameters != null) {
				bridgeURI.addParameters(clientWindowParameters);
			}
		}

		bridgeURI.setParameter(Bridge.FACES_AJAX_PARAMETER, "true");
	}

	@Override
	public BaseURL toBaseURL(FacesContext facesContext) throws MalformedURLException {

		BaseURL baseURL = null;

		String uri = bridgeURI.toString();

		if (uri != null) {

			if (uri.startsWith("http")) {
				baseURL = new BaseURLBridgeURIAdapterImpl(bridgeURI);
				logger.debug("URL starts with http so assuming that it has already been encoded: url=[{0}]", uri);
			}
			else {
				baseURL = createResourceURL(facesContext, bridgeURI.getParameterMap());
			}
		}
		else {
			logger.warn("Unable to encode PartialActionURL for url=[null]");
		}

		return baseURL;
	}

	@Override
	protected String getViewIdParameterName() {
		return viewIdResourceParameterName;
	}
}
