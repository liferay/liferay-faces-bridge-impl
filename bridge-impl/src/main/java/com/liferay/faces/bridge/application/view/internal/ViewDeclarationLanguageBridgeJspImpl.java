/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.application.view.internal;

import java.io.IOException;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.filter.internal.HeaderRequestHttpServletAdapter;
import com.liferay.faces.bridge.filter.internal.HeaderResponseHttpServletAdapter;
import com.liferay.faces.bridge.filter.internal.ResourceRequestHttpServletAdapter;
import com.liferay.faces.bridge.filter.internal.ResourceResponseHttpServletAdapter;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class decorates the Mojarra/MyFaces {@link ViewDeclarationLanguage} implementation for JSP in order to
 * work-around Servlet API dependencies.
 *
 * @author  Neil Griffin
 */
public class ViewDeclarationLanguageBridgeJspImpl extends ViewDeclarationLanguageBridgeBase {

	public ViewDeclarationLanguageBridgeJspImpl(ViewDeclarationLanguage viewDeclarationLanguage) {
		super(viewDeclarationLanguage);
	}

	@Override
	public void buildView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		ProductFactory productFactory = (ProductFactory) FactoryExtensionFinder.getFactory(externalContext,
				ProductFactory.class);
		final Product MYFACES = productFactory.getProductInfo(Product.Name.MYFACES);
		final boolean MYFACES_DETECTED = MYFACES.isDetected();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		// If MyFaces is detected, then work-around a Servlet API dependency by decorating the PortletRequest with an
		// adapter that implements HttpServletRequest.
		if (MYFACES_DETECTED) {

			if (portletRequest instanceof HeaderRequest) {

				String requestCharacterEncoding = externalContext.getRequestCharacterEncoding();
				externalContext.setRequest(new HeaderRequestHttpServletAdapter((HeaderRequest) portletRequest,
						requestCharacterEncoding));
			}
			else if (portletRequest instanceof ResourceRequest) {
				externalContext.setRequest(new ResourceRequestHttpServletAdapter((ResourceRequest) portletRequest));
			}
		}

		final Product MOJARRA = productFactory.getProductInfo(Product.Name.MOJARRA);
		final boolean MOJARRA_DETECTED = MOJARRA.isDetected();

		// If Mojarra or MyFaces is detected, then work-around a Servlet API dependency by decorating the
		// PortletResponse with an adapter that implements HttpServletResponse.
		if (MOJARRA_DETECTED || MYFACES_DETECTED) {

			if (portletResponse instanceof HeaderResponse) {
				externalContext.setResponse(new HeaderResponseHttpServletAdapter((HeaderResponse) portletResponse));
			}
			else if (portletResponse instanceof ResourceResponse) {
				externalContext.setResponse(new ResourceResponseHttpServletAdapter((ResourceResponse) portletResponse));
			}
		}

		// Delegate
		super.buildView(facesContext, uiViewRoot);

		// If Mojarra or MyFaces is detected, then un-decorate the PortletRequest.
		if (MOJARRA_DETECTED || MYFACES_DETECTED) {
			externalContext.setResponse(portletResponse);
		}

		// If MyFaces is detected, then un-decorate the PortletResponse.
		if (MYFACES_DETECTED) {
			externalContext.setRequest(portletRequest);
		}
	}
}
