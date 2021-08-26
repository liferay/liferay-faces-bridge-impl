/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.filter.BridgePortletRequestFactory;
import javax.portlet.faces.filter.BridgePortletResponseFactory;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePhaseHeaderImpl extends BridgePhaseHeaderRenderCommon {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseHeaderImpl.class);

	// Private Data Members
	private HeaderRequest headerRequest;
	private HeaderResponse headerResponse;

	public BridgePhaseHeaderImpl(HeaderRequest headerRequest, HeaderResponse headerResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		super(portletConfig, bridgeConfig);
		this.headerRequest = BridgePortletRequestFactory.getHeaderRequestInstance(headerRequest, headerResponse,
				portletConfig, bridgeConfig);
		this.headerResponse = BridgePortletResponseFactory.getHeaderResponseInstance(headerRequest, headerResponse,
				portletConfig, bridgeConfig);
	}

	@Override
	public void execute() throws BridgeException {

		logger.debug(Logger.SEPARATOR);
		logger.debug("execute(RenderRequest, RenderResponse) portletName=[{0}] portletMode=[{1}]", portletName,
			headerRequest.getPortletMode());

		try {
			executeRender(null, PortletPhase.HEADER_PHASE);
		}
		catch (BridgeException e) {
			throw e;
		}
		catch (Throwable t) {
			throw new BridgeException(t);
		}
		finally {
			cleanup(headerRequest);
		}

		logger.debug(Logger.SEPARATOR);
	}

	@Override
	protected MimeResponse getMimeResponse() {
		return headerResponse;
	}

	@Override
	protected RenderRequest getRenderRequest() {
		return headerRequest;
	}
}
