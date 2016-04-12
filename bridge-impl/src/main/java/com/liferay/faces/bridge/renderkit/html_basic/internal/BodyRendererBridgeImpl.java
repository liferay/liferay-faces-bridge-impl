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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is a JSF renderer that is designed for use with the h:body component tag in a portlet environment.
 * Implementations of this renderer, including the default JSF implementation, normally render &lt;body&gt; elements,
 * but portlets are forbidden from rendering the &lt;body&gt; elements, so this class renders &lt;div&gt; elements
 * instead. In order to render &lt;div&gt; elements while still calling through to the wrapped renderer's methods, this
 * class replaces the default {@link ResponseWriter} with a {@link ResponseWriterBridgeBodyImpl} and calls through to
 * the wrapped body renderer. The {@link ResponseWriterBridgeBodyImpl} replaces the &lt;body&gt; elements with
 * &lt;div&gt; elements.
 *
 * @author  Neil Griffin
 */
public class BodyRendererBridgeImpl extends RendererWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BodyRendererBridgeImpl.class);

	// Package-Private Constants
	/* package-private */ static final String STYLE_CLASS_PORTLET_BODY = "liferay-faces-bridge-body";

	// Private Members
	private Renderer wrappedBodyRenderer;

	public BodyRendererBridgeImpl(Renderer wrappedBodyRenderer) {
		this.wrappedBodyRenderer = wrappedBodyRenderer;
	}

	/**
	 * It is forbidden for a portlet to render the &amp;&lt;body&amp;&gt; element, so instead, render a
	 * &amp;&lt;div&amp;&gt;element.
	 *
	 * @see  Renderer#encodeBegin(FacesContext, UIComponent)
	 */
	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();
		ResponseWriterBridgeBodyImpl responseWriterPortletBodyImpl = new ResponseWriterBridgeBodyImpl(responseWriter);
		facesContext.setResponseWriter(responseWriterPortletBodyImpl);
		super.encodeBegin(facesContext, uiComponent);

		PortletNamingContainerUIViewRoot viewRoot = (PortletNamingContainerUIViewRoot) facesContext.getViewRoot();
		String clientId = viewRoot.getContainerClientId(facesContext);
		responseWriterPortletBodyImpl.writeAttribute("id", clientId, null);

		String styleClass = (String) uiComponent.getAttributes().get("styleClass");

		// If no styleClass has been specified, add a special CSS class name in the rendered markup in order to
		// clue-in the developer that a <div> was rendered instead of <body>. If styleClass is not null, then the
		// responseWriterBridgeBodyImpl will append STYLE_CLASS_PORTLET_BODY to the specified styleClass.
		if (styleClass == null) {
			responseWriterPortletBodyImpl.writeAttribute("class", STYLE_CLASS_PORTLET_BODY, "styleClass");
		}

		facesContext.setResponseWriter(responseWriter);

		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortalContext portalContext = portletRequest.getPortalContext();

		// If the bridge cannot add a resource to the <head> section, then add it to the top of the portlet body (the
		// outer <div> of the portlet). If <style> elements or <link rel="stylesheet"> elements are relocated, the
		// generated html will be invalid because those elements are only valid in the <head> section. See
		// https://html.spec.whatwg.org/multipage/semantics.html#the-style-element and
		// https://html.spec.whatwg.org/multipage/semantics.html#the-link-element for more details about valid html
		// markup. However, the invalid html generated works perfectly in all popular browsers.
		if (!canAddAllResourceTypesToHead(portalContext)) {

			UIViewRoot uiViewRoot = facesContext.getViewRoot();
			List<UIComponent> headResources = uiViewRoot.getComponentResources(facesContext, "head");

			if (!headResources.isEmpty()) {

				HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);
				Set<String> headResourceIds;

				if (headManagedBean == null) {
					headResourceIds = new HashSet<String>();
				}
				else {
					headResourceIds = headManagedBean.getHeadResourceIds();
				}

				for (UIComponent headResource : headResources) {

					String headResourceId = ResourceUtil.getResourceId(headResource);

					if (!HeadResourceUtil.canAddResourceToHead(portalContext, headResource)) {

						headResource.encodeAll(facesContext);

						// In order to prevent script resources from being loaded multiple times, add script resources
						// to the list of headResourceIds.
						if (HeadResourceUtil.isScriptResource(headResource)) {
							headResourceIds.add(headResourceId);
						}
					}
				}
			}
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter originalResponseWriter = facesContext.getResponseWriter();
		ResponseWriter responseWriter = new ResponseWriterBridgeBodyImpl(originalResponseWriter);
		facesContext.setResponseWriter(responseWriter);
		super.encodeEnd(facesContext, uiComponent);
		facesContext.setResponseWriter(originalResponseWriter);
	}

	private boolean canAddAllResourceTypesToHead(PortalContext portalContext) {

		boolean canAddStyleSheetResourcesToHead = portalContext.getProperty(
				BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT) != null;
		boolean canAddStyleSheetTextToHead = portalContext.getProperty(
				BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT) != null;
		boolean canAddScriptResourcesToHead = portalContext.getProperty(
				BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT) != null;
		boolean canAddScriptTextToHead = portalContext.getProperty(BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT) !=
			null;

		return (canAddStyleSheetResourcesToHead && canAddStyleSheetTextToHead && canAddScriptResourcesToHead &&
				canAddScriptTextToHead);
	}

	@Override
	public Renderer getWrapped() {
		return wrappedBodyRenderer;
	}
}
