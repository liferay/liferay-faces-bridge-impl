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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is here to help log error messages regarding missing resources.
 *
 * @author  "Neil Griffin"
 */
public class MissingResourceImpl extends Resource {

	// Public Constants
	public static final String RES_NOT_FOUND = "RES_NOT_FOUND";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(MissingResourceImpl.class);

	// Private Constants
	private static final String ERROR_MSG =
		"Resource handler=[{0}] was unable to create a resource for resourceName=[{1}] libraryName=[{2}] contentType=[{3}]";

	// Private Data Members
	private String contentType;
	ResourceHandler failedResourceHandler;
	private String libraryName;
	private String resourceName;

	public MissingResourceImpl(ResourceHandler failedResourceHandler, String resourceName) {
		this.failedResourceHandler = failedResourceHandler;
		this.resourceName = resourceName;
	}

	public MissingResourceImpl(ResourceHandler failedResourceHandler, String resourceName, String libraryName) {
		this.failedResourceHandler = failedResourceHandler;
		this.resourceName = resourceName;
		this.libraryName = libraryName;
	}

	public MissingResourceImpl(ResourceHandler failedResourceHandler, String resourceName, String libraryName,
		String contentType) {
		this.failedResourceHandler = failedResourceHandler;
		this.resourceName = resourceName;
		this.libraryName = libraryName;
		this.contentType = contentType;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		logger.error(ERROR_MSG, failedResourceHandler, resourceName, libraryName, contentType);

		return new ByteArrayInputStream(new byte[] {});
	}

	@Override
	public String getLibraryName() {
		return libraryName;
	}

	@Override
	public String getRequestPath() {
		logger.error(ERROR_MSG, failedResourceHandler, resourceName, libraryName, contentType);

		return RES_NOT_FOUND;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public Map<String, String> getResponseHeaders() {
		logger.error(ERROR_MSG, failedResourceHandler, resourceName, libraryName, contentType);

		return new HashMap<String, String>();
	}

	@Override
	public URL getURL() {
		logger.error(ERROR_MSG, failedResourceHandler, resourceName, libraryName, contentType);

		return null;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	@Override
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Override
	public String toString() {
		return RES_NOT_FOUND;
	}

	@Override
	public boolean userAgentNeedsUpdate(FacesContext context) {
		logger.error(ERROR_MSG, failedResourceHandler, resourceName, libraryName, contentType);

		return false;
	}

}
