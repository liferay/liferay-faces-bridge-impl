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
package com.liferay.faces.bridge.application.internal;

import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;


/**
 * @author  Neil Griffin
 */
public class BridgeNavigationUtil {

	/**
	 * Convenience method that applies the parameters found in the specified URL to the specified {@link
	 * StateAwareResponse} by calling methods such as {@link
	 * StateAwareResponse#setPortletMode(javax.portlet.PortletMode)} and {@link
	 * StateAwareResponse#setWindowState(javax.portlet.WindowState)}, etc.
	 */
	public static void navigate(PortletRequest portletRequest, StateAwareResponse stateAwareResponse,
		BridgeRequestScope bridgeRequestScope, Map<String, String[]> parameterMap) throws PortletModeException,
		WindowStateException {

		// For each parameter found in the encoded <to-view-id> Action-URL:
		Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();

		for (Map.Entry<String, String[]> mapEntry : entrySet) {

			String parameterName = mapEntry.getKey();
			String[] parameterValues = mapEntry.getValue();
			String firstParameterValue = null;

			if ((parameterValues != null) && (parameterValues.length > 0)) {
				firstParameterValue = parameterValues[0];
			}

			// If the URL contains the "javax.portlet.faces.PortletMode" parameter, then set the
			// PortletMode on the ActionResponse.
			if (Bridge.PORTLET_MODE_PARAMETER.equals(parameterName)) {

				PortletMode portletMode = new PortletMode(firstParameterValue);

				if (bridgeRequestScope != null) {

					if (!portletRequest.getPortletMode().equals(portletMode) &&
							portletRequest.isPortletModeAllowed(portletMode)) {
						stateAwareResponse.setPortletMode(portletMode);
						bridgeRequestScope.setPortletModeChanged(true);
					}
				}
			}

			// Otherwise, if the URL contains the "javax.portlet.faces.WindowState" parameter, then
			// set the WindowState on the ActionResponse.
			else if (Bridge.PORTLET_WINDOWSTATE_PARAMETER.equals(parameterName)) {

				WindowState windowState = new WindowState(firstParameterValue);

				if (portletRequest.isWindowStateAllowed(windowState)) {
					stateAwareResponse.setWindowState(windowState);
				}
			}

			// Otherwise, if the URL contains the "_jsfBridgeNonFacesView" parameter, then set a
			// render parameter so that the Non-Faces-View will get picked up by the GenericFacesPortlet for
			// dispatch.
			else if (Bridge.NONFACES_TARGET_PATH_PARAMETER.equals(parameterName)) {
				stateAwareResponse.setRenderParameter(Bridge.NONFACES_TARGET_PATH_PARAMETER, firstParameterValue);
			}

			// Otherwise, it's not a special parameter recognized by the bridge. Regardless, set a render
			// parameter so that it can be picked up by the RENDER_RESPONSE phase if necessary.
			else {
				stateAwareResponse.setRenderParameter(parameterName, parameterValues);
			}
		}
	}
}
