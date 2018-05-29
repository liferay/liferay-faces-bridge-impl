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
package com.liferay.faces.bridge.internal;

import javax.faces.context.ExternalContext;

import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public final class BridgeDependencyVerifier {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeDependencyVerifier.class);

	private BridgeDependencyVerifier() {
		throw new AssertionError();
	}

	public static void verify(ExternalContext externalContext) {

		Package bridgePackage = BridgeDependencyVerifier.class.getPackage();
		String implementationTitle = bridgePackage.getImplementationTitle();
		String implementationVersion = bridgePackage.getImplementationVersion();
		ProductFactory productFactory = (ProductFactory) FactoryExtensionFinder.getFactory(externalContext,
				ProductFactory.class);
		final Product LIFERAY_PORTAL = productFactory.getProductInfo(Product.Name.LIFERAY_PORTAL);

		if (LIFERAY_PORTAL.isDetected()) {

			final Product LIFERAY_FACES_BRIDGE_EXT = productFactory.getProductInfo(
					Product.Name.LIFERAY_FACES_BRIDGE_EXT);

			if (!LIFERAY_FACES_BRIDGE_EXT.isDetected()) {
				logger.error(
					"{0} {1} is running in Liferay Portal {2}.{3} but the com.liferay.faces.bridge.ext.jar dependency is not in the classpath",
					implementationTitle, implementationVersion, LIFERAY_PORTAL.getMajorVersion(),
					LIFERAY_PORTAL.getMinorVersion());
			}
		}

		final Product PORTLET_API = productFactory.getProductInfo(Product.Name.PORTLET_API);
		final int PORTLET_API_MAJOR_VERSION = PORTLET_API.getMajorVersion();
		final int PORTLET_API_MINOR_VERSION = PORTLET_API.getMinorVersion();

		if (!((PORTLET_API_MAJOR_VERSION > 3) ||
					((PORTLET_API_MAJOR_VERSION == 3) && (PORTLET_API_MINOR_VERSION >= 0)))) {
			logger.error("{0} {1} is designed to be used with Portlet 3.0+ but detected {2}.{3}", implementationTitle,
				implementationVersion, PORTLET_API_MAJOR_VERSION, PORTLET_API_MINOR_VERSION);
		}

		final Product JSF = productFactory.getProductInfo(Product.Name.JSF);
		final int JSF_MAJOR_VERSION = JSF.getMajorVersion();
		final int JSF_MINOR_VERSION = JSF.getMinorVersion();

		if (!((JSF_MAJOR_VERSION == 2) && (JSF_MINOR_VERSION == 3))) {
			logger.error("{0} {1} is designed to be used with JSF 2.3 but detected {2}.{3}", implementationTitle,
				implementationVersion, JSF_MAJOR_VERSION, JSF_MINOR_VERSION);
		}
	}
}
