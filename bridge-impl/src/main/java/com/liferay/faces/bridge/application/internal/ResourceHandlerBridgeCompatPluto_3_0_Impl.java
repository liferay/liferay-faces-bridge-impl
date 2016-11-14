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
package com.liferay.faces.bridge.application.internal;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class provides a compatibility layer that isolates differences between Pluto 2.0 and Pluto 3.0.
 */
public abstract class ResourceHandlerBridgeCompatPluto_3_0_Impl extends ResourceHandlerBridgeCompat_2_0_Impl {

	// Private Constants
	private static final boolean PLUTO_DETECTED = ProductFactory.getProduct(Product.Name.PLUTO).isDetected();

	@Override
	public boolean isResourceURL(String url) {
		return (PLUTO_DETECTED && url.contains("javax.faces.resource:")) || super.isResourceURL(url);
	}
}
