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
package com.liferay.faces.bridge.application.internal;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.filter.internal.RenderRequestHttpServletAdapter;
import com.liferay.faces.bridge.filter.internal.RenderResponseHttpServletAdapter;
import com.liferay.faces.bridge.filter.internal.ResourceRequestHttpServletAdapter;
import com.liferay.faces.bridge.filter.internal.ResourceResponseHttpServletAdapter;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public class JspViewHandlerCompat extends ViewHandlerWrapper {

	// Private Constants
	private static final boolean MOJARRA_DETECTED = ProductFactory.getProduct(Product.Name.MOJARRA).isDetected();
	private static final boolean MYFACES_DETECTED = ProductFactory.getProduct(Product.Name.MYFACES).isDetected();

	// Private Data Members
	private ViewHandler wrappedViewHandler;

	public JspViewHandlerCompat(ViewHandler viewHandler) {
		this.wrappedViewHandler = viewHandler;
	}

	public static boolean isJspView(UIViewRoot uiViewRoot) {

		String viewId = uiViewRoot.getViewId();

		return ((viewId != null) && viewId.toLowerCase().contains(".jsp"));
	}

	// In the JSF 2.x branches, JSP detection and the following Servlet API work-arounds are found in the
	// ViewDeclarationLanguageBridgeJspImp.java class (which is a more appropriate integration point for JSF 2.x).
	@Override
	public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException, FacesException {

		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		// If Mojarra or MyFaces is detected, then work-around a Servlet API dependency by decorating the
		// PortletRequest with an adapter that implements HttpServletRequest and the PortletResponse with an adapter
		// that implements HttpServletResponse.
		if (MOJARRA_DETECTED || MYFACES_DETECTED) {

			if (portletRequest instanceof RenderRequest) {
				String requestCharacterEncoding = externalContext.getRequestCharacterEncoding();
				externalContext.setRequest(new RenderRequestHttpServletAdapter((RenderRequest) portletRequest,
						requestCharacterEncoding));
			}
			else if (portletRequest instanceof ResourceRequest) {
				externalContext.setRequest(new ResourceRequestHttpServletAdapter((ResourceRequest) portletRequest));
			}

			if (portletResponse instanceof RenderResponse) {
				externalContext.setResponse(new RenderResponseHttpServletAdapter((RenderResponse) portletResponse));
			}
			else if (portletResponse instanceof ResourceResponse) {
				externalContext.setResponse(new ResourceResponseHttpServletAdapter((ResourceResponse) portletResponse));
			}
		}

		// Delegate to the Mojarra/MyFaces ViewHandler.
		super.renderView(facesContext, uiViewRoot);

		// If Mojarra or MyFaces is detected, then un-decorate the PortletResponse and PortletRequest.
		if (MOJARRA_DETECTED || MYFACES_DETECTED) {
			externalContext.setResponse(portletResponse);
			externalContext.setRequest(portletRequest);
		}
	}

	@Override
	protected ViewHandler getWrapped() {
		return wrappedViewHandler;
	}
}
