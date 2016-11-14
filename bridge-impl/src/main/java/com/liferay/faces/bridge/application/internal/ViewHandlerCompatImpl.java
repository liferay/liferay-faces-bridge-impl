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
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.internal.BridgeExt;
import com.liferay.faces.bridge.util.internal.PortletResourceUtilCompat;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class ViewHandlerCompatImpl extends ViewHandlerWrapper {

	// Private Constants
	private static final boolean MOJARRA_DETECTED = ProductFactory.getProduct(Product.Name.MOJARRA).isDetected();

	@Override
	public String getRedirectURL(FacesContext facesContext, String viewId, Map<String, List<String>> parameters,
		boolean includeViewParams) {

		PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		// Determine whether or not it is necessary to work-around the patch applied to Mojarra in JAVASERVERFACES-3023.
		boolean workaroundMojarra = (MOJARRA_DETECTED) &&
			((portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.EVENT_PHASE));

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		if (workaroundMojarra) {
			requestMap.put(BridgeExt.RESPONSE_CHARACTER_ENCODING, "UTF-8");
		}

		String redirectURL = super.getRedirectURL(facesContext, viewId, parameters, includeViewParams);

		if (workaroundMojarra) {
			requestMap.remove(BridgeExt.RESPONSE_CHARACTER_ENCODING);
		}

		return redirectURL;
	}

	@Override
	public String getResourceURL(FacesContext facesContext, String path) {

		// In order to render an image for a button, the Mojarra 2.1 ButtonRenderer calls
		// RenderKitUtils#getImageSource(FacesContext,UIComponent,String) in order to get the image URL. Mojarra 2.2
		// correctly uses the JSF 2.2 ResourceHandler.isResourceURL(String) method to first see if the URL is a JSF 2
		// resource URL:
		//
		// https://github.com/javaserverfaces/mojarra/blob/2.2.12/jsf-ri/src/main/java/com/sun/faces/renderkit/RenderKitUtils.java#L1402
		//
		// When it determines that it is indeed a JSF 2.2 resource URL, it simply returns the URL.
		//
		// However, Mojarra 2.1 can't take advantage of this JSF 2.2 feature and incorrectly looks for
		// "/javax.faces.resource" in the path instead:
		// https://github.com/javaserverfaces/mojarra/blob/2.1.29-05/jsf-ri/src/main/java/com/sun/faces/renderkit/RenderKitUtils.java#L1356
		//
		// Since portlet resource URLs specify "javax.faces.resource" as a URL parameter and not part of the URL path,
		// this check fails. Rather than simply returning the URL, Mojarra 2.1 incorrectly proceeds to call
		// ViewHandler#getResourceURL(FacesContext,String). This method override exists to overcome the problem by
		// preventing Mojarra's MultiViewHandler#getResourceURL(FacesContext,String) method from causing an incorrect
		// URL to be returned.
		if ((MOJARRA_DETECTED) && PortletResourceUtilCompat.isPortletResourceURL(path)) {
			return path;
		}
		else {
			return super.getResourceURL(facesContext, path);
		}
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {

		if (viewId != null) {

			int pos = viewId.indexOf("?");

			if (pos > 0) {
				viewId = viewId.substring(0, pos);
			}
		}

		return super.getViewDeclarationLanguage(context, viewId);
	}

	@Override
	public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException, FacesException {

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
		getWrapped().renderView(facesContext, uiViewRoot);
		externalContext.setResponse(portletResponse);
	}

	/**
	 * Mojarra 1.x does not have the ability to process faces-config navigation-rule entries with to-view-id containing
	 * EL-expressions. This method compensates for that shortcoming by evaluating the EL-expression that may be present
	 * in the specified viewId.
	 *
	 * @param   facesContext  The current FacesContext.
	 * @param   viewId        The viewId that may contain an EL expression.
	 *
	 * @return  If an EL-expression was present in the specified viewId, then returns the evaluated expression.
	 *          Otherwise, returns the specified viewId unchanged.
	 */
	protected String evaluateExpressionJSF1(FacesContext facesContext, String viewId) {

		// This method has overridden behavior for JSF 1 but simply returns the specified viewId for JSF 2
		return viewId;
	}
}
