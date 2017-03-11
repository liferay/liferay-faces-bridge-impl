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

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;


/**
 * This class serves as a fix for FACES-2133 such that the {@link #getRequestPathInfo()} and {@link
 * #getRequestServletPath()} methods work around an implicit servlet dependency in the a4j:mediaOutput component.
 *
 * @author  Kyle Stiemann
 */
public class ExternalContextRichFacesResourceImpl extends ExternalContextWrapper {

	// Private Data Members
	private ExternalContext wrappedExternalContext;

	public ExternalContextRichFacesResourceImpl(ExternalContext wrappedExternalContext) {
		this.wrappedExternalContext = wrappedExternalContext;
	}

	/**
	 * When this method returns null, {@link org.richfaces.resource.ResourceUtils#getMappingForRequest()} assumes that
	 * the Faces Servlet URL mapping is extension based and strips the extension off the value returned by {@link
	 * ExternalContextRichFacesResourceImpl#getRequestServletPath()}. This method always returns null to avoid adding code
	 * in this class to detect the actual Faces Servlet URL mapping since RichFaces never uses it.
	 */
	@Override
	public String getRequestPathInfo() {
		return null;
	}

	/**
	 * This method returns "/rfRes/org.richfaces.resource.MediaOutputResource.faces" so that {@link
	 * org.richfaces.resource.ResourceUtils#decodeResourceURL()} will return
	 * "/rfRes/org.richfaces.resource.MediaOutputResource" to {@link
	 * org.richfaces.resource.ResourceHandlerImpl#getResourcePathFromRequest()} which will cause {@link
	 * org.richfaces.resource.ResourceHandlerImpl} to handle the resource in this request as a {@link
	 * org.richfaces.resource.MediaOutputResource}.
	 */
	@Override
	public String getRequestServletPath() {
		return "/rfRes/org.richfaces.resource.MediaOutputResource.faces";
	}

	@Override
	public ExternalContext getWrapped() {
		return wrappedExternalContext;
	}
}
