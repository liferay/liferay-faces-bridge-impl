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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class ExceptionHandlerFactoryBridgeImpl extends ExceptionHandlerFactory {

	// Private Constants
	private static final Product ICEFACES = ProductFactory.getProduct(Product.Name.ICEFACES);
	private static final boolean ICEFACES_DETECTED = ICEFACES.isDetected();
	private static final int ICEFACES_MAJOR_VERSION = ICEFACES.getMajorVersion();
	private static final int ICEFACES_MINOR_VERSION = ICEFACES.getMinorVersion();

	// Private Data Members
	private ExceptionHandlerFactory wrappedExceptionHandlerFactory;

	public ExceptionHandlerFactoryBridgeImpl(ExceptionHandlerFactory exceptionHandlerFactory) {
		this.wrappedExceptionHandlerFactory = exceptionHandlerFactory;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {

		ExceptionHandler wrappedExceptionHandler = wrappedExceptionHandlerFactory.getExceptionHandler();

		if (ICEFACES_DETECTED &&
				(((ICEFACES_MAJOR_VERSION == 4) && (ICEFACES_MINOR_VERSION < 2)) || (ICEFACES_MAJOR_VERSION < 4))) {

			// Workaround for https://issues.liferay.com/browse/FACES-3012
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.getPartialViewContext();
		}

		return new ExceptionHandlerBridgeImpl(wrappedExceptionHandler);
	}

	@Override
	public ExceptionHandlerFactory getWrapped() {
		return wrappedExceptionHandlerFactory;
	}
}
