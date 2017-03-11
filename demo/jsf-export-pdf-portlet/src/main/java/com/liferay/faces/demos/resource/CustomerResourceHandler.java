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
package com.liferay.faces.demos.resource;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;


/**
 * @author  Neil Griffin
 */
public class CustomerResourceHandler extends ResourceHandlerWrapper {

	// Public Constants
	public static final String LIBRARY_NAME = "customerResources";

	// Private Data Members
	private ResourceHandler wrappedResourceHandler;

	public CustomerResourceHandler(ResourceHandler resourceHandler) {
		this.wrappedResourceHandler = resourceHandler;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {

		if (LIBRARY_NAME.equals(libraryName)) {

			if (CustomerExportResource.RESOURCE_NAME.equals(resourceName)) {
				return new CustomerExportResource();
			}
			else {
				return super.createResource(resourceName, libraryName);
			}
		}
		else {
			return super.createResource(resourceName, libraryName);
		}
	}

	@Override
	public ResourceHandler getWrapped() {
		return wrappedResourceHandler;
	}

	@Override
	public boolean libraryExists(String libraryName) {

		if (LIBRARY_NAME.equals(libraryName)) {
			return true;
		}
		else {
			return super.libraryExists(libraryName);
		}
	}
}
