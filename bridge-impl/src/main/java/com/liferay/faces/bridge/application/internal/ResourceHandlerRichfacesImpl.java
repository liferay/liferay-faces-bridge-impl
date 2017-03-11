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
package com.liferay.faces.bridge.application.internal;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;

import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * Unlike the {@link ResourceHandlerInnerImpl} class, this class is designed to wrap RichFaces {@link ResourceHandler}
 * in the chain-of-responsibility. In order to wrap RichFaces {@link ResourceHandler}, this class does not appear in the
 * bridge's faces-config.xml descriptor. Rather, it is created by the {@link ApplicationCompatImpl#getResourceHandler()}
 * method. If necessary, it wraps resources with {@link Resource} instances that override certain methods to ensure that
 * RichFaces resources work correctly in a Portlet environment.
 *
 * @author  Neil Griffin
 */
public class ResourceHandlerRichfacesImpl extends ResourceHandlerWrapper {

	// Private Constants
	private static final String ORG_RICHFACES_RESOURCE = "org.richfaces.resource";
	private static final boolean RICHFACES_DETECTED = ProductFactory.getProduct(Product.Name.RICHFACES).isDetected();
	private static final String RICHFACES_STATIC_RESOURCE = "org.richfaces.staticResource";

	// Private Data Members
	private ResourceHandler wrappedResourceHandler;

	public ResourceHandlerRichfacesImpl(ResourceHandler wrappedResourceHandler) {
		this.wrappedResourceHandler = wrappedResourceHandler;
	}

	@Override
	public Resource createResource(String resourceName) {

		Resource resource = super.createResource(resourceName);
		resource = createResource(resourceName, resource);

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {

		Resource resource = super.createResource(resourceName, libraryName);
		resource = createResource(resourceName, resource);

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {

		Resource resource = super.createResource(resourceName, libraryName, contentType);
		resource = createResource(resourceName, resource);

		return resource;
	}

	@Override
	public ResourceHandler getWrapped() {
		return wrappedResourceHandler;
	}

	private Resource createResource(String resourceName, Resource resource) {

		if ((resource != null) && RICHFACES_DETECTED) {

			// If this is a RichFaces static css resource, then return a filtered Richfaces static CSS resource.
			if (resourceName.startsWith(RICHFACES_STATIC_RESOURCE) && resourceName.endsWith(".css")) {
				resource = new ResourceRichFacesCSSImpl(resource);
			}

			// If this is a RichFaces static packed.js resource, then return a filtered Richfaces static packed.js
			// resource.
			else if (resourceName.startsWith(RICHFACES_STATIC_RESOURCE) && resourceName.endsWith("packed.js")) {
				resource = new ResourceRichFacesPackedJSImpl(resource);
			}
			else if (resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
				resource = new ResourceRichFacesImpl(resource);
			}
		}

		return resource;
	}
}
