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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.context.url.BridgeURI;
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
	private Boolean escaped;
	private Boolean external;
	private Boolean hierarchical;
	private Map<String, String[]> parameters;
	private Boolean pathRelative;
	private Bridge.PortletPhase portletPhase;
	private Boolean portletScheme;
	private String query;
	private String stringValue;
	private URI uri;

	public BridgeURIImpl(String uri) throws URISyntaxException {
		this.stringValue = uri;
		this.uri = new URI(uri);
		portletScheme = "portlet".equals(this.uri.getScheme());

		if (portletScheme) {
			int queryPos = uri.indexOf('?');

			if (queryPos > 0) {
				query = uri.substring(queryPos + 1);
			}
		}
	}

	@Override
	public String toString() {
		return stringValue;
	}

	@Override
	public String getContextRelativePath(String contextPath) {

		String contextRelativePath = null;

		// If the URI is not external, then determine the relative path of the URI based on the specified context
		// path.
		if (!isExternal(contextPath)) {

			String path = uri.getPath();

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
	public boolean isEscaped() {

		if (escaped == null) {

			escaped = Boolean.FALSE;

			String query = getQuery();

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
	public boolean isAbsolute() {
		return uri.isAbsolute();
	}

	@Override
	public boolean isOpaque() {
		return portletScheme || uri.isOpaque();
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
	public boolean isExternal(String contextPath) {

		if (external == null) {

			external = Boolean.TRUE;

			if (portletScheme) {
				external = Boolean.FALSE;
			}
			else {

				if (!isAbsolute()) {

					if (((contextPath != null) && stringValue.startsWith(contextPath)) ||
							stringValue.startsWith(RELATIVE_PATH_PREFIX)) {
						external = Boolean.FALSE;
					}
				}
			}

			if (stringValue.startsWith("wsrp_rewrite")) {
				external = Boolean.FALSE;
			}
		}

		return external;
	}

	@Override
	public boolean isHierarchical() {

		if (hierarchical == null) {

			hierarchical = Boolean.FALSE;

			if ((isAbsolute() && uri.getSchemeSpecificPart().startsWith("/")) || isRelative()) {
				hierarchical = Boolean.TRUE;
			}
		}

		return hierarchical;
	}

	@Override
	public Map<String, String[]> getParameterMap() {

		if (parameters == null) {
			parameters = Collections.unmodifiableMap(URLUtil.parseParameterMapValuesArray(uri.toString()));
		}

		return parameters;
	}

	@Override
	public String getPath() {
		return uri.getPath();
	}

	@Override
	public Bridge.PortletPhase getPortletPhase() {

		if (portletPhase == null) {

			String uriAsString = uri.toString();

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
			return uri.getQuery();
		}
		else {
			return query;
		}
	}
}
