/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.bridge.application.internal;

import com.liferay.faces.util.application.ResourceValidator;
import com.liferay.faces.util.application.ResourceValidatorFactory;
import com.liferay.faces.util.product.ProductConstants;
import com.liferay.faces.util.product.ProductMap;


/**
 * @author  Neil Griffin
 */
public class ResourceValidatorFactoryBridgeImpl extends ResourceValidatorFactory {

	// Private Constants
	private static final boolean PLUTO_DETECTED = ProductMap.getInstance().get(ProductConstants.PLUTO).isDetected();

	// Private Data Members
	private ResourceValidatorFactory wrappedResourceValidatorFactory;

	public ResourceValidatorFactoryBridgeImpl(ResourceValidatorFactory resourceValidatorFactory) {
		this.wrappedResourceValidatorFactory = resourceValidatorFactory;
	}

	@Override
	public ResourceValidator getResourceValidator() {

		ResourceValidator wrappedResourceValidator = wrappedResourceValidatorFactory.getResourceValidator();

		if (PLUTO_DETECTED) {
			return new ResourceValidatorPlutoImpl(wrappedResourceValidator);
		}
		else {
			return wrappedResourceValidator;
		}
	}

	@Override
	public ResourceValidatorFactory getWrapped() {
		return wrappedResourceValidatorFactory;
	}
}
