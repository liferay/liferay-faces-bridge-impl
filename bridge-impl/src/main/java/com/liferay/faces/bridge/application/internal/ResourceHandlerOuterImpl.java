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

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;


/**
 * Unlike the {@link ResourceHandlerInnerImpl} class, this class is designed to be the outermost {@link ResourceHandler}
 * in the chain-of-responsibility. In order to achieve this outermost status, this class does not appear in the bridge's
 * faces-config.xml descriptor. Rather, it is created by the {@link ApplicationImpl#getResourceHandler()} method. If
 * necessary, it wraps resources with {@link ResourceHandlerWrapper} instances that override the {@link
 * Resource#getRequestPath()} method. This provides the bridge with the opportunity to have the final authority
 * regarding the format of resource URLs created by ResourceHandlers like the ones provided by ICEfaces, PrimeFaces, and
 * RichFaces.
 *
 * @author  Neil Griffin
 */
public class ResourceHandlerOuterImpl extends ResourceHandlerWrapper {

	// Private Constants
	private static final String ORG_RICHFACES_RESOURCE = "org.richfaces.resource";

	// Private Data Members
	private ResourceHandler wrappedResourceHandler;

	public ResourceHandlerOuterImpl(ResourceHandler resourceHandler) {
		this.wrappedResourceHandler = resourceHandler;
	}

	@Override
	public Resource createResource(String resourceName) {

		Resource resource = super.createResource(resourceName);

		if ((resource != null) && resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
			resource = new ResourceRichFacesImpl(resource);
		}

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {

		Resource resource = super.createResource(resourceName, libraryName);

		if ((resource != null) && resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
			resource = new ResourceRichFacesImpl(resource);
		}

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {

		Resource resource = super.createResource(resourceName, libraryName, contentType);

		if ((resource != null) && resource.getClass().getName().startsWith(ORG_RICHFACES_RESOURCE)) {
			resource = new ResourceRichFacesImpl(resource);
		}

		return resource;
	}

	@Override
	public ResourceHandler getWrapped() {
		return wrappedResourceHandler;
	}

}
