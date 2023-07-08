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
package com.liferay.faces.bridge.internal;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeDependencyVerifier {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeDependencyVerifier.class);

	public static void verify() {

		Package bridgePackage = BridgeDependencyVerifier.class.getPackage();
		String implementationTitle = bridgePackage.getImplementationTitle();
		String implementationVersion = bridgePackage.getImplementationVersion();
		Product liferayPortal = ProductFactory.getProduct(Product.Name.LIFERAY_PORTAL);

		if (liferayPortal.isDetected()) {

			Product liferayFacesBridgeExt = ProductFactory.getProduct(Product.Name.LIFERAY_FACES_BRIDGE_EXT);

			if (!liferayFacesBridgeExt.isDetected()) {
				logger.error(
					"{0} {1} is running in Liferay Portal {2}.{3} but the com.liferay.faces.bridge.ext.jar dependency is not in the classpath",
					implementationTitle, implementationVersion, liferayPortal.getMajorVersion(),
					liferayPortal.getMinorVersion());
			}
		}

		final Product PORTLET_API = ProductFactory.getProduct(Product.Name.PORTLET_API);
		final int PORTLET_API_MAJOR_VERSION = PORTLET_API.getMajorVersion();
		final int PORTLET_API_MINOR_VERSION = PORTLET_API.getMinorVersion();

		if (!((PORTLET_API_MAJOR_VERSION > 2) ||
			((PORTLET_API_MAJOR_VERSION == 2) && (PORTLET_API_MINOR_VERSION >= 0)))) {
			logger.error("{0} {1} is designed to be used with Portlet 2.0+ but detected {2}.{3}", implementationTitle,
				implementationVersion, PORTLET_API_MAJOR_VERSION, PORTLET_API_MINOR_VERSION);
		}

		Product jsf = ProductFactory.getProduct(Product.Name.JSF);

		int jsfMajorVersion = jsf.getMajorVersion();
		int jsfMinorVersion = jsf.getMinorVersion();

		if (!((jsfMajorVersion == 1) && (jsfMinorVersion == 2))) {
			logger.error("{0} {1} is designed to be used with JSF 1.2 but detected {2}.{3}", implementationTitle,
				implementationVersion, jsfMajorVersion, jsfMinorVersion);
		}
	}
}
