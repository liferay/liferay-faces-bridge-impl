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
package com.liferay.faces.bridge.context.map.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.ServletContext;

import com.liferay.faces.bridge.context.ContextMapFactory;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.bridge.model.internal.UploadedFileBridgeImpl;
import com.liferay.faces.bridge.scope.BridgeRequestScope;
import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.context.map.MultiPartFormData;
import com.liferay.faces.util.product.ProductConstants;
import com.liferay.faces.util.product.ProductMap;


/**
 * @author  Neil Griffin
 */
public class ContextMapFactoryImpl extends ContextMapFactory {

	// Private Constants
	private static final boolean ICEFACES_DETECTED = ProductMap.getInstance().get(ProductConstants.ICEFACES)
		.isDetected();
	private static final String MULTIPART_FORM_DATA_FQCN = MultiPartFormData.class.getName();

	@Override
	public Map<String, Object> getApplicationScopeMap(PortletContext portletContext, boolean preferPredestroy) {
		return new ApplicationScopeMap(portletContext, preferPredestroy);
	}

	protected FacesRequestParameterMap getFacesRequestParameterMap(PortletRequest portletRequest,
		String responseNamespace, PortletConfig portletConfig, BridgeRequestScope bridgeRequestScope,
		String defaultRenderKitId, String facesViewQueryString) {

		FacesRequestParameterMap facesRequestParameterMap = null;
		Map<String, String> facesViewParameterMap = getFacesViewParameterMap(facesViewQueryString);

		if (portletRequest instanceof ClientDataRequest) {

			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;
			String contentType = clientDataRequest.getContentType();

			// Note: ICEfaces ace:fileEntry relies on its own mechanism for handling file upload.
			if (!ICEFACES_DETECTED && (contentType != null) && contentType.toLowerCase().startsWith("multipart/")) {

				MultiPartFormData multiPartFormData = (MultiPartFormData) portletRequest.getAttribute(
						MULTIPART_FORM_DATA_FQCN);

				if (multiPartFormData == null) {
					facesRequestParameterMap = new FacesRequestParameterMapImpl(responseNamespace, bridgeRequestScope,
							facesViewParameterMap, defaultRenderKitId);

					MultiPartFormDataProcessor multiPartFormDataProcessor = new MultiPartFormDataProcessorImpl();
					Map<String, List<com.liferay.faces.util.model.UploadedFile>> uploadedFileMap =
						multiPartFormDataProcessor.process(clientDataRequest, portletConfig, facesRequestParameterMap);

					multiPartFormData = new MultiPartFormDataImpl(facesRequestParameterMap, uploadedFileMap);

					// Save the multipart/form-data in a request attribute so that it can be referenced later-on in the
					// JSF lifecycle by file upload component renderers.
					portletRequest.setAttribute(MULTIPART_FORM_DATA_FQCN, multiPartFormData);
				}
				else {
					facesRequestParameterMap = multiPartFormData.getFacesRequestParameterMap();
				}
			}
		}

		if (facesRequestParameterMap == null) {
			Map<String, String[]> parameterMap = portletRequest.getParameterMap();
			facesRequestParameterMap = new FacesRequestParameterMapImpl(parameterMap, responseNamespace,
					bridgeRequestScope, facesViewParameterMap, defaultRenderKitId);
		}

		return facesRequestParameterMap;
	}

	@Override
	public Map<String, String> getFacesViewParameterMap(String facesViewQueryString) {
		return new FacesViewParameterMap(facesViewQueryString);
	}

	@Override
	public Map<String, String> getInitParameterMap(PortletContext portletContext) {
		return Collections.unmodifiableMap(new InitParameterMap(portletContext));
	}

	@Override
	public Map<String, Object> getRequestCookieMap(PortletRequest portletRequest) {
		return new RequestCookieMap(portletRequest.getCookies());
	}

	@Override
	public Map<String, String> getRequestHeaderMap(PortletRequest portletRequest) {
		return new RequestHeaderMap(getRequestHeaderValuesMap(portletRequest));
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap(PortletRequest portletRequest) {
		return new RequestHeaderValuesMap(portletRequest);
	}

	@Override
	public Map<String, String> getRequestParameterMap(PortletRequest portletRequest, String responseNamespace,
		PortletConfig portletConfig, BridgeRequestScope bridgeRequestScope, String defaultRenderKitId,
		String facesViewQueryString) {

		FacesRequestParameterMap facesRequestParameterMap = getFacesRequestParameterMap(portletRequest,
				responseNamespace, portletConfig, bridgeRequestScope, defaultRenderKitId, facesViewQueryString);

		return new RequestParameterMap(facesRequestParameterMap);
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap(PortletRequest portletRequest, String responseNamespace,
		PortletConfig portletConfig, BridgeRequestScope bridgeRequestScope, String defaultRenderKitId,
		String facesViewQueryString) {

		FacesRequestParameterMap facesRequestParameterMap = getFacesRequestParameterMap(portletRequest,
				responseNamespace, portletConfig, bridgeRequestScope, defaultRenderKitId, facesViewQueryString);

		return new RequestParameterValuesMap(facesRequestParameterMap);
	}

	@Override
	public Map<String, Object> getRequestScopeMap(PortletContext portletContext, PortletRequest portletRequest,
		Set<String> removedAttributeNames, boolean preferPreDestroy) {
		return new RequestScopeMap(portletContext, portletRequest, removedAttributeNames, preferPreDestroy);
	}

	@Override
	public Map<String, Object> getServletContextAttributeMap(ServletContext servletContext) {
		return new ServletContextAttributeMap(servletContext);
	}

	@Override
	public Map<String, Object> getSessionScopeMap(PortletContext portletContext, PortletSession portletSession,
		int scope, boolean preferPreDestroy) {
		return new SessionScopeMap(portletContext, portletSession, scope, preferPreDestroy);
	}

	@Override
	public Map<String, List<UploadedFile>> getUploadedFileMap(PortletRequest portletRequest) {

		Map<String, List<UploadedFile>> bridgeUploadedFileMap = null;
		MultiPartFormData multiPartFormData = (MultiPartFormData) portletRequest.getAttribute(MULTIPART_FORM_DATA_FQCN);

		if (multiPartFormData != null) {
			Map<String, List<com.liferay.faces.util.model.UploadedFile>> uploadedFileMap =
				multiPartFormData.getUploadedFileMap();

			if (uploadedFileMap != null) {
				bridgeUploadedFileMap = new HashMap<String, List<UploadedFile>>(uploadedFileMap.size());

				Set<Map.Entry<String, List<com.liferay.faces.util.model.UploadedFile>>> entrySet =
					uploadedFileMap.entrySet();

				for (Map.Entry<String, List<com.liferay.faces.util.model.UploadedFile>> mapEntry : entrySet) {
					List<com.liferay.faces.util.model.UploadedFile> uploadedFileList = mapEntry.getValue();

					if (uploadedFileList != null) {
						List<UploadedFile> bridgeUploadedFileList = new ArrayList<UploadedFile>(
								uploadedFileList.size());

						for (com.liferay.faces.util.model.UploadedFile uploadedFile : uploadedFileList) {
							bridgeUploadedFileList.add(new UploadedFileBridgeImpl(uploadedFile));
						}

						bridgeUploadedFileMap.put(mapEntry.getKey(), bridgeUploadedFileList);
					}
				}

			}
		}

		return bridgeUploadedFileMap;
	}

	@Override
	public ContextMapFactoryImpl getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
