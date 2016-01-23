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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.ResponseWriter;

import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.HeadResponseWriter;
import com.liferay.faces.bridge.context.HeadResponseWriterFactory;


/**
 * @author  Neil Griffin
 */
public class HeadResponseWriterFactoryImpl extends HeadResponseWriterFactory {

	@Override
	public HeadResponseWriter getHeadResponseWriter(BridgeContext bridgeContext, ResponseWriter responseWriter) {

		return new HeadResponseWriterImpl(responseWriter, bridgeContext.getPortletResponse());
	}

	@Override
	public HeadResponseWriterFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
