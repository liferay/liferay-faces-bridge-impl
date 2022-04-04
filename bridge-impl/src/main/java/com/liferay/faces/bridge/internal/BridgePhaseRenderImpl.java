/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeException;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePhaseRenderImpl extends BridgePhaseRenderCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseRenderImpl.class);

	public BridgePhaseRenderImpl(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		super(renderRequest, renderResponse, portletConfig, bridgeConfig);
	}

	@Override
	public void execute() throws BridgeException {

		logger.debug(Logger.SEPARATOR);
		logger.debug("execute(RenderRequest, RenderResponse) portletName=[{0}] portletMode=[{1}]", portletName,
			renderRequest.getPortletMode());

		Object renderPartAttribute = renderRequest.getAttribute(RenderRequest.RENDER_PART);

		if ((renderPartAttribute != null) && renderPartAttribute.equals(RenderRequest.RENDER_HEADERS)) {
			doFacesHeaders(renderRequest, renderResponse);
		}
		else {

			try {
				execute(null);
			}
			catch (BridgeException e) {
				throw e;
			}
			catch (Throwable t) {
				throw new BridgeException(t);
			}
			finally {
				cleanup(renderRequest);
			}

			logger.debug(Logger.SEPARATOR);
		}
	}

	@Override
	protected void cleanup(PortletRequest portletRequest) {

		// If required, cause the BridgeRequestScope to go out-of-scope.
		if (!bridgeRequestScopeActionEnabled) {
			bridgeRequestScopeCache.removeValue(bridgeRequestScope.getId());
		}

		super.cleanup(portletRequest);
	}

	protected void doFacesHeaders(RenderRequest renderRequest, RenderResponse renderResponse) {
		logger.trace(
			"doFacesHeaders(RenderRequest, RenderResponse) this=[{0}], renderRequest=[{1}], renderResponse=[{2}]", this,
			renderRequest, renderResponse);
	}
}
