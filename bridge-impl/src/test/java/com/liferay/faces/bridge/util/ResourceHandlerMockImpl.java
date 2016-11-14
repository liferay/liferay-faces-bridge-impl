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
package com.liferay.faces.bridge.util;

import java.io.IOException;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;


/**
 * This class is designed to be wrapped by the Bridge's {@link ResourceHandler} implementation in order to test the
 * {@link ResourceHandler#isResourceURL(java.lang.String)} method.
 *
 * @author  Kyle Stiemann
 */
public class ResourceHandlerMockImpl extends ResourceHandler {

	@Override
	public Resource createResource(String resourceName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource createResource(String resourceName, String libraryOrContractName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRendererTypeForResourceName(String resourceName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void handleResourceRequest(FacesContext context) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isResourceRequest(FacesContext context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean libraryExists(String libraryName) {
		throw new UnsupportedOperationException();
	}
}
