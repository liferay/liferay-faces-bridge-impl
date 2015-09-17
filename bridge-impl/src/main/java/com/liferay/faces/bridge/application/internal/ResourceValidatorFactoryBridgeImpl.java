/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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

	// Java 1.6+ @Override
	public ResourceValidatorFactory getWrapped() {
		return wrappedResourceValidatorFactory;
	}
}
