/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class ExceptionHandlerFactoryBridgeImpl extends ExceptionHandlerFactory {

	// Private Data Members
	private ExceptionHandlerFactory wrappedExceptionHandlerFactory;

	public ExceptionHandlerFactoryBridgeImpl(ExceptionHandlerFactory exceptionHandlerFactory) {
		this.wrappedExceptionHandlerFactory = exceptionHandlerFactory;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {

		ExceptionHandler wrappedExceptionHandler = wrappedExceptionHandlerFactory.getExceptionHandler();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Product ICEFACES = ProductFactory.getProductInstance(externalContext, Product.Name.ICEFACES);
		final int MAJOR_VERSION = ICEFACES.getMajorVersion();

		if (ICEFACES.isDetected() &&
				(((MAJOR_VERSION == 4) && (ICEFACES.getMinorVersion() < 2)) || (MAJOR_VERSION < 4))) {

			// Workaround for https://issues.liferay.com/browse/FACES-3012
			facesContext.getPartialViewContext();
		}

		return new ExceptionHandlerBridgeImpl(wrappedExceptionHandler);
	}

	@Override
	public ExceptionHandlerFactory getWrapped() {
		return wrappedExceptionHandlerFactory;
	}
}
