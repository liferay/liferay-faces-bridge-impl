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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeWriteBehindResponse;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.application.internal.ViewHandlerImpl;
import com.liferay.faces.bridge.application.view.internal.BridgeWriteBehindSupportFactory;
import com.liferay.faces.bridge.context.ContextMapFactory;
import com.liferay.faces.bridge.filter.internal.HttpServletResponseRenderAdapter;
import com.liferay.faces.bridge.filter.internal.HttpServletResponseResourceAdapter;
import com.liferay.faces.bridge.util.internal.LocaleIterator;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.ProductConstants;
import com.liferay.faces.util.product.ProductMap;


/**
 * @author  Neil Griffin
 */
public class ExternalContextImpl extends ExternalContextCompat_2_2_Impl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ExternalContextImpl.class);

	// Private Constants
	private static final boolean RICHFACES_DETECTED = ProductMap.getInstance().get(ProductConstants.RICHFACES)
		.isDetected();
	private static final String ORG_RICHFACES_EXTENSION = "org.richfaces.extension";

	// Pre-initialized Data Members
	private Map<String, Object> applicationMap;
	private ContextMapFactory contextMapFactory;
	private Map<String, Object> requestAttributeMap;
	private String requestContextPath;
	private Map<String, Object> sessionMap;

	// Lazily-Initialized Data Members
	private String authType;
	private Map<String, String> initParameterMap;
	private String remoteUser;
	private Map<String, Object> requestCookieMap;
	private Locale requestLocale;
	private Principal userPrincipal;

	public ExternalContextImpl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		super(portletContext, portletRequest, portletResponse);

		this.contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(ContextMapFactory.class);

		preInitializeObjects();
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
				if (bridgeContext.isRenderRedirectAfterDispatch()) {
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
		return bridgeContext.encodeResourceURL(url).toString();
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
		bridgeContext.redirect(url);
	}

	/**
	 * In order to increase runtime performance, this method caches values of objects that are typically called more
	 * than once during the JSF lifecycle. Other values will be cached lazily, or might not be cached since their getter
	 * methods may never get called.
	 */
	protected void preInitializeObjects() {

		// Retrieve the portlet lifecycle phase.
		portletPhase = bridgeContext.getPortletRequestPhase();

		// Initialize the application map.
		applicationMap = contextMapFactory.getApplicationScopeMap(bridgeContext);

		// Initialize the request attribute map.
		requestAttributeMap = contextMapFactory.getRequestScopeMap(bridgeContext);

		// Initialize the session map.
		sessionMap = contextMapFactory.getSessionScopeMap(bridgeContext, PortletSession.PORTLET_SCOPE);

		// Initialize the init parameter map.
		initParameterMap = contextMapFactory.getInitParameterMap(portletContext);

		// Initialize the request context path.
		requestContextPath = portletRequest.getContextPath();
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

	@Override
	public boolean isUserInRole(String role) {
		return portletRequest.isUserInRole(role);
	}

	@Override
	public String getInitParameter(String name) {
		return bridgeContext.getInitParameter(name);
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
	public void setRequest(Object request) {

		if ((request != null) && (request instanceof PortletRequest)) {

			this.portletRequest = (PortletRequest) request;
			preInitializeObjects();
		}
		else {
			throw new IllegalArgumentException("Must be an instance of javax.portlet.PortletRequest");
		}
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
			requestCookieMap = contextMapFactory.getRequestCookieMap(bridgeContext);
		}

		return requestCookieMap;
	}

	@Override
	public Map<String, String> getRequestHeaderMap() {
		return bridgeContext.getRequestHeaderMap();
	}

	@Override
	public Map<String, String[]> getRequestHeaderValuesMap() {
		return bridgeContext.getRequestHeaderValuesMap();
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
		return bridgeContext.getRequestParameterMap();
	}

	@Override
	public Iterator<String> getRequestParameterNames() {
		return getRequestParameterMap().keySet().iterator();
	}

	@Override
	public Map<String, String[]> getRequestParameterValuesMap() {
		return bridgeContext.getRequestParameterValuesMap();
	}

	/**
	 * This method returns the relative path to the viewId that is to be rendered.
	 *
	 * @see  javax.faces.context.ExternalContext#getRequestPathInfo()
	 */
	@Override
	public String getRequestPathInfo() {
		return bridgeContext.getRequestPathInfo();
	}

	/**
	 * Section 6.1.3.1 of the JSR 329 spec describes the logic for this method.
	 */
	@Override
	public String getRequestServletPath() {
		return bridgeContext.getRequestServletPath();
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
			// com.sun.faces.application.view.JspViewHandlingStrategoy will attempt to decorate the response with an
			// isntance of com.sun.faces.application.ViewHandlerResponseWrapper. Similarly, the MyFaces
			// org.apache.myfaces.view.jsp.JspViewDeclarationLanguage class will attempt to decorate the response with
			// an instance of org.apache.myfaces.application.jsp.ServletViewResponseWrapper.
			else if (response instanceof HttpServletResponse) {

				// If executing the RENDER_PHASE of the portlet lifecycle, then decorate the specified
				// HttpServletResponse with an adapter that implements RenderRequest.
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				Bridge.PortletPhase portletRequestPhase = bridgeContext.getPortletRequestPhase();

				if (portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) {
					portletResponse = new HttpServletResponseRenderAdapter(httpServletResponse,
							portletResponse.getNamespace());
				}

				// Otherwise, if executing the RESOURCE_PHASE of the portlet lifecycle, then decorate the specified
				// HttpServletResponse with an adapter that implements ResourceResponse.
				else if (portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
					portletResponse = new HttpServletResponseResourceAdapter(httpServletResponse,
							portletResponse.getNamespace());
				}

				// Otherwise, throw an exception since HttpServletResponse is not supported in other phases of the
				// portlet lifecycle.
				else {
					throw new IllegalArgumentException("Unable to decorate httpServletResponse=[" + response +
						"] in phase=[" + portletRequestPhase + "]");
				}

				// Section 7.2.1 of the Spec requires that the response be an instance of BridgeWriteBehindResponse.
				BridgeWriteBehindSupportFactory bridgeWriteBehindSupportFactory = (BridgeWriteBehindSupportFactory)
					BridgeFactoryFinder.getFactory(BridgeWriteBehindSupportFactory.class);
				BridgeWriteBehindResponse bridgeWriteBehindResponse =
					bridgeWriteBehindSupportFactory.getBridgeWriteBehindResponse((MimeResponse) portletResponse);
				portletResponse = (PortletResponse) bridgeWriteBehindResponse;
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

					String characterEncoding = (String) bridgeContext.getAttributes().get(
							ViewHandlerImpl.RESPONSE_CHARACTER_ENCODING);

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
}
