/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.portlet.component.renderurl.internal;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import com.liferay.faces.portlet.component.renderurl.RenderURLBase;


/**
 * @author  Kyle Stiemann
 */
public class AbstractRenderURLRenderer extends RenderURLRendererBase {

	@Override
	protected BaseURL getBaseURL(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
		PortletURL renderURL = getPortletURL(mimeResponse, uiComponent);
		RenderURLBase renderURLcomponent = (RenderURLBase) uiComponent;
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		if (renderURLcomponent.isCopyCurrentRenderParameters()) {

			Map<String, String[]> currentRenderParameters = portletRequest.getParameterMap();
			String namespace = facesContext.getExternalContext().encodeNamespace("");

			for (Map.Entry<String, String[]> param : currentRenderParameters.entrySet()) {

				String name = param.getKey();

				if (name.contains(namespace)) {
					name = name.substring(namespace.length());
				}

				renderURL.setParameter(name, param.getValue());
			}
		}

		String portletModeString = renderURLcomponent.getPortletMode();
		PortletMode portletMode;

		if (portletModeString != null) {
			portletMode = new PortletMode(portletModeString);
		}
		else {
			portletMode = portletRequest.getPortletMode();
		}

		try {
			renderURL.setPortletMode(portletMode);
		}
		catch (PortletModeException e) {
			throw new IOException(e);
		}

		String windowStateString = renderURLcomponent.getWindowState();
		WindowState windowState;

		if (windowStateString != null) {
			windowState = new WindowState(windowStateString);
		}
		else {
			windowState = portletRequest.getWindowState();
		}

		try {
			renderURL.setWindowState(windowState);
		}
		catch (WindowStateException e) {
			throw new IOException(e);
		}

		return renderURL;
	}

	protected PortletURL getPortletURL(MimeResponse mimeResponse, UIComponent uiComponent) {
		return mimeResponse.createRenderURL();
	}
}
