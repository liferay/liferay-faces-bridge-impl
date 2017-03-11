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
package com.liferay.faces.portlet.component.portleturl.internal;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;


/**
 * @author  Neil Griffin
 */
public abstract class PortletURLRenderer extends PortletURLRendererBase {

	protected abstract javax.portlet.PortletURL createPortletURL(ExternalContext externalContext,
		UIComponent uiComponent);

	protected abstract String getPortletMode(UIComponent uiComponent);

	protected abstract String getWindowState(UIComponent uiComponent);

	protected abstract boolean isCopyCurrentRenderParameters(UIComponent uiComponent);

	@Override
	protected BaseURL createBaseURL(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		javax.portlet.PortletURL portletURL = createPortletURL(externalContext, uiComponent);
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		String portletModeString = getPortletMode(uiComponent);
		PortletMode portletMode;

		if (portletModeString != null) {
			portletMode = new PortletMode(portletModeString);
		}
		else {
			portletMode = portletRequest.getPortletMode();
		}

		try {
			portletURL.setPortletMode(portletMode);
		}
		catch (PortletModeException e) {
			throw new IOException(e);
		}

		String windowStateString = getWindowState(uiComponent);
		WindowState windowState;

		if (windowStateString != null) {
			windowState = new WindowState(windowStateString);
		}
		else {
			windowState = portletRequest.getWindowState();
		}

		try {
			portletURL.setWindowState(windowState);
		}
		catch (WindowStateException e) {
			throw new IOException(e);
		}

		return portletURL;
	}
}
