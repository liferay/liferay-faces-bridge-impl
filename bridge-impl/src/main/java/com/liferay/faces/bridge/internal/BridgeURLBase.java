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
package com.liferay.faces.bridge.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.BridgeURL;
import com.liferay.faces.bridge.PortletModeValidator;
import com.liferay.faces.bridge.PortletModeValidatorFactory;
import com.liferay.faces.bridge.WindowStateValidator;
import com.liferay.faces.bridge.WindowStateValidatorFactory;
import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeURLBase implements BridgeURL {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLBase.class);

	// Protected Data Members
	protected BridgeURI bridgeURI;
	protected List<ConfiguredServletMapping> configuredFacesServletMappings;
	protected String contextPath;
	protected String currentViewId;
	protected boolean selfReferencing;
	protected String viewIdResourceParameterName;
	protected String viewIdRenderParameterName;

	// Private Data Members
	private String namespace;
	private Map<String, String[]> parameterMap;
	private String viewId;

	@SuppressWarnings("unchecked")
	public BridgeURLBase(String uri, String contextPath, String namespace, String currentViewId,
		BridgeConfig bridgeConfig) throws URISyntaxException {

		this.bridgeURI = BridgeURIFactory.getBridgeURIInstance(uri);
		this.configuredFacesServletMappings = (List<ConfiguredServletMapping>) bridgeConfig.getAttributes().get(
				BridgeConfigAttributeMap.CONFIGURED_FACES_SERVLET_MAPPINGS);
		this.contextPath = contextPath;
		this.namespace = namespace;
		this.currentViewId = currentViewId;
		this.viewIdRenderParameterName = bridgeConfig.getViewIdRenderParameterName();
		this.viewIdResourceParameterName = bridgeConfig.getViewIdResourceParameterName();
	}

	@Override
	public String getParameter(String name) {

		Map<String, String[]> parameterMap = bridgeURI.getParameterMap();
		String[] values = parameterMap.get(name);

		if (values == null) {
			values = parameterMap.get(namespace.concat(name));
		}

		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		else {
			return null;
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return bridgeURI.getParameterMap();
	}

	@Override
	public String getViewId() {

		if (viewId == null) {

			String contextRelativePath = bridgeURI.getContextRelativePath(contextPath);

			if ((currentViewId != null) && (currentViewId.equals(contextRelativePath))) {
				viewId = currentViewId;
			}
			else {

				// If the context relative path maps to an actual Faces View due to an implicit servlet mapping or an
				// explicit servlet-mapping entry in the WEB-INF/web.xml descriptor, then the context relative path
				// is a faces view target.
				if (isViewPathMappedToFacesServlet(contextRelativePath)) {
					viewId = contextRelativePath;
				}

				// Otherwise,
				else {
					String potentialFacesViewId;

					// If the context relative path is not available, then
					if (contextRelativePath == null) {

						// TCK TestPage005 (modeViewIDTest)
						// * currentViewId="/tests/modeViewIdTest.xhtml"
						//
						// TCK TestPage042 (requestRenderIgnoresScopeViaCreateViewTest)
						// TCK TestPage043 (requestRenderRedisplayTest)
						// TCK TestPage044 (requestRedisplayOutOfScopeTest)
						// TCK TestPage049 (renderRedirectTest)
						// TCK TestPage050 (ignoreCurrentViewIdModeChangeTest)
						// TCK TestPage051 (exceptionThrownWhenNoDefaultViewIdTest)
						// * currentViewId="/tests/redisplayRenderNewModeRequestTest.xhtml"
						//
						// TCK TestPage073 (scopeAfterRedisplayResourcePPRTest)
						// * currentViewId="/tests/redisplayResourceAjaxResult.xhtml"
						//
						// TCK TestPage088 (encodeActionURLPortletRenderTest)
						// TCK TestPage089 (encodeActionURLPortletActionTest)
						// * currentViewId="/tests/singleRequestTest.xhtml"
						//
						// TCK TestPage179 (redirectRenderPRP1Test)
						// * currentViewId=null
						potentialFacesViewId = currentViewId;
					}

					// Otherwise, if the context relative path is indeed available, then
					else {

						// TCK TestPage059 (renderPhaseListenerTest)
						// TCK TestPage095 (encodeActionURLWithWindowStateActionTest)
						// TCK TestPage097 (encodeActionURLNonJSFViewRenderTest)
						// TCK TestPage098 (encodeActionURLNonJSFViewWithParamRenderTest)
						// TCK TestPage099 (encodeActionURLNonJSFViewWithModeRenderTest)
						// TCK TestPage100 (encodeActionURLNonJSFViewWithInvalidModeRenderTest)
						// TCK TestPage101 (encodeActionURLNonJSFViewWithWindowStateRenderTest)
						// TCK TestPage102 (encodeActionURLNonJSFViewWithInvalidWindowStateRenderTest)
						// TCK TestPage103 (encodeActionURLNonJSFViewResourceTest)
						// TCK TestPage104 (encodeActionURLNonJSFViewWithParamResourceTest)
						// TCK TestPage105 (encodeActionURLNonJSFViewWithModeResourceTest)
						// TCK TestPage106 (encodeActionURLNonJSFViewWithInvalidModeResourceTest)
						// TCK TestPage107 (encodeActionURLNonJSFViewWithWindowStateResourceTest)
						// TCK TestPage108 (encodeActionURLNonJSFViewWithInvalidWindowStateResourceTest)
						// * contextRelativeViewPath="/nonFacesViewTestPortlet.ptlt"
						// * currentViewId="/tests/nonJSFViewTest.xhtml"
						//
						// TCK TestPage071 (nonFacesResourceTest)
						// * contextRelativeViewPath="/tck/nonFacesResource"
						// * currentViewId="/tests/nonFacesResourceTest.xhtml"
						//
						// TCK TestPage134 (encodeResourceURLBackLinkTest)
						// * contextRelativePath="/resources/myImage.jpg"
						// * currentViewId="/tests/singleRequestTest.xhtml"
						//
						// TCK TestPage181 (illegalRedirectRenderTest)
						// * contextRelativeViewPath="/tests/NonJSFView.portlet"
						// * currentViewId=null
						potentialFacesViewId = contextRelativePath;
					}

					// If the extension/suffix of the context relative path matches that of the current viewId at the
					// time of construction, then it is a it is a faces view target.
					if ((currentViewId != null) && (matchPathAndExtension(currentViewId, potentialFacesViewId))) {

						// TCK TestPage005 (modeViewIDTest)
						// * contextRelativeViewPath=null
						// * potentialFacesViewId="/tests/modeViewIdTest.xhtml"
						// * currentViewId="/tests/modeViewIdTest.xhtml"
						viewId = potentialFacesViewId;

						logger.debug(
							"Regarding path=[{0}] as a Faces view since it has the same path and extension as the current viewId=[{1}]",
							potentialFacesViewId, currentViewId);
					}
				}
			}
		}

		return viewId;
	}

	@Override
	public void setParameter(String name, String[] value) {
		bridgeURI.getParameterMap().put(name, value);
	}

	@Override
	public void setParameter(String name, String value) {
		bridgeURI.getParameterMap().put(name, new String[] { value });
	}

	@Override
	public String removeParameter(String name) {

		String[] values = bridgeURI.getParameterMap().remove(name);

		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		else {
			return null;
		}
	}

	@Override
	public String toString() {

		String stringValue = null;

		try {

			// Ask the Portlet Container for a BaseURL that contains the modified parameters.
			BaseURL baseURL = toBaseURL();

			// If the URL string has escaped characters (like %20 for space, etc) then ask the
			// portlet container to create an escaped representation of the URL string.
			if (bridgeURI.isEscaped()) {

				StringWriter urlWriter = new StringWriter();

				try {
					baseURL.write(urlWriter, true);
				}
				catch (IOException e) {
					logger.error(e);
					stringValue = baseURL.toString();
				}

				stringValue = urlWriter.toString();
			}

			// Otherwise, ask the portlet container to create a normal (non-escaped) string
			// representation of the URL string.
			else {
				stringValue = baseURL.toString();
			}
		}
		catch (MalformedURLException e) {
			logger.error(e);
		}

		return stringValue;
	}

	protected abstract BaseURL toBaseURL() throws MalformedURLException;

	protected String getViewIdParameterName() {

		if (bridgeURI.isPortletScheme() && (bridgeURI.getPortletPhase() == Bridge.PortletPhase.RESOURCE_PHASE)) {
			return viewIdResourceParameterName;
		}
		else {
			return viewIdRenderParameterName;
		}
	}

	protected void copyRenderParameters(PortletRequest portletRequest, BaseURL baseURL) {

		// Copy the public render parameters of the current view to the BaseURL.
		Map<String, String[]> publicParameterMap = portletRequest.getPublicParameterMap();

		if (publicParameterMap != null) {
			copyParameterMapToBaseURL(publicParameterMap, baseURL);
		}

		// Copy the private render parameters of the current view to the BaseURL.
		Map<String, String[]> privateParameterMap = portletRequest.getPrivateParameterMap();

		if (privateParameterMap != null) {
			copyParameterMapToBaseURL(privateParameterMap, baseURL);
		}
	}

	protected PortletURL createActionURL(FacesContext facesContext, boolean modeChanged) throws MalformedURLException {

		List<URIParameter> toStringParameters = getToStringParameters(modeChanged);

		return createActionURL(facesContext, toStringParameters);
	}

	protected PortletURL createActionURL(FacesContext facesContext, Set<String> excludedParameterNames)
		throws MalformedURLException {

		List<URIParameter> toStringParameters = getToStringParameters(false, excludedParameterNames);

		return createActionURL(facesContext, toStringParameters);
	}

	protected PortletURL createRenderURL(FacesContext facesContext, Map<String, String[]> parameterMap)
		throws MalformedURLException {

		List<URIParameter> uriParameters = parameterMapToList(parameterMap);

		return createRenderURL(facesContext, uriParameters);
	}

	protected PortletURL createRenderURL(FacesContext facesContext, Set<String> excludedParameterNames)
		throws MalformedURLException {

		List<URIParameter> toStringParameters = getToStringParameters(false, excludedParameterNames);

		return createRenderURL(facesContext, toStringParameters);
	}

	protected PortletURL createRenderURL(FacesContext facesContext, boolean modeChanged) throws MalformedURLException {

		List<URIParameter> toStringParameters = getToStringParameters(modeChanged);

		return createRenderURL(facesContext, toStringParameters);
	}

	protected ResourceURL createResourceURL(FacesContext facesContext) throws MalformedURLException {
		return createResourceURL(facesContext, (List<URIParameter>) null);
	}

	protected ResourceURL createResourceURL(FacesContext facesContext, Map<String, String[]> parameterMap)
		throws MalformedURLException {

		List<URIParameter> uriParameters = getToStringParameters(false);
		Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();

		for (Map.Entry<String, String[]> mapEntry : entrySet) {
			uriParameters.add(new URIParameter(mapEntry.getKey(), mapEntry.getValue()));
		}

		return createResourceURL(facesContext, uriParameters);
	}

	protected ResourceURL createResourceURL(FacesContext facesContext, Set<String> excludedParameterNames)
		throws MalformedURLException {

		List<URIParameter> toStringParameters = getToStringParameters(false, excludedParameterNames);

		return createResourceURL(facesContext, toStringParameters);
	}

	protected ResourceURL createResourceURL(FacesContext facesContext, boolean modeChanged)
		throws MalformedURLException {

		List<URIParameter> toStringParameters = getToStringParameters(modeChanged);

		return createResourceURL(facesContext, toStringParameters);
	}

	private List<URIParameter> getToStringParameters(boolean modeChanged) {
		return getToStringParameters(modeChanged, null);
	}

	private List<URIParameter> getToStringParameters(boolean modeChanged, Set<String> excludedParameterNames) {

		List<URIParameter> toStringParameters = new ArrayList<URIParameter>();
		Map<String, String[]> parameterMap = bridgeURI.getParameterMap();
		Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();
		boolean foundFacesViewIdParam = false;
		boolean foundFacesViewPathParam = false;

		for (Map.Entry<String, String[]> mapEntry : entrySet) {

			boolean addParameter = false;
			String parameterName = mapEntry.getKey();
			String[] parameterValues = mapEntry.getValue();
			String firstParameterValue = null;

			if ((parameterValues != null) && (parameterValues.length > 0)) {
				firstParameterValue = parameterValues[0];
			}

			if (Bridge.PORTLET_MODE_PARAMETER.equals(parameterName)) {

				// Only add the "javax.portlet.faces.PortletMode" parameter if it has a valid value.
				if (firstParameterValue != null) {

					PortletModeValidator portletModeValidator = PortletModeValidatorFactory.getPortletModeValidatorInstance();
					addParameter = portletModeValidator.isValid(firstParameterValue);
				}
			}
			else if (Bridge.PORTLET_SECURE_PARAMETER.equals(parameterName)) {
				addParameter = ((firstParameterValue != null) &&
						("true".equalsIgnoreCase(firstParameterValue) ||
							"false".equalsIgnoreCase(firstParameterValue)));
			}
			else if (Bridge.PORTLET_WINDOWSTATE_PARAMETER.equals(parameterName)) {

				WindowStateValidator windowStateValidator = WindowStateValidatorFactory.getWindowStateValidatorInstance();
				addParameter = windowStateValidator.isValid(firstParameterValue);
			}
			else {

				if (!foundFacesViewIdParam) {
					foundFacesViewIdParam = Bridge.FACES_VIEW_ID_PARAMETER.equals(parameterName);
				}

				if (!foundFacesViewPathParam) {
					foundFacesViewPathParam = Bridge.FACES_VIEW_PATH_PARAMETER.equals(parameterName);
				}

				addParameter = true;
			}

			if ((addParameter) &&
					((excludedParameterNames == null) || !excludedParameterNames.contains(parameterName))) {
				toStringParameters.add(new URIParameter(parameterName, parameterValues));
			}
		}

		// If the "_jsfBridgeViewId" and "_jsfBridgeViewPath" parameters are not present in the URL, then add a
		// parameter that indicates the target Faces viewId.
		if (!foundFacesViewIdParam && !foundFacesViewPathParam && (getViewId() != null)) {

			if (!bridgeURI.isPortletScheme()) {

				// Note that if the "javax.portlet.faces.PortletMode" parameter is specified, then a mode change is
				// being requested and the target Faces viewId parameter must NOT be added.
				if (!modeChanged) {

					String contextRelativePath = bridgeURI.getContextRelativePath(contextPath);
					if (contextRelativePath != null) {
						toStringParameters.add(new URIParameter(getViewIdParameterName(), contextRelativePath));
					}
				}
			}
		}

		return toStringParameters;
	}

	private boolean isViewPathMappedToFacesServlet(String viewPath) {

		// Try to determine the viewId by examining the servlet-mapping entries for the Faces Servlet.
		// For each servlet-mapping:
		for (ConfiguredServletMapping configuredFacesServletMapping : configuredFacesServletMappings) {

			// If the current servlet-mapping matches the viewPath, then
			logger.debug("Attempting to determine the facesViewId from {0}=[{1}]", Bridge.VIEW_PATH, viewPath);

			if (configuredFacesServletMapping.isMatch(viewPath)) {
				return true;
			}
		}

		return false;
	}

	private void copyParameterMapToBaseURL(Map<String, String[]> parameterMap, BaseURL baseURL) {

		Map<String, String[]> bridgeURLParameterMap = bridgeURI.getParameterMap();
		Set<Map.Entry<String, String[]>> parameterMapEntrySet = parameterMap.entrySet();

		for (Map.Entry<String, String[]> mapEntry : parameterMapEntrySet) {

			String parameterName = mapEntry.getKey();

			// Note that preserved action parameters, parameters that already exist in the URL string,
			// and "javax.faces.ViewState" must not be copied.
			if (!ResponseStateManager.VIEW_STATE_PARAM.equals(parameterName) &&
					!bridgeURLParameterMap.containsKey(parameterName)) {
				baseURL.setParameter(parameterName, mapEntry.getValue());
			}
		}
	}

	private void copyURIParametersToBaseURL(List<URIParameter> uriParameters, BaseURL baseURL)
		throws MalformedURLException {

		for (URIParameter uriParameter : uriParameters) {
			String name = uriParameter.getName();
			String[] values = uriParameter.getValues();
			baseURL.setParameter(name, values);
			logger.debug("Copied parameter to baseURL name=[{0}] value=[{1}]", name, values);
		}
	}

	private PortletURL createActionURL(FacesContext facesContext, List<URIParameter> uriParameters)
		throws MalformedURLException {

		try {
			logger.debug("createActionURL uriParameters=[{0}]", uriParameters);

			ExternalContext externalContext = facesContext.getExternalContext();
			MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
			PortletURL actionURL = mimeResponse.createActionURL();

			if (uriParameters != null) {
				copyURIParametersToBaseURL(uriParameters, actionURL);
			}

			return actionURL;
		}
		catch (ClassCastException e) {
			throw new MalformedURLException(e.getMessage());
		}
	}

	private PortletURL createRenderURL(FacesContext facesContext, List<URIParameter> uriParameters)
		throws MalformedURLException {

		// TODO: FACES-2648 Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if ((portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE)) {

			try {
				logger.debug("createRenderURL uriParameters=[{0}]", uriParameters);

				ExternalContext externalContext = facesContext.getExternalContext();
				MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
				PortletURL renderURL = mimeResponse.createRenderURL();

				if (uriParameters != null) {
					copyURIParametersToBaseURL(uriParameters, renderURL);
				}

				return renderURL;
			}
			catch (ClassCastException e) {
				throw new MalformedURLException(e.getMessage());
			}
		}
		else {
			throw new MalformedURLException("Unable to create a RenderURL during " + portletRequestPhase.toString());
		}

	}

	private ResourceURL createResourceURL(FacesContext facesContext, List<URIParameter> uriParameters)
		throws MalformedURLException {

		try {
			logger.debug("createResourceURL uriParameters=[{0}]", uriParameters);

			// Ask the portlet container to create a portlet resource URL.
			ExternalContext externalContext = facesContext.getExternalContext();
			MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
			ResourceURL resourceURL = mimeResponse.createResourceURL();

			// If the "javax.faces.resource" token is found in the URL, then
			String bridgeURIAsString = bridgeURI.toString();
			int tokenPos = bridgeURIAsString.indexOf("javax.faces.resource");

			if (tokenPos >= 0) {

				// Parse-out the resourceId
				String resourceId = bridgeURIAsString.substring(tokenPos);

				// Parse-out the resourceName and convert it to a URL parameter on the portlet resource URL.
				int queryStringPos = resourceId.indexOf('?');

				String resourceName = resourceId;

				if (queryStringPos > 0) {
					resourceName = resourceName.substring(0, queryStringPos);
				}

				int slashPos = resourceName.indexOf('/');

				if (slashPos > 0) {
					resourceName = resourceName.substring(slashPos + 1);
				}
				else {
					logger.debug("There is no slash after the [{0}] token in resourceURL=[{1}]", "javax.faces.resource",
						uriParameters);
				}

				resourceURL.setParameter("javax.faces.resource", resourceName);
				logger.debug("Added parameter to portletURL name=[{0}] value=[{1}]", "javax.faces.resource",
					resourceName);
			}

			// Copy the request parameters to the portlet resource URL.
			if (uriParameters != null) {
				copyURIParametersToBaseURL(uriParameters, resourceURL);
			}

			return resourceURL;
		}
		catch (ClassCastException e) {
			throw new MalformedURLException(e.getMessage());
		}
	}

	/**
	 * Determines whether or not the specified files have the same path (prefix) and extension (suffix).
	 *
	 * @param   file1  The first file to compare.
	 * @param   file2  The second file to compare.
	 *
	 * @return  <code>true</code> if the specified files have the same path (prefix) and extension (suffix), otherwise
	 *          <code>false</code>.
	 */
	private boolean matchPathAndExtension(String file1, String file2) {

		boolean match = false;

		String path1 = null;
		int lastSlashPos = file1.lastIndexOf("/");

		if (lastSlashPos > 0) {
			path1 = file1.substring(0, lastSlashPos);
		}

		String path2 = null;
		lastSlashPos = file2.lastIndexOf("/");

		if (lastSlashPos > 0) {
			path2 = file2.substring(0, lastSlashPos);
		}

		if (((path1 == null) && (path2 == null)) || ((path1 != null) && (path2 != null) && path1.equals(path2))) {

			String ext1 = null;
			int lastDotPos = file1.indexOf(".");

			if (lastDotPos > 0) {
				ext1 = file1.substring(lastDotPos);
			}

			String ext2 = null;
			lastDotPos = file2.indexOf(".");

			if (lastDotPos > 0) {
				ext2 = file2.substring(lastDotPos);
			}

			if (((ext1 == null) && (ext2 == null)) || ((ext1 != null) && (ext2 != null) && ext1.equals(ext2))) {
				match = true;
			}
		}

		return match;
	}

	private List<URIParameter> parameterMapToList(Map<String, String[]> parameterMap) {

		List<URIParameter> uriParameters = new ArrayList<URIParameter>();
		Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();

		for (Map.Entry<String, String[]> mapEntry : entrySet) {
			uriParameters.add(new URIParameter(mapEntry.getKey(), mapEntry.getValue()));
		}

		return uriParameters;
	}

	private static class URIParameter {

		// Private Data Members
		private String name;
		private String[] values;

		public URIParameter(String name, String value) {
			this(name, new String[] { value });
		}

		public URIParameter(String name, String[] values) {
			this.name = name;
			this.values = values;
		}

		public String getName() {
			return name;
		}

		public String[] getValues() {
			return values;
		}
	}
}
