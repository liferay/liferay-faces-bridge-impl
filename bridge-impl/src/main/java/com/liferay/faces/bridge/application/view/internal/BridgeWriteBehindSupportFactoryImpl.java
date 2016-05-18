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
package com.liferay.faces.bridge.application.view.internal;

import java.lang.reflect.Constructor;

import javax.faces.FacesException;
import javax.portlet.MimeResponse;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.BridgeWriteBehindResponse;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeWriteBehindSupportFactoryImpl extends BridgeWriteBehindSupportFactory {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeWriteBehindSupportFactoryImpl.class);

	// Private Data Members
	private Class<? extends BridgeWriteBehindResponse> bridgeWriteBehindRenderResponseClass;
	private Class<? extends BridgeWriteBehindResponse> bridgeWriteBehindResourceResponseClass;

	@SuppressWarnings("unchecked")
	protected Class<? extends BridgeWriteBehindResponse> loadClass(String className,
		Class<? extends BridgeWriteBehindResponse> defaultClass) {

		Class<? extends BridgeWriteBehindResponse> bridgeWriteBehindResponseClass;

		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Class<?> clazz = classLoader.loadClass(className);

			if ((BridgeWriteBehindResponse.class.isAssignableFrom(clazz))) {
				bridgeWriteBehindResponseClass = (Class<? extends BridgeWriteBehindResponse>) clazz;
				logger.debug("Loaded bridgeWriteBehindResponseClass=[{0}]", bridgeWriteBehindResponseClass);
			}
			else {
				logger.error(
					"Class=[{0}] does not implement the BridgeWriteBehindResponse interface; using default=[{1}]",
					className, defaultClass);
				bridgeWriteBehindResponseClass = defaultClass;
			}
		}
		catch (ClassNotFoundException e) {
			logger.error(e);
			bridgeWriteBehindResponseClass = defaultClass;
		}

		return bridgeWriteBehindResponseClass;
	}

	@Override
	public BridgeWriteBehindResponse getBridgeWriteBehindResponse(MimeResponse mimeResponse, BridgeConfig bridgeConfig)
		throws FacesException {

		BridgeWriteBehindResponse bridgeWriteBehindResponse;

		if ((bridgeWriteBehindRenderResponseClass == null) || (bridgeWriteBehindResourceResponseClass == null)) {

			if (bridgeWriteBehindRenderResponseClass == null) {
				String className = bridgeConfig.getWriteBehindRenderResponseWrapper();
				bridgeWriteBehindRenderResponseClass = loadClass(className, BridgeWriteBehindResponseRenderImpl.class);
			}

			if (bridgeWriteBehindResourceResponseClass == null) {
				String className = bridgeConfig.getWriteBehindResourceResponseWrapper();
				bridgeWriteBehindResourceResponseClass = loadClass(className,
						BridgeWriteBehindResponseResourceImpl.class);
			}
		}

		Class<? extends BridgeWriteBehindResponse> bridgeWriteBehindResponseClass;

		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if (portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) {
			bridgeWriteBehindResponseClass = bridgeWriteBehindRenderResponseClass;
		}
		else {
			bridgeWriteBehindResponseClass = bridgeWriteBehindResourceResponseClass;
		}

		try {

			if (portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) {

				// Third, try to call a constructor that takes a RenderResponse as a single parameter, which
				// is the required signature for any class that extends RenderResponseWrapper.
				@SuppressWarnings("unchecked")
				Constructor<BridgeWriteBehindResponse> constructor = (Constructor<BridgeWriteBehindResponse>)
					bridgeWriteBehindResponseClass.getConstructor(RenderResponse.class);
				bridgeWriteBehindResponse = constructor.newInstance((RenderResponse) mimeResponse);
			}
			else {

				// Third, try to call a constructor that takes a ResourceResponse as a single parameter,
				// which is the required signature for any class that extends RenderResponseWrapper.
				@SuppressWarnings("unchecked")
				Constructor<BridgeWriteBehindResponse> constructor = (Constructor<BridgeWriteBehindResponse>)
					bridgeWriteBehindResponseClass.getConstructor(ResourceResponse.class);
				bridgeWriteBehindResponse = constructor.newInstance((ResourceResponse) mimeResponse);
			}
		}
		catch (Exception e) {
			throw new FacesException(e.getMessage());
		}

		return bridgeWriteBehindResponse;
	}

	@Override
	public BridgeWriteBehindSupportFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
