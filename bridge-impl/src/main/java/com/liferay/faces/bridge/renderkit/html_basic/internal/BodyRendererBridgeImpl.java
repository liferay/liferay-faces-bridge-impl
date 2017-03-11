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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.util.application.ResourceUtil;


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
	@SuppressWarnings("unchecked")
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

		// Render each of the head resources that were not renderable in the head section into the top of the portlet
		// body (the outer <div> of the portlet markup). This happens when the portlet container may not support adding
		// resources to the <head> section. For example, Pluto does not support the feature of adding resources to the
		// <head> section. Liferay supports it with the exception of "runtime" and WSRP portlets. See
		// PortalContextBridgeImpl and PortalContextBridgeLiferayImpl (in bridge-ext) for more information. Script
		// resources are rendered at the top of the portlet body instead of the bottom because elements and scripts in
		// the portlet body may depend on these resources (for example jquery.js), so head resource scripts must be
		// loaded before the portlet body is rendered.
		Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
		List<UIComponent> headResourcesToRenderInBody = (List<UIComponent>) facesContextAttributes.get(
				HeadRendererBridgeImpl.HEAD_RESOURCES_TO_RENDER_IN_BODY);

		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);
		Set<String> headResourceIds;

		if (headManagedBean == null) {
			headResourceIds = new HashSet<String>();
		}
		else {
			headResourceIds = headManagedBean.getHeadResourceIds();
		}

		// Note: If <style> elements or <link rel="stylesheet"> elements are not able to be rendered in the head, they
		// will be relocated to the <body>. However, because those elements are only valid in the <head> section, the
		// generated HTML will be invalid. Despite the invalidness of the generated HTML, all popular browsers will
		// correctly load and render the CSS. For more details about valid HTML markup, see:
		// https://html.spec.whatwg.org/multipage/semantics.html#the-style-element
		// https://html.spec.whatwg.org/multipage/semantics.html#the-link-element
		for (UIComponent headResource : headResourcesToRenderInBody) {

			headResource.encodeAll(facesContext);

			// If the portlet's body (<div>) section is reloaded during an Ajax request, stylesheet resources included
			// in the <div> will be removed and unloaded. Since the stylesheet resources will be unloaded (and reloaded
			// if necessary), we do not need to track them when they are rendered to the body section. However, scripts
			// cannot be unloaded, so relocated scripts rendered in the body section must be tracked as if they were
			// rendered in the <head> section so that they are not loaded multiple times.
			if (HeadRendererBridgeImpl.isScriptResource(headResource)) {
				headResourceIds.add(ResourceUtil.getResourceId(headResource));
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

	@Override
	public Renderer getWrapped() {
		return wrappedBodyRenderer;
	}
}
