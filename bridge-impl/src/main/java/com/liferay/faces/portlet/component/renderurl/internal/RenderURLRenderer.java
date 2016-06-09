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
package com.liferay.faces.portlet.component.renderurl.internal;

import javax.faces.component.UIComponent;
// JSF 2: import javax.faces.render.FacesRenderer;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;

import com.liferay.faces.portlet.component.renderurl.RenderURL;


/**
 * @author  Kyle Stiemann
 */

//J-
// JSF 2: @FacesRenderer(componentFamily = RenderURL.COMPONENT_FAMILY, rendererType = RenderURL.RENDERER_TYPE)
//J+
public class RenderURLRenderer extends RenderURLRendererBase {

	@Override
	protected PortletURL createPortletURL(MimeResponse mimeResponse, UIComponent uiComponent) {
		return mimeResponse.createRenderURL();
	}

	@Override
	protected String getPortletMode(UIComponent uiComponent) {
		return ((RenderURL) uiComponent).getPortletMode();
	}

	@Override
	protected String getWindowState(UIComponent uiComponent) {
		return ((RenderURL) uiComponent).getWindowState();
	}

	@Override
	protected boolean isCopyCurrentRenderParameters(UIComponent uiComponent) {
		return ((RenderURL) uiComponent).isCopyCurrentRenderParameters();
	}
}
