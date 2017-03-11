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
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


/**
 * @author  Neil Griffin
 */
public class ResourceRichFacesImpl extends ResourceWrapper {

	// Public Constants
	public static final String ORG_RICHFACES = "org.richfaces";

	// Private Constants
	private static final String RICHFACES_PATH_TOKEN = "/rfRes/";

	// Private Data Members
	private Resource wrappedResource;

	public ResourceRichFacesImpl(Resource resource) {
		this.wrappedResource = resource;
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

		String requestPath = super.getRequestPath();

		if (requestPath != null) {

			// If the /rfRes/ token is found in the request path, then RichFaces has likely added a dynamic resource.
			// Such resources have not had the request path processed by ExternalContext.encodeResourceURL(String) and
			// are therefore incompatible with a portlet environment.
			int pos = requestPath.indexOf(RICHFACES_PATH_TOKEN);

			if (pos > 0) {

				// Some resources like fileUploadProgress will have an extension like ".xhtml" appended to them which
				// must be removed.
				requestPath = requestPath.replaceAll("[.]faces", "");
				requestPath = requestPath.replaceAll("[.]jsf", "");
				requestPath = requestPath.replaceAll("[.]xhtml", "");

				// Encode the request path as a portlet ResourceURL.
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				StringBuilder buf = new StringBuilder();
				buf.append("/javax.faces.resource/");
				buf.append(requestPath.substring(pos + RICHFACES_PATH_TOKEN.length()));
				requestPath = externalContext.encodeResourceURL(buf.toString());
			}
		}

		return requestPath;
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
