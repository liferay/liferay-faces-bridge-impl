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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.faces.BridgeConfig;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLPartialActionImpl extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLPartialActionImpl.class);

	public BridgeURLPartialActionImpl(String uri, String contextPath, String namespace, String currentViewId,
		BridgeConfig bridgeConfig) throws URISyntaxException {

		super(uri, contextPath, namespace, currentViewId, bridgeConfig);

		bridgeURI.setParameter(BridgeExt.FACES_AJAX_PARAMETER, "true");
	}

	@Override
	public BaseURL toBaseURL(FacesContext facesContext) throws MalformedURLException {

		BaseURL baseURL = null;

		String uri = bridgeURI.toString();

		if (uri != null) {

			if (uri.startsWith("http")) {
				baseURL = new BaseURLNonEncodedImpl(bridgeURI);
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
