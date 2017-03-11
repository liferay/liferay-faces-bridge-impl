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

		Product portletApi = ProductFactory.getProduct(Product.Name.PORTLET_API);
		int portletApiMajorVersion = portletApi.getMajorVersion();
		int portletApiMinorVersion = portletApi.getMinorVersion();
		if (!((portletApiMajorVersion == 3) && (portletApiMinorVersion >= 0))) {

			logger.error("{0} {1} is designed to be used with Portlet 3.0 but detected {2}.{3}", implementationTitle,
				implementationVersion, portletApiMajorVersion, portletApiMinorVersion);
		}

		Product jsf = ProductFactory.getProduct(Product.Name.JSF);

		int jsfMajorVersion = jsf.getMajorVersion();
		int jsfMinorVersion = jsf.getMinorVersion();

		if (!((jsfMajorVersion == 2) && (jsfMinorVersion == 3))) {
			logger.error("{0} {1} is designed to be used with JSF 2.3 but detected {2}.{3}", implementationTitle,
				implementationVersion, jsfMajorVersion, jsfMinorVersion);
		}
	}
}
