/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import com.liferay.faces.util.application.ResourceVerifier;
import com.liferay.faces.util.application.ResourceVerifierFactory;


/**
 * @author  Kyle Stiemann
 */
public class ResourceVerifierFactoryBridgeImpl extends ResourceVerifierFactory {

	// Private Members
	private ResourceVerifierFactory wrappedResourceVerifierFactory;

	public ResourceVerifierFactoryBridgeImpl(ResourceVerifierFactory wrappedResourceVerifierFactory) {
		this.wrappedResourceVerifierFactory = wrappedResourceVerifierFactory;
	}

	@Override
	public ResourceVerifier getResourceVerifier() {
		return new ResourceVerifierBridgeImpl(wrappedResourceVerifierFactory.getResourceVerifier());
	}

	@Override
	public ResourceVerifierFactory getWrapped() {
		return wrappedResourceVerifierFactory;
	}
}
