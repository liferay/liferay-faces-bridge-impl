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
package com.liferay.faces.portlet.component.renderurl.internal;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.render.FacesRenderer;
import javax.portlet.PortletURL;
import javax.portlet.faces.component.PortletRenderURL;


/**
 * @author  Kyle Stiemann
 */

//J-
@FacesRenderer(componentFamily = PortletRenderURL.COMPONENT_FAMILY, rendererType = "javax.portlet.faces.RenderURL")
//J+
public class RenderURLRenderer extends RenderURLRendererCompat {

	@Override
	protected PortletURL createPortletURL(ExternalContext externalContext, UIComponent uiComponent) {

		if (isCopyCurrentRenderParameters(uiComponent)) {
			return createRenderURL(externalContext, ParamCopyOption.ALL_PUBLIC_PRIVATE);
		}
		else {
			return createRenderURL(externalContext, ParamCopyOption.NONE);
		}
	}

	@Override
	protected String getPortletMode(UIComponent uiComponent) {
		return ((PortletRenderURL) uiComponent).getPortletMode();
	}

	@Override
	protected Boolean getSecure(UIComponent uiComponent) {
		return ((PortletRenderURL) uiComponent).getSecure();
	}

	@Override
	protected String getVar(UIComponent uiComponent) {
		return ((PortletRenderURL) uiComponent).getVar();
	}

	@Override
	protected String getWindowState(UIComponent uiComponent) {
		return ((PortletRenderURL) uiComponent).getWindowState();
	}

	@Override
	protected boolean isCopyCurrentRenderParameters(UIComponent uiComponent) {
		return ((PortletRenderURL) uiComponent).isCopyCurrentRenderParameters();
	}

	@Override
	protected boolean isEscapeXml(UIComponent uiComponent) {
		return ((PortletRenderURL) uiComponent).isEscapeXml();
	}
}
