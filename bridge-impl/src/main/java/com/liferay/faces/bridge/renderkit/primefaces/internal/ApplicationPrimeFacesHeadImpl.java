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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;

import com.liferay.faces.bridge.application.internal.ResourceHandlerOuterImpl;


/**
 * @author  Kyle Stiemann
 */
public class ApplicationPrimeFacesHeadImpl extends ApplicationWrapper {

	// Private Members
	private Application wrappedApplication;

	public ApplicationPrimeFacesHeadImpl(Application wrappedApplication) {
		this.wrappedApplication = wrappedApplication;
	}

	@Override
	public ResourceHandler getResourceHandler() {

		ResourceHandler resourceHandler = super.getResourceHandler();

		// Remove ResourceHandlerOuterImpl from the chain so that Resource.getRequestPath() will return only the
		// request path, not an encoded resourceURL.
		if (resourceHandler instanceof ResourceHandlerOuterImpl) {

			ResourceHandlerWrapper resourceHandlerWrapper = (ResourceHandlerOuterImpl) resourceHandler;
			resourceHandler = resourceHandlerWrapper.getWrapped();
		}

		return resourceHandler;
	}

	@Override
	public Application getWrapped() {
		return wrappedApplication;
	}
}
