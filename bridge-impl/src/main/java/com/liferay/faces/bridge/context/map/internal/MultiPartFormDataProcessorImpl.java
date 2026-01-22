/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletSession;

import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.model.UploadedFile;


/**
 * @author  Neil Griffin
 */
public class MultiPartFormDataProcessorImpl extends MultiPartFormDataProcessorCompatImpl
	implements MultiPartFormDataProcessor {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(MultiPartFormDataProcessorImpl.class);

	@Override
	public Map<String, List<UploadedFile>> process(ClientDataRequest clientDataRequest, PortletConfig portletConfig,
		FacesRequestParameterMap facesRequestParameterMap, long maxFileSize) {

		PortletSession portletSession = clientDataRequest.getPortletSession();

		String uploadedFilesDir = PortletConfigParam.UploadedFilesDir.getStringValue(portletConfig);

		// Using the portlet sessionId, determine a unique folder path and create the path if it does not exist.
		String sessionId = portletSession.getId();

		// FACES-1452: Non-alpha-numeric characters must be removed order to ensure that the folder will be
		// created properly.
		sessionId = sessionId.replaceAll("[^A-Za-z0-9]", "");

		File uploadedFilesPath = new File(uploadedFilesDir, sessionId);

		if (!uploadedFilesPath.exists()) {

			if (!uploadedFilesPath.mkdirs()) {
				logger.warn("Unable to create directory for uploadedFilesPath=[{0}]", uploadedFilesPath);
			}
		}

		return iterateOver(clientDataRequest, portletConfig, facesRequestParameterMap, uploadedFilesPath, maxFileSize);
	}

	protected void addUploadedFile(Map<String, List<UploadedFile>> uploadedFileMap, String fieldName,
		UploadedFile uploadedFile) {
		List<UploadedFile> uploadedFiles = uploadedFileMap.get(fieldName);

		if (uploadedFiles == null) {
			uploadedFiles = new ArrayList<UploadedFile>();
			uploadedFileMap.put(fieldName, uploadedFiles);
		}

		uploadedFiles.add(uploadedFile);
	}

	protected String stripIllegalCharacters(String fileName) {

		// FACES-64: Need to strip out invalid characters.
		// http://technet.microsoft.com/en-us/library/cc956689.aspx
		String strippedFileName = fileName;

		if (fileName != null) {

			int pos = fileName.lastIndexOf(".");
			strippedFileName = fileName.replaceAll("[\\\\/\\[\\]:|<>+;=.?\"]", "-");

			if (pos > 0) {
				strippedFileName = strippedFileName.substring(0, pos) + "." + strippedFileName.substring(pos + 1);
			}
		}

		return strippedFileName;
	}

}
