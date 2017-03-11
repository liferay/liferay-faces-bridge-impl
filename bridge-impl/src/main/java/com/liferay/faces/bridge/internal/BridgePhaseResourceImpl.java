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
package com.liferay.faces.bridge.internal;

import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.filter.BridgePortletRequestFactory;
import javax.portlet.faces.filter.BridgePortletResponseFactory;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.util.application.ResourceValidator;
import com.liferay.faces.util.application.ResourceValidatorFactory;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.config.FacesConfig;
import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePhaseResourceImpl extends BridgePhaseCompat_2_2_Impl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseResourceImpl.class);

	// Private Constants
	private static final boolean LIFERAY_PORTAL_DETECTED = ProductFactory.getProduct(Product.Name.LIFERAY_PORTAL)
		.isDetected();
	private static final String[] URL_SEPARATOR_CHARS = new String[] { "?", "#", ";" };

	// Private Data Members
	private ResourceRequest resourceRequest;
	private ResourceResponse resourceResponse;

	public BridgePhaseResourceImpl(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		super(portletConfig, bridgeConfig);

		this.resourceRequest = BridgePortletRequestFactory.getResourceRequestInstance(resourceRequest, resourceResponse,
				portletConfig, bridgeConfig);

		this.resourceResponse = BridgePortletResponseFactory.getResourceResponseInstance(resourceRequest,
				resourceResponse, portletConfig, bridgeConfig);
	}

	@Override
	public void execute() throws BridgeDefaultViewNotSpecifiedException, BridgeException {

		logger.debug(Logger.SEPARATOR);
		logger.debug("execute(ResourceRequest, ResourceResponse) portletName=[{0}]", portletName);

		try {

			init(resourceRequest, resourceResponse, Bridge.PortletPhase.RESOURCE_PHASE);

			// If the Faces resource handler indicates that this is a request for an image/javascript/css type of
			// resource, then
			if (isJSF2ResourceRequest(facesContext)) {

				logger.debug("Detected JSF2 resource request");

				// Ask the Faces resource handler to copy the contents of the resource to the response.
				handleJSF2ResourceRequest(facesContext);
			}
			else if ((resourceRequest.getResourceID() != null) && !resourceRequest.getResourceID().equals("wsrp")) {

				logger.debug("Detected non-Faces resource");

				String resourceId = resourceRequest.getResourceID();
				String autoResourceDispatch = portletConfig.getInitParameter(
						"javax.portlet.automaticResourceDispatching");

				if ((autoResourceDispatch != null) && autoResourceDispatch.equalsIgnoreCase("true")) {

					ResourceValidator resourceValidator = ResourceValidatorFactory.getResourceValidatorInstance();

					// If the resourceId contains a banned path like WEB-INF or META-INF, then do not serve the
					// resource.
					if (resourceValidator.containsBannedPath(resourceId)) {

						// Simulate Liferay Portal's behavior for containers like Pluto
						logger.warn("Invalid request for resource with banned path: resourceId=[{0}]", resourceId);
						resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
							Integer.toString(HttpServletResponse.SC_OK));
					}

					// Otherwise, if the resourceId contains a banned sequence like double slashes, etc. then do not
					// serve the resource.
					else if (resourceValidator.isBannedSequence(resourceId)) {

						logger.warn("Invalid request for resource with banned sequence: resourceId=[{0}]", resourceId);
						resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
							Integer.toString(HttpServletResponse.SC_NOT_FOUND));
					}

					// Otherwise, if the resourceId targets a Facelet document, then do not serve the resource.
					else if (resourceValidator.isFaceletDocument(facesContext, resourceId)) {
						logger.warn("Invalid request for Facelet document: resourceId=[{0}]", resourceId);
						resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
							Integer.toString(HttpServletResponse.SC_NOT_FOUND));
					}

					// Otherwise,
					else {

						// Sanitize the resource path by removing special characters that indicate URL fragments, URL
						// query-strings, etc.
						String resourcePath = resourceId;

						for (String urlSeparatorChar : URL_SEPARATOR_CHARS) {

							int pos = resourcePath.indexOf(urlSeparatorChar);

							if (pos > 0) {
								resourcePath = resourcePath.substring(0, pos);
							}
						}

						// If the resource path is empty, then log a warning. When running in Liferay Portal, this
						// likely indicates a condition in which the portlet.resource.id.banned.paths.regexp property
						// has been enforced.
						if (resourcePath.trim().length() == 0) {

							if (LIFERAY_PORTAL_DETECTED) {

								logger.warn(
									"Invalid request for resourceId=[] possibly due to Liferay Portal enforcing the portlet.resource.id.banned.paths.regexp property.");
							}
							else {
								logger.warn("Invalid request for resourceId=[].");
								resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
									Integer.toString(HttpServletResponse.SC_NOT_FOUND));
							}
						}

						// Otherwise,
						else {

							// If the resourceId is self-referencing, meaning it targets the servlet-mapping of the
							// portlet, then do not serve the resource.
							if (resourceValidator.isSelfReferencing(facesContext, resourcePath)) {

								logger.warn("Invalid request for resource that is self-referencing: resourceId=[{0}]",
									resourceId);
								resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
									Integer.toString(HttpServletResponse.SC_NOT_FOUND));
							}

							// Otherwise,
							else {

								// If the resourceId maps to the FacesServlet, then do not serve the resource.
								boolean mappedToFacesServlet = false;
								ConfiguredServletMapping explicitFacesServletExtensionMapping =
									getExplicitFacesServletExtensionMapping(resourcePath);

								if (explicitFacesServletExtensionMapping != null) {

									logger.warn(
										"Invalid request for resource that is EXPLICITLY extension-mapped to the FacesServlet: resourceId=[{0}] resourcePath=[{1}] servlet-mapping extension=[{2}]",
										resourceId, resourcePath, explicitFacesServletExtensionMapping.getExtension());
									mappedToFacesServlet = true;

									resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
										Integer.toString(HttpServletResponse.SC_NOT_FOUND));
								}
								else {
									ConfiguredServletMapping facesServletPathMapping = getFacesServletPathMapping(
											resourceId);

									if (facesServletPathMapping != null) {

										logger.warn(
											"Invalid request for resource that is path-mapped to the FacesServlet: resourceId=[{0}] resourcePath=[{1}] servlet-mapping url-pattern=[{2}]",
											resourceId, resourcePath, facesServletPathMapping.getUrlPattern());

										mappedToFacesServlet = true;

										resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
											Integer.toString(HttpServletResponse.SC_NOT_FOUND));
									}
								}

								// Otherwise, attempt to serve the resource.
								if (!mappedToFacesServlet) {
									PortletRequestDispatcher portletRequestDispatcher =
										portletContext.getRequestDispatcher(resourceId);

									if (portletRequestDispatcher != null) {
										portletRequestDispatcher.forward(resourceRequest, resourceResponse);
									}
									else {
										logger.warn(
											"Request for non-Faces resource=[{0}] but request dispatcher was null.",
											resourceId);
										resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE,
											Integer.toString(HttpServletResponse.SC_NOT_FOUND));
									}
								}
							}
						}
					}
				}
				else {
					logger.warn("Request for non-Faces resource=[{0}] but automatic dispatching is disabled.",
						resourceId);
				}
			}

			// Otherwise, must be an Ajax (partial-submit) request. Though technically a postback type of request,
			// Ajax requests also utilize the portlet RESOURCE_PHASE. Therefore treat it like a postback, and
			// execute the entire Faces lifecycle: RESTORE_VIEW, APPLY_REQUEST_VALUES, PROCESS_VALIDATIONS,
			// UPDATE_MODEL, INVOKE_APPLICATION.
			else {

				ExternalContext externalContext = facesContext.getExternalContext();

				if (logger.isDebugEnabled()) {

					String facesAjaxParameter = externalContext.getRequestParameterMap().get(
							BridgeExt.FACES_AJAX_PARAMETER);

					if (BooleanHelper.isTrueToken(facesAjaxParameter)) {
						logger.debug("Detected Ajax ResourceRequest");
					}
					else {
						logger.debug("Detected Non-Ajax ResourceRequest");
					}
				}

				String viewId = getFacesViewId(externalContext);
				logger.debug("Running Faces lifecycle for viewId=[{0}]", viewId);

				// Attach the JSF 2.2 client window to the JSF lifecycle so that Faces Flows can be utilized.
				attachClientWindowToLifecycle(facesContext, facesLifecycle);

				// Execute the JSF lifecycle.
				facesLifecycle.execute(facesContext);

				// Also execute the RENDER_RESPONSE phase of the Faces lifecycle, which will ultimately return a
				// DOM-update back to the jsf.js Javascript code that issued the XmlHttpRequest in the first place.
				facesLifecycle.render(facesContext);

				// The default behavior of Liferay Faces Bridge is to not manage the BridgeRequestScope during the
				// RESOURCE_PHASE of the portlet lifecycle. But if the developer has enabled it via the
				// "com.liferay.faces.bridge.bridgeRequestScopeAjaxEnabled" configuration parameter, then
				if (bridgeRequestScope != null) {

					// PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-202
					bridgeRequestScope.setPortletMode(resourceRequest.getPortletMode());

					// TCK TestPage071: nonFacesResourceTest
					// TCK TestPage073: scopeAfterRedisplayResourcePPRTest -- Preserve the non-excluded request
					// attributes in the BridgeRequestScope so that they can be restored in subsequent render requests.
					bridgeRequestScope.saveState(facesContext);
					maintainBridgeRequestScope(resourceRequest, resourceResponse,
						BridgeRequestScope.Transport.PORTLET_SESSION_ATTRIBUTE);
				}

				// Spec 6.6 (Namespacing)
				indicateNamespacingToConsumers(facesContext.getViewRoot(), resourceResponse);
			}
		}
		catch (Throwable t) {
			throw new BridgeException(t);
		}
		finally {
			cleanup(resourceRequest);
		}

		logger.debug(Logger.SEPARATOR);
	}

	protected List<ConfiguredServletMapping> getConfiguredFacesServletMappings() {

		String appConfigAttrName = ApplicationConfig.class.getName();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> applicationMap = externalContext.getApplicationMap();
		ApplicationConfig applicationConfig = (ApplicationConfig) applicationMap.get(appConfigAttrName);
		FacesConfig facesConfig = applicationConfig.getFacesConfig();

		return facesConfig.getConfiguredFacesServletMappings();
	}

	protected ConfiguredServletMapping getExplicitFacesServletExtensionMapping(String resourceId) {

		ConfiguredServletMapping explicitFacesServletExtensionMapping = null;
		List<ConfiguredServletMapping> facesServletMappings = getConfiguredFacesServletMappings();

		for (ConfiguredServletMapping facesServletMapping : facesServletMappings) {

			if (facesServletMapping.isMatch(resourceId) && facesServletMapping.isExtensionMapped() &&
					!facesServletMapping.isImplicit()) {

				explicitFacesServletExtensionMapping = facesServletMapping;

				break;
			}
		}

		return explicitFacesServletExtensionMapping;
	}

	protected ConfiguredServletMapping getFacesServletPathMapping(String resourceId) {

		ConfiguredServletMapping facesServletPathMapping = null;
		List<ConfiguredServletMapping> facesServletMappings = getConfiguredFacesServletMappings();

		for (ConfiguredServletMapping facesServletMapping : facesServletMappings) {

			if (facesServletMapping.isMatch(resourceId) && facesServletMapping.isPathMapped()) {

				facesServletPathMapping = facesServletMapping;

				break;
			}
		}

		return facesServletPathMapping;
	}
}
