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

import java.io.IOException;
import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.util.application.ResourceValidator;
import com.liferay.faces.util.application.ResourceValidatorFactory;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Unlike the {@link ResourceHandlerOuterImpl} class, this class is designed to be the innermost {@link ResourceHandler}
 * in the chain-of-responsibility (only the Mojarra/MyFaces ResourceHandlerImpl has a more inner status). In order to
 * achieve this innermost status, it is registered in the application section of the bridge's faces-config.xml
 * descriptor. It is responsible for wrapping resources created by Mojarra/MyFaces so that resource URLs will work in a
 * portlet environment. It is also responsible for serving up resources via the {@link
 * #handleResourceRequest(FacesContext)} method.
 *
 * @author  Neil Griffin
 */
public class ResourceHandlerInnerImpl extends ResourceHandlerBridgeImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceHandlerInnerImpl.class);

	public ResourceHandlerInnerImpl(ResourceHandler resourceHandler) {
		super(resourceHandler);
	}

	@Override
	public void handleResourceRequest(FacesContext facesContext) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();

		String resourceName = requestParameterMap.get("javax.faces.resource");

		if (resourceName == null) {
			resourceName = "";
		}

		String libraryName = requestParameterMap.get("ln");

		if (libraryName == null) {
			libraryName = "";
		}

		String resourceId = libraryName + "/" + resourceName;

		ResourceValidator resourceValidator = ResourceValidatorFactory.getResourceValidatorInstance();

		// If the resource name or library name contains a banned path like WEB-INF or META-INF, then do not serve
		// the resource.
		if (resourceValidator.containsBannedPath(resourceId)) {

			logger.warn("Invalid path for resourceId=[{0}]", resourceId);
			externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		// Otherwise, if the resource name or library name contains a banned sequence like double slashes, etc. then
		// do not serve the resource.
		else if (resourceValidator.isBannedSequence(resourceId)) {

			logger.warn("Invalid sequence for resourceId=[{0}]", resourceId);
			externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		// Otherwise, if the resource name or library name targets a Facelet document, then do not serve the
		// resource.
		else if (resourceValidator.isFaceletDocument(facesContext, resourceId)) {

			logger.warn("Invalid request for Facelet document resourceId=[{0}]", resourceId);
			externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		else if (!resourceValidator.isValidResourceName(resourceName)) {

			logger.warn("Invalid request due to invalid resourceName=[{0}]", resourceName);
			externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		else if (!resourceValidator.isValidLibraryName(libraryName)) {

			logger.warn("Invalid request due to invalid libraryName=[{0}]", libraryName);
			externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		else {
			super.handleResourceRequest(facesContext);
		}
	}
}
