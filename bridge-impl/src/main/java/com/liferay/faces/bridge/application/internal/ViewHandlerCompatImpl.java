/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
import java.lang.reflect.Method;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
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
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class ViewHandlerCompatImpl extends ViewHandlerWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ViewHandlerCompatImpl.class);

	// Private Constants
	private static final String EL_EXPRESSION_PREFIX = "#{";

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

			ViewHandler viewHandler = getFacesRuntimeViewHandler();

			if (JspViewHandlerCompat.isJspView(uiViewRoot)) {
				viewHandler = new JspViewHandlerCompat(viewHandler);
			}

			viewHandler.renderView(facesContext, uiViewRoot);
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

				ViewHandler viewHandler = getFacesRuntimeViewHandler();

				if (JspViewHandlerCompat.isJspView(uiViewRoot)) {
					viewHandler = new JspViewHandlerCompat(viewHandler);
				}

				viewHandler.renderView(facesContext, uiViewRoot);
			}
		}
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

		if (JspViewHandlerCompat.isJspView(uiViewRoot)) {
			ViewHandler jspViewHanlder = new JspViewHandlerCompat(getWrapped());
			jspViewHanlder.renderView(facesContext, uiViewRoot);
		}
		else {
			getWrapped().renderView(facesContext, uiViewRoot);
		}

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

		int pos = viewId.indexOf(EL_EXPRESSION_PREFIX);

		if (pos > 0) {
			ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
			ELContext elContext = facesContext.getELContext();
			ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, viewId, String.class);
			viewId = (String) valueExpression.getValue(elContext);

			if ((viewId != null) && !viewId.startsWith("/")) {
				viewId = "/" + viewId;
			}
		}

		return viewId;
	}

	protected ViewHandler getFacesRuntimeViewHandler() {

		ViewHandler viewHandler = getWrapped();

		while (viewHandler instanceof ViewHandlerWrapper) {
			ViewHandlerWrapper viewHandlerWrapper = (ViewHandlerWrapper) viewHandler;
			viewHandler = getWrappedViewHandler(viewHandlerWrapper);
		}

		return viewHandler;
	}

	protected ViewHandler getWrappedViewHandler(ViewHandlerWrapper viewHandlerWrapper) {

		ViewHandler wrappedViewHandler = null;

		try {
			Method getWrappedMethod = viewHandlerWrapper.getClass().getMethod("getWrapped");

			// ViewHandlerWrapper.getWrapped() is public in JSF 2.x but is protected in JSF 1.2
			getWrappedMethod.setAccessible(true);
			wrappedViewHandler = (ViewHandler) getWrappedMethod.invoke(viewHandlerWrapper);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return wrappedViewHandler;
	}
}
