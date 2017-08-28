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
package com.liferay.faces.bridge.bean.internal;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class PreDestroyInvokerFactoryImpl extends PreDestroyInvokerFactory {

	@Override
	public PreDestroyInvoker getPreDestroyInvoker(ServletContext servletContext) {

		final boolean MOJARRA_DETECTED = ProductFactory.getProduct(Product.Name.MOJARRA).isDetected();

		if (MOJARRA_DETECTED) {
			return new PreDestroyInvokerMojarraImpl(servletContext);
		}
		else {
			return new PreDestroyInvokerImpl();
		}
	}

	@Override
	public PreDestroyInvoker getPreDestroyInvoker(PortletContext portletContext) {

		final boolean MOJARRA_DETECTED = ProductFactory.getProduct(Product.Name.MOJARRA).isDetected();

		if (MOJARRA_DETECTED) {
			return new PreDestroyInvokerMojarraImpl(portletContext);
		}
		else {
			return new PreDestroyInvokerImpl();
		}
	}

	@Override
	public PreDestroyInvokerFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
