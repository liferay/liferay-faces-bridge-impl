/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_6;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ final class BridgeServiceUtil {

	// Private Constants
	private static final boolean OSGI_ENVIRONMENT_DETECTED;

	static {

		boolean frameworkUtilDetected = false;

		try {

			Class.forName("org.osgi.framework.FrameworkUtil");
			frameworkUtilDetected = true;
		}
		catch (ClassNotFoundException e) {
			// Do nothing.
		}
		catch (NoClassDefFoundError e) {
			// Do nothing.
		}
		catch (Throwable t) {

			System.err.println("An unexpected error occurred when attempting to detect OSGi:");
			t.printStackTrace(System.err);
		}

		boolean osgiEnvironmentDetected = false;

		if (frameworkUtilDetected) {

			Bundle currentBundle = FrameworkUtil.getBundle(BridgeServiceUtil.class);

			if (currentBundle != null) {
				osgiEnvironmentDetected = true;
			}
		}

		OSGI_ENVIRONMENT_DETECTED = osgiEnvironmentDetected;
	}

	private BridgeServiceUtil() {
		throw new AssertionError();
	}

	/* package-private */ static ClassLoader getBridgeServiceClassLoader(ClassLoader defaultClassLoader) {

		ClassLoader classLoader = defaultClassLoader;

		if (OSGI_ENVIRONMENT_DETECTED) {

			Bundle bundle = FrameworkUtil.getBundle(BridgeServiceUtil.class);
			BundleWiring tckBundleWiring = bundle.adapt(BundleWiring.class);

			if (tckBundleWiring != null) {

				List<BundleWire> providedWires = tckBundleWiring.getRequiredWires(null);

				if (providedWires != null) {

					for (BundleWire providedWire : providedWires) {

						BundleCapability capability = providedWire.getCapability();

						if ((capability == null) || !"osgi.serviceloader".equals(capability.getNamespace())) {
							continue;
						}

						BundleRevision provider = providedWire.getProvider();

						if (provider == null) {
							continue;
						}

						BundleWiring bundleWiring = provider.getWiring();

						if (bundleWiring == null) {
							continue;
						}

						classLoader = bundleWiring.getClassLoader();

						break;
					}
				}
			}
		}

		return classLoader;
	}
}
