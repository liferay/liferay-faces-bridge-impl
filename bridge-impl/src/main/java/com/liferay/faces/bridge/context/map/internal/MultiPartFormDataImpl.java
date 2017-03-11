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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.context.map.MultiPartFormData;
import com.liferay.faces.util.model.UploadedFile;


/**
 * @author  Neil Griffin
 */
public class MultiPartFormDataImpl implements MultiPartFormData, Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 3882809202740249227L;

	// Private Data Members
	private FacesRequestParameterMap facesRequestParameterMap;
	private Map<String, List<UploadedFile>> uploadedFileMap;

	public MultiPartFormDataImpl(FacesRequestParameterMap facesRequestParameterMap,
		Map<String, List<UploadedFile>> uploadedFileMap) {
		this.facesRequestParameterMap = facesRequestParameterMap;
		this.uploadedFileMap = uploadedFileMap;
	}

	@Override
	public FacesRequestParameterMap getFacesRequestParameterMap() {
		return facesRequestParameterMap;
	}

	@Override
	public Map<String, List<UploadedFile>> getUploadedFileMap() {
		return uploadedFileMap;
	}
}
