/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import jakarta.portlet.faces.Bridge;

import com.liferay.faces.bridge.util.internal.URLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.render.FacesURLEncoder;


/**
 * @author  Neil Griffin
 */
public class BridgeURI {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURI.class);

	// Private Constants
	private static final String RELATIVE_PATH_PREFIX = "../";

	// Private Final Data Members
	private final String encoding;

	// Private Data Members
	private boolean absolute;
	private boolean escaped;
	private Boolean external;
	private String fragment;
	private Boolean hierarchical;
	private String host;
	private String namespace;
	private boolean opaque;
	private Map<String, String[]> parameters;
	private String path;
	private Boolean pathRelative;
	private Bridge.PortletPhase portletPhase;
	private int port;
	private Boolean portletScheme;
	private String portletSchemeType;
	private String query;
	private String scheme;
	private String schemeSpecificPart;
	private String stringValue;
	private String userInfo;

	public BridgeURI(String uri, String namespace, FacesURLEncoder facesURLEncoder, String encoding)
		throws URISyntaxException, UnsupportedEncodingException {

		escaped = false;

		if (uri != null) {

			int ampersandPos = uri.indexOf("&");

			while (ampersandPos > 0) {

				String queryPart = uri.substring(ampersandPos);

				if (queryPart.startsWith("&amp;")) {
					escaped = true;
					ampersandPos = uri.indexOf("&", ampersandPos + 1);
				}
				else {
					escaped = false;

					break;
				}
			}
		}

		this.encoding = encoding;
		this.namespace = namespace;

		String encodedURI = URLUtil.encodeURL(uri, facesURLEncoder, encoding);
		URI tempURI = new URI(encodedURI);

		this.absolute = tempURI.isAbsolute();
		this.fragment = tempURI.getRawFragment();
		this.host = tempURI.getHost();
		this.opaque = tempURI.isOpaque();
		this.port = tempURI.getPort();
		this.path = tempURI.getRawPath();
		this.query = tempURI.getRawQuery();
		this.stringValue = encodedURI;
		this.scheme = tempURI.getScheme();
		this.schemeSpecificPart = tempURI.getRawSchemeSpecificPart();
		this.userInfo = tempURI.getRawUserInfo();

		this.portletScheme = "portlet".equals(scheme);

		if (this.portletScheme) {

			int portletPos = encodedURI.indexOf("portlet:");
			int queryPos = encodedURI.indexOf('?');

			if (queryPos > 0) {
				this.query = encodedURI.substring(queryPos + 1);
				this.portletSchemeType = encodedURI.substring(portletPos + 8, queryPos);
			}
			else {
				this.portletSchemeType = encodedURI.substring(portletPos + 8);
			}
		}
	}

	/**
	 * Sets the parameters of the underlying {@link BridgeURI#getParameterMap()} according to the specified parameter
	 * map.
	 */
	public void addParameters(Map<String, String> parameters) {
		addParameters(parameters, String.class);
	}

	/**
	 * Returns the path component of the URI, relative to the specified context-path.
	 */
	public String getContextRelativePath(String contextPath) {

		String contextRelativePath = null;

		// If the URI is not external, then determine the relative path of the URI based on the specified context
		// path.
		if (!isExternal(contextPath)) {

			if ((path != null) && (path.length() > 0)) {

				// If the context path is present, then remove it since we want the return value to be a path that
				// is relative to the context path.
				int contextPathPos = path.indexOf(contextPath);

				if (contextPathPos >= 0) {
					contextRelativePath = path.substring(contextPathPos + contextPath.length());
				}
				else {
					contextRelativePath = path;
				}
			}
		}

		return contextRelativePath;
	}

	/**
	 * Returns the first value of the underlying {@link BridgeURI#getParameterMap()} with the specified <code>
	 * name</code>.
	 */
	public String getParameter(String name) {

		Map<String, String[]> parameterMap = getParameterMap();
		String[] values = parameterMap.get(name);

		if (values == null) {
			values = parameterMap.get(namespace + name);
		}

		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		else {
			return null;
		}
	}

	/**
	 * Returns an mutable {@link Map} representing the URI query parameters.
	 */
	public Map<String, String[]> getParameterMap() {

		if (parameters == null) {
			parameters = URLUtil.parseParameterMapValuesArray(stringValue, encoding);
		}

		return parameters;
	}

	/**
	 * Returns the path component of the URI.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the {@link jakarta.portlet.faces.Bridge.PortletPhase} associated with this URI. Note that the value will be
	 * null if the URI does not begin with the "portlet:" scheme/prefix.
	 */
	public Bridge.PortletPhase getPortletPhase() {

		if (portletPhase == null) {

			String uriAsString = toString();

			if (uriAsString != null) {

				if (portletScheme) {

					if (uriAsString.startsWith("portlet:action")) {
						portletPhase = Bridge.PortletPhase.ACTION_PHASE;
					}
					else if (uriAsString.startsWith("portlet:render")) {
						portletPhase = Bridge.PortletPhase.RENDER_PHASE;
					}
					else if (uriAsString.startsWith("portlet:resource")) {
						portletPhase = Bridge.PortletPhase.RESOURCE_PHASE;
					}
					else {
						portletPhase = Bridge.PortletPhase.RESOURCE_PHASE;
						logger.warn("Invalid keyword after 'portlet:' in URI=[{0}]", uriAsString);
					}
				}
			}
			else {
				portletPhase = Bridge.PortletPhase.RESOURCE_PHASE;
				logger.warn("Unable to determine portlet phase in null URI");
			}
		}

		return portletPhase;
	}

	/**
	 * Returns the query component, meaning all characters after the question-mark of the scheme-specific-part of the
	 * URI.
	 */
	public String getQuery() {

		if (query == null) {

			StringBuilder queryBuilder = new StringBuilder();
			Map<String, String[]> parameterMap = getParameterMap();
			Set<Map.Entry<String, String[]>> mapEntries = parameterMap.entrySet();
			boolean firstParam = true;

			for (Map.Entry<String, String[]> mapEntry : mapEntries) {

				if (firstParam) {
					firstParam = false;
				}
				else {
					queryBuilder.append("&");
				}

				String name = mapEntry.getKey();
				String[] values = mapEntry.getValue();

				try {

					name = URLUtil.encodeParameterNameOrValue(name, encoding);
					values = URLUtil.encodeParameterValues(values, encoding);
				}
				catch (UnsupportedEncodingException e) {

					logger.error("Unable to encode parameter name=\"{0}\" and values=\"{1}\" with encoding \"{2}\".",
						name, values, encoding);
					logger.error(e);
				}

				if (values != null) {

					boolean firstValue = true;

					for (String value : values) {

						if (firstValue) {
							firstValue = false;
						}
						else {
							queryBuilder.append("&");
						}

						queryBuilder.append(name);
						queryBuilder.append("=");
						queryBuilder.append(value);
					}
				}
				else {
					queryBuilder.append(name);
				}
			}

			query = queryBuilder.toString();
		}

		return query;
	}

	/**
	 * Determines whether or not the URI is absolute, meaning it contains a scheme component. Note that according to the
	 * class-level documentation of {@link java.net.URI} an absolute URI is non-relative.
	 *
	 * @return  Returns true if the URI is absolute, otherwise returns false.
	 */
	public boolean isAbsolute() {
		return absolute;
	}

	/**
	 * Determines whether or not the URI is escaped.
	 *
	 * @return  <code>true</code> if all occurrences of the ampersand character appear as &amp; otherwise, returns
	 *          <code>false</code>.
	 */
	public boolean isEscaped() {
		return escaped;
	}

	/**
	 * Determines whether or not the URI is external with respect to a context path, meaning it is not absolute and does
	 * not start with the context path.
	 *
	 * @param   contextPath  The context path for determining whether or not the URI is external.
	 *
	 * @return  <code>true</code> if external, otherwise <code>false</code>.
	 */
	public boolean isExternal(String contextPath) {

		if (external == null) {

			external = Boolean.TRUE;

			String uriAsString = toString();

			if (portletScheme) {
				external = Boolean.FALSE;
			}
			else {

				if (!isAbsolute()) {

					if (((contextPath != null) && uriAsString.startsWith(contextPath)) ||
							uriAsString.startsWith(RELATIVE_PATH_PREFIX)) {
						external = Boolean.FALSE;
					}
				}
			}

			if (uriAsString.startsWith("wsrp_rewrite")) {
				external = Boolean.FALSE;
			}
		}

		return external;
	}

	/**
	 * Determines whether or not the URI is hierarchical, meaning it is either 1) absolute and the scheme-specific part
	 * begins with a forward-slash character, or 2) is relative.
	 *
	 * @return  <code>true</code> if the URI is hierarchical, otherwise <code>false</code>.
	 */
	public boolean isHierarchical() {

		if (hierarchical == null) {

			hierarchical = Boolean.FALSE;

			if ((isAbsolute() && schemeSpecificPart.startsWith("/")) || isRelative()) {
				hierarchical = Boolean.TRUE;
			}
		}

		return hierarchical;
	}

	/**
	 * Determines whether or not the URI is opaque, meaning it is absolute and its scheme component does not begin with
	 * a forward-slash character. For more information see {@link java.net.URI#isOpaque()}.
	 *
	 * @return  <code>true</code> if the URI is opaque, otherwise <code>false</code>.
	 */
	public boolean isOpaque() {
		return portletScheme || opaque;
	}

	/**
	 * Determines whether or not the path component of the URI is relative, meaning it does not begin with a
	 * forward-slash character.
	 *
	 * @return  <code>true</code> if the path is relative, otherwise <code>false</code>.
	 */
	public boolean isPathRelative() {

		if (pathRelative == null) {

			pathRelative = Boolean.FALSE;

			String path = getPath();

			if ((path != null) && (path.length() > 0) &&
					(!path.startsWith("/") || path.startsWith(RELATIVE_PATH_PREFIX))) {
				pathRelative = Boolean.TRUE;
			}

		}

		return pathRelative;
	}

	/**
	 * Determines whether or not the URI begins with the "portlet:" scheme.
	 *
	 * @return  <code>true</code> if the URI begins with the "portlet:" scheme, otherwise <code>false</code>.
	 */
	public boolean isPortletScheme() {
		return portletScheme;
	}

	/**
	 * Determines whether or not the URI is relative, meaning it does not have a scheme component. Note that according
	 * to the class-level documentation of {@link java.net.URI} a relative URI is non-absolute.
	 *
	 * @return  Returns true if the URI is relative, otherwise returns false.
	 */
	public boolean isRelative() {
		return !isAbsolute();
	}

	/**
	 * Removes the entry of the underlying {@link BridgeURI#getParameterMap()} according to the specified <code>
	 * name</code>.
	 *
	 * @return  the first value of the underlying {@link BridgeURI#getParameterMap()} with the specified <code>
	 *          name</code>.
	 */
	public String removeParameter(String name) {

		String[] values = null;
		Map<String, String[]> parameterMap = getParameterMap();

		if (parameterMap.containsKey(name)) {
			values = parameterMap.remove(name);
			invalidateToString();
		}
		else {

			String namespacedParameterName = namespace + name;

			if (parameterMap.containsKey(namespacedParameterName)) {
				values = parameterMap.remove(namespacedParameterName);
				invalidateToString();
			}
		}

		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		else {
			return null;
		}
	}

	/**
	 * Sets the <code>values</code> of the underlying {@link BridgeURI#getParameterMap()} according to the specified
	 * <code>name</code>.
	 */
	public void setParameter(String name, String[] values) {

		getParameterMap().put(name, values);
		invalidateToString();
	}

	/**
	 * Sets the <code>value</code> of the underlying {@link BridgeURI#getParameterMap()} according to the specified
	 * <code>name</code>.
	 */
	public void setParameter(String name, String value) {
		setParameter(name, new String[] { value });
	}

	public void setParameters(Map<String, String[]> parameters) {

		getParameterMap().clear();
		addParameters(parameters, String[].class);
	}

	/**
	 * Returns a string-based representation of the URI.
	 */
	@Override
	public String toString() {

		if (stringValue == null) {

			StringBuilder uriBuilder = new StringBuilder();

			if (scheme != null) {
				uriBuilder.append(scheme);
				uriBuilder.append(':');

				if (portletScheme) {
					uriBuilder.append(portletSchemeType);
				}
			}

			if ((userInfo != null) || (host != null) || (port != -1)) {
				uriBuilder.append("//");

				if (userInfo != null) {
					uriBuilder.append(userInfo);
					uriBuilder.append('@');
				}

				if (host != null) {
					uriBuilder.append(host);
				}

				if (port != -1) {
					uriBuilder.append(':');
					uriBuilder.append(port);
				}
			}

			if (path != null) {
				uriBuilder.append(path);
			}

			String queryString = getQuery();

			if ((queryString != null) && (queryString.length() > 0)) {
				uriBuilder.append('?');
				uriBuilder.append(queryString);
			}

			if (fragment != null) {
				uriBuilder.append('#');
				uriBuilder.append(fragment);
			}

			stringValue = uriBuilder.toString();
		}

		return stringValue;
	}

	private void addParameters(Map parameters, Class<?> mapValueType) {

		Set<Map.Entry> entrySet = parameters.entrySet();

		for (Map.Entry mapEntry : entrySet) {

			String key = (String) mapEntry.getKey();

			if (mapValueType.equals(String.class)) {
				setParameter(key, (String) mapEntry.getValue());
			}
			else if (mapValueType.equals(String[].class)) {
				setParameter(key, (String[]) mapEntry.getValue());
			}
			else {
				throw new IllegalArgumentException("Parameter map value type must be String or String[].");
			}
		}
	}

	private void invalidateToString() {
		query = null;
		stringValue = null;
	}
}
