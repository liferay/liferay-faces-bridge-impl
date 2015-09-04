/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
	protected void logError(Throwable t) {
		logger.error(t);
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
}
