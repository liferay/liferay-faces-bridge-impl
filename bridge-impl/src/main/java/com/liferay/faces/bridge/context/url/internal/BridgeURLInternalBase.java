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

import java.util.logging.Level;

import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.bridge.context.url.BridgeURLBase;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeURLInternalBase extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLInternalBase.class);

	public BridgeURLInternalBase(BridgeContext bridgeContext, BridgeURI bridgeURI, String viewId) {
		super(bridgeContext, bridgeURI, viewId);
	}

	@Override
	protected void log(Level level, String message, Object... arguments) {

		if (level != null) {

			if (level == Level.SEVERE) {
				logger.error(message, arguments);
			}
			else if (level == Level.WARNING) {
				logger.warn(message, arguments);
			}
			else if (level == Level.INFO) {
				logger.info(message, arguments);
			}
			else {
				logger.debug(message, arguments);
			}
		}
	}

	@Override
	protected void logError(Throwable t) {
		logger.error(t);
	}
}
