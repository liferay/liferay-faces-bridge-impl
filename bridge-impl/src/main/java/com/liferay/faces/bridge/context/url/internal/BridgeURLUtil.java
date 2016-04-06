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

import java.util.List;
import java.util.Map;

import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.context.url.BridgeURI;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public final class BridgeURLUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLUtil.class);

	private BridgeURLUtil() {
		throw new AssertionError("should never be called");
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
	private static boolean matchPathAndExtension(String file1, String file2) {

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

	/**
	 * @return  the viewId if the URL targets a Faces View. Otherwise returns <code>null</code>.
	 */
	public static String getFacesViewTarget(BridgeURI bridgeURI, String contextPath,
		List<ConfiguredServletMapping> configuredFacesServletMappings) {
		return getFacesViewTarget(bridgeURI, contextPath, null, configuredFacesServletMappings);
	}

	/**
	 * @return  the viewId if the URL targets a Faces View. Otherwise returns <code>null</code>.
	 */
	/* package-private */ static String getFacesViewTarget(BridgeURI bridgeURI, String contextPath, String viewId,
		List<ConfiguredServletMapping> configuredFacesServletMappings) {

		String facesViewTarget = null;
		String contextRelativePath = bridgeURI.getContextRelativePath(contextPath);

		if ((viewId != null) && (viewId.equals(contextRelativePath))) {
			facesViewTarget = viewId;
		}
		else {

			// If the context relative path maps to an actual Faces View due to an implicit servlet mapping or an
			// explicit servlet-mapping entry in the WEB-INF/web.xml descriptor, then the context relative path
			// is a faces view target.
			if (isMappedToFacesServlet(contextRelativePath, configuredFacesServletMappings)) {
				facesViewTarget = contextRelativePath;
			}

			// Otherwise,
			else {
				String potentialFacesViewId;

				// If the context relative path is not available, then
				if (contextRelativePath == null) {

					// TCK TestPage005 (modeViewIDTest)
					// * viewId="/tests/modeViewIdTest.xhtml"
					//
					// TCK TestPage042 (requestRenderIgnoresScopeViaCreateViewTest)
					// TCK TestPage043 (requestRenderRedisplayTest)
					// TCK TestPage044 (requestRedisplayOutOfScopeTest)
					// TCK TestPage049 (renderRedirectTest)
					// TCK TestPage050 (ignoreCurrentViewIdModeChangeTest)
					// TCK TestPage051 (exceptionThrownWhenNoDefaultViewIdTest)
					// * viewId="/tests/redisplayRenderNewModeRequestTest.xhtml"
					//
					// TCK TestPage073 (scopeAfterRedisplayResourcePPRTest)
					// * viewId="/tests/redisplayResourceAjaxResult.xhtml"
					//
					// TCK TestPage088 (encodeActionURLPortletRenderTest)
					// TCK TestPage089 (encodeActionURLPortletActionTest)
					// * viewId="/tests/singleRequestTest.xhtml"
					//
					// TCK TestPage179 (redirectRenderPRP1Test)
					// * viewId=null
					potentialFacesViewId = viewId;
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
					// * viewId="/tests/nonJSFViewTest.xhtml"
					//
					// TCK TestPage071 (nonFacesResourceTest)
					// * contextRelativeViewPath="/tck/nonFacesResource"
					// * viewId="/tests/nonFacesResourceTest.xhtml"
					//
					// TCK TestPage134 (encodeResourceURLBackLinkTest)
					// * contextRelativePath="/resources/myImage.jpg"
					// * viewId="/tests/singleRequestTest.xhtml"
					//
					// TCK TestPage181 (illegalRedirectRenderTest)
					// * contextRelativeViewPath="/tests/NonJSFView.portlet"
					// * viewId=null
					potentialFacesViewId = contextRelativePath;
				}

				// If the extension/suffix of the context relative path matches that of the viewId at the time of
				// construction, then it is a it is a faces view target.
				if ((viewId != null) && (matchPathAndExtension(viewId, potentialFacesViewId))) {

					// TCK TestPage005 (modeViewIDTest)
					// * contextRelativeViewPath=null
					// * potentialFacesViewId="/tests/modeViewIdTest.xhtml"
					// * viewId="/tests/modeViewIdTest.xhtml"
					facesViewTarget = potentialFacesViewId;

					logger.debug(
						"Regarding path=[{0}] as a Faces view since it has the same path and extension as the current viewId=[{1}]",
						potentialFacesViewId, viewId);
				}
			}
		}

		return facesViewTarget;
	}

	/* package-private */ static boolean isViewIdSelfReferencing(BridgeURI bridgeURI, String namespace) {

		// If the "_jsfBridgeViewId" URL parameter is equal to "_jsfBridgeCurrentView" then the URL is
		// self-referencing.
		Map<String, String[]> parameterMap = bridgeURI.getParameterMap();
		String facesViewIdParameter = getParameter(parameterMap, namespace, Bridge.FACES_VIEW_ID_PARAMETER);

		return Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewIdParameter);
	}

	/* package-private */ static boolean isViewPathSelfReferencing(BridgeURI bridgeURI, String namespace) {

		// If the "_jsfBridgeViewPath" URL parameter is equal to "_jsfBridgeCurrentView" then the URL is
		// self-referencing.
		Map<String, String[]> parameterMap = bridgeURI.getParameterMap();
		String facesViewPathParameter = getParameter(parameterMap, namespace, Bridge.FACES_VIEW_PATH_PARAMETER);

		return Bridge.FACES_USE_CURRENT_VIEW_PARAMETER.equals(facesViewPathParameter);
	}

	public static String getParameter(Map<String, String[]> parameterMap, String namespace, String name) {

		String[] values = parameterMap.get(name);

		if (values == null) {
			values = parameterMap.get(namespace + name);
		}

		String value = null;

		if ((values != null) && (values.length > 0)) {
			value = values[0];
		}

		return value;
	}

	private static boolean isMappedToFacesServlet(String viewPath,
		List<ConfiguredServletMapping> configuredFacesServletMappings) {

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
}
