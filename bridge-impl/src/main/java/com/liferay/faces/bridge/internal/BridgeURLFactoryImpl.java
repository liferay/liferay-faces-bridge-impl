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
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeURL;
import javax.portlet.faces.BridgeURLFactory;

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

		ContextInfo contextInfo = new ContextInfo(facesContext);

		try {
			return new BridgeURLActionImpl(uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgeBookmarkableURL(FacesContext facesContext, String uri,
		Map<String, List<String>> parameters) throws BridgeException {

		ContextInfo contextInfo = new ContextInfo(facesContext);

		try {
			return new BridgeURLBookmarkableImpl(uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, parameters, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgePartialActionURL(FacesContext facesContext, String uri) throws BridgeException {

		ContextInfo contextInfo = new ContextInfo(facesContext);

		try {
			return new BridgeURLPartialActionImpl(uri, contextInfo.contextPath, contextInfo.namespace,
					contextInfo.currentFacesViewId, contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgeRedirectURL(FacesContext facesContext, String uri, Map<String, List<String>> parameters)
		throws BridgeException {

		ContextInfo contextInfo = new ContextInfo(facesContext);

		try {
			return new BridgeURLRedirectImpl(uri, contextInfo.contextPath, contextInfo.namespace, parameters,
					contextInfo.bridgeConfig);
		}
		catch (URISyntaxException e) {
			throw new BridgeException(e);
		}
	}

	@Override
	public BridgeURL getBridgeResourceURL(FacesContext facesContext, String uri) throws BridgeException {

		ContextInfo contextInfo = new ContextInfo(facesContext);

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

		public ContextInfo(FacesContext facesContext) {

			ExternalContext externalContext = facesContext.getExternalContext();
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			this.bridgeConfig = RequestMapUtil.getBridgeConfig(portletRequest);
			this.contextPath = externalContext.getRequestContextPath();

			UIViewRoot viewRoot = facesContext.getViewRoot();

			if (viewRoot != null) {
				this.currentFacesViewId = viewRoot.getViewId();
			}

			this.namespace = externalContext.encodeNamespace("");
		}
	}
}
