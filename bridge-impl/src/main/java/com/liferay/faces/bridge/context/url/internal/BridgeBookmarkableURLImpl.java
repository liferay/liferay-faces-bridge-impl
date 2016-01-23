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

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.BaseURL;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeBookmarkableURLImpl extends BridgeURLInternalBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeBookmarkableURLImpl.class);

	// Private Data Members
	private String uri;
	private String viewId;
	private String viewIdRenderParameterName;

	public BridgeBookmarkableURLImpl(BridgeContext bridgeContext, BridgeURI bridgeURI,
		Map<String, List<String>> parameters, String viewId) {

		super(bridgeContext, bridgeURI, viewId);
		this.uri = bridgeURI.toString();
		this.viewId = viewId;
		this.viewIdRenderParameterName = bridgeContext.getBridgeConfig().getViewIdRenderParameterName();

		if (uri != null) {

			if (parameters != null) {

				Map<String, String[]> parameterMap = getParameterMap();
				Set<Map.Entry<String, List<String>>> entrySet = parameters.entrySet();

				for (Map.Entry<String, List<String>> mapEntry : entrySet) {

					String key = mapEntry.getKey();
					String[] valueArray = null;
					List<String> valueList = mapEntry.getValue();

					if (valueList != null) {
						valueArray = valueList.toArray(new String[valueList.size()]);
					}

					parameterMap.put(key, valueArray);
				}
			}
		}
	}

	@Override
	public BaseURL toBaseURL() throws MalformedURLException {

		BaseURL baseURL = null;

		// If this is executing during the RENDER_PHASE or RESOURCE_PHASE of the portlet lifecycle, then
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if ((portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE)) {

			baseURL = createRenderURL(uri);
			baseURL.setParameter(viewIdRenderParameterName, viewId);
			baseURL.setParameters(getParameterMap());
		}

		// Otherwise, log an error.
		else {
			logger.error("Unable to encode bookmarkable URL during Bridge.PortletPhase.[{0}].", portletRequestPhase);
		}

		return baseURL;
	}
}
