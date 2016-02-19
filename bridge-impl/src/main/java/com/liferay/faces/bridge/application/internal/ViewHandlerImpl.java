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
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ViewHandlerImpl extends ViewHandlerCompatImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ViewHandlerImpl.class);

	// Private Data Members
	private ViewHandler wrappedViewHandler;

	public ViewHandlerImpl(ViewHandler viewHandler) {
		this.wrappedViewHandler = viewHandler;
	}

	/**
	 * The purpose of overriding this method is to work-around a problem in the Mojarra
	 * com.sun.faces.application.view.MultiViewHanderl#derivePhysicalViewId(FacesContext, String, boolean) method.
	 * Specifically, the method does not expect a query-string (like ?javax.portlet.faces.PortletMode=edit) in the
	 * viewId. This is expected actually, because the JSF spec does not provide for query-strings. However, it is indeed
	 * a Bridge Spec feature. This method temporarily removes the query-string before asking Mojarra to create the view,
	 * and then adds it back.
	 */
	@Override
	public UIViewRoot createView(FacesContext facesContext, String viewId) {

		logger.debug("Creating view for viewId=[{0}]", viewId);

		String queryString = null;

		if (viewId != null) {

			viewId = evaluateExpressionJSF1(facesContext, viewId);

			int pos = viewId.indexOf("?");

			if (pos > 0) {
				queryString = viewId.substring(pos);
				logger.debug("Temporarily removed query-string from viewId=[{0}]", viewId);
				viewId = viewId.substring(0, pos);
			}
		}

		UIViewRoot uiViewRoot = super.createView(facesContext, viewId);

		if (queryString != null) {
			logger.debug("Adding back query-string viewId=[{0}]", viewId);
			uiViewRoot.setViewId(viewId + queryString);
		}

		return uiViewRoot;
	}

	@Override
	public void renderView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException, FacesException {

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String initParam = externalContext.getInitParameter(Bridge.RENDER_POLICY);
		Bridge.BridgeRenderPolicy bridgeRenderPolicy = Bridge.BridgeRenderPolicy.DEFAULT;

		if (initParam != null) {
			bridgeRenderPolicy = Bridge.BridgeRenderPolicy.valueOf(initParam);
		}

		// If the developer has specified ALWAYS_DELEGATE in the WEB-INF/web.xml descriptor, then execute
		// delegate to the ViewHandler chain-of-responsibility.
		if (bridgeRenderPolicy == Bridge.BridgeRenderPolicy.ALWAYS_DELEGATE) {
			_delegateRenderView(facesContext, externalContext, uiViewRoot);
		}

		// If the specified render policy is NEVER_DELEGATE, then do not delegate rendering to the delegation chain.
		// In this case, JSR 329 expected that the bridge would execute its own implementation of ViewHandler. However,
		// this proved to be unnecessary. Instead, executing the ViewHandler provided by the Faces runtime works fine,
		// provided that the bridge work around some Servlet-API dependencies (such as casting to HttpServletRequest)
		// in the Faces runtime.
		else if (bridgeRenderPolicy == Bridge.BridgeRenderPolicy.NEVER_DELEGATE) {

			ViewHandler facesRuntimeViewHandler = getFacesRuntimeViewHandler();
			facesRuntimeViewHandler.renderView(facesContext, uiViewRoot);
		}

		// Otherwise,
		else {

			// Delegate the render to the view-handler chain-of-responsibility.
			try {
				_delegateRenderView(facesContext, externalContext, uiViewRoot);
			}

			// If an exception is thrown, then execute the Mojarra/MyFaces render directly, bypassing the view-handler
			// chain-of-responsibility.
			catch (FacesException e) {

				ViewHandler facesRuntimeViewHandler = getFacesRuntimeViewHandler();

				facesRuntimeViewHandler.renderView(facesContext, uiViewRoot);
			}
		}
	}

	@Override
	public UIViewRoot restoreView(FacesContext facesContext, String viewId) {
		logger.debug("Restoring view for viewId=[{0}]", viewId);

		return super.restoreView(facesContext, viewId);
	}

	protected void _delegateRenderView(FacesContext facesContext, ExternalContext externalContext,
		UIViewRoot uiViewRoot) throws IOException, FacesException {

		// Section 7.1 of the JSR 301 Spec requires that there be legacy support for the use of a servlet-filter in
		// order to capture plain HTML markup that may appear after the closing </f:view> component tag (a.k.a. "after
		// view markup"). This requirement was kept in JSR 329 for backward-compatibility. The servlet-filter approach
		// was necessary in Portlet 1.0 environments since wrapper classes like PortletResponseWrapper were not
		// available until Portlet 2.0.
		Map<String, Object> requestAttributeMap = externalContext.getRequestMap();
		requestAttributeMap.put(Bridge.RENDER_CONTENT_AFTER_VIEW, Boolean.TRUE);

		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		super.renderView(facesContext, uiViewRoot);
		requestAttributeMap.remove(Bridge.RENDER_CONTENT_AFTER_VIEW);
		externalContext.setResponse(portletResponse);

		// TCK TestPage201: renderContentAfterViewTest
		Object afterViewContent = requestAttributeMap.get(Bridge.AFTER_VIEW_CONTENT);

		if (afterViewContent != null) {

			if (afterViewContent instanceof char[]) {
				facesContext.getResponseWriter().write((char[]) afterViewContent);
			}
			else if (afterViewContent instanceof byte[]) {
				facesContext.getResponseWriter().write(new String((byte[]) afterViewContent));
			}
			else {
				logger.error("Invalid type for {0}={1}", Bridge.AFTER_VIEW_CONTENT, afterViewContent.getClass());
			}
		}
	}

	/**
	 * The purpose of this method is to provide a workaround for an incompatibility with the Mojarra implementation of
	 * JSF. Specifically, the Mojarra {@link com.sun.faces.application.view.MultiViewHandler#getActionURL(FacesContext,
	 * String)} method does not properly handle viewId values that contain dot characters as part of the query-string.
	 * For example, if the specified viewId is "/view.xhtml?javax.portlet.faces.PortletMode=edit" then Mojarra will
	 * think the filename extension is ".PortletMode" instead of ".xhtml". This method works around the problem by
	 * temporarily substituting all dot characters in the viewId query-string with a token before delegating to the
	 * Mojarra method. After delegation, the dot characters are replaced.
	 */
	@Override
	public String getActionURL(FacesContext facesContext, String viewId) {

		String actionURL = null;

		if (viewId != null) {
			boolean replacedDotChars = false;
			int questionMarkPos = viewId.indexOf("?");

			if (questionMarkPos > 0) {

				int dotPos = viewId.indexOf(".", questionMarkPos);

				if (dotPos > 0) {
					String queryString = viewId.substring(questionMarkPos);
					queryString = queryString.replaceAll("[.]", "_DOT_");
					viewId = viewId.substring(0, questionMarkPos) + queryString;
					replacedDotChars = true;
				}

			}

			actionURL = super.getActionURL(facesContext, viewId);

			if (replacedDotChars) {
				actionURL = actionURL.replaceAll("_DOT_", ".");
			}
		}

		return actionURL;
	}

	@Override
	public ViewHandler getWrapped() {
		return wrappedViewHandler;
	}

}
