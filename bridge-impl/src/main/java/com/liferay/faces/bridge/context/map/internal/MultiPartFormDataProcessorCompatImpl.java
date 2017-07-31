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
package com.liferay.faces.bridge.context.map.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletParameters;
import javax.portlet.ResourceRequest;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.servlet.http.Part;

import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.model.UploadedFile;
import com.liferay.faces.util.model.UploadedFileFactory;


/**
 * @author  Neil Griffin
 */
public abstract class MultiPartFormDataProcessorCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(MultiPartFormDataProcessorCompatImpl.class);

	protected abstract void addUploadedFile(Map<String, List<UploadedFile>> uploadedFileMap, String fieldName,
		UploadedFile uploadedFile);

	protected abstract String stripIllegalCharacters(String fileName);

	/* package-private */ Map<String, List<UploadedFile>> iterateOver(ClientDataRequest clientDataRequest, PortletConfig portletConfig,
		FacesRequestParameterMap facesRequestParameterMap, File uploadedFilesPath) {

		// Parse the request parameters and save all uploaded files in a map.
		Map<String, List<UploadedFile>> uploadedFileMap = new HashMap<>();

		// Determine the max file upload size threshold (in bytes).
		long defaultMaxFileSize = PortletConfigParam.UploadedFileMaxSize.getDefaultLongValue();
		long uploadedFileMaxSize = PortletConfigParam.UploadedFileMaxSize.getLongValue(portletConfig);

		if (defaultMaxFileSize != uploadedFileMaxSize) {
			logger.warn("Ignoring init param {0}=[{1}] since it has been replaced by <multipart-config> in web.xml",
				PortletConfigParam.UploadedFileMaxSize.getName(), uploadedFileMaxSize);
		}

		// FACES-271: Include name+value pairs found in the ActionRequest/ResourceRequest.
		PortletParameters portletParameters;

		boolean actionPhase;

		if (clientDataRequest instanceof ActionRequest) {
			actionPhase = true;

			ActionRequest actionRequest = (ActionRequest) clientDataRequest;
			portletParameters = actionRequest.getActionParameters();
		}
		else {
			actionPhase = false;

			ResourceRequest resourceRequest = (ResourceRequest) clientDataRequest;
			portletParameters = resourceRequest.getResourceParameters();
		}

		Set<String> portletParameterNames = portletParameters.getNames();

		for (String parameterName : portletParameterNames) {

			String[] parameterValues = portletParameters.getValues(parameterName);

			if (parameterValues.length > 0) {

				for (String parameterValue : parameterValues) {
					facesRequestParameterMap.addValue(parameterName, parameterValue);

					if (actionPhase) {
						logger.debug("Added action parameter name={0} value={1}", parameterName, parameterValue);
					}
					else {
						logger.debug("Added resource parameter name={0} value={1}", parameterName, parameterValue);
					}
				}
			}
		}

		UploadedFileFactory uploadedFileFactory = (UploadedFileFactory) BridgeFactoryFinder.getFactory(
				portletConfig.getPortletContext(), UploadedFileFactory.class);

		// Begin parsing the request for file parts:
		try {
			Collection<Part> parts = clientDataRequest.getParts();

			List<String> fileUploadFieldNames = new ArrayList<String>();
			int totalFiles = 0;

			// For each field found in the request:
			for (Part part : parts) {

				String fieldName = part.getName();
				fileUploadFieldNames.add(fieldName);

				try {
					totalFiles++;

					String characterEncoding = clientDataRequest.getCharacterEncoding();
					String contentDispositionHeader = part.getHeader("content-disposition");
					String fileName = getValidFileName(contentDispositionHeader);

					// If the current field is a simple form-field, then save the form field value in the map.
					if ((fileName != null) && (fileName.length() > 0)) {

						File uploadedFilePath = new File(uploadedFilesPath, fileName);
						String uploadedFilePathAbsolutePath = uploadedFilePath.getAbsolutePath();
						part.write(uploadedFilePathAbsolutePath);

						// If the copy was successful, then
						if (uploadedFilePath.exists()) {

							// If present, build up a map of headers. According to Hypertext Transfer Protocol --
							// HTTP/1.1 (http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2), header names
							// are case-insensitive. In order to support this, use a TreeMap with case insensitive
							// keys.
							Map<String, List<String>> headersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

							Collection<String> headerNames = part.getHeaderNames();

							for (String headerName : headerNames) {
								Collection<String> headerValues = part.getHeaders(headerName);
								List<String> headerValueList = new ArrayList<>();

								for (String headerValue : headerValues) {
									headerValueList.add(headerValue);
								}

								headersMap.put(headerName, headerValueList);
							}

							// Put a valid UploadedFile instance into the map that contains all of the
							// uploaded file's attributes, along with a successful status.
							Map<String, Object> attributeMap = new HashMap<>();
							String id = Long.toString(((long) hashCode()) + System.currentTimeMillis());
							com.liferay.faces.util.model.UploadedFile uploadedFile =
								uploadedFileFactory.getUploadedFile(uploadedFilePathAbsolutePath, attributeMap,
									characterEncoding, part.getContentType(), headersMap, id, null, fileName,
									part.getSize(), com.liferay.faces.util.model.UploadedFile.Status.FILE_SAVED);

							facesRequestParameterMap.addValue(fieldName, uploadedFilePathAbsolutePath);
							addUploadedFile(uploadedFileMap, fieldName, uploadedFile);
							logger.debug("Received uploaded file fieldName=[{0}] fileName=[{1}]", fieldName, fileName);
						}
						else {

							if (fileName.trim().length() > 0) {
								Exception e = new IOException("Failed to copy the stream of uploaded file=[" +
										fileName + "] to a temporary file (possibly a zero-length uploaded file)");
								com.liferay.faces.util.model.UploadedFile uploadedFile =
									uploadedFileFactory.getUploadedFile(e);
								addUploadedFile(uploadedFileMap, fieldName, uploadedFile);
							}
						}
					}
				}
				catch (Exception e) {
					logger.error(e);

					com.liferay.faces.util.model.UploadedFile uploadedFile = uploadedFileFactory.getUploadedFile(e);
					String totalFilesfieldName = Integer.toString(totalFiles);
					addUploadedFile(uploadedFileMap, totalFilesfieldName, uploadedFile);
				}
			}

			for (String fileUploadFieldName : fileUploadFieldNames) {

				// Ensure that fields submitted without a file are present in the uploadedFileMap so that
				// HtmlInputFileRenderer.decode() can determine whether or not the field was submitted with an empty
				// value.
				if (!uploadedFileMap.containsKey(fileUploadFieldName)) {
					uploadedFileMap.put(fileUploadFieldName, Collections.<UploadedFile>emptyList());
				}
			}
		}

		// If there was an error in parsing the request for file parts, then put a bogus UploadedFile instance in
		// the map so that the developer can have some idea that something went wrong.
		catch (Exception e) {
			logger.error(e);

			com.liferay.faces.util.model.UploadedFile uploadedFile = uploadedFileFactory.getUploadedFile(e);
			addUploadedFile(uploadedFileMap, "unknown", uploadedFile);

		}

		return uploadedFileMap;
	}

	private String getValidFileName(String contentDispositionHeader) {

		String[] headerParts = contentDispositionHeader.split(";");

		for (String headerPart : headerParts) {

			String trimmedHeaderPart = headerPart.trim();

			if (trimmedHeaderPart.startsWith("filename")) {

				int equalsPos = trimmedHeaderPart.indexOf("=");

				if (equalsPos > 0) {

					String fileName = trimmedHeaderPart.substring(equalsPos + 1).trim();

					if (fileName.startsWith("\"")) {
						fileName = fileName.substring(1);
					}

					if (fileName.endsWith("\"")) {
						fileName = fileName.substring(0, fileName.length() - 1);
					}

					return stripIllegalCharacters(fileName);
				}
			}
		}

		return null;
	}
}
