/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.io.Serializable;

import com.liferay.faces.util.application.ResourceValidator;
import com.liferay.faces.util.application.ResourceValidatorFactory;


/**
 * @author  Neil Griffin
 */
public class ResourceValidatorFactoryBridgeImpl extends ResourceValidatorFactory implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 4615985625648751613L;

	// Private Data Members
	private ResourceValidator resourceValidator;
	private ResourceValidatorFactory wrappedResourceValidatorFactory;

	public ResourceValidatorFactoryBridgeImpl(ResourceValidatorFactory resourceValidatorFactory) {

		ResourceValidator wrappedResourceValidator = resourceValidatorFactory.getResourceValidator();
		this.resourceValidator = new ResourceValidatorBridgeImpl(wrappedResourceValidator);
		this.wrappedResourceValidatorFactory = resourceValidatorFactory;
	}

	@Override
	public ResourceValidator getResourceValidator() {
		return resourceValidator;
	}

	@Override
	public ResourceValidatorFactory getWrapped() {
		return wrappedResourceValidatorFactory;
	}
}
