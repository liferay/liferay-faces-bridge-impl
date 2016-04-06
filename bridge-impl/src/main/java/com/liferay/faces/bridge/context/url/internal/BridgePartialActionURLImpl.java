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

import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePartialActionURLImpl extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePartialActionURLImpl.class);

	public BridgePartialActionURLImpl(BridgeURI bridgeURI, String contextPath, String namespace, String viewId,
		String viewIdResourceParameterName, BridgeConfig bridgeConfig) {
		super(bridgeURI, contextPath, namespace, viewId, viewIdResourceParameterName, bridgeConfig);
	}

	@Override
	public String toString() {

		String stringValue = null;

		BridgeURI bridgeURI = getBridgeURI();
		String uri = bridgeURI.toString();

		if (uri != null) {

			if (uri.startsWith("http")) {

				stringValue = toNonEncodedURLString(uri);
				logger.debug("URL starts with http so assuming that it has already been encoded: url=[{0}]", uri);
			}
			else {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				BaseURL baseURL = createResourceURL(facesContext, false);
				stringValue = baseURLtoString(baseURL, bridgeURI.isEscaped());
			}
		}
		else {
			logger.warn("Unable to encode PartialActionURL for url=[null]");
		}

		return stringValue;
	}
}
