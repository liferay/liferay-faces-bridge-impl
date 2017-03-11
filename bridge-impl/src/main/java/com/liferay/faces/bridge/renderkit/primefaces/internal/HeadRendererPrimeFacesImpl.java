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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.liferay.faces.bridge.component.internal.ResourceComponent;
import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadRendererBridgeImpl;
import com.liferay.faces.bridge.renderkit.html_basic.internal.InlineScript;
import com.liferay.faces.bridge.util.internal.URLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class replaces the PrimeFaces HeadRenderer because it renders a &lt;head&gt;...&lt;/head&gt; element to the
 * response writer which is forbidden in a portlet environment.
 *
 * @author  Neil Griffin
 */
public class HeadRendererPrimeFacesImpl extends HeadRendererBridgeImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadRendererPrimeFacesImpl.class);

	// Private Constants
	private static final String MOBILE_COMPONENT_RESOURCES_KEY = HeadRendererPrimeFacesImpl.class.getName() +
		"_mobileComponentResources";
	private static final String PRIMEFACES_THEME_PREFIX = "primefaces-";
	private static final String PRIMEFACES_THEME_RESOURCE_NAME = "theme.css";

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		UIViewRoot originalUIViewRoot = facesContext.getViewRoot();

		if (isMobile(facesContext)) {

			List<UIComponent> componentResources = originalUIViewRoot.getComponentResources(facesContext, "head");
			List<UIComponent> resourcesToRemove = new ArrayList<UIComponent>();

			for (UIComponent componentResource : componentResources) {

				// Certain resources should not be rendered when PrimeFaces' PRIMEFACES_MOBILE RenderKit is used. For
				// more information, see
				// https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L96-L104.
				if (isComponentResourceSuppressedWhenMobile(componentResource)) {
					componentResource.setRendered(false);
				}

				// Mobile resources must be rendered/loaded before other scripts, so mobile resources must be
				// removed from the UIViewRoot head facet and rendered before other scripts. For more information, see
				// http://demos.jquerymobile.com/1.0/docs/api/globalconfig.html and
				// https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L68-L87.
				else if (isMobileComponentResource(componentResource)) {
					resourcesToRemove.add(componentResource);
				}
			}

			for (UIComponent resourceToRemove : resourcesToRemove) {
				originalUIViewRoot.removeComponentResource(facesContext, resourceToRemove, "head");
			}
		}

		// Invoke the PrimeFaces HeadRenderer so that it has the opportunity to add css and/or script resources to the
		// view root. However, the PrimeFaces HeadRenderer must be captured (and thus prevented from actually rendering
		// any resources) so that they can instead be rendered by the superclass.
		FacesContext primeFacesContext = new FacesContextPrimeFacesHeadImpl(facesContext);
		ResponseWriter origResponseWriter = primeFacesContext.getResponseWriter();
		PrimeFacesHeadResponseWriter primeFacesHeadResponseWriter = new PrimeFacesHeadResponseWriter();
		primeFacesContext.setResponseWriter(primeFacesHeadResponseWriter);

		ResourceCapturingUIViewRoot resourceCapturingUIViewRoot = new ResourceCapturingUIViewRoot();
		primeFacesContext.setViewRoot(resourceCapturingUIViewRoot);

		Renderer primeFacesHeadRenderer = getPrimeFacesHeadRenderer(facesContext);
		primeFacesHeadRenderer.encodeBegin(primeFacesContext, uiComponent);
		primeFacesContext.setViewRoot(originalUIViewRoot);
		primeFacesContext.setResponseWriter(origResponseWriter);

		// Get the list of captured resources.
		List<UIComponent> capturedResources = resourceCapturingUIViewRoot.getCapturedComponentResources("head");
		List<UIComponent> capturedMobileResources = new ArrayList<UIComponent>();

		// The PrimeFaces 5.1+ HeadRenderer properly adds resources like "validation/validation.js" to the view root,
		// which makes it possible to easily capture the resources that it wants to add to the head. However, the
		// PrimeFaces 5.0/4.0 HeadRenderer does not add resources to the view root. Instead, it encodes a <script>
		// element to the response writer with a "src" attribute containing a URL (an external script). When this
		// occurs, it is necessary to reverse-engineer the URL of each external script in order to determine the
		// name/library of the corresponding JSF2 resource.
		List<String> externalResourceURLs = primeFacesHeadResponseWriter.getExternalResourceURLs();

		// For each external script URL:
		if (externalResourceURLs.size() > 0) {

			ExternalContext externalContext = facesContext.getExternalContext();
			String resourceNameParam = externalContext.encodeNamespace("javax.faces.resource");
			String libraryNameParam = externalContext.encodeNamespace("ln");

			for (String externalResourceURL : externalResourceURLs) {

				// Determine the value of the "javax.faces.resource" and "ln" parameters from the URL.
				String resourceName = null;
				String libraryName = null;
				String decodedExternalResourceURL = URLDecoder.decode(externalResourceURL, "UTF-8");
				Map<String, String[]> parsedParameterMapValuesArray = URLUtil.parseParameterMapValuesArray(
						decodedExternalResourceURL);

				if (parsedParameterMapValuesArray != null) {

					String[] resourceNameParamValues = parsedParameterMapValuesArray.get(resourceNameParam);

					if ((resourceNameParamValues == null) || (resourceNameParamValues.length < 1)) {
						resourceNameParamValues = parsedParameterMapValuesArray.get("javax.faces.resource");
					}

					if ((resourceNameParamValues != null) && (resourceNameParamValues.length > 0)) {
						resourceName = resourceNameParamValues[0];
					}

					if (resourceName == null) {

						int indexOfResource = decodedExternalResourceURL.indexOf("javax.faces.resource/");
						int indexOfQuery = decodedExternalResourceURL.indexOf("?");

						if (indexOfResource > -1) {

							int indexOfResourceName = indexOfResource + "javax.faces.resource/".length();

							if (indexOfQuery > -1) {
								resourceName = decodedExternalResourceURL.substring(indexOfResourceName, indexOfQuery);
							}
							else {
								resourceName = decodedExternalResourceURL.substring(indexOfResourceName);
							}
						}
					}

					String[] libraryNameParamValues = parsedParameterMapValuesArray.get(libraryNameParam);

					if ((libraryNameParamValues == null) || (libraryNameParamValues.length < 1)) {
						libraryNameParamValues = parsedParameterMapValuesArray.get("ln");
					}

					if ((libraryNameParamValues != null) && (libraryNameParamValues.length > 0)) {
						libraryName = libraryNameParamValues[0];
					}
				}

				// If the "javax.faces.resource" and "ln" parameters were found, then create the corresponding JSF2
				// resource and add it to the view root.
				if ((resourceName != null) && (libraryName != null)) {

					if (resourceName.equals(PRIMEFACES_THEME_RESOURCE_NAME) &&
							libraryName.startsWith(PRIMEFACES_THEME_PREFIX)) {

						ResourceComponent primefacesThemeResource = new ResourceComponent(facesContext, resourceName,
								libraryName, externalContext.encodeNamespace(""));
						Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
						facesContextAttributes.put("primefacesTheme", primefacesThemeResource);
					}
					else {

						Application application = facesContext.getApplication();
						ResourceHandler resourceHandler = application.getResourceHandler();
						UIComponent resource = application.createComponent(UIOutput.COMPONENT_TYPE);
						String rendererType = resourceHandler.getRendererTypeForResourceName(resourceName);
						resource.setRendererType(rendererType);
						resource.setTransient(true);
						resource.getAttributes().put("name", resourceName);
						resource.getAttributes().put("library", libraryName);
						resource.getAttributes().put("target", "head");

						if (isMobile(facesContext)) {

							if (isMobileComponentResource(resourceName, libraryName)) {
								capturedMobileResources.add(resource);
							}
							else {

								if (isComponentResourceSuppressedWhenMobile(resourceName, libraryName)) {
									resource.setRendered(false);
								}

								capturedResources.add(resource);
							}
						}
						else {
							capturedResources.add(resource);
						}
					}
				}
			}
		}

		// Add each component resources that was captured to the real view root so that they will be rendered by the
		// superclass.
		for (UIComponent componentResource : capturedResources) {
			originalUIViewRoot.addComponentResource(facesContext, componentResource, "head");
		}

		// FACES-2061: If the PrimeFaces HeadRenderer attempted to render an inline script (as is the case when
		// PrimeFaces client side validation is activated) then add a component that can render the script to the view
		// root.
		List<InlineScript> inlineScripts = primeFacesHeadResponseWriter.getInlineScripts();

		if (!inlineScripts.isEmpty()) {

			// If the PrimeFaces Mobile HeadRenderer is being used, the first inline script in the <head> section must
			// be rendered after jQuery.js is rendered/loaded and before other mobile resources. For more information,
			// see http://demos.jquerymobile.com/1.0/docs/api/globalconfig.html and
			// https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L68-L87.
			if (isMobile(facesContext)) {

				InlineScript mobileInlineScript = inlineScripts.remove(0);
				ListIterator<UIComponent> listIterator = capturedMobileResources.listIterator();

				while (listIterator.hasNext()) {

					UIComponent mobileComponentResource = listIterator.next();
					Map<String, Object> attributes = mobileComponentResource.getAttributes();
					String name = (String) attributes.get("name");

					if (name.equals("jquery/jquery.js")) {

						listIterator.add(mobileInlineScript);

						break;
					}
				}
			}

			for (InlineScript primeFacesInlineScript : inlineScripts) {
				originalUIViewRoot.addComponentResource(facesContext, primeFacesInlineScript, "head");
			}
		}

		if (isMobile(facesContext)) {

			// Save captured mobile resources so that they can be rendered in as middle resources before other scripts.
			// For more information, see HeadRendererBridgeImpl.encodeChildren(),
			// http://demos.jquerymobile.com/1.0/docs/api/globalconfig.html, and
			// https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L68-L87.
			Map<Object, Object> attributes = facesContext.getAttributes();
			attributes.put(MOBILE_COMPONENT_RESOURCES_KEY, capturedMobileResources);
		}

		// Delegate rendering to the superclass so that it can write resources found in the view root to the head
		// section of the portal page.
		super.encodeBegin(facesContext, uiComponent);
	}

	@Override
	protected List<UIComponent> getFirstResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> firstResources = super.getFirstResources(facesContext, uiComponent);

		if (firstResources == null) {
			firstResources = new ArrayList<UIComponent>();
		}

		Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
		ResourceComponent primefacesThemeResource = (ResourceComponent) facesContextAttributes.remove(
				"primefacesTheme");

		if (primefacesThemeResource != null) {
			firstResources.add(primefacesThemeResource);
		}

		return firstResources;
	}

	@Override
	protected List<UIComponent> getMiddleResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> middleResources = super.getMiddleResources(facesContext, uiComponent);

		if (isMobile(facesContext)) {

			if (middleResources == null) {
				middleResources = new ArrayList<UIComponent>();
			}

			Map<Object, Object> attributes = facesContext.getAttributes();
			@SuppressWarnings(value = { "unchecked" })
			List<UIComponent> mobileComponentResources = (List<UIComponent>) attributes.remove(
					MOBILE_COMPONENT_RESOURCES_KEY);

			// Add mobile resources to the list of middle resources so that they are rendered before other scripts. For
			// more information, see HeadRendererBridgeImpl.encodeChildren(),
			// http://demos.jquerymobile.com/1.0/docs/api/globalconfig.html, and
			// https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L68-L87.
			middleResources.addAll(mobileComponentResources);
		}

		return middleResources;
	}

	private Renderer getPrimeFacesHeadRenderer(FacesContext facesContext) {

		if (isMobile(facesContext)) {
			return OnDemandPrimeFacesMobileHeadRenderer.instance;
		}
		else {
			return OnDemandPrimeFacesHeadRenderer.instance;
		}
	}

	/**
	 * Returns true if a resource should be suppressed when PrimeFaces' PRIMEFACES_MOBILE RenderKit is used. For more
	 * information, see {@link #isComponentResourceSuppressedWhenMobile(java.lang.String, java.lang.String)}.
	 */
	private boolean isComponentResourceSuppressedWhenMobile(UIComponent componentResource) {

		Map<String, Object> attributes = componentResource.getAttributes();
		String libraryName = (String) attributes.get("library");
		String resourceName = (String) attributes.get("name");

		return isComponentResourceSuppressedWhenMobile(resourceName, libraryName);
	}

	/**
	 * Returns true if a resource should be suppressed when PrimeFaces' PRIMEFACES_MOBILE RenderKit is used. The
	 * criteria for which resources should be suppressed was obtained from here:
	 * https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L96-L104.
	 */
	private boolean isComponentResourceSuppressedWhenMobile(String resourceName, String libraryName) {
		return !isMobileComponentResource(resourceName, libraryName) && "primefaces".equals(libraryName) &&
			(resourceName.startsWith("jquery") || resourceName.startsWith("primefaces") ||
				resourceName.startsWith("components") || resourceName.startsWith("core"));
	}

	private boolean isMobile(FacesContext facesContext) {

		String renderKitId;
		UIViewRoot uiViewRoot = facesContext.getViewRoot();

		if (uiViewRoot != null) {
			renderKitId = uiViewRoot.getRenderKitId();
		}
		else {

			Application application = facesContext.getApplication();
			ViewHandler viewHandler = application.getViewHandler();
			renderKitId = viewHandler.calculateRenderKitId(facesContext);
		}

		return "PRIMEFACES_MOBILE".equals(renderKitId);
	}

	/**
	 * Returns true if a resource must be rendered in a certain place when PrimeFaces' PRIMEFACES_MOBILE RenderKit is
	 * used. For more information, see {@link #isMobileComponentResource(javax.faces.component.UIComponent)}.
	 */
	private boolean isMobileComponentResource(UIComponent componentResource) {

		Map<String, Object> attributes = componentResource.getAttributes();
		String libraryName = (String) attributes.get("library");
		String resourceName = (String) attributes.get("name");

		return isMobileComponentResource(resourceName, libraryName);
	}

	/**
	 * Returns true if a resource must be rendered in a certain place when PrimeFaces' PRIMEFACES_MOBILE RenderKit is
	 * used. The list of resources was obtained from here:
	 * https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L68-L87.
	 */
	private boolean isMobileComponentResource(String resourceName, String libraryName) {

		return "primefaces".equals(libraryName) &&
			("jquery/jquery.js".equals(resourceName) || "mobile/jquery-mobile.js".equals(resourceName) ||
				"core.js".equals(resourceName) || "components-mobile.js".equals(resourceName));
	}

	private static class OnDemandPrimeFacesHeadRenderer {

		// Since this class is not referenced until HeadRendererPrimeFacesImpl.getPrimeFacesHeadRenderer() is called,
		// the primeFacesHeadRenderer instance will be lazily initialized when
		// HeadRendererPrimeFacesImpl.getPrimeFacesHeadRenderer() is called and HeadRendererPrimeFacesImpl.isMobile() is
		// false. Class initialization is thread-safe. For more details on this pattern, see
		// http://stackoverflow.com/questions/7420504/threading-lazy-initialization-vs-static-lazy-initialization.
		private static final Renderer instance;

		static {

			Renderer primeFacesHeadRenderer = null;

			try {
				Class<?> headRendererClass = Class.forName("org.primefaces.renderkit.HeadRenderer");
				primeFacesHeadRenderer = (Renderer) headRendererClass.newInstance();
			}
			catch (Exception e) {
				logger.error(e);
			}

			instance = primeFacesHeadRenderer;
		}
	}

	private static class OnDemandPrimeFacesMobileHeadRenderer {

		// Since this class is not referenced until HeadRendererPrimeFacesImpl.getPrimeFacesHeadRenderer() is called,
		// the primeFacesMobileHeadRenderer instance will be lazily initialized when
		// HeadRendererPrimeFacesImpl.getPrimeFacesHeadRenderer() is called and HeadRendererPrimeFacesImpl.isMobile() is
		// true. Class initialization is thread-safe. For more details on this pattern, see
		// http://stackoverflow.com/questions/7420504/threading-lazy-initialization-vs-static-lazy-initialization.
		private static final Renderer instance;

		static {

			Renderer primeFacesMobileHeadRenderer = null;

			try {
				Class<?> headRendererClass = Class.forName("org.primefaces.mobile.renderkit.HeadRenderer");
				primeFacesMobileHeadRenderer = (Renderer) headRendererClass.newInstance();
			}
			catch (Exception e) {
				logger.error(e);
			}

			instance = primeFacesMobileHeadRenderer;
		}
	}
}
