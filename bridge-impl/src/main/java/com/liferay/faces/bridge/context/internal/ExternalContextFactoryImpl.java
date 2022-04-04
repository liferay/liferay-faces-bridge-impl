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
package com.liferay.faces.bridge.context.internal;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.faces.bridge.util.internal.RichFacesUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ExternalContextFactoryImpl extends ExternalContextFactory {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ExternalContextFactoryImpl.class);

	// Private Data Members
	private ExternalContextFactory wrappedFactory;
	private boolean wrappedFactoryLiferayFacesUtil = false;

	public ExternalContextFactoryImpl(ExternalContextFactory externalContextFactory) {
		this.wrappedFactory = externalContextFactory;

		Class<? extends ExternalContextFactory> externalContextFactoryClass = externalContextFactory.getClass();
		String externalContextFactoryClassName = externalContextFactoryClass.getName();

		if (externalContextFactoryClassName.startsWith("com.liferay.faces.util")) {
			wrappedFactoryLiferayFacesUtil = true;
		}
	}

	@Override
	public ExternalContext getExternalContext(Object context, Object request, Object response) throws FacesException {

		// If the specified objects are coming from a portlet container, then return an instance of the
		// bridge's ExternalContext implementation.
		//
		// NOTE: Can't use BridgeUtil.isPortletRequest() here because the FacesContext is in the process of
		// initialization.
		if (context instanceof PortletContext) {

			PortletContext portletContext = (PortletContext) context;
			PortletRequest portletRequest = (PortletRequest) request;
			PortletResponse portletResponse = (PortletResponse) response;
			ExternalContext externalContext = null;

			if (wrappedFactoryLiferayFacesUtil) {
				ExternalContext utilExternalContext = wrappedFactory.getExternalContext(portletContext, portletRequest,
						portletResponse);
				externalContext = new ExternalContextImpl(portletContext, portletRequest, portletResponse,
						utilExternalContext);
			}
			else {
				externalContext = new ExternalContextImpl(portletContext, portletRequest, portletResponse);
			}

			String resourceName = portletRequest.getParameter("javax.faces.resource");

			// Workaround for FACES-2133
			if ("org.richfaces.resource.MediaOutputResource".equals(resourceName)) {
				return new ExternalContextRichFacesResourceImpl(externalContext);
			}
			else {
				return externalContext;
			}
		}

		// Otherwise, it is possible that a request hit the FacesServlet directly, and we should delegate
		// to the chain. Such requests can happen when JSF UI components are not properly calling
		// @link {ViewHandler.getResourceURL(FacesContext, String)} to get properly encoded resource URLs that
		// invoke the Portlet 2.0 RESOURCE_PHASE. In such a case, we just delegate to the Mojarra or MyFaces
		// wrapped ExternalContextFactory implementation to get an ExternalContext.
		else {
			logger.debug("Received a non-portlet request; delegating to wrapped ExternalContextFactory");

			return wrappedFactory.getExternalContext(context, request, response);
		}
	}

	/**
	 * This is an overridden method that provides the ability for the FacesWrapper decorator pattern to delegate to
	 * other ExternalContextFactory implementations that are registered.
	 */
	@Override
	public ExternalContextFactory getWrapped() {
		return wrappedFactory;
	}
}
