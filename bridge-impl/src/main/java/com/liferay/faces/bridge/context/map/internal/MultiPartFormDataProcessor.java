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

import java.util.List;
import java.util.Map;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletConfig;

import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.model.UploadedFile;


/**
 * @author  Neil Griffin
 */
public interface MultiPartFormDataProcessor {

	/**
	 * Processes the specified client data request by populating the specified namespaced parameter map and returning a
	 * map of uploaded files. This method pre-supposes that the user agent submitted an HTML form with
	 * enctype="multipart/form-data".
	 *
	 * @param   clientDataRequest         The client data request that is to be processed.
	 * @param   portletConfig             The portlet configuration.
	 * @param   facesRequestParameterMap  The mutable namespaced paramter map that is to be populated.
	 *
	 * @return  The map of uploaded files.
	 */
	public Map<String, List<UploadedFile>> process(ClientDataRequest clientDataRequest, PortletConfig portletConfig,
		FacesRequestParameterMap facesRequestParameterMap);
}
