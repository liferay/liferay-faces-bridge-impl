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

import java.io.Serializable;

import javax.faces.application.Resource;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.FacesContext;


/**
 * This class decorates the resource implementation from the JSF implementation.
 *
 * @author  Neil Griffin
 */
public class ResourceOuterImpl extends ResourceWrapper implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 3068138459349365358L;

	// Private Data Members
	private Resource wrappedResource;

	/**
	 * This constructor is used by Mojarra via reflection during state saving.
	 */
	public ResourceOuterImpl() {
	}

	public ResourceOuterImpl(Resource wrappedResource) {
		this.wrappedResource = wrappedResource;
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public String getContentType() {
		return wrappedResource.getContentType();
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public String getLibraryName() {
		return wrappedResource.getLibraryName();
	}

	@Override
	public String getRequestPath() {

		// Get the requestPath value from the wrapped resource.
		String wrappedRequestPath = wrappedResource.getRequestPath();
		FacesContext facesContext = FacesContext.getCurrentInstance();

		return facesContext.getExternalContext().encodeResourceURL(wrappedRequestPath);
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public String getResourceName() {
		return wrappedResource.getResourceName();
	}

	@Override
	public Resource getWrapped() {
		return wrappedResource;
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public void setContentType(String contentType) {
		wrappedResource.setContentType(contentType);
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public void setLibraryName(String libraryName) {
		wrappedResource.setLibraryName(libraryName);
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public void setResourceName(String resourceName) {
		wrappedResource.setResourceName(resourceName);
	}

	/**
	 * Since this method is not supplied by the {@link ResourceWrapper} class it has to be implemented here.
	 */
	@Override
	public String toString() {
		return wrappedResource.toString();
	}
}
