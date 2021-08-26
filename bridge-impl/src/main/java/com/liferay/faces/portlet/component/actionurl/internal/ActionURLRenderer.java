/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.portlet.component.actionurl.internal;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.render.FacesRenderer;
import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;

import com.liferay.faces.portlet.component.actionurl.ActionURL;


/**
 * @author  Kyle Stiemann
 */

//J-
@FacesRenderer(componentFamily = ActionURL.COMPONENT_FAMILY, rendererType = ActionURL.RENDERER_TYPE)
//J+
public class ActionURLRenderer extends ActionURLRendererCompat {

	@Override
	protected PortletURL createPortletURL(ExternalContext externalContext, UIComponent uiComponent) {

		javax.portlet.PortletURL actionURL;

		if (isCopyCurrentRenderParameters(uiComponent)) {
			actionURL = createActionURL(externalContext, ParamCopyOption.ALL_PUBLIC_PRIVATE);
		}
		else {
			actionURL = createActionURL(externalContext, ParamCopyOption.NONE);
		}

		ActionURL actionURLComponent = (ActionURL) uiComponent;
		String name = actionURLComponent.getName();

		if (name != null) {
			actionURL.setParameter(ActionRequest.ACTION_NAME, name);
		}

		return actionURL;
	}

	@Override
	protected String getPortletMode(UIComponent uiComponent) {
		return ((ActionURL) uiComponent).getPortletMode();
	}

	@Override
	protected Boolean getSecure(UIComponent uiComponent) {
		return ((ActionURL) uiComponent).getSecure();
	}

	@Override
	protected String getVar(UIComponent uiComponent) {
		return ((ActionURL) uiComponent).getVar();
	}

	@Override
	protected String getWindowState(UIComponent uiComponent) {
		return ((ActionURL) uiComponent).getWindowState();
	}

	@Override
	protected boolean isCopyCurrentRenderParameters(UIComponent uiComponent) {
		return ((ActionURL) uiComponent).isCopyCurrentRenderParameters();
	}

	@Override
	protected boolean isEscapeXml(UIComponent uiComponent) {
		return ((ActionURL) uiComponent).isEscapeXml();
	}
}
