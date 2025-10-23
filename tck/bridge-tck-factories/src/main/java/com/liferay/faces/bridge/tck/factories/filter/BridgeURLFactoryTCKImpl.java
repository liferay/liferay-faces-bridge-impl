/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.factories.filter;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeURL;
import javax.portlet.faces.BridgeURLFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLFactoryTCKImpl extends BridgeURLFactory {

	private BridgeURLFactory wrappedFactory;

	public BridgeURLFactoryTCKImpl(BridgeURLFactory bridgeURLFactory) {
		this.wrappedFactory = bridgeURLFactory;
	}

	@Override
	public BridgeURL getBridgeActionURL(FacesContext facesContext, String uri) throws BridgeException {
		return wrappedFactory.getBridgeActionURL(facesContext, uri);
	}

	@Override
	public BridgeURL getBridgeBookmarkableURL(FacesContext facesContext, String uri,
		Map<String, List<String>> parameters) throws BridgeException {
		return wrappedFactory.getBridgeBookmarkableURL(facesContext, uri, parameters);
	}

	@Override
	public BridgeURL getBridgePartialActionURL(FacesContext facesContext, String uri) throws BridgeException {
		return wrappedFactory.getBridgePartialActionURL(facesContext, uri);
	}

	@Override
	public BridgeURL getBridgeRedirectURL(FacesContext facesContext, String uri, Map<String, List<String>> parameters)
		throws BridgeException {
		return wrappedFactory.getBridgeRedirectURL(facesContext, uri, parameters);
	}

	@Override
	public BridgeURL getBridgeResourceURL(FacesContext facesContext, String uri) throws BridgeException {
		return wrappedFactory.getBridgeResourceURL(facesContext, uri);
	}

	@Override
	public BridgeURLFactory getWrapped() {
		return wrappedFactory;
	}
}
