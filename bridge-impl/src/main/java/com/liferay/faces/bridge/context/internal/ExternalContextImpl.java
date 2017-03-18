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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeInvalidViewPathException;
import javax.portlet.faces.BridgeURL;
import javax.portlet.faces.GenericFacesPortlet;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.application.internal.BridgeNavigationUtil;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.map.internal.ContextMapFactory;
import com.liferay.faces.bridge.context.map.internal.RequestHeaderMap;
import com.liferay.faces.bridge.context.map.internal.RequestHeaderValuesMap;
import com.liferay.faces.bridge.filter.internal.HttpServletResponseHeaderAdapter;
import com.liferay.faces.bridge.filter.internal.HttpServletResponseResourceAdapter;
import com.liferay.faces.bridge.internal.BridgeExt;
import com.liferay.faces.bridge.internal.BridgeURI;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.util.internal.LocaleIterator;
import com.liferay.faces.bridge.util.internal.ViewUtil;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Neil Griffin
 */
public class ExternalContextImpl extends ExternalContextCompat_Portlet3_Impl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ExternalContextImpl.class);

	// Private Constants
	private static final String DIRECT_LINK_EQUALS_TRUE = Bridge.DIRECT_LINK + "=true";
	private static final String ORG_RICHFACES_EXTENSION = "org.richfaces.extension";
	private static final String REQUEST_ATTR_PORTLET_REQUEST = "javax.portlet.request";
	private static final String REQUEST_ATTR_QUERY_STRING = "javax.servlet.forward.query_string";
	private static final boolean RICHFACES_DETECTED = ProductFactory.getProduct(Product.Name.RICHFACES).isDetected();

	// Pre-initialized Data Members
	private Map<String, Object> applicationMap;
	private ContextMapFactory contextMapFactory;
	private Map<String, Object> requestAttributeMap;
	private String requestContextPath;
	private Map<String, Object> sessionMap;

	// Lazily-Initialized Data Members
	private String authType;
	private String defaultRenderKitId;
	private Map<String, String> defaultViewIdMap;
	private FacesView facesView;
	private Map<String, String> initParameterMap;
	private String remoteUser;
	private Map<String, Object> requestCookieMap;
	private Locale requestLocale;
	private Map<String, String> requestHeaderMap;
	private Map<String, String[]> requestHeaderValuesMap;
	private Map<String, String> requestParameterMap;
	private Map<String, String[]> requestParameterValuesMap;
	private StringWrapper requestPathInfo;
	private String requestServletPath;
	private Principal userPrincipal;
	private String viewIdAndQueryString;

	public ExternalContextImpl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		super(portletContext, portletRequest, portletResponse);

		this.contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(portletContext,
				ContextMapFactory.class);

		preInitializeObjects();

		logger.debug("User-Agent requested URL=[{0}]", getRequestURL());
	}

	@Override
	public void dispatch(String path) throws IOException {

		logger.debug("Acquiring dispatcher for JSP path=[{0}]", path);

		PortletRequestDispatcher portletRequestDispacher = portletContext.getRequestDispatcher(path);

		try {

			if (portletRequestDispacher != null) {

				// If a render-redirect has occurred after dispatching to a JSP, that means that the previous
				// dispatch called PortletRequestDispatcher#forward(String) which marked the response as "complete",
				// thereby making it impossible to forward again. In such cases, need to "include" instead of
				// "forward".
				Boolean renderRedirectAfterDispatch = (Boolean) portletRequest.getAttribute(
						BridgeExt.RENDER_REDIRECT_AFTER_DISPATCH);

				if ((renderRedirectAfterDispatch != null) && renderRedirectAfterDispatch) {
					portletRequestDispacher.include(portletRequest, portletResponse);
				}

				// Otherwise,
				else {

					// If running in the RESOURCE_PHASE of the portlet lifecycle, then need to "include" instead of
					// "forward" or else the markup will not be properly rendered to the ResourceResponse.
					if (portletPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
						portletRequestDispacher.include(portletRequest, portletResponse);
					}

					// Otherwise, "forward" to the specified path.
					else {
						portletRequestDispacher.forward(portletRequest, portletResponse);
					}
				}
			}
			else {
				throw new IOException("Unable to acquire PortletRequestDispatcher for path=[" + path + "]");
			}
		}
		catch (PortletException e) {
			logger.error(e);
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * @see  ExternalContext#encodeNamespace(String)
	 */
	@Override
	public String encodeNamespace(String name) {

		if (name == null) {
			return portletResponse.getNamespace();
		}
		else if (RICHFACES_DETECTED && (name.equals(ORG_RICHFACES_EXTENSION))) {

			// http://issues.liferay.com/browse/FACES-1416
			return name;
		}
		else {
			return portletResponse.getNamespace() + name;
		}
	}

	/**
	 * @see  ExternalContext#encodeResourceURL(String)
	 */
	@Override
	public String encodeResourceURL(String url) {

		if (url == null) {
			throw new NullPointerException();
		}
		else {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			try {
				BridgeURL encodedResourceURL = bridgeURLFactory.getBridgeResourceURL(facesContext, url);

				return encodedResourceURL.toString();
			}
			catch (BridgeException e) {
				throw new FacesException(e);
			}
		}
	}

	@Override
	public Map<String, Object> getApplicationMap() {
		return applicationMap;
	}

	@Override
	public String getAuthType() {

		if (authType == null) {
			authType = portletRequest.getAuthType();
		}

		return authType;
	}

	@Override
	public Object getContext() {
		return portletContext;
	}

	/**
	 * NOTE: PROPOSE-FOR-BRIDGE3-API Returns the value of the specified initialization parameter. If found, return the
	 * value of the {@link javax.portlet.PortletConfig#getInitParameter(String)} method. Otherwise, return the value of
	 * the {@link PortletContext#getInitParameter(String)} method. This provides a way for init-param values found in
	 * the WEB-INF/portlet.xml descriptor to override context-param values found in the WEB-INF/web.xml descriptor.
	 */
	@Override
	public String getInitParameter(String name) {

		String initParameter = portletConfig.getInitParameter(name);

		if (initParameter == null) {
			initParameter = portletContext.getInitParameter(name);
		}

		return initParameter;
	}

	@Override
	public Map<String, String> getInitParameterMap() {
		return initParameterMap;
	}

	@Override
	public String getRemoteUser() {

		if (remoteUser == null) {
			remoteUser = portletRequest.getRemoteUser();
		}

		return remoteUser;
	}

	@Override
	public Object getRequest() {
		return portletRequest;
	}

	@Override
	public String getRequestCharacterEncoding() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;
			String requestCharacterEncoding = clientDataRequest.getCharacterEncoding();

			if (manageIncongruities) {

				try {
					incongruityContext.setRequestCharacterEncoding(requestCharacterEncoding);
				}
				catch (Exception e) {
					logger.error(e);
				}
			}

			return requestCharacterEncoding;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getRequestCharacterEncoding();
			}
			else {

				// The Mojarra 2.x {@link MultiViewHandler#initView(FacesContext)} method expects a null value to be
				// returned, so throwing an IllegalStateException is not an option.
				return null;
			}
		}
	}

	@Override
	public String getRequestContentType() {

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			// If using ICEfaces 3.0.x/2.0.x then need to return the legacy value.
			// http://issues.liferay.com/browse/FACES-1228
			String requestContentType;

			if (isICEfacesLegacyMode(clientDataRequest)) {
				requestContentType = clientDataRequest.getResponseContentType();
			}
			else {
				requestContentType = clientDataRequest.getContentType();
			}

			if (manageIncongruities) {
				incongruityContext.setRequestContentType(requestContentType);
			}

			return requestContentType;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getRequestContentType();
			}
			else {

				// TestPage166: getRequestContentTypeEventTest expects this condition to return null so throwing an
				// IllegalStateException is not an option.
				return null;
			}
		}
	}

	@Override
	public String getRequestContextPath() {
		return requestContextPath;
	}

	@Override
	public Map<String, Object> getRequestCookieMap() {

		if (requestCookieMap == null) {
			requestCookieMap = contextMapFactory.getRequestCookieMap(portletRequest);
		}

		return requestCookieMap;
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {

		if (requestHeaderMap == null) {
			requestHeaderMap = Collections.unmodifiableMap(new RequestHeaderMap(getRequestHeaderValuesMap()));
		}

		return requestHeaderMap;
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {

		if (requestHeaderValuesMap == null) {
			requestHeaderValuesMap = Collections.unmodifiableMap(new RequestHeaderValuesMap(portletRequest));
		}

		return requestHeaderValuesMap;
	}

	@Override
	public Locale getRequestLocale() {

		if (requestLocale == null) {
			requestLocale = portletRequest.getLocale();
		}

		return requestLocale;
	}

	@Override
	public Iterator<Locale> getRequestLocales() {
		return new LocaleIterator(portletRequest.getLocales());
	}

	@Override
	public Map<String, Object> getRequestMap() {
		return requestAttributeMap;
	}

	@Override
	public Map<String, String> getRequestParameterMap() {

		if (requestParameterMap == null) {
			PortletRequest portletRequest = (PortletRequest) getRequest();
			PortletResponse portletResponse = (PortletResponse) getResponse();
			String responseNamespace = portletResponse.getNamespace();
			String facesViewQueryString = getFacesView().getQueryString();

			requestParameterMap = contextMapFactory.getRequestParameterMap(portletRequest, responseNamespace,
					portletConfig, bridgeRequestScope, defaultRenderKitId, facesViewQueryString);
		}

		return requestParameterMap;
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		return getRequestParameterMap().keySet().iterator();
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {

		if (requestParameterValuesMap == null) {
			PortletRequest portletRequest = (PortletRequest) getRequest();
			PortletResponse portletResponse = (PortletResponse) getResponse();
			String responseNamespace = portletResponse.getNamespace();
			String facesViewQueryString = getFacesView().getQueryString();

			requestParameterValuesMap = contextMapFactory.getRequestParameterValuesMap(portletRequest,
					responseNamespace, portletConfig, bridgeRequestScope, defaultRenderKitId, facesViewQueryString);
		}

		return requestParameterValuesMap;
	}

	/**
	 * This method returns the relative path to the viewId that is to be rendered.
	 *
	 * @see  javax.faces.context.ExternalContext#getRequestPathInfo()
	 */
	@Override
	public String getRequestPathInfo() {

		String returnValue = null;

		if (requestPathInfo == null) {

			FacesView facesView = getFacesView();
			String viewId = facesView.getViewId();

			// If the facesView is extension-mapped (like *.faces), then return a null value as required by Section
			// 6.1.3.1 of the spec.
			if (facesView.isExtensionMapped()) {
				logger.debug("requestPathInfo=[null] EXTENSION=[{1}] viewId=[{2}]", facesView.getExtension(), viewId);
			}

			// Otherwise, if the facesViewId (like /faces/foo/bar/test.jspx) is path-mapped (like /faces/*), then return
			// the /foo/bar/test.jspx part as the reqestPathInfo. This is the way the path-mapped feature works -- it
			// treats the /faces/* part as a "virtual" path used to match the url-pattern of the servlet-mapping. But it
			// has to be removed from the requestPathInfo in order to provide a context-relative path to a file resource
			// that can be found by a RequestDispatcher (or in the case of portlets, a PortletRequestDispatcher).
			else if (facesView.isPathMapped()) {

				returnValue = viewId.substring(facesView.getServletPath().length());
				logger.debug("requestPathInfo=[{0}] PATH=[{1}] viewId=[{2}]", returnValue, facesView.getServletPath(),
					viewId);
			}

			// Otherwise, since it is neither extension-mapped nor path-mapped, simply return the viewId. This typically
			// occurs in a Facelets environment.
			else {
				returnValue = facesView.getViewId();
				logger.debug("requestPathInfo=[{0}] servletMapping=[NONE] viewId=[{1}]", returnValue, viewId);
			}

			// The StringWrapper is used to support the lazy-initialization of technique of this method but still
			// have the ability to return a null value.
			requestPathInfo = new StringWrapper(returnValue);
		}
		else {
			returnValue = requestPathInfo.getValue();
		}

		return returnValue;
	}

	/**
	 * Section 6.1.3.1 of the JSR 329 spec describes the logic for this method.
	 */
	@Override
	public String getRequestServletPath() {

		if (requestServletPath == null) {

			FacesView facesView = getFacesView();
			String viewId = facesView.getViewId();

			// If the facesView is extension-mapped (like *.faces), then simply return the viewId as required by Section
			// 6.1.3.1 of the spec. This also conforms to the behavior of the HttpServletRequest#getServletPath()
			// method.
			if (facesView.isExtensionMapped()) {
				requestServletPath = facesView.getViewId();
				logger.debug("requestServletPath=[{0}] extensionMapped=[{1}] viewId=[{2}]", requestServletPath,
					facesView.getExtension(), viewId);
			}

			// If the facesView is path-mapped (like /faces/*) then return everything up until the last forward-slash as
			// required by Section 6.1.3.1 of the spec. This also conforms to the behavior of the
			// HttpServletRequest#getServletPath() method.
			else if (facesView.isPathMapped()) {
				requestServletPath = facesView.getViewId();

				int pos = requestServletPath.lastIndexOf("/*");

				if (pos >= 0) {
					requestServletPath = requestServletPath.substring(0, pos);
				}

				logger.debug("requestServletPath=[{0}] pathMapped=[{1}] viewId=[{2}]", requestServletPath,
					facesView.getServletPath(), viewId);
			}

			// Otherwise, since there is no servlet-mapping, return an empty string. This is not required by the spec
			// but seems to work in a Facelets environment where there is no servlet-mapping.
			else {
				requestServletPath = "";
				logger.debug("requestServletPath=[{0}] servletMapping=[NONE] viewId=[{1}]", requestServletPath, viewId);
			}

		}

		return requestServletPath;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return portletContext.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return portletContext.getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return portletContext.getResourcePaths(path);
	}

	@Override
	public Object getResponse() {
		return portletResponse;
	}

	@Override
	public String getResponseCharacterEncoding() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			String characterEncoding = mimeResponse.getCharacterEncoding();

			if (manageIncongruities) {
				incongruityContext.setResponseCharacterEncoding(characterEncoding);
			}

			return characterEncoding;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getResponseCharacterEncoding();
			}
			else {

				if (portletResponse instanceof StateAwareResponse) {

					FacesContext facesContext = FacesContext.getCurrentInstance();
					ExternalContext externalContext = facesContext.getExternalContext();
					String characterEncoding = (String) externalContext.getRequestMap().get(
							BridgeExt.RESPONSE_CHARACTER_ENCODING);

					if (characterEncoding != null) {

						// Workaround for patch applied to Mojarra in JAVASERVERFACES-3023
						return characterEncoding;
					}
					else {

						// TCK TestPage169: getResponseCharacterEncodingActionTest
						// TCK TestPage180: getResponseCharacterEncodingEventTest
						throw new IllegalStateException();
					}
				}
				else {
					return null;
				}
			}
		}
	}

	/**
	 * @see  {@link ExternalContext#getResponseContentType()}
	 */
	@Override
	public String getResponseContentType() {

		if (portletResponse instanceof MimeResponse) {

			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			String responseContentType = mimeResponse.getContentType();

			if (responseContentType == null) {
				responseContentType = portletRequest.getResponseContentType();
			}

			return responseContentType;
		}
		else {

			// TCK TestPage173: getResponseContentTypeActionTest
			// TCK TestPage174: getResponseContentTypeEventTest
			throw new IllegalStateException();
		}
	}

	/**
	 * @see  {@link ExternalContext#getSession(boolean)}
	 */
	@Override
	public Object getSession(boolean create) {
		return portletRequest.getPortletSession(create);
	}

	@Override
	public Map<String, Object> getSessionMap() {
		return sessionMap;
	}

	@Override
	public Principal getUserPrincipal() {

		if (userPrincipal == null) {
			userPrincipal = portletRequest.getUserPrincipal();
		}

		return userPrincipal;
	}

	@Override
	public boolean isUserInRole(String role) {
		return portletRequest.isUserInRole(role);
	}

	@Override
	public void log(String message) {
		portletContext.log(message);
	}

	@Override
	public void log(String message, Throwable exception) {
		portletContext.log(message, exception);
	}

	@Override
	public void redirect(String url) throws IOException {

		if (url != null) {

			logger.debug("redirect url=[{0}]", url);

			// If currently executing the ACTION_PHASE, EVENT_PHASE, HEADER_PHASE, or RENDER_PHASE of the portlet
			// lifecycle, then
			if ((portletPhase == Bridge.PortletPhase.ACTION_PHASE) ||
					(portletPhase == Bridge.PortletPhase.EVENT_PHASE) || isHeaderPhase(portletPhase) ||
					(portletPhase == Bridge.PortletPhase.RENDER_PHASE)) {

				// If the specified URL starts with a "#" character, is external to this application, or has a
				// "javax.portlet.faces.DirectLink" parameter value of "true", then
				try {
					BridgeURI bridgeURI = bridgeURIFactory.getBridgeURI(portletResponse.getNamespace(), url);
					String queryString = bridgeURI.getQuery();
					boolean directLink = (queryString != null) && queryString.contains(DIRECT_LINK_EQUALS_TRUE);
					FacesContext facesContext = FacesContext.getCurrentInstance();
					ExternalContext externalContext = facesContext.getExternalContext();
					String contextPath = externalContext.getRequestContextPath();

					if ((portletPhase == Bridge.PortletPhase.ACTION_PHASE) &&
							(url.startsWith("#") || bridgeURI.isExternal(contextPath) || directLink)) {

						if (bridgeRequestScope != null) {
							bridgeRequestScope.setRedirectOccurred(true);
						}

						// TestPage039-requestNoScopeOnRedirectTest.
						ActionResponse actionResponse = (ActionResponse) portletResponse;
						actionResponse.sendRedirect(url);
					}

					// Otherwise,
					else {

						// If running in the ACTION_PHASE of the portlet lifecycle and the portlet container has the
						// ability to create a render URL during the ACTION_PHASE, then assume that the specified URL is
						// an encoded RenderURL and issue a redirect.
						PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
						PortalContext portalContext = portletRequest.getPortalContext();
						String createRenderUrlDuringActionPhaseSupport = portalContext.getProperty(
								BridgePortalContext.CREATE_RENDER_URL_DURING_ACTION_PHASE_SUPPORT);

						if ((portletPhase == Bridge.PortletPhase.ACTION_PHASE) &&
								(createRenderUrlDuringActionPhaseSupport != null)) {

							// Redirect to the targeted view.
							BridgeURL bridgeRedirectURL = bridgeURLFactory.getBridgeRedirectURL(facesContext,
									bridgeURI.toString(), null);
							ActionResponse actionResponse = (ActionResponse) portletResponse;
							actionResponse.sendRedirect(bridgeRedirectURL.toString());
						}

						// Otherwise, if running in the ACTION_PHASE or EVENT_PHASE of the portlet lifecycle, then
						// assume that the specified URL is NOT an encoded RenderURL, but rather a simple context-path
						// based string like "/my-portlet/views/myView.faces" that contains a context-relative JSF view
						// id an potentially some URL parameters that indicate the portlet mode and window state. In
						// this case, all that can be done is to simply navigate to the target view id (not an actual
						// redirect).
						else if ((portletPhase == Bridge.PortletPhase.ACTION_PHASE) ||
								(portletPhase == Bridge.PortletPhase.EVENT_PHASE)) {

							// TCK NOTE: The TCK will invoke this condition during the
							// TestPage039-requestNoScopeOnRedirectTest and TestPage176-redirectActionTest.
							String newViewId = bridgeURI.getContextRelativePath(contextPath);

							// If redirecting to a different view, then create the target view and place it into the
							// FacesContext.
							UIViewRoot viewRoot = facesContext.getViewRoot();
							String currentFacesViewId = viewRoot.getViewId();

							if (!currentFacesViewId.equals(newViewId)) {

								ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
								UIViewRoot newViewRoot = viewHandler.createView(facesContext, newViewId);
								facesContext.setViewRoot(newViewRoot);
							}

							// Set the "_facesViewIdRender" parameter on the URL to the new viewId so that the call
							// to BridgeNavigationUtil.navigate(...) below will cause a render parameter to be set
							// which will inform containers that implement POST-REDIRECT-GET (like Pluto) that the
							// 302 redirect URL needs to specify the new viewId in order for redirection to work in
							// the subsequent RENDER_PHASE.
							bridgeURI.setParameter(bridgeConfig.getViewIdRenderParameterName(), newViewId);

							// Update the PartialViewContext.
							partialViewContextRenderAll(facesContext);

							// Set the response as "complete" in the FacesContext.
							facesContext.responseComplete();

							// Set a flag on the {@link BridgeRequestScope} indicating that a <redirect />
							// occurred which means that the request attributes should not be preserved.
							if (bridgeRequestScope != null) {
								bridgeRequestScope.setRedirectOccurred(true);
							}

							// Apply the PortletMode, WindowState, etc. that may be present in the URL to the response.
							try {
								StateAwareResponse stateAwareResponse = (StateAwareResponse) portletResponse;
								BridgeNavigationUtil.navigate(portletRequest, stateAwareResponse, bridgeRequestScope,
									bridgeURI.getParameterMap());
							}
							catch (PortletException e) {
								logger.error(e.getMessage());
							}
						}

						// Otherwise, if currently executing the HEADER_PHASE or RENDER_PHASE of the portlet
						// lifecycle, then
						else if (isHeaderPhase(portletPhase) || (portletPhase == Bridge.PortletPhase.RENDER_PHASE)) {

							// If the specified URL is for a JSF viewId, then prepare for a render-redirect.
							BridgeURL bridgeRedirectURL = bridgeURLFactory.getBridgeRedirectURL(facesContext, url,
									null);

							String redirectURLViewId = bridgeRedirectURL.getViewId();

							if (redirectURLViewId != null) {

								// TCK TestPage 049: renderRedirectTest
								// TCK TestPage 178: redirectRenderTest
								// TCK TestPage 180: redirectRenderPRP2Test
								portletRequest.setAttribute(BridgeExt.RENDER_REDIRECT, Boolean.TRUE);
								portletRequest.setAttribute(BridgeExt.RENDER_REDIRECT_VIEW_ID, redirectURLViewId);
							}

							// Otherwise,
							else {

								// If there is a URL parameter specifying a JSF viewId, then prepare for a
								// render-redirect.
								String viewIdRenderParameterName = bridgeConfig.getViewIdRenderParameterName();
								String viewIdRenderParameterValue = bridgeRedirectURL.getParameter(
										viewIdRenderParameterName);

								// FACES-2978: Support render-redirect.
								if (viewIdRenderParameterValue == null) {

									Map<String, Object> requestMap = externalContext.getRequestMap();
									String requestMapKey = Bridge.VIEW_ID + url;
									viewIdRenderParameterValue = (String) requestMap.remove(requestMapKey);
								}

								if (viewIdRenderParameterValue != null) {

									// TCK TestPage 179: redirectRenderPRP1Test
									portletRequest.setAttribute(BridgeExt.RENDER_REDIRECT, Boolean.TRUE);
									portletRequest.setAttribute(BridgeExt.RENDER_REDIRECT_VIEW_ID,
										URLDecoder.decode(viewIdRenderParameterValue, "UTF-8"));
								}

								// Otherwise, throw an IllegalStateException according to Section 6.1.3.1 of the Spec.
								else {
									throw new IllegalStateException(
										"6.1.3.1: Unable to redirect to a non-Faces view during the RENDER_PHASE.");
								}
							}
						}
					}
				}
				catch (URISyntaxException e) {
					logger.error(e);
				}
			}

			// Otherwise, since executing the RESOURCE_PHASE of the portlet lifecycle:
			else {

				// NOTE: The Bridge Spec indicates that the redirect is to be ignored, but JSF 2 has the ability to
				// redirect during Ajax.
				FacesContext facesContext = FacesContext.getCurrentInstance();

				if (isJSF2PartialRequest(facesContext)) {

					try {
						redirectJSF2PartialResponse(facesContext, (ResourceResponse) portletResponse, url);
					}
					catch (Exception e) {
						logger.error(e);
						throw new IOException(e.getMessage());
					}
				}
				else {
					throw new UnsupportedEncodingException(
						"Can only redirect during RESOURCE_PHASE if a JSF partial/Ajax request has been triggered");
				}
			}
		}
		else {
			logger.error("redirect url=null");
		}
	}

	@Override
	public void setRequest(Object request) {

		if ((request != null) && (request instanceof PortletRequest)) {

			this.portletRequest = (PortletRequest) request;
			this.requestParameterMap = null;
			this.requestParameterValuesMap = null;
			this.requestHeaderMap = null;
			this.requestHeaderValuesMap = null;
			preInitializeObjects();
		}
		else {
			throw new IllegalArgumentException("Must be an instance of javax.portlet.PortletRequest");
		}
	}

	@Override
	public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException,
		IllegalStateException {

		// Although the JSF API's ViewHandler.initView(FacesContext) method will call this method during the portlet
		// RENDER_PHASE, the RenderRequest does not implement the ClientDataRequest interface, which means it does not
		// have a setCharacterEncoding(String) method, therefore this should only be done in the case of a
		// ClientDataRequest.
		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			try {
				clientDataRequest.setCharacterEncoding(encoding);
			}
			catch (IllegalStateException e) {
				// TestPage141: setRequestCharacterEncodingActionTest -- exception is to be ignored
			}
		}
		else {

			if (manageIncongruities) {
				incongruityContext.setRequestCharacterEncoding(encoding);
			}
			else {
				// TestPage140: setRequestCharacterEncodingRenderTest expects this to be a no-op so throwing an
				// IllegalStateException is not an option.
			}
		}
	}

	@Override
	public void setResponse(Object response) {

		if (response != null) {

			// If the specified response is a PortletResponse, then simply remember the value.
			if (response instanceof PortletResponse) {

				portletResponse = (PortletResponse) response;
				preInitializeObjects();
			}

			// Otherwise, if it is an HttpServletResponse, then assume that the Faces runtime is attempting to decorate
			// the response. This typically happens when a JSP view (rather than a Facelets view) is to be rendered and
			// the Faces runtime is attempting to capture the plain HTML markup that may appear after the closing
			// </f:view> component tag (a.k.a. "after view markup"). For example, the Mojarra
			// com.sun.faces.application.view.JspViewHandlingStrategy will attempt to decorate the response with an
			// isntance of com.sun.faces.application.ViewHandlerResponseWrapper. Similarly, the MyFaces
			// org.apache.myfaces.view.jsp.JspViewDeclarationLanguage class will attempt to decorate the response with
			// an instance of org.apache.myfaces.application.jsp.ServletViewResponseWrapper.
			else if (response instanceof HttpServletResponse) {

				// If executing the HEADER_PHASE of the portlet lifecycle, then decorate the specified
				// HttpServletResponse with an adapter that implements HeaderRequest.
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;

				if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {
					portletResponse = new HttpServletResponseHeaderAdapter(httpServletResponse,
							portletResponse.getNamespace());
				}

				// Otherwise, if executing the RESOURCE_PHASE of the portlet lifecycle, then decorate the specified
				// HttpServletResponse with an adapter that implements ResourceResponse.
				else if (portletPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
					portletResponse = new HttpServletResponseResourceAdapter(httpServletResponse,
							portletResponse.getNamespace());
				}

				// Otherwise, throw an exception since HttpServletResponse is not supported in other phases of the
				// portlet lifecycle.
				else {
					throw new IllegalArgumentException("Unable to decorate httpServletResponse=[" + response +
						"] in phase=[" + portletPhase + "]");
				}

				preInitializeObjects();
			}
			else {
				throw new IllegalArgumentException("response=[" + response +
					"] is not an instance of javax.portlet.PortletResponse");
			}
		}
		else {
			throw new IllegalArgumentException("response cannot be null");
		}
	}

	/**
	 * @see  ExternalContext#setResponseCharacterEncoding(String)
	 */
	@Override
	public void setResponseCharacterEncoding(String encoding) {

		if (encoding != null) {

			if (portletResponse instanceof ResourceResponse) {
				ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
				resourceResponse.setCharacterEncoding(encoding);
			}
			else {

				if (manageIncongruities) {
					incongruityContext.setResponseCharacterEncoding(encoding);
				}
				else {
					// TestPage196: setResponseCharacterEncodingTest expects this to be a no-op so throwing
					// IllegalStateException is not an option.
				}
			}
		}
	}

	protected BridgeConfig getBridgeConfig() {
		return (BridgeConfig) getRequestMap().get(BridgeConfig.class.getName());
	}

	protected Map<String, String> getDefaultViewIdMap(PortletConfig portletConfig) {

		if (defaultViewIdMap == null) {
			defaultViewIdMap = ViewUtil.getDefaultViewIdMap(portletConfig);
		}

		return defaultViewIdMap;
	}

	/**
	 * Returns an instance of {@link FacesView} that represents the target view (and optional query string) as described
	 * in section 5.2.3 of the Bridge Spec titled "Determining the Target View".
	 *
	 * @throws  BridgeDefaultViewNotSpecifiedException  when the default view is not specified in the
	 *                                                  WEB-INF/portlet.xml descriptor.
	 * @throws  BridgeInvalidViewPathException          when the {@link Bridge#VIEW_PATH} request attribute contains an
	 *                                                  invalid path such that the target view cannot be determined.
	 */
	protected FacesView getFacesView() throws BridgeDefaultViewNotSpecifiedException, BridgeInvalidViewPathException {

		if (facesView == null) {
			String fullViewId = getFacesViewIdAndQueryString();
			String viewId = null;
			String navigationQueryString = null;

			if (fullViewId != null) {
				int pos = fullViewId.indexOf("?");

				if (pos > 0) {
					navigationQueryString = fullViewId.substring(pos + 1);
					viewId = fullViewId.substring(0, pos);
				}
				else {
					viewId = fullViewId;
				}
			}

			facesView = new FacesViewImpl(viewId, navigationQueryString, configuredSuffixes,
					configuredFacesServletMappings);
		}

		return facesView;
	}

	/**
	 * <p>This method returns the target view (and optional query string) as described in section 5.2.3 of the Bridge
	 * Spec titled "Determining the Target View".</p>
	 *
	 * <p>Try#1: Get the viewId from the {@link Bridge#VIEW_ID} (javax.portlet.faces.viewId) request attribute. As
	 * described in sections 3.4 and 4.2.5 of the bridge spec, this attribute is set by the {@link GenericFacesPortlet}
	 * when it encounters the {@link Bridge#FACES_VIEW_ID_PARAMETER} request parameter.</p>
	 *
	 * <p>Try#2: Get the viewId from the {@link Bridge#VIEW_PATH} (javax.portlet.faces.viewPath) request attribute. As
	 * described in sections 3.4 and 4.2.5 of the bridge spec, this attribute is set by the {@link GenericFacesPortlet}
	 * when it encounters the {@link Bridge#FACES_VIEW_PATH_PARAMETER} request parameter. If the viewId cannot be
	 * determined, then {@link BridgeInvalidViewPathException} is thrown.</p>
	 *
	 * <p>Try#3: Get the viewId from a prior render-redirect (if one has occurred).</p>
	 *
	 * <p>Try#4: Get the viewId from a request parameter, the name of which is dynamic depending on the {@link
	 * Bridge.PortletPhase}.</p>
	 *
	 * <p>Try#5:Get the viewId from the init-param value in the portlet.xml descriptor according the current {@link
	 * PortletMode}.</p>
	 *
	 * @throws  BridgeDefaultViewNotSpecifiedException  when the default view is not specified in the
	 *                                                  WEB-INF/portlet.xml descriptor.
	 * @throws  BridgeInvalidViewPathException          when the {@link Bridge#VIEW_PATH} request attribute contains an
	 *                                                  invalid path such that the target view cannot be determined.
	 */
	protected String getFacesViewIdAndQueryString() throws BridgeDefaultViewNotSpecifiedException,
		BridgeInvalidViewPathException {

		if (viewIdAndQueryString == null) {

			// Try#1: Get the viewId the "javax.portlet.faces.viewId" request attribute.
			viewIdAndQueryString = getFacesViewIdRequestAttribute(Bridge.VIEW_ID);

			if (viewIdAndQueryString == null) {

				// Try#2: Get the viewId from the "javax.portlet.faces.viewPath" request attribute.
				String viewPath = getFacesViewIdRequestAttribute(Bridge.VIEW_PATH);

				if (viewPath != null) {

					// If present, remove the query string from the specified viewPath.
					int pos = viewPath.indexOf("?");

					if (pos > 0) {
						viewPath = viewPath.substring(0, pos);
					}

					// If present, remove everything up to (and including) the context path from the viewPath.
					String contextPath = portletRequest.getContextPath();
					pos = viewPath.indexOf(contextPath);

					if (pos >= 0) {
						viewPath = viewPath.substring(pos + contextPath.length());
					}

					viewIdAndQueryString = getFacesViewIdFromPath(viewPath);

					if (viewIdAndQueryString == null) {
						throw new BridgeInvalidViewPathException();
					}
				}

				if (viewIdAndQueryString == null) {

					// Try #3: Get the viewId from a prior render-redirect (if one has occurred). Note that this logic
					// depends on the BridgePhaseRenderImpl calling the setRenderRedirectURL(BridgeRedirectURL) method
					// on this class instance when a render-redirect takes place.
					String renderRedirectViewId = (String) portletRequest.getAttribute(
							BridgeExt.RENDER_REDIRECT_VIEW_ID);

					if (renderRedirectViewId != null) {
						viewIdAndQueryString = renderRedirectViewId;
					}

					if (viewIdAndQueryString == null) {

						// Try#4: Get the viewId from a request parameter, the name of which is dynamic depending on
						// the portlet phase.
						String requestParameterName;

						if (portletPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
							requestParameterName = bridgeConfig.getViewIdResourceParameterName();
						}
						else {
							requestParameterName = bridgeConfig.getViewIdRenderParameterName();
						}

						viewIdAndQueryString = getFacesViewIdRequestParameter(requestParameterName);

						if (viewIdAndQueryString == null) {

							// Try#5: Get the viewId from the init-param value in the portlet.xml descriptor according
							// to the current portlet mode.
							PortletMode currentPortletMode = portletRequest.getPortletMode();
							viewIdAndQueryString = getDefaultViewIdMap(portletConfig).get(
									currentPortletMode.toString());
							logger.debug("portlet.xml viewId=[{0}] portletMode=[{1}]", viewIdAndQueryString,
								currentPortletMode);

							if (viewIdAndQueryString == null) {
								throw new BridgeDefaultViewNotSpecifiedException();
							}
						}
						else {
							logger.debug("request parameter {0}=[{1}]", requestParameterName, viewIdAndQueryString);
						}
					}
					else {
						logger.debug("redirect viewId=[{0}]", viewIdAndQueryString);
					}
				}
			}
			else {
				logger.debug("javax.portlet.faces.viewId=[{0}]", viewIdAndQueryString);
			}
		}

		return viewIdAndQueryString;
	}

	/**
	 * Returns the viewId associated with the specified <code>viewPath</code> by examining the servlet-mapping entries
	 * from the WEB-INF/web.xml descriptor.
	 *
	 * @param   viewPath  The path to the view.
	 *
	 * @return  The viewId associated with the specified <code>viewPath</code> (providing that the view physically
	 *          exists). Otherwise returns null.
	 */
	protected String getFacesViewIdFromPath(String viewPath) {

		String facesViewId = null;

		// Try to determine the viewId by examining the servlet-mapping entries for the Faces Servlet.
		// For each servlet-mapping:
		for (ConfiguredServletMapping configuredFacesServletMapping : configuredFacesServletMappings) {

			// If the current servlet-mapping matches the viewPath, then
			logger.debug("Attempting to determine the facesViewId from {0}=[{1}]", Bridge.VIEW_PATH, viewPath);

			if (configuredFacesServletMapping.isMatch(viewPath)) {

				// If the servlet-mapping is extension mapped (like *.faces or *.jsf), then
				if (configuredFacesServletMapping.isExtensionMapped()) {

					// Iterate through each of the valid extensions (.jsp, .jspx, etc.) that the developer
					// may have specified in the web.xml descriptor. For each extension, see if file exists
					// within the filesystem of this context.
					for (String defaultSuffix : configuredSuffixes) {

						int pos = viewPath.lastIndexOf(".");

						if (pos > 0) {

							String resourcePath = viewPath.substring(0, pos) + defaultSuffix;

							try {
								URL resourceURL = portletContext.getResource(resourcePath);

								// If the file exists, then we've determined the viewId from the viewPath.
								if (resourceURL != null) {
									facesViewId = viewPath;

									break;
								}

							}
							catch (MalformedURLException e) {
								logger.error(e);
							}
						}
					}

					if (facesViewId == null) {
						logger.error(
							"Matched EXTENSION MAPPING for for urlPattern=[{0}] and viewPath=[{1}] but unable to find a facesViewId with extensions[{2}]",
							configuredFacesServletMapping.getUrlPattern(), viewPath, configuredSuffixes);
					}
				}

				// Otherwise, if the servlet-mapping is path-mapped, then
				else if (configuredFacesServletMapping.isPathMapped()) {
					facesViewId = viewPath;
				}

				if (facesViewId != null) {
					break;
				}
			}
		}

		return facesViewId;
	}

	protected String getFacesViewIdRequestAttribute(String name) {

		String value = (String) portletRequest.getAttribute(name);

		if ((value != null) && (value.contains(":") || value.contains("%3A") || value.contains("%253A"))) {

			logger.warn("Invalid character in request attribute {0}=[{1}]", name, value);
			value = null;
		}

		return value;
	}

	protected String getFacesViewIdRequestParameter(String name) {

		String value = portletRequest.getParameter(name);

		if ((value != null) && (value.contains(":") || value.contains("%3A") || value.contains("%253A"))) {

			logger.warn("Invalid character in request parameter {0}=[{1}]", name, value);
			value = null;
		}

		return value;
	}

	protected String getRequestQueryString(PortletRequest portletRequest) {

		// Servlet-API request attribute that indicates the query part of the URL requested by the user-agent
		String requestQueryString = (String) portletRequest.getAttribute(REQUEST_ATTR_QUERY_STRING);

		if (requestQueryString == null) {

			// Some portlet bridges wrap the portal's PortletRequest implementation instance (which prevents us from
			// getting the query_string). As a workaround, we might still be able to get  the original
			// PortletRequest instance, because the Portlet spec says it must be stored in the
			// "javax.portlet.request" attribute.
			Object portletRequestAsObject = portletRequest.getAttribute(REQUEST_ATTR_PORTLET_REQUEST);

			if ((portletRequestAsObject != null) && (portletRequestAsObject instanceof PortletRequest)) {
				portletRequest = (PortletRequest) portletRequestAsObject;
				requestQueryString = (String) portletRequest.getAttribute(REQUEST_ATTR_QUERY_STRING);
			}
		}

		return requestQueryString;
	}

	protected String getRequestURL() {

		// Note that this is an approximation (best guess) of the original URL.
		return portletRequest.getScheme() + "://" + portletRequest.getServerName() + ":" +
			portletRequest.getServerPort() + portletRequest.getContextPath() + "?" +
			getRequestQueryString(portletRequest);
	}

	/**
	 * In order to increase runtime performance, this method caches values of objects that are typically called more
	 * than once during the JSF lifecycle. Other values will be cached lazily, or might not be cached since their getter
	 * methods may never get called.
	 */
	protected void preInitializeObjects() {

		// Retrieve the portlet lifecycle phase.
		portletPhase = (Bridge.PortletPhase) portletRequest.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);

		// Initialize the application map.
		boolean preferPreDestroy = PortletConfigParam.PreferPreDestroy.getBooleanValue(portletConfig);
		applicationMap = contextMapFactory.getApplicationScopeMap(portletContext, preferPreDestroy);

		// Initialize the request attribute map.
		Set<String> removedAttributeNames = null;

		if (bridgeRequestScope != null) {
			removedAttributeNames = bridgeRequestScope.getRemovedAttributeNames();
		}

		requestAttributeMap = contextMapFactory.getRequestScopeMap(portletContext, portletRequest,
				portletResponse.getNamespace(), removedAttributeNames, preferPreDestroy);

		// Initialize the session map.
		sessionMap = contextMapFactory.getSessionScopeMap(portletContext, portletRequest.getPortletSession(true),
				PortletSession.PORTLET_SCOPE, preferPreDestroy);

		// Initialize the init parameter map.
		initParameterMap = contextMapFactory.getInitParameterMap(portletContext);

		// Initialize the request context path.
		requestContextPath = portletRequest.getContextPath();

		String attributeName = Bridge.BRIDGE_PACKAGE_PREFIX + portletConfig.getPortletName() + "." +
			Bridge.DEFAULT_RENDERKIT_ID;
		defaultRenderKitId = (String) portletContext.getAttribute(attributeName);
	}

	private static class StringWrapper {

		private String value;

		public StringWrapper(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
