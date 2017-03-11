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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.util.internal.URLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURIImpl implements BridgeURI {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURIImpl.class);

	// Private Constants
	private static final String RELATIVE_PATH_PREFIX = "../";

	// Private Data Members
	private boolean absolute;
	private Boolean escaped;
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

	public BridgeURIImpl(String namespace, String uri) throws URISyntaxException {

		this.namespace = namespace;

		URI tempURI = new URI(uri);

		this.absolute = tempURI.isAbsolute();
		this.fragment = tempURI.getFragment();
		this.host = tempURI.getHost();
		this.opaque = tempURI.isOpaque();
		this.port = tempURI.getPort();
		this.path = tempURI.getPath();
		this.query = tempURI.getQuery();
		this.stringValue = uri;
		this.scheme = tempURI.getScheme();
		this.schemeSpecificPart = tempURI.getSchemeSpecificPart();
		this.userInfo = tempURI.getUserInfo();

		this.portletScheme = "portlet".equals(scheme);

		if (this.portletScheme) {

			int portletPos = uri.indexOf("portlet:");
			int queryPos = uri.indexOf('?');

			if (queryPos > 0) {
				this.query = uri.substring(queryPos + 1);
				this.portletSchemeType = uri.substring(portletPos + 8, queryPos);
			}
			else {
				this.portletSchemeType = uri.substring(portletPos + 8);
			}
		}
	}

	@Override
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

	@Override
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

	@Override
	public Map<String, String[]> getParameterMap() {

		if (parameters == null) {
			parameters = URLUtil.parseParameterMapValuesArray(stringValue);
		}

		return parameters;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
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

	@Override
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

				String[] values = mapEntry.getValue();

				if (values != null) {

					boolean firstValue = true;

					for (String value : values) {

						if (firstValue) {
							firstValue = false;
						}
						else {
							queryBuilder.append("&");
						}

						queryBuilder.append(mapEntry.getKey());
						queryBuilder.append("=");
						queryBuilder.append(value);
					}
				}
			}

			query = queryBuilder.toString();
		}

		return query;
	}

	@Override
	public boolean isAbsolute() {
		return absolute;
	}

	@Override
	public boolean isEscaped() {

		if (escaped == null) {

			escaped = Boolean.FALSE;

			if (query != null) {

				int ampersandPos = query.indexOf("&");

				while (ampersandPos > 0) {

					String queryPart = query.substring(ampersandPos);

					if (queryPart.startsWith("&amp;")) {
						escaped = Boolean.TRUE;
						ampersandPos = query.indexOf("&", ampersandPos + 1);
					}
					else {
						escaped = Boolean.FALSE;

						break;
					}
				}
			}
		}

		return escaped;
	}

	@Override
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

	@Override
	public boolean isHierarchical() {

		if (hierarchical == null) {

			hierarchical = Boolean.FALSE;

			if ((isAbsolute() && schemeSpecificPart.startsWith("/")) || isRelative()) {
				hierarchical = Boolean.TRUE;
			}
		}

		return hierarchical;
	}

	@Override
	public boolean isOpaque() {
		return portletScheme || opaque;
	}

	@Override
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

	@Override
	public boolean isPortletScheme() {
		return portletScheme;
	}

	@Override
	public boolean isRelative() {
		return !isAbsolute();
	}

	@Override
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

	@Override
	public void setParameter(String name, String[] values) {

		getParameterMap().put(name, values);
		invalidateToString();
	}

	@Override
	public void setParameter(String name, String value) {

		getParameterMap().put(name, new String[] { value });
		invalidateToString();
	}

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

	private void invalidateToString() {
		query = null;
		stringValue = null;
	}
}
