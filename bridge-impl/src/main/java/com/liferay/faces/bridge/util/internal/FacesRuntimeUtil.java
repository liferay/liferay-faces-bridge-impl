/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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

import javax.faces.render.ResponseStateManager;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class FacesRuntimeUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FacesRuntimeUtil.class);

	// Private Constants
	private static final boolean JSF_RUNTIME_SUPPORTS_NAMESPACING_VIEWSTATE;

	static {
		boolean namespacedViewStateSupported = false;
		Product mojarra = ProductFactory.getProduct(Product.Name.MOJARRA);

		if (mojarra.isDetected()) {

			int mojarraMajorVersion = mojarra.getMajorVersion();

			if (mojarraMajorVersion == 2) {

				int mojarraMinorVersion = mojarra.getMinorVersion();

				if (mojarraMinorVersion == 1) {
					namespacedViewStateSupported = (mojarra.getPatchVersion() >= 27);
				}
				else if (mojarraMinorVersion == 2) {
					namespacedViewStateSupported = (mojarra.getPatchVersion() >= 4);
				}
				else if (mojarraMinorVersion > 2) {
					namespacedViewStateSupported = true;
				}
			}
			else if (mojarraMajorVersion > 2) {
				namespacedViewStateSupported = true;
			}
		}

		Product jsf = ProductFactory.getProduct(Product.Name.JSF);
		logger.debug("JSF runtime [{0}] version [{1}].[{2}].[{3}] supports namespacing [{4}]: [{5}]", jsf.getTitle(),
			jsf.getMajorVersion(), jsf.getMinorVersion(), jsf.getPatchVersion(), ResponseStateManager.VIEW_STATE_PARAM,
			namespacedViewStateSupported);

		JSF_RUNTIME_SUPPORTS_NAMESPACING_VIEWSTATE = namespacedViewStateSupported;
	}

	public static boolean isNamespacedViewStateSupported() {
		return JSF_RUNTIME_SUPPORTS_NAMESPACING_VIEWSTATE;
	}
}
