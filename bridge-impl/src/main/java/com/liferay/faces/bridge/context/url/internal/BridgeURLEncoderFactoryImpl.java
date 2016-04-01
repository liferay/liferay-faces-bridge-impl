/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.context.url.BridgeURLEncoder;
import com.liferay.faces.bridge.context.url.BridgeURLEncoderFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLEncoderFactoryImpl extends BridgeURLEncoderFactory {

	@Override
	public BridgeURLEncoder getBridgeURLEncoder(BridgeConfig bridgeConfig) {
		return new BridgeURLEncoderImpl(bridgeConfig);
	}

	@Override
	public BridgeURLEncoderFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
