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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class ResourceHandlerBridgeImpl extends ResourceHandlerBridgeCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceHandlerBridgeImpl.class);

	// Private Data Members
	private Integer bufferSize;
	private ResourceHandler wrappedResourceHandler;

	public ResourceHandlerBridgeImpl(ResourceHandler wrappedResourceHandler) {
		this.wrappedResourceHandler = wrappedResourceHandler;
	}

	@Override
	public Resource createResource(String resourceName) {

		Resource wrappableResource = getWrapped().createResource(resourceName);

		if (wrappableResource == null) {
			return new MissingResourceImpl(getWrapped(), resourceName);
		}
		else {
			return new ResourceInnerImpl(wrappableResource);
		}
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {

		Resource wrappableResource = getWrapped().createResource(resourceName, libraryName);

		if (wrappableResource == null) {
			return new MissingResourceImpl(getWrapped(), resourceName, libraryName);
		}
		else {
			return new ResourceInnerImpl(wrappableResource);
		}
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {

		Resource wrappableResource = getWrapped().createResource(resourceName, libraryName, contentType);

		if (wrappableResource == null) {
			return new MissingResourceImpl(getWrapped(), resourceName, libraryName, contentType);
		}
		else {
			return new ResourceInnerImpl(wrappableResource);
		}
	}

	@Override
	public ResourceHandler getWrapped() {
		return wrappedResourceHandler;
	}

	/**
	 * This method handles the current request which is assumed to be a request for a {@link Resource}.
	 */
	@Override
	public void handleResourceRequest(FacesContext facesContext) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
		String resourceName = requestParameterMap.get("javax.faces.resource");

		// Assume that the resource  ExternalContext.encodeResourceURL(String) was properly called, and that
		// which adds the "javax.faces.resource" request parameter.
		// If the "javax.faces.resource" request parameter was found, then ask Faces to create the resource and
		// assume that calling resource.getInputStream() will provide the ability to send the contents of the
		// resource to the response.
		if (resourceName != null) {

			String libraryName = requestParameterMap.get("ln");

			if (logger.isTraceEnabled()) {

				// Surround with isTraceEnabled check in order to avoid unnecessary creation of object array.
				logger.trace("Handling - resourceName=[{0}], libraryName[{1}]", resourceName, libraryName);
			}

			// FACES-57: Provide the opportunity for applications to decorate the createResource methods of this
			// class by delegating creation of the resource to the chain-of-responsibility found in the application's
			// ResourceHandler.
			ResourceHandler resourceHandlerChain = facesContext.getApplication().getResourceHandler();
			Resource resource;

			if (libraryName == null) {
				resource = resourceHandlerChain.createResource(resourceName);
			}
			else {
				resource = resourceHandlerChain.createResource(resourceName, libraryName);
			}

			handleResource(facesContext, externalContext, resource);
		}
		else {

			logger.debug("NOT HANDLED - Missing request parameter {0} so delegating handleResourceRequest to chain",
				"javax.faces.resource");
			getWrapped().handleResourceRequest(facesContext);
		}
	}

	@Override
	public boolean isResourceRequest(FacesContext facesContext) {

		// If the "javax.faces.resource" request parameter is present, then that means the resource's URL was
		// properly created with the ExternalContext.encodeResourceURL(String) method.
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
		String resourceId = requestParameterMap.get("javax.faces.resource");

		if (resourceId != null) {
			logger.debug("Found {0} request parameter and recognized resourceId=[{1}] as a resource",
				"javax.faces.resource", resourceId);

			return true;
		}
		else {
			logger.debug("Did not find the {0} request parameter so delegating isResourceRequest to chain",
				"javax.faces.resource");

			return getWrapped().isResourceRequest(facesContext);
		}
	}

	private void handleResource(FacesContext facesContext, ExternalContext externalContext, Resource resource)
		throws IOException {

		boolean needsUpdate = resource.userAgentNeedsUpdate(facesContext);

		if (!isAbleToSetHttpStatusCode(facesContext)) {

			if (!needsUpdate) {
				needsUpdate = true;
				logger.debug(
					"Unable to set the status code to HttpServletResponse.SC_NOT_MODIFIED ({0}) for resourceName=[{1}]",
					HttpServletResponse.SC_NOT_MODIFIED, resource.getResourceName());
			}
		}

		if (needsUpdate) {

			logger.trace("Handling - Resource was either modified or has not yet been downloaded.");

			ReadableByteChannel readableByteChannel = null;
			WritableByteChannel writableByteChannel = null;
			InputStream inputStream = null;

			if (bufferSize == null) {

				PortletConfig portletConfig = RequestMapUtil.getPortletConfig(facesContext);
				bufferSize = PortletConfigParam.ResourceBufferSize.getIntegerValue(portletConfig);
			}

			ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);

			try {

				// Open an input stream in order to read the resource's contents/data.
				inputStream = resource.getInputStream();

				if (inputStream != null) {

					// Set the response headers by copying them from the resource.
					Map<String, String> responseHeaderMap = resource.getResponseHeaders();

					if (responseHeaderMap != null) {

						for (Map.Entry<String, String> mapEntry : responseHeaderMap.entrySet()) {
							String name = mapEntry.getKey();
							String value = mapEntry.getValue();
							externalContext.setResponseHeader(name, value);

							if (logger.isDebugEnabled()) {

								// Surround with isDebugEnabled check in order to avoid unnecessary creation
								// of object array.
								logger.debug("Handling - COPIED resource header name=[{0}] value=[{1}]", name, value);
							}
						}
					}

					// Set the response Content-Type header.
					String responseContentType = resource.getContentType();
					logger.trace("Handling - responseContentType=[{0}]", responseContentType);

					if (responseContentType != null) {
						externalContext.setResponseContentType(responseContentType);
					}

					// Rather than write the input stream directly to the response, write it to an
					// buffered output stream so that the length can be calculated for the
					// Content-Length header. See: http://issues.liferay.com/browse/FACES-1207
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufferSize);

					int responseContentLength = 0;
					readableByteChannel = Channels.newChannel(inputStream);
					writableByteChannel = Channels.newChannel(byteArrayOutputStream);

					int bytesRead = readableByteChannel.read(byteBuffer);

					if (logger.isTraceEnabled()) {

						// Surround with isTraceEnabled check in order to avoid unnecessary conversion of
						// int to String.
						logger.trace("Handling - bytesRead=[{0}]", Integer.toString(bytesRead));
					}

					int bytesWritten = 0;

					while (bytesRead != -1) {
						byteBuffer.rewind();
						byteBuffer.limit(bytesRead);

						do {
							bytesWritten += writableByteChannel.write(byteBuffer);
						}
						while (bytesWritten < responseContentLength);

						byteBuffer.clear();
						responseContentLength += bytesRead;
						bytesRead = readableByteChannel.read(byteBuffer);

						if (logger.isTraceEnabled()) {

							// Surround with isTraceEnabled check in order to avoid unnecessary conversion
							// of int to String.
							logger.trace("Handling - MORE bytesRead=[{0}]", Integer.toString(bytesRead));
						}
					}

					responseContentLength = byteArrayOutputStream.size();

					// Now that we know how big the file is, set the response Content-Length header and the status.
					externalContext.setResponseContentLength(responseContentLength);
					externalContext.setResponseStatus(HttpServletResponse.SC_OK);

					// Set the response buffer size.
					externalContext.setResponseBufferSize(responseContentLength);

					if (logger.isTraceEnabled()) {

						// Surround with isTraceEnabled check in order to avoid unnecessary conversion of
						// int to String.
						logger.trace("Handling - responseBufferSize=[{0}]", Integer.toString(responseContentLength));
					}

					// Write the data to the response.
					byteArrayOutputStream.writeTo(externalContext.getResponseOutputStream());
					byteArrayOutputStream.flush();
					byteArrayOutputStream.close();

					if (logger.isDebugEnabled()) {
						logger.debug(
							"HANDLED (SC_OK) resourceName=[{0}], libraryName[{1}], responseContentType=[{4}], responseContentLength=[{5}]",
							resource.getResourceName(), resource.getLibraryName(), responseContentType,
							responseContentLength);
					}
				}
				else {
					externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
					logger.error(
						"NOT HANDLED (SC_NOT_FOUND) because InputStream was null - resourceName=[{0}], libraryName[{1}]",
						resource.getResourceName(), resource.getLibraryName());
				}
			}
			catch (IOException e) {
				externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
				logger.error("NOT HANDLED (SC_NOT_FOUND) resourceName=[{0}], libraryName[{1}], errorMessage=[{4}]",
					new Object[] { resource.getResourceName(), resource.getLibraryName(), e.getMessage() }, e);
			}
			finally {

				if (writableByteChannel != null) {
					writableByteChannel.close();
				}

				if (readableByteChannel != null) {
					readableByteChannel.close();
				}

				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		else {

			externalContext.setResponseStatus(HttpServletResponse.SC_NOT_MODIFIED);

			if (logger.isDebugEnabled()) {

				// Surround with isDebugEnabled check in order to avoid unnecessary creation of object array.
				logger.debug("HANDLED (SC_NOT_MODIFIED) resourceName=[{0}], libraryName[{1}]",
					resource.getResourceName(), resource.getLibraryName());
			}

		}
	}

	private boolean isAbleToSetHttpStatusCode(FacesContext facesContext) {

		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortalContext portalContext = portletRequest.getPortalContext();
		String setHttpStatusCodeSupport = portalContext.getProperty(BridgePortalContext.SET_HTTP_STATUS_CODE_SUPPORT);

		return (setHttpStatusCodeSupport != null);
	}
}
