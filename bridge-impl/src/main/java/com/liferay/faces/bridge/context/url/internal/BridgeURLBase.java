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
package com.liferay.faces.bridge.context.url.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeFactoryFinder;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.PortletModeValidator;
import com.liferay.faces.bridge.PortletModeValidatorFactory;
import com.liferay.faces.bridge.WindowStateValidator;
import com.liferay.faces.bridge.WindowStateValidatorFactory;
import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.bridge.config.internal.BridgeConfigAttributeMap;
import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.bridge.context.url.BridgeURL;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeURLBase implements BridgeURL {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLBase.class);

	// Private Data Members
	private BridgeURI bridgeURI;
	private List<ConfiguredServletMapping> configuredFacesServletMappings;
	private String contextPath;
	private String facesViewTarget;
	private String namespace;
	private Map<String, String[]> parameterMap;
	private String viewId;
	private String viewIdParameterName;

	public BridgeURLBase(BridgeURI bridgeURI, String contextPath, String namespace, String viewId,
		String viewIdParameterName, BridgeConfig bridgeConfig) {
		this.bridgeURI = bridgeURI;
		this.contextPath = contextPath;
		this.namespace = namespace;
		this.viewId = viewId;
		this.viewIdParameterName = viewIdParameterName;
		this.configuredFacesServletMappings = (List<ConfiguredServletMapping>) bridgeConfig.getAttributes().get(
				BridgeConfigAttributeMap.CONFIGURED_FACES_SERVLET_MAPPINGS);
	}

	protected static String baseURLtoString(BaseURL baseURL, boolean isEscaped) {

		String stringValue = null;

		if (baseURL != null) {

			// If the URL string has escaped characters (like %20 for space, etc) then ask the
			// portlet container to create an escaped representation of the URL string.
			if (isEscaped) {

				StringWriter urlWriter = new StringWriter();

				try {

					baseURL.write(urlWriter, true);
					stringValue = urlWriter.toString();
				}
				catch (IOException e) {

					logger.error(e);
					stringValue = baseURL.toString();
				}
			}

			// Otherwise, ask the portlet container to create a normal (non-escaped) string
			// representation of the URL string.
			else {
				stringValue = baseURL.toString();
			}
		}

		return stringValue;
	}

	/**
	 * Returns the query-string part of the URL (without a leading question mark).
	 */
	private static String getQueryString(String url, Map<String, String[]> parameterMap) {

		StringBuilder buf = new StringBuilder();

		// Get the original query-string from the URL.
		String originalQuery = "";
		boolean firstParam = true;
		int pos = url.indexOf("?");

		if (pos >= 0) {
			originalQuery = url.substring(pos + 1);
		}

		pos = originalQuery.indexOf("#");

		String fragmentId = null;

		if (pos > 0) {
			fragmentId = originalQuery.substring(pos);
			originalQuery = originalQuery.substring(0, pos);
		}

		// Keep track of all the parameters that are appended to the return value.
		Map<String, Integer> parameterOccurrenceMap = new HashMap<String, Integer>(parameterMap.size());

		// The TCK expects query parameters to appear in exactly the same order as they do in the query-string of
		// the original URL. For this reason, need to iterate over the parameters found in the original
		// query-string.
		String[] queryParameters = originalQuery.split("[&]");

		// For each parameter found in the original query-string:
		for (String queryParameter : queryParameters) {

			if ((queryParameter != null) && (queryParameter.length() > 0)) {

				// Parse the name and value from the name=value pair.
				String[] nameValueArray = queryParameter.split("[=]");

				String name = null;
				String[] values = null;

				if (nameValueArray.length == 1) {

					name = nameValueArray[0].trim();
					values = new String[] { "" };
				}
				else if (nameValueArray.length == 2) {

					name = nameValueArray[0].trim();

					// If the parameter name is present in the parameter map, then that means it should be appended
					// to the return value. Otherwise, it should not be appended, because absence from the parameter
					// map means that it was deliberately removed.
					values = parameterMap.get(name);
				}

				if ((name == null) || (name.length() == 0)) {
					logger.error("Invalid name=value pair=[{0}] in URL=[{1}]: name cannot be empty", queryParameter,
						url);
				}
				else if ((values == null) || (values.length == 0)) {

					// Note that "javax.portlet.faces.BackLink" is sometimes deliberately removed and therefore is
					// not an error.
					if (!Bridge.BACK_LINK.equals(name)) {
						logger.error("Invalid name=value pair=[{0}] in URL=[{1}]", queryParameter, url);
					}
				}
				else {

					if (firstParam) {
						firstParam = false;
					}
					else {
						buf.append("&");
					}

					buf.append(name);
					buf.append("=");

					Integer parameterOccurrences = parameterOccurrenceMap.get(name);

					if (parameterOccurrences == null) {
						parameterOccurrences = new Integer(0);
					}

					String value = values[parameterOccurrences.intValue()];
					buf.append(value);
					parameterOccurrences = new Integer(parameterOccurrences.intValue() + 1);
					parameterOccurrenceMap.put(name, parameterOccurrences);
				}
			}
		}

		// Now iterate through the entire parameter map to see and append any additional parameters to the return
		// value.
		Set<Map.Entry<String, String[]>> mapEntries = parameterMap.entrySet();

		for (Map.Entry<String, String[]> mapEntry : mapEntries) {

			String name = mapEntry.getKey();

			if (parameterOccurrenceMap.get(name) == null) {
				String[] values = mapEntry.getValue();

				if ((values != null) && (values.length > 0)) {

					if (firstParam) {
						firstParam = false;
					}
					else {
						buf.append("&");
					}

					buf.append(name);
					buf.append("=");
					buf.append(values[0]);
				}
			}
		}

		if (fragmentId != null) {
			buf.append(fragmentId);
		}

		return buf.toString();
	}

	@Override
	public final String removeParameter(String name) {

		Map<String, String[]> parameterMap = getParameterMap();
		String[] values = parameterMap.remove(name);

		if (values == null) {
			values = parameterMap.remove(namespace + name);
		}

		String value = null;

		if ((values != null) && (values.length > 0)) {
			value = values[0];
		}

		return value;
	}

	protected final PortletURL createActionURL(FacesContext facesContext, boolean modeChanged) {
		return createActionURL(facesContext, modeChanged, null);
	}

	protected final PortletURL createActionURL(FacesContext facesContext, boolean modeChanged,
		Set<String> excludedParameterNames) {

		PortletURL actionURL = null;

		try {

			String urlString = toURLWithModifiedParametersString(modeChanged, excludedParameterNames);
			logger.debug("createActionURL fromURL=[{0}]", urlString);

			ExternalContext externalContext = facesContext.getExternalContext();
			MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
			actionURL = mimeResponse.createActionURL();
			copyParameters(urlString, actionURL);
		}
		catch (ClassCastException e) {
			logger.error(e);
		}
		catch (MalformedURLException e) {
			logger.error(e);
		}

		return actionURL;
	}

	protected final PortletURL createRenderURL(FacesContext facesContext, boolean modeChanged) {
		return createRenderURL(facesContext, modeChanged, null);
	}

	protected final PortletURL createRenderURL(FacesContext facesContext, String fromURL) {

		PortletURL renderURL = null;

		// TODO: FACES-2648 Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if ((portletRequestPhase == Bridge.PortletPhase.RENDER_PHASE) ||
				(portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE)) {

			try {

				logger.debug("createRenderURL fromURL=[{0}]", fromURL);

				ExternalContext externalContext = facesContext.getExternalContext();
				MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
				renderURL = mimeResponse.createRenderURL();
				copyParameters(fromURL, renderURL);
			}
			catch (ClassCastException e) {
				logger.error(e);
			}
			catch (MalformedURLException e) {
				logger.error(e);
			}
		}
		else {
			logger.error(new MalformedURLException(
					"Unable to create a RenderURL during " + portletRequestPhase.toString()));
		}

		return renderURL;
	}

	protected final PortletURL createRenderURL(FacesContext facesContext, boolean modeChanged,
		Set<String> excludedParameterNames) {
		return createRenderURL(facesContext, toURLWithModifiedParametersString(modeChanged, excludedParameterNames));
	}

	protected final ResourceURL createResourceURL(FacesContext facesContext, boolean modeChanged) {
		return createResourceURL(facesContext, toURLWithModifiedParametersString(modeChanged, null));
	}

	protected final ResourceURL createResourceURL(FacesContext facesContext, String fromURL) {

		ResourceURL resourceURL = null;

		try {

			logger.debug("createResourceURL fromURL=[{0}]", fromURL);

			// Ask the portlet container to create a portlet resource URL.
			ExternalContext externalContext = facesContext.getExternalContext();
			MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
			resourceURL = mimeResponse.createResourceURL();

			// If the "javax.faces.resource" token is found in the URL, then
			int tokenPos = fromURL.indexOf("javax.faces.resource");

			if (tokenPos >= 0) {

				// Parse-out the resourceId
				String resourceId = fromURL.substring(tokenPos);

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
						fromURL);
				}

				resourceURL.setParameter("javax.faces.resource", resourceName);
				logger.debug("Added parameter to portletURL name=[{0}] value=[{1}]", "javax.faces.resource",
					resourceName);
			}

			// Copy the request parameters to the portlet resource URL.
			copyParameters(fromURL, resourceURL);
		}
		catch (ClassCastException e) {
			logger.error(e);
		}
		catch (MalformedURLException e) {
			logger.error(e);
		}

		return resourceURL;
	}

	protected final ResourceURL createResourceURL(FacesContext facesContext, boolean modeChanged,
		Set<String> excludedParameterNames) {
		return createResourceURL(facesContext, toURLWithModifiedParametersString(modeChanged, excludedParameterNames));
	}

	protected final String toNonEncodedURLString(String url) {

		StringBuilder buf = new StringBuilder();

		Map<String, String[]> parameterMap = getParameterMap();

		if ((parameterMap != null) && !parameterMap.isEmpty()) {

			String urlWithoutParams = url;
			int queryPos = url.indexOf("?");

			if (queryPos >= 0) {
				urlWithoutParams = url.substring(0, queryPos);
			}

			buf.append(urlWithoutParams);

			String queryString = getQueryString(url, parameterMap);

			if (queryString.length() > 0) {

				buf.append("?");
				buf.append(queryString);
			}
		}
		else {
			buf.append(url);
		}

		return buf.toString();
	}

	protected final String toString(boolean modeChanged) {
		return toURLWithModifiedParametersString(modeChanged, null);
	}

	/**
	 * Copies any query paramters present in the specified "from" URL to the specified "to" URL.
	 */
	private void copyParameters(String fromURL, BaseURL toURL) throws MalformedURLException {

		List<RequestParameter> requestParameters = parseRequestParameters(fromURL);

		if (requestParameters != null) {

			for (RequestParameter requestParameter : requestParameters) {
				String name = requestParameter.getName();
				String value = requestParameter.getValue();
				toURL.setParameter(name, value);

				Object[] arguments = new Object[] { name, value };
				logger.debug("Copied parameter to portletURL name=[{0}] value=[{1}]", arguments);
			}
		}
	}

	/**
	 * Parses the specified URL and returns a list of query parameters that are found.
	 *
	 * @param   url  The URL to parse.
	 *
	 * @return  The list of query parameters found.
	 *
	 * @throws  MalformedURLException
	 */
	private List<RequestParameter> parseRequestParameters(String url) throws MalformedURLException {

		List<RequestParameter> requestParameters = null;

		if (url != null) {
			int pos = url.indexOf("?");

			if (pos >= 0) {
				String queryString = url.substring(pos + 1);

				if (queryString.length() > 0) {
					requestParameters = new ArrayList<RequestParameter>();

					String[] queryParameters = queryString.split("[&]");

					for (String queryParameter : queryParameters) {

						String[] nameValueArray = queryParameter.split("[=]");

						if (nameValueArray.length == 1) {

							String name = nameValueArray[0].trim();

							if (name.length() == 0) {
								throw new MalformedURLException("Invalid name/value pair=[" + queryParameter +
									"]: name cannot be empty.");
							}
							else {
								requestParameters.add(new RequestParameter(name, ""));
							}
						}
						else if (nameValueArray.length == 2) {

							String name = nameValueArray[0].trim();

							if (name.length() == 0) {
								throw new MalformedURLException("Invalid name/value pair=[" + queryParameter +
									"]: name cannot be empty.");
							}
							else {

								String value = nameValueArray[1];
								requestParameters.add(new RequestParameter(name, value));
							}
						}
						else {
							throw new MalformedURLException("Invalid name/value pair: " + queryParameter);
						}
					}
				}
			}
		}

		return requestParameters;
	}

	private String toURLWithModifiedParametersString(boolean modeChanged, Set<String> excludedParameterNames) {

		StringBuilder buf = new StringBuilder();
		String uri = bridgeURI.toString();

		int endPos = uri.indexOf("?");

		if (endPos < 0) {
			endPos = uri.length();
		}

		if (bridgeURI.isPortletScheme()) {
			Bridge.PortletPhase urlPortletPhase = bridgeURI.getPortletPhase();

			if (urlPortletPhase == Bridge.PortletPhase.ACTION_PHASE) {
				buf.append(uri.substring("portlet:action".length(), endPos));
			}
			else if (urlPortletPhase == Bridge.PortletPhase.RENDER_PHASE) {
				buf.append(uri.substring("portlet:render".length(), endPos));
			}
			else {
				buf.append(uri.substring("portlet:resource".length(), endPos));
			}
		}
		else {
			buf.append(uri.subSequence(0, endPos));
		}

		boolean firstParam = true;

		buf.append("?");

		Set<String> parameterNames = getParameterMap().keySet();
		boolean foundFacesViewIdParam = false;
		boolean foundFacesViewPathParam = false;

		for (String parameterName : parameterNames) {

			boolean addParameter = false;
			String parameterValue = getParameter(parameterName);

			if (Bridge.PORTLET_MODE_PARAMETER.equals(parameterName)) {

				// Only add the "javax.portlet.faces.PortletMode" parameter if it has a valid value.
				if (parameterValue != null) {

					PortletModeValidatorFactory portletModeValidatorFactory = (PortletModeValidatorFactory)
						BridgeFactoryFinder.getFactory(PortletModeValidatorFactory.class);
					PortletModeValidator portletModeValidator = portletModeValidatorFactory.getPortletModeValidator();
					addParameter = portletModeValidator.isValid(parameterValue);
				}
			}
			else if (Bridge.PORTLET_SECURE_PARAMETER.equals(parameterName)) {
				addParameter = ((parameterValue != null) &&
						("true".equalsIgnoreCase(parameterValue) || "false".equalsIgnoreCase(parameterValue)));
			}
			else if (Bridge.PORTLET_WINDOWSTATE_PARAMETER.equals(parameterName)) {

				WindowStateValidatorFactory windowStateValidatorFactory = (WindowStateValidatorFactory)
					BridgeFactoryFinder.getFactory(WindowStateValidatorFactory.class);
				WindowStateValidator windowStateValidator = windowStateValidatorFactory.getWindowStateValidator();
				addParameter = windowStateValidator.isValid(parameterValue);
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

				if (firstParam) {
					firstParam = false;
				}
				else {
					buf.append("&");
				}

				buf.append(parameterName);
				buf.append("=");
				buf.append(parameterValue);
			}
		}

		// If the "_jsfBridgeViewId" and "_jsfBridgeViewPath" parameters are not present in the URL, then add a
		// parameter that indicates the target Faces viewId.
		if (!foundFacesViewIdParam && !foundFacesViewPathParam && (getFacesViewTarget() != null)) {

			if (!bridgeURI.isPortletScheme()) {

				// Note that if the "javax.portlet.faces.PortletMode" parameter is specified, then a mode change is
				// being requested and the target Faces viewId parameter must NOT be added.
				if (!modeChanged) {

					if (!firstParam) {
						buf.append("&");
					}

					buf.append(getViewIdParameterName());
					buf.append("=");

					String contextRelativePath = bridgeURI.getContextRelativePath(contextPath);
					buf.append(contextRelativePath);
				}
			}
		}

		return buf.toString();
	}

	protected final BridgeURI getBridgeURI() {
		return bridgeURI;
	}

	protected final String getContextPath() {
		return contextPath;
	}

	protected final String getFacesViewTarget() {

		if (facesViewTarget == null) {
			facesViewTarget = BridgeURLUtil.getFacesViewTarget(bridgeURI, contextPath, getViewId(),
					configuredFacesServletMappings);
		}

		return facesViewTarget;
	}

	@Override
	public final String getParameter(String name) {

		Map<String, String[]> parameterMap = getParameterMap();

		return BridgeURLUtil.getParameter(parameterMap, namespace, name);
	}

	@Override
	public final void setParameter(String name, String[] value) {
		getParameterMap().put(name, value);
	}

	@Override
	public final void setParameter(String name, String value) {
		getParameterMap().put(name, new String[] { value });
	}

	@Override
	public final Map<String, String[]> getParameterMap() {

		if (parameterMap == null) {
			parameterMap = new LinkedHashMap<String, String[]>(bridgeURI.getParameterMap());
		}

		return parameterMap;
	}

	protected final String getViewId() {
		return viewId;
	}

	protected final String getViewIdParameterName() {
		return viewIdParameterName;
	}

	private static class RequestParameter {

		private String name;
		private String value;

		private RequestParameter(String name, String value) {
			this.name = name;
			this.value = value;
		}

		private String getName() {
			return name;
		}

		private String getValue() {
			return value;
		}
	}
}
