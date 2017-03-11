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

import javax.portlet.BaseURL;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import com.liferay.faces.util.helper.BooleanHelper;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class PortletURLHelper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PortletURLHelper.class);

	public static void setPortletMode(PortletURL portletURL, String portletMode, PortletRequest portletRequest) {

		if (portletMode != null) {

			try {
				PortletMode candidatePortletMode = new PortletMode(portletMode);

				if (portletRequest.isPortletModeAllowed(candidatePortletMode)) {
					portletURL.setPortletMode(candidatePortletMode);
				}
				else {
					// TCK TestPage118: encodeActionURLWithInvalidModeRenderTest
				}
			}
			catch (PortletModeException e) {
				logger.error(e.getMessage() + " portletMode=[" + portletMode + "]");
			}
		}
	}

	public static void setSecure(BaseURL baseURL, String secure) {
		setSecure(baseURL, BooleanHelper.isTrueToken(secure));
	}

	public static void setSecure(BaseURL baseURL, boolean secure) {

		try {
			baseURL.setSecure(secure);
		}
		catch (PortletSecurityException e) {

			// Portlet containers like Pluto 3.0 throws PortletSecurityException when the secure URL feature is
			// unsupported. In such cases, log the exception message at the DEBUG level in order to prevent unnecessary
			// stacktraces in the log.
			logger.debug(e.getMessage());
		}
	}

	public static void setWindowState(PortletURL portletURL, String windowState, PortletRequest portletRequest) {

		if (windowState != null) {

			try {
				WindowState candidateWindowState = new WindowState(windowState);

				if (portletRequest.isWindowStateAllowed(candidateWindowState)) {
					portletURL.setWindowState(candidateWindowState);
				}
				else {
					// TCK TestPage120: encodeActionURLWithInvalidWindowStateRenderTest
				}
			}
			catch (WindowStateException e) {
				logger.error(e.getMessage() + " windowState=[" + windowState + "]");
			}
		}
	}
}
