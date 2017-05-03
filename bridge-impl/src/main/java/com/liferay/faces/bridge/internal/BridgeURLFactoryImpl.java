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
package com.liferay.faces.bridge.internal;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeException;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.BridgeURL;
import com.liferay.faces.bridge.BridgeURLFactory;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;


/**
 * @author  Neil Griffin
 */
public class BridgeURLFactoryImpl extends BridgeURLFactory implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 4036112087598188923L;

	public BridgeURLFactoryImpl() {
		BridgeDependencyVerifier.verify();
	}

	@Override
	public BridgeURL getBridgeActionURL(FacesContext facesContext, String uri) throws BridgeException {

		ExternalContext externalContext = facesContext.getExternalContext();
		ContextInfo contextInfo = new ContextInfo(facesContext.getViewRoot(), externalContext);
		ClientWindowInfo clientWindowInfo = new ClientWindowInfo(externalContext);

		try {
			return new BridgeURLActionImpl(uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, clientWindowInfo.isRenderModeEnabled(facesContext),
					clientWindowInfo.getId(), clientWindowInfo.getUrlParameters(facesContext),
					contextInfo.portletContext, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgeBookmarkableURL(FacesContext facesContext, String uri,
		Map<String, List<String>> parameters) throws BridgeException {

		ExternalContext externalContext = facesContext.getExternalContext();
		ContextInfo contextInfo = new ContextInfo(facesContext.getViewRoot(), externalContext);
		ClientWindowInfo clientWindowInfo = new ClientWindowInfo(externalContext);

		try {
			return new BridgeURLBookmarkableImpl(uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, parameters, clientWindowInfo.isRenderModeEnabled(facesContext),
					clientWindowInfo.getId(), clientWindowInfo.getUrlParameters(facesContext),
					contextInfo.portletContext, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgePartialActionURL(FacesContext facesContext, String uri) throws BridgeException {

		ExternalContext externalContext = facesContext.getExternalContext();
		ContextInfo contextInfo = new ContextInfo(facesContext.getViewRoot(), externalContext);

		try {

			ClientWindowInfo clientWindowInfo = new ClientWindowInfo(externalContext);

			return new BridgeURLPartialActionImpl(uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, clientWindowInfo.isRenderModeEnabled(facesContext),
					clientWindowInfo.getId(), clientWindowInfo.getUrlParameters(facesContext),
					contextInfo.portletContext, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgeRedirectURL(FacesContext facesContext, String uri, Map<String, List<String>> parameters)
		throws BridgeException {

		ExternalContext externalContext = facesContext.getExternalContext();
		ContextInfo contextInfo = new ContextInfo(facesContext.getViewRoot(), externalContext);
		ClientWindowInfo clientWindowInfo = new ClientWindowInfo(externalContext);

		try {
			return new BridgeURLRedirectImpl(uri, contextInfo.contextPath, contextInfo.namespace, parameters,
					clientWindowInfo.isRenderModeEnabled(facesContext), clientWindowInfo.getId(),
					clientWindowInfo.getUrlParameters(facesContext), contextInfo.portletContext,
					contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgeResourceURL(FacesContext facesContext, String uri) throws BridgeException {

		ContextInfo contextInfo = new ContextInfo(facesContext.getViewRoot(), facesContext.getExternalContext());

		try {
			return new BridgeURLResourceImpl(facesContext, uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURLFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}

	private static class ContextInfo {

		// Private Data Members
		public BridgeConfig bridgeConfig;
		public String contextPath;
		public String currentFacesViewId;
		public String namespace;
		public PortletContext portletContext;

		public ContextInfo(UIViewRoot uiViewRoot, ExternalContext externalContext) {

			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			this.bridgeConfig = RequestMapUtil.getBridgeConfig(portletRequest);
			this.contextPath = externalContext.getRequestContextPath();

			if (uiViewRoot != null) {
				this.currentFacesViewId = uiViewRoot.getViewId();
			}

			this.namespace = externalContext.encodeNamespace("");
			this.portletContext = (PortletContext) externalContext.getContext();
		}
	}
}
