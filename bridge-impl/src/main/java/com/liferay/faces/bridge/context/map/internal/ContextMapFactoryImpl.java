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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.servlet.ServletContext;

import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.bridge.model.internal.UploadedFileBridgeImpl;
import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.bridge.util.internal.FacesRuntimeUtil;
import com.liferay.faces.util.context.map.FacesRequestParameterMap;
import com.liferay.faces.util.context.map.MultiPartFormData;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class ContextMapFactoryImpl extends ContextMapFactoryCompatImpl {

	// Private Constants
	private static final String MULTIPART_FORM_DATA_FQCN = MultiPartFormData.class.getName();

	@Override
	public Map<String, Object> getApplicationScopeMap(PortletContext portletContext, boolean preferPredestroy) {
		return new ApplicationScopeMap(portletContext, preferPredestroy);
	}

	@Override
	public Map<String, String> getFacesViewParameterMap(String facesViewQueryString) {
		return new FacesViewParameterMap(facesViewQueryString);
	}

	@Override
	public Map<String, String> getInitParameterMap(PortletConfig portletConfig) {
		return Collections.unmodifiableMap(new InitParameterMap(portletConfig));
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
		String responseNamespace, boolean preferPreDestroy) {
		return new RequestScopeMap(portletContext, portletRequest, preferPreDestroy);
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

		Map<String, List<UploadedFile>> bridgeUploadedFileMap = Collections.emptyMap();
		MultiPartFormData multiPartFormData = (MultiPartFormData) portletRequest.getAttribute(MULTIPART_FORM_DATA_FQCN);

		if (multiPartFormData != null) {
			Map<String, List<com.liferay.faces.util.model.UploadedFile>> uploadedFileMap =
				multiPartFormData.getUploadedFileMap();

			if (uploadedFileMap != null) {

				PortletSession portletSession = portletRequest.getPortletSession(true);
				PortletContext portletContext = portletSession.getPortletContext();
				Map<String, Object> applicationScopeMap = getApplicationScopeMap(portletContext, true);
				ExternalContext externalContext = new ExternalContextProductImpl(applicationScopeMap);
				ProductFactory productFactory = (ProductFactory) FactoryExtensionFinder.getFactory(externalContext,
						ProductFactory.class);
				Product PRIMEFACES = productFactory.getProductInfo(Product.Name.PRIMEFACES);
				boolean primeFacesDetected = PRIMEFACES.isDetected();
				int primeFacesMajorVersion = PRIMEFACES.getMajorVersion();

				bridgeUploadedFileMap = new HashMap<String, List<UploadedFile>>(uploadedFileMap.size());

				Set<Map.Entry<String, List<com.liferay.faces.util.model.UploadedFile>>> entrySet =
					uploadedFileMap.entrySet();

				for (Map.Entry<String, List<com.liferay.faces.util.model.UploadedFile>> mapEntry : entrySet) {
					List<com.liferay.faces.util.model.UploadedFile> uploadedFileList = mapEntry.getValue();

					if (uploadedFileList != null) {
						List<UploadedFile> bridgeUploadedFileList = new ArrayList<UploadedFile>(
								uploadedFileList.size());

						for (com.liferay.faces.util.model.UploadedFile uploadedFile : uploadedFileList) {
							bridgeUploadedFileList.add(new UploadedFileBridgeImpl(uploadedFile, primeFacesDetected,
									primeFacesMajorVersion));
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

	protected FacesRequestParameterMap getFacesRequestParameterMap(PortletRequest portletRequest,
		String responseNamespace, PortletConfig portletConfig, BridgeRequestScope bridgeRequestScope,
		String defaultRenderKitId, String facesViewQueryString) {

		FacesRequestParameterMap facesRequestParameterMap = null;
		Map<String, String> facesViewParameterMap = getFacesViewParameterMap(facesViewQueryString);

		PortalContext portalContext = portletRequest.getPortalContext();
		boolean strictParameterNamespacingSupported = FacesRuntimeUtil.isStrictParameterNamespacingSupported(
				portalContext);
		PortletContext portletContext = portletConfig.getPortletContext();
		boolean namespaceViewState = strictParameterNamespacingSupported &&
			FacesRuntimeUtil.isNamespaceViewState(strictParameterNamespacingSupported, portletContext);

		if (portletRequest instanceof ClientDataRequest) {

			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;
			String contentType = clientDataRequest.getContentType();

			// Note: af:inputFile (ADF Faces) ace:fileEntry (ICEfaces) rely on their own mechanisms for handling file
			// uploads.
			ProductFactory productFactory = (ProductFactory) BridgeFactoryFinder.getFactory(portletContext,
					ProductFactory.class);
			Product ADF_FACES_RICH_CLIENT = productFactory.getProductInfo(Product.Name.ADF_FACES);
			Product ICEFACES = productFactory.getProductInfo(Product.Name.ICEFACES);

			if (!ADF_FACES_RICH_CLIENT.isDetected() && !ICEFACES.isDetected() && (contentType != null) &&
					contentType.toLowerCase().startsWith("multipart/")) {

				MultiPartFormData multiPartFormData = (MultiPartFormData) portletRequest.getAttribute(
						MULTIPART_FORM_DATA_FQCN);

				if (multiPartFormData == null) {
					facesRequestParameterMap = new FacesRequestParameterMapImpl(responseNamespace, bridgeRequestScope,
							facesViewParameterMap, defaultRenderKitId, getSeparatorChar(),
							strictParameterNamespacingSupported, namespaceViewState);

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
					bridgeRequestScope, facesViewParameterMap, defaultRenderKitId, getSeparatorChar(),
					strictParameterNamespacingSupported, namespaceViewState);
		}

		return facesRequestParameterMap;
	}

	private class ExternalContextProductImpl extends ExternalContextWrapper {

		private Map<String, Object> applicationMap;

		public ExternalContextProductImpl(Map<String, Object> applicationMap) {
			this.applicationMap = applicationMap;
		}

		@Override
		public Map<String, Object> getApplicationMap() {
			return applicationMap;
		}

		@Override
		public ExternalContext getWrapped() {
			throw new UnsupportedOperationException();
		}
	}
}
