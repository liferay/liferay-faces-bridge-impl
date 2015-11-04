/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.HeadResponseWriter;
import com.liferay.faces.bridge.context.HeadResponseWriterFactory;
import com.liferay.faces.util.application.ComponentResource;
import com.liferay.faces.util.application.ComponentResourceFactory;
import com.liferay.faces.util.application.ComponentResourceUtil;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
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

	// Private Constants
	private static final String ADDED = UIComponentBase.class.getName() + ".ADDED";
	private static final String EXTENSION_CSS = "css";
	private static final String FIRST_FACET = "first";
	private static final String MIDDLE_FACET = "middle";
	private static final String LAST_FACET = "last";
	private static final String RENDERER_TYPE_RICHFACES_RESOURCE_LIBRARY =
		"org.richfaces.renderkit.ResourceLibraryRenderer";
	private static final String RICHFACES_RESLIB_SUFFIX = "reslib";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadRendererBridgeImpl.class);

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		// no-op because Portlets are forbidden from rendering the <head>...</head> section.
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		// Build up a list of components that are intended for the <head> section of the portal page.
		PortletNamingContainerUIViewRoot uiViewRoot = (PortletNamingContainerUIViewRoot) facesContext.getViewRoot();
		List<UIComponent> uiComponentResources = new ArrayList<UIComponent>();

		// Add the list of components that are to appear first.
		List<UIComponent> firstResources = getFirstResources(facesContext, uiComponent);

		if (firstResources != null) {
			uiComponentResources.addAll(firstResources);
		}

		// Sort the components that are in the view root into stylesheets and scripts.
		List<UIComponent> uiViewRootComponentResources = uiViewRoot.getComponentResources(facesContext, "head");
		List<UIComponent> uiViewRootStyleSheetResources = null;
		List<UIComponent> uiViewRootScriptResources = null;

		for (UIComponent curComponent : uiViewRootComponentResources) {
			String resourceName = (String) curComponent.getAttributes().get("name");

			if ((resourceName != null) && resourceName.endsWith(EXTENSION_CSS)) {

				if (uiViewRootStyleSheetResources == null) {
					uiViewRootStyleSheetResources = new ArrayList<UIComponent>();
				}

				uiViewRootStyleSheetResources.add(curComponent);
			}
			else {

				if (uiViewRootScriptResources == null) {
					uiViewRootScriptResources = new ArrayList<UIComponent>();
				}

				uiViewRootScriptResources.add(curComponent);
			}
		}

		// Add the list of stylesheet components that are in the view root.
		if (uiViewRootStyleSheetResources != null) {
			uiComponentResources.addAll(uiViewRootStyleSheetResources);
		}

		// Add the list of components that are to appear in the middle.
		List<UIComponent> middleResources = getMiddleResources(facesContext, uiComponent);

		if (middleResources != null) {
			uiComponentResources.addAll(middleResources);
		}

		// Add the list of script components that are in the view root.
		if (uiViewRootScriptResources != null) {
			uiComponentResources.addAll(uiViewRootScriptResources);
		}

		// Add the list of components that are to appear last.
		List<UIComponent> lastResources = getLastResources(facesContext, uiComponent);

		if (lastResources != null) {
			uiComponentResources.addAll(lastResources);
		}

		// Initializations
		boolean ajaxRequest = facesContext.getPartialViewContext().isAjaxRequest();
		List<UIComponent> resourcesForAddingToHead = new ArrayList<UIComponent>();
		List<UIComponent> resourcesForRelocatingToBody = new ArrayList<UIComponent>();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		BridgeContext bridgeContext = BridgeContext.getCurrentInstance();

		// Determine whether or not the portlet container is able to add script resources to the head.
		PortalContext portalContext = portletRequest.getPortalContext();
		String addScriptResourceToHeadSupport = portalContext.getProperty(
				BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT);
		boolean portletContainerAbleToAddScriptResourceToHead = (addScriptResourceToHeadSupport != null);

		// Determine whether or not this might be a Liferay runtime portlet (which does not have the ability to add
		// script resources to the head).
		Boolean renderPortletResource = (Boolean) portletRequest.getAttribute("RENDER_PORTLET_RESOURCE");
		boolean liferayRuntimePortlet = (renderPortletResource != null) && renderPortletResource.booleanValue();

		// Note: The HeadManagedBean is a ViewScoped manage-bean that keeps a list of resources that have been added to
		// the <head> section of the portal page. Note that the HeadManagedBean will be null in a JSP context since
		// there is no h:head JSP component tag in JSF 2.x.
		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);

		Set<String> headResourceIdsFromManagedBean;

		if (headManagedBean == null) {
			headResourceIdsFromManagedBean = new HashSet<String>();
		}
		else {
			headResourceIdsFromManagedBean = headManagedBean.getHeadResourceIds();
		}

		ComponentResourceFactory componentResourceFactory = (ComponentResourceFactory) FactoryExtensionFinder
			.getFactory(ComponentResourceFactory.class);

		// For each resource in the ViewRoot: Determine if it should added to the <head> section of the portal page,
		// or if it should be relocated to the body (which is actually not a <body> element, but a <div> element
		// rendered by the bridge's BodyRenderer).
		for (UIComponent uiComponentResource : uiComponentResources) {

			// If this is taking place during an Ajax request or this is a Liferay runtime portlet, then
			if (ajaxRequest || liferayRuntimePortlet) {

				// Determine whether or not the resource is already present in the <head> section of the portal page.
				// Note that this can happen in one of two ways: 1) If this is NON-Liferay-Runtime portlet (currently
				// doing Ajax) but has already added the resource during initial page HTTP-GET render, or 2) By another
				// NON-Liferay-Runtime portlet that has already added the same JavaScript resource.
				ComponentResource componentResource = componentResourceFactory.getComponentResource(
						uiComponentResource);
				boolean alreadyPresentInPortalPageHead = headResourceIdsFromManagedBean.contains(
						componentResource.getId());

				// If the resource is already present in the <head> section of the portal page, then simply output a
				// logger message to this fact.
				if (alreadyPresentInPortalPageHead) {

					if (logger.isDebugEnabled()) {

						logger.debug(
							"Resource already present in head: name=[{0}] library=[{1}] rendererType=[{2}] value=[{3}] className=[{4}]",
							new Object[] {
								componentResource.getName(), componentResource.getLibrary(),
								uiComponentResource.getRendererType(),
								ComponentResourceUtil.getComponentValue(uiComponentResource),
								uiComponentResource.getClass().getName(),
							});
					}
				}

				// Otherwise, since it is not possible to add it to the <head> section, the resource must be relocated
				// to the body.
				else {
					logger.debug(
						"Relocating resource to body (since it was added via Ajax and is not yet present in head): name=[{0}] library=[{1}] rendererType=[{2}] value=[{3}] className=[{4}]",
						new Object[] {
							componentResource.getName(), componentResource.getLibrary(),
							uiComponentResource.getRendererType(),
							ComponentResourceUtil.getComponentValue(uiComponentResource),
							uiComponentResource.getClass().getName(),
						});

					resourcesForRelocatingToBody.add(uiComponentResource);
				}
			}

			// Otherwise, if the portlet container has the ability to add resources to the <head> section of the
			// portal page, then add it to the list of resources that are to be added to the <head> section.
			else if (portletContainerAbleToAddScriptResourceToHead) {
				resourcesForAddingToHead.add(uiComponentResource);
			}

			// Otherwise, we have no choice but to add it to the list of resources that are to be relocated to
			// the body.
			else {
				resourcesForRelocatingToBody.add(uiComponentResource);
			}
		}

		// If the portlet container has the ability to add resources to the <head> section of the portal page, then
		if (portletContainerAbleToAddScriptResourceToHead) {

			// Save a temporary reference to the ResponseWriter provided by the FacesContext.
			ResponseWriter responseWriterBackup = facesContext.getResponseWriter();

			// Replace the ResponseWriter in the FacesContext with a HeadResponseWriter that knows how to write to
			// the <head>...</head> section of the rendered portal page.
			HeadResponseWriter headResponseWriter = (HeadResponseWriter) portletRequest.getAttribute(
					HeadResponseWriter.class.getName());

			if (headResponseWriter == null) {
				HeadResponseWriterFactory headResponseWriterFactory = (HeadResponseWriterFactory) BridgeFactoryFinder
					.getFactory(HeadResponseWriterFactory.class);
				headResponseWriter = headResponseWriterFactory.getHeadResponseWriter(bridgeContext,
						responseWriterBackup);
			}

			portletRequest.setAttribute(HeadResponseWriter.class.getName(), headResponseWriter);
			facesContext.setResponseWriter(headResponseWriter);

			// For each resource:
			for (UIComponent uiComponentResource : resourcesForAddingToHead) {

				ComponentResource componentResource = componentResourceFactory.getComponentResource(
						uiComponentResource);

				// Command the resource to render itself to the HeadResponseWriter
				if (componentResource.isRenderable()) {
					uiComponentResource.encodeAll(facesContext);
				}

				String resourceId = componentResource.getId();

				// If the resource has not yet been marked as having been added, then mark it now. Note that unless the
				// resource is a RichFaces Resource Library (see comments below), the resource has probably already been
				// marked as being in the head by ResourceRendererBridgeImpl#encodeEnd(FacesContext, UIComponent).
				if (!headResourceIdsFromManagedBean.contains(resourceId)) {

					headResourceIdsFromManagedBean.add(resourceId);

					if (logger.isDebugEnabled()) {

						if (resourceId.endsWith(RICHFACES_RESLIB_SUFFIX) ||
								RENDERER_TYPE_RICHFACES_RESOURCE_LIBRARY.equals(
									uiComponentResource.getRendererType())) {

							// RichFaces has resources like "org.richfaces:base-component.reslib",
							// "org.richfaces:message.reslib", and "org.richfaces:ajax.reslib" that represent a
							// collection of resources.
							logger.debug("Marking RichFaces resource library [{0}] as being present in the head",
								resourceId);
						}
						else {
							logger.debug("Marking non-RichFaces resourceId=[{0}] as being present in the head",
								resourceId);
						}
					}
				}

			}

			super.encodeChildren(facesContext, uiComponent);

			// Restore the temporary ResponseWriter reference.
			facesContext.setResponseWriter(responseWriterBackup);
		}

		// Relocate resources to the body if necessary. Note that the "ADDED" attribute has to be set to true
		// in order to prevent events from firing during the relocation process.
		for (UIComponent uiComponentResource : resourcesForRelocatingToBody) {

			uiComponentResource.getAttributes().put(RenderKitBridgeImpl.ORIGINAL_TARGET, "head");
			uiComponentResource.getAttributes().put(ADDED, Boolean.TRUE);
			uiViewRoot.addComponentResource(facesContext, uiComponentResource, "body");

			if (logger.isDebugEnabled()) {
				ComponentResource componentResource = componentResourceFactory.getComponentResource(
						uiComponentResource);

				logger.debug(
					"Relocating resource to body: name=[{0}] library=[{1}] rendererType=[{2}] value=[{3}] className=[{4}]",
					new Object[] {
						componentResource.getName(), componentResource.getLibrary(),
						uiComponentResource.getRendererType(),
						ComponentResourceUtil.getComponentValue(uiComponentResource),
						uiComponentResource.getClass().getName(),
					});
			}
		}

	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		// no-op because Portlets are forbidden from rendering the <head>...</head> section.
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

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
