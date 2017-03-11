/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.util.internal;

import com.liferay.faces.bridge.application.internal.MissingResourceImpl;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Kyle Stiemann
 */
public final class PortletResourceUtilCompat {

	// Private Constants
	private static final boolean PLUTO_DETECTED = ProductFactory.getProduct(Product.Name.PLUTO).isDetected();

	private PortletResourceUtilCompat() {
		throw new AssertionError();
	}

	public static boolean isPortletResourceURL(String url) {
		return (url != null) &&
			((PLUTO_DETECTED && url.contains("javax.faces.resource:")) || url.contains("javax.faces.resource=") ||
				url.equals(MissingResourceImpl.RES_NOT_FOUND));
	}
}
