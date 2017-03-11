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
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.portlet.ClientDataRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeURL;
import javax.servlet.http.Cookie;

import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.util.internal.FileNameUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * This class provides a compatibility layer that isolates differences between JSF 1.2 and JSF 2.0.
 *
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_2_0_Impl extends ExternalContextCompat_2_0_FlashImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ExternalContextCompat_2_0_Impl.class);

	// Private Constants
	private static final String COOKIE_PROPERTY_COMMENT = "comment";
	private static final String COOKIE_PROPERTY_DOMAIN = "domain";
	private static final String COOKIE_PROPERTY_MAX_AGE = "maxAge";
	private static final String COOKIE_PROPERTY_PATH = "path";
	private static final String COOKIE_PROPERTY_SECURE = "secure";

	// Lazy-Initialized Data Members
	private Boolean iceFacesLegacyMode;
	private String portletContextName;
	private Boolean renderRedirectEnabled;
	private Writer responseOutputWriter;

	// Protected Data Members
	protected Bridge.PortletPhase portletPhase;

	public ExternalContextCompat_2_0_Impl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		super(portletContext, portletRequest, portletResponse);
	}

	/**
	 * @see    {@link ExternalContext#addResponseCookie(String, String, Map)}
	 * @since  JSF 2.0
	 */
	@Override
	public void addResponseCookie(String name, String value, Map<String, Object> properties) {

		Cookie cookie = createCookie(name, value, properties);
		portletResponse.addProperty(cookie);
	}

	/**
	 * @see    {@link ExternalContext#addResponseHeader(String, String)}
	 * @since  JSF 2.0
	 */
	@Override
	public void addResponseHeader(String name, String value) {

		if (portletResponse instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
			resourceResponse.addProperty(name, value);
		}
		else {
			logger.warn("Unable to call {0} for portletResponse=[{1}] because it is not a ResourceResponse.",
				"portletResponse.addProperty(String, String)", portletResponse.getClass().getName());
		}
	}

	/**
	 * @see    {@link ExternalContext#encodeBookmarkableURL(String, Map)}
	 * @since  JSF 2.0
	 */
	@Override
	public String encodeBookmarkableURL(String baseURL, Map<String, List<String>> parameters) {

		if (baseURL == null) {
			throw new NullPointerException();
		}
		else {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			try {
				BridgeURL bridgeBookmarkableURL = bridgeURLFactory.getBridgeBookmarkableURL(facesContext, baseURL,
						parameters);

				return bridgeBookmarkableURL.toString();
			}
			catch (BridgeException e) {
				throw new FacesException(e);
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#encodePartialActionURL(String)}
	 * @since  JSF 2.0
	 */
	@Override
	public String encodePartialActionURL(String url) {

		if (url == null) {
			throw new NullPointerException();
		}
		else {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			try {
				BridgeURL bridgePartialActionURL = bridgeURLFactory.getBridgePartialActionURL(facesContext, url);

				return bridgePartialActionURL.toString();
			}
			catch (BridgeException e) {
				throw new FacesException(e);
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#encodeRedirectURL(String, Map)}
	 * @since  JSF 2.0
	 */
	@Override
	public String encodeRedirectURL(String baseUrl, Map<String, List<String>> parameters) {

		if (baseUrl == null) {
			throw new NullPointerException();
		}
		else {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			try {
				BridgeURL bridgeRedirectURL = bridgeURLFactory.getBridgeRedirectURL(facesContext, baseUrl, parameters);

				return bridgeRedirectURL.toString();
			}
			catch (BridgeException e) {
				throw new FacesException(e);
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#getContextName()}
	 * @since  JSF 2.0
	 */
	@Override
	public String getContextName() {

		if (portletContextName == null) {
			portletContextName = portletContext.getPortletContextName();
		}

		return portletContextName;
	}

	/**
	 * @see    {@link ExternalContext#getMimeType(String)}
	 * @since  JSF 2.0
	 */
	@Override
	public String getMimeType(String fileName) {
		String mimeType = portletContext.getMimeType(fileName);

		if ((mimeType == null) || (mimeType.length() == 0)) {
			mimeType = FileNameUtil.getFileNameMimeType(fileName);
		}

		return mimeType;
	}

	/**
	 * @see    {@link ExternalContext#getRealPath(String)}
	 * @since  JSF 2.0
	 */
	@Override
	public String getRealPath(String path) {
		return portletContext.getRealPath(path);
	}

	/**
	 * @see    {@link ExternalContext#getRequestContentLength()}
	 * @since  JSF 2.0
	 */
	@Override
	public int getRequestContentLength() {

		int requestContentLength;

		if (portletRequest instanceof ClientDataRequest) {
			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;
			requestContentLength = clientDataRequest.getContentLength();

			if (manageIncongruities) {
				incongruityContext.setRequestContentLength(requestContentLength);
			}

			return requestContentLength;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getRequestContentLength();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#getRequestScheme()}
	 * @since  JSF 2.0
	 */
	@Override
	public String getRequestScheme() {
		return portletRequest.getScheme();
	}

	/**
	 * @see    {@link ExternalContext#getRequestServerName()}
	 * @since  JSF 2.0
	 */
	@Override
	public String getRequestServerName() {
		return portletRequest.getServerName();
	}

	/**
	 * @see    {@link ExternalContext#getRequestServerPort()}
	 * @since  JSF 2.0
	 */
	@Override
	public int getRequestServerPort() {
		return portletRequest.getServerPort();
	}

	/**
	 * @see    {@link ExternalContext#getResponseBufferSize()}
	 * @since  JSF 2.0
	 */
	@Override
	public int getResponseBufferSize() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			int responseBufferSize = mimeResponse.getBufferSize();

			if (manageIncongruities) {
				incongruityContext.setResponseBufferSize(responseBufferSize);
			}

			return responseBufferSize;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getResponseBufferSize();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#getResponseOutputStream()}
	 * @since  JSF 2.0
	 */
	@Override
	public OutputStream getResponseOutputStream() throws IOException {

		if (portletResponse instanceof MimeResponse) {

			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			return mimeResponse.getPortletOutputStream();
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getResponseOutputStream();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#getResponseOutputWriter()}
	 * @since  JSF 2.0
	 */
	@Override
	public Writer getResponseOutputWriter() throws IOException {

		if (portletResponse instanceof MimeResponse) {

			if (responseOutputWriter == null) {

				// If executing in the HEADER_PHASE of the portlet lifecycle, then return a capturing writer so that
				// writing can be delayed until the RENDER_PHASE of the portlet lifecycle.
				if (isHeaderPhase(portletPhase)) {
					responseOutputWriter = new CapturingWriterImpl();
				}

				// Otherwise, since executing in the RENDER_PHASE of the portlet lifecycle:
				else {

					// If the HEADER_PHASE is supported, then assume that the writer's output was captured in the
					// HEADER_PHASE of the portlet lifecycle and now needs to be written in the RENDER_PHASE. In this
					// case, return a writer that will write directly to the response.
					if (isHeaderPhaseSupported()) {

						MimeResponse mimeResponse = (MimeResponse) portletResponse;
						responseOutputWriter = mimeResponse.getWriter();
					}

					// Otherwise, since the HEADER_PHASE is not supported:
					else {

						// If the render-redirect feature is enabled, then return a capturing writer so that the
						// bridge has the opportunity to discard output in the case that a render-redirect actually
						// occurs.
						if (renderRedirectEnabled == null) {
							renderRedirectEnabled = PortletConfigParam.RenderRedirectEnabled.getBooleanValue(
									portletConfig);
						}

						if (renderRedirectEnabled) {
							responseOutputWriter = new CapturingWriterImpl();
						}

						// Otherwise, return a writer that will write directly to the response.
						else {

							MimeResponse mimeResponse = (MimeResponse) portletResponse;
							responseOutputWriter = mimeResponse.getWriter();
						}
					}
				}
			}

			return responseOutputWriter;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.getResponseOutputWriter();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#invalidateSession()}
	 * @since  JSF 2.0
	 */
	@Override
	public void invalidateSession() {
		portletRequest.getPortletSession().invalidate();
	}

	/**
	 * @see    {@link ExternalContext#isResponseCommitted()}
	 * @since  JSF 2.0
	 */
	@Override
	public boolean isResponseCommitted() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			boolean responseCommitted = mimeResponse.isCommitted();

			if (manageIncongruities) {
				incongruityContext.setResponseCommitted(responseCommitted);
			}

			return responseCommitted;
		}
		else {

			if (manageIncongruities) {
				return incongruityContext.isResponseCommitted();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#responseFlushBuffer()}
	 * @since  JSF 2.0
	 */
	@Override
	public void responseFlushBuffer() throws IOException {

		if (portletResponse instanceof MimeResponse) {

			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.flushBuffer();
		}
		else {

			if (manageIncongruities) {
				incongruityContext.responseFlushBuffer();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#responseReset()}
	 * @since  JSF 2.0
	 */
	@Override
	public void responseReset() {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.reset();
		}
		else {

			if (manageIncongruities) {
				incongruityContext.responseReset();
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * The Portlet API does not have an equivalent to {@link javax.servlet.http.HttpServletResponse#sendError(int,
	 * String)}. Since the Mojarra JSF implementation basically only calls this when a Facelet is not found, better in a
	 * portlet environment to simply log an error and throw an IOException up the call stack so that the portlet will
	 * give the portlet container a chance to render an error message.
	 *
	 * @see    {@link ExternalContext#responseSendError(int, String)}
	 * @since  JSF 2.0
	 */
	@Override
	public void responseSendError(int statusCode, String message) throws IOException {
		String errorMessage = "Status code " + statusCode + ": " + message;
		logger.error(errorMessage);
		throw new IOException(errorMessage);
	}

	/**
	 * @see    {@link ExternalContext#setResponseBufferSize(int)}
	 * @since  JSF 2.0
	 */
	@Override
	public void setResponseBufferSize(int size) {

		if (portletResponse instanceof ResourceResponse) {

			PortalContext portalContext = portletRequest.getPortalContext();
			String setResponseBufferSizeSupport = portalContext.getProperty(
					BridgePortalContext.SET_RESOURCE_RESPONSE_BUFFER_SIZE_SUPPORT);

			if (setResponseBufferSizeSupport != null) {
				ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
				resourceResponse.setBufferSize(size);
			}
		}
		else {

			if (manageIncongruities) {
				incongruityContext.setResponseBufferSize(size);
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#setResponseContentLength(int)}
	 * @since  JSF 2.0
	 */
	@Override
	public void setResponseContentLength(int length) {

		if (portletResponse instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
			resourceResponse.setContentLength(length);
		}
		else {

			if (manageIncongruities) {
				incongruityContext.setResponseContentLength(length);
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#setResponseContentType(String)}
	 * @since  JSF 2.0
	 */
	@Override
	public void setResponseContentType(String contentType) {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.setContentType(contentType);
		}
		else {

			if (manageIncongruities) {
				incongruityContext.setResponseContentType(contentType);
			}
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * @see    {@link ExternalContext#setResponseHeader(String, String)}
	 * @since  JSF 2.0
	 */
	@Override
	public void setResponseHeader(String name, String value) {
		addResponseHeader(name, value);
	}

	/**
	 * Sets the status of the portlet response to the specified status code. Note that this is only possible for a
	 * portlet request of type PortletResponse because that is the only type of portlet response that is delivered
	 * directly back to the client (without additional markup added by the portlet container).
	 *
	 * @see    {@link ExternalContext#setResponseStatus(int)}
	 * @since  JSF 2.0
	 */
	@Override
	public void setResponseStatus(int statusCode) {

		if (portletResponse instanceof ResourceResponse) {

			ResourceResponse resourceResponse = (ResourceResponse) portletResponse;
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(statusCode));
		}
		else {

			if (manageIncongruities) {
				incongruityContext.setResponseStatus(statusCode);
			}
			else {
				// Mojarra will call this method if a runtime exception occurs during execution of the JSF lifecycle, so
				// must not throw an IllegalStateException.
			}
		}
	}

	protected abstract boolean isHeaderPhase(Bridge.PortletPhase portletPhase);

	protected abstract boolean isHeaderPhaseSupported();

	protected Cookie createCookie(String name, String value, Map<String, Object> properties) {

		Cookie cookie = new Cookie(name, value);

		if ((properties != null) && !properties.isEmpty()) {

			try {
				String comment = (String) properties.get(COOKIE_PROPERTY_COMMENT);

				if (comment != null) {
					cookie.setComment(comment);
				}

				String domain = (String) properties.get(COOKIE_PROPERTY_DOMAIN);

				if (domain != null) {
					cookie.setDomain(domain);
				}

				Integer maxAge = (Integer) properties.get(COOKIE_PROPERTY_MAX_AGE);

				if (maxAge != null) {
					cookie.setMaxAge(maxAge);
				}

				String path = (String) properties.get(COOKIE_PROPERTY_PATH);

				if (path != null) {
					cookie.setPath(path);
				}

				Boolean secure = (Boolean) properties.get(COOKIE_PROPERTY_SECURE);

				if (secure != null) {
					cookie.setSecure(secure);
				}
			}
			catch (ClassCastException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return cookie;
	}

	protected boolean isICEfacesLegacyMode(ClientDataRequest clientDataRequest) {

		if (iceFacesLegacyMode == null) {

			iceFacesLegacyMode = Boolean.FALSE;

			String requestContentType = clientDataRequest.getContentType();

			if ((requestContentType != null) && requestContentType.toLowerCase().startsWith("multipart/")) {

				Product iceFaces = ProductFactory.getProduct(Product.Name.ICEFACES);

				if (iceFaces.isDetected() &&
						((iceFaces.getMajorVersion() == 2) ||
							((iceFaces.getMajorVersion() == 3) && (iceFaces.getMinorVersion() == 0)))) {

					iceFacesLegacyMode = Boolean.TRUE;
				}
			}
		}

		return iceFacesLegacyMode;
	}

	protected boolean isJSF2PartialRequest(FacesContext facesContext) {
		return facesContext.getPartialViewContext().isPartialRequest();
	}

	protected void partialViewContextRenderAll(FacesContext facesContext) {

		PartialViewContext partialViewContext = facesContext.getPartialViewContext();

		if (!partialViewContext.isRenderAll()) {
			partialViewContext.setRenderAll(true);
		}
	}

	protected void redirectJSF2PartialResponse(FacesContext facesContext, ResourceResponse resourceResponse, String url)
		throws IOException {
		resourceResponse.setContentType("text/xml");
		resourceResponse.setCharacterEncoding("UTF-8");

		PartialResponseWriter partialResponseWriter;
		ResponseWriter responseWriter = facesContext.getResponseWriter();

		if (responseWriter instanceof PartialResponseWriter) {
			partialResponseWriter = (PartialResponseWriter) responseWriter;
		}
		else {
			partialResponseWriter = facesContext.getPartialViewContext().getPartialResponseWriter();
		}

		partialResponseWriter.startDocument();
		partialResponseWriter.redirect(url);
		partialResponseWriter.endDocument();
		facesContext.responseComplete();
	}
}
