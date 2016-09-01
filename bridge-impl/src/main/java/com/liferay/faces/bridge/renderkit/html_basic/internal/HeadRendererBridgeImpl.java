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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.faces.bridge.component.internal.ComponentUtil;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.HeadResponseWriterFactory;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is a JSF renderer that is designed for use with the h:head component tag. Portlets are forbidden from
 * rendering the <head>...</head> section, which is what is done by the JSF implementation's version of this renderer.
 * This renderer avoids rendering the <head>...</head> section and instead delegates that responsibility to the portal.
 *
 * @author  Neil Griffin
 */
public class HeadRendererBridgeImpl extends Renderer {

	// Package-Private Constants
	/* package-private */ static final String HEAD_RESOURCES_TO_RENDER_IN_BODY = "headResourcesToRenderInBody";

	// Private Constants
	private static final String FIRST_FACET = "first";
	private static final String MIDDLE_FACET = "middle";
	private static final String LAST_FACET = "last";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadRendererBridgeImpl.class);

	/* package-private */ static boolean isScriptResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("js");
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		// no-op because Portlets are forbidden from rendering the <head>...</head> section.
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		// Build up a list of components that are intended for the <head> section of the portal page.
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		List<UIComponent> headResources = new ArrayList<UIComponent>();

		// Add the list of components that are to appear first.
		List<UIComponent> firstResources = getFirstResources(facesContext, uiComponent);

		if (firstResources != null) {
			headResources.addAll(firstResources);
		}

		// Sort the components that are in the view root into stylesheets and scripts.
		List<UIComponent> headComponentResources = uiViewRoot.getComponentResources(facesContext, "head");
		List<UIComponent> styleSheetResources = new ArrayList<UIComponent>();
		List<UIComponent> scriptResources = new ArrayList<UIComponent>();

		for (UIComponent headComponentResource : headComponentResources) {

			if (isStyleSheetResource(headComponentResource) || isInlineStyleSheet(headComponentResource)) {
				styleSheetResources.add(headComponentResource);
			}
			else {
				scriptResources.add(headComponentResource);
			}
		}

		// Sort children into stylesheets and scripts.
		List<UIComponent> children = uiComponent.getChildren();

		for (UIComponent child : children) {

			if (isStyleSheetResource(child) || isInlineStyleSheet(child)) {
				styleSheetResources.add(child);
			}
			else if (isScriptResource(child) || isInlineScript(child)) {
				scriptResources.add(child);
			}
		}

		// Add the list of stylesheet components that are in the view root.
		if (!styleSheetResources.isEmpty()) {
			headResources.addAll(styleSheetResources);
		}

		// Add the list of components that are to appear in the middle.
		List<UIComponent> middleResources = getMiddleResources(facesContext, uiComponent);

		if (middleResources != null) {
			headResources.addAll(middleResources);
		}

		// Add the list of script components that are in the view root.
		if (!scriptResources.isEmpty()) {
			headResources.addAll(scriptResources);
		}

		// Add the list of components that are to appear last.
		List<UIComponent> lastResources = getLastResources(facesContext, uiComponent);

		if (lastResources != null) {
			headResources.addAll(lastResources);
		}

		List<UIComponent> headResourcesToRenderInBody = new ArrayList<UIComponent>();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortalContext portalContext = portletRequest.getPortalContext();
		Iterator<UIComponent> iterator = headResources.iterator();

		while (iterator.hasNext()) {

			UIComponent headResource = iterator.next();

			// If the portlet container does not have the ability to add the resource to the <head> section of the
			// portal page, then
			if (!ableToAddResourceToHead(portalContext, headResource)) {

				// Add it to the list of resources that are to be rendered in the body section by the body renderer.
				headResourcesToRenderInBody.add(headResource);

				// Remove it from the list of resources that are to be rendered in the head section by this renderer.
				iterator.remove();

				if (logger.isDebugEnabled()) {

					Map<String, Object> componentResourceAttributes = headResource.getAttributes();

					logger.debug(
						"Relocating resource to body: name=[{0}] library=[{1}] rendererType=[{2}] value=[{3}] className=[{4}]",
						componentResourceAttributes.get("name"), componentResourceAttributes.get("library"),
						headResource.getRendererType(), ComponentUtil.getComponentValue(headResource),
						headResource.getClass().getName());
				}
			}
		}

		// Save the list of resources that are to be rendered in the body section so that the body renderer can find it.
		Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
		facesContextAttributes.put(HEAD_RESOURCES_TO_RENDER_IN_BODY, headResourcesToRenderInBody);

		if (!headResources.isEmpty()) {

			// Save a temporary reference to the ResponseWriter provided by the FacesContext.
			ResponseWriter responseWriterBackup = facesContext.getResponseWriter();

			// Replace the ResponseWriter in the FacesContext with a HeadResponseWriter that knows how to write to
			// the <head>...</head> section of the rendered portal page.
			ResponseWriter headResponseWriter = (ResponseWriter) portletRequest.getAttribute(
					"com.liferay.faces.bridge.HeadResponseWriter");

			if (headResponseWriter == null) {

				PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
				headResponseWriter = HeadResponseWriterFactory.getHeadResponseWriterInstance(responseWriterBackup,
						portletResponse);
			}

			portletRequest.setAttribute("com.liferay.faces.bridge.HeadResponseWriter", headResponseWriter);
			facesContext.setResponseWriter(headResponseWriter);

			HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);
			Set<String> headResourceIds;

			if (headManagedBean == null) {
				headResourceIds = new HashSet<String>();
			}
			else {
				headResourceIds = headManagedBean.getHeadResourceIds();
			}

			for (UIComponent headResource : headResources) {

				headResource.encodeAll(facesContext);

				if (isScriptResource(headResource) || isStyleSheetResource(headResource)) {
					headResourceIds.add(ResourceUtil.getResourceId(headResource));
				}
			}

			// Restore the temporary ResponseWriter reference.
			facesContext.setResponseWriter(responseWriterBackup);
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		// no-op because Portlets are forbidden from rendering the <head>...</head> section.
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	protected List<UIComponent> getFirstResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = null;

		UIComponent firstFacet = uiComponent.getFacet(FIRST_FACET);

		if (firstFacet != null) {
			resources = new ArrayList<UIComponent>();
			resources.add(firstFacet);
		}

		return resources;
	}

	protected List<UIComponent> getLastResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = null;

		UIComponent firstFacet = uiComponent.getFacet(LAST_FACET);

		if (firstFacet != null) {
			resources = new ArrayList<UIComponent>();
			resources.add(firstFacet);
		}

		return resources;
	}

	protected List<UIComponent> getMiddleResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = null;

		UIComponent firstFacet = uiComponent.getFacet(MIDDLE_FACET);

		if (firstFacet != null) {
			resources = new ArrayList<UIComponent>();
			resources.add(firstFacet);
		}

		return resources;
	}

	private boolean ableToAddResourceToHead(PortalContext portalContext, UIComponent componentResource) {

		if (isStyleSheetResource(componentResource)) {
			return (portalContext.getProperty(BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT) != null);
		}
		else if (isScriptResource(componentResource)) {
			return (portalContext.getProperty(BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT) != null);
		}
		else if (isInlineStyleSheet(componentResource)) {
			return (portalContext.getProperty(BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT) != null);
		}
		else if (isInlineScript(componentResource)) {
			return (portalContext.getProperty(BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT) != null);
		}
		else {
			return false;
		}
	}

	private boolean isInlineScript(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String rendererType = componentResource.getRendererType();

		return (componentResource instanceof InlineScript) ||
			((resourceName == null) &&
				(RenderKitBridgeImpl.SCRIPT_RENDERER_TYPE.equals(rendererType) ||
					"com.liferay.faces.alloy.component.outputscript.OutputScriptRenderer".equals(rendererType) ||
					"com.liferay.faces.metal.component.outputscript.OutputScriptRenderer".equals(rendererType)));
	}

	private boolean isInlineStyleSheet(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String rendererType = componentResource.getRendererType();

		return (resourceName == null) &&
			(RenderKitBridgeImpl.STYLESHEET_RENDERER_TYPE.equals(rendererType) ||
				"com.liferay.faces.alloy.component.outputstylesheet.OutputStylesheetRenderer".equals(rendererType) ||
				"com.liferay.faces.metal.component.outputstylesheet.OutputStylesheetRenderer".equals(rendererType));
	}

	private boolean isStyleSheetResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("css");
	}
}
