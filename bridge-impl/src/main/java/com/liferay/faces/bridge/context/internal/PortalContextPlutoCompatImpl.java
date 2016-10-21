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

import javax.portlet.PortalContext;

import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class PortalContextPlutoCompatImpl extends PortalContextBridgeImpl {

	public PortalContextPlutoCompatImpl(PortalContext portalContext) {
		super(portalContext);
	}

	@Override
	public String getProperty(String name) {

		if (BridgePortalContext.STRICT_NAMESPACED_PARAMETERS_SUPPORT.equals(name)) {
			return Boolean.TRUE.toString();
		}
		else {
			return super.getProperty(name);
		}
	}
}
