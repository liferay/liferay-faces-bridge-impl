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

import java.io.Serializable;

import javax.portlet.PortletConfig;
import javax.portlet.faces.BridgePublicRenderParameterHandler;

import com.liferay.faces.bridge.BridgePublicRenderParameterHandlerFactory;
import com.liferay.faces.bridge.util.internal.TCCLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePublicRenderParameterHandlerFactoryImpl extends BridgePublicRenderParameterHandlerFactory
	implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7901892927958470297L;

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePublicRenderParameterHandlerFactoryImpl.class);

	@Override
	public BridgePublicRenderParameterHandler getBridgePublicRenderParameterHandler(PortletConfig portletConfig) {

		BridgePublicRenderParameterHandler bridgePublicRenderParameterHandler = null;

		// TCK: initMethodTest
		String bridgePublicRenderParameterHandlerClass = portletConfig.getInitParameter(
				"javax.portlet.faces.bridgePublicRenderParameterHandler");

		if (bridgePublicRenderParameterHandlerClass != null) {

			try {

				Class<?> clazz = TCCLUtil.loadClassFromContext(getClass(), bridgePublicRenderParameterHandlerClass);
				bridgePublicRenderParameterHandler = (BridgePublicRenderParameterHandler) clazz.newInstance();
			}
			catch (Exception e) {
				logger.error(e);
			}
		}

		return bridgePublicRenderParameterHandler;
	}

	@Override
	public BridgePublicRenderParameterHandlerFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
