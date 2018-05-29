/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
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

	private FacesRuntimeUtil() {
		throw new AssertionError();
	}

	public static boolean isAbleToNamespaceViewState(PortalContext portalContext, ExternalContext externalContext) {

		Boolean namespacedViewStateSupported = false;

		if (isStrictParameterNamespacingSupported(portalContext)) {

			Map<String, Object> applicationMap = externalContext.getApplicationMap();
			namespacedViewStateSupported = (Boolean) applicationMap.get(FacesRuntimeUtil.class.getName());

			if (namespacedViewStateSupported == null) {

				ProductFactory productFactory = (ProductFactory) FactoryExtensionFinder.getFactory(externalContext,
						ProductFactory.class);
				namespacedViewStateSupported = isNamespacedViewStateSupported(productFactory);
			}
		}

		return namespacedViewStateSupported;
	}

	public static boolean isNamespaceViewState(boolean strictParameterNamespacingSupported,
		PortletContext portletContext) {

		Boolean namespacedViewStateSupported = false;

		if (strictParameterNamespacingSupported) {

			namespacedViewStateSupported = (Boolean) portletContext.getAttribute(FacesRuntimeUtil.class.getName());

			if (namespacedViewStateSupported == null) {

				ProductFactory productFactory = (ProductFactory) BridgeFactoryFinder.getFactory(portletContext,
						ProductFactory.class);
				namespacedViewStateSupported = isNamespacedViewStateSupported(productFactory);
			}
		}

		return namespacedViewStateSupported;
	}

	public static boolean isStrictParameterNamespacingSupported(PortalContext portalContext) {

		String strictNamespacedParametersSupport = portalContext.getProperty(
				BridgePortalContext.STRICT_NAMESPACED_PARAMETERS_SUPPORT);

		return (strictNamespacedParametersSupport != null);
	}

	private static boolean isNamespacedViewStateSupported(ProductFactory productFactory) {

		boolean namespacedViewStateSupported = false;
		final Product MOJARRA = productFactory.getProductInfo(Product.Name.MOJARRA);
		final Product JSF = productFactory.getProductInfo(Product.Name.JSF);
		int jsfMajorVersion = JSF.getMajorVersion();

		if (MOJARRA.isDetected()) {

			int mojarraMajorVersion = MOJARRA.getMajorVersion();

			if (mojarraMajorVersion == 2) {

				int mojarraMinorVersion = MOJARRA.getMinorVersion();

				if (mojarraMinorVersion == 1) {
					namespacedViewStateSupported = (MOJARRA.getPatchVersion() >= 27);
				}
				else if (mojarraMinorVersion == 2) {
					namespacedViewStateSupported = (MOJARRA.getPatchVersion() >= 4);
				}
				else if (mojarraMinorVersion > 2) {
					namespacedViewStateSupported = true;
				}
			}
			else if (mojarraMajorVersion > 2) {
				namespacedViewStateSupported = true;
			}
		}
		else if ((jsfMajorVersion > 2) || ((jsfMajorVersion == 2) && (JSF.getMinorVersion() >= 3))) {
			namespacedViewStateSupported = true;
		}

		logger.debug("JSF runtime [{0}] version [{1}].[{2}].[{3}] supports namespacing [{4}]: [{5}]", JSF.getTitle(),
			jsfMajorVersion, JSF.getMinorVersion(), JSF.getPatchVersion(), ResponseStateManager.VIEW_STATE_PARAM,
			namespacedViewStateSupported);

		return namespacedViewStateSupported;
	}
}
