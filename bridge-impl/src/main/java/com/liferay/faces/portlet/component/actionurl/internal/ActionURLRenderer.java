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
package com.liferay.faces.portlet.component.actionurl.internal;

import javax.faces.component.UIComponent;
import javax.faces.render.FacesRenderer;
import javax.portlet.ActionRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;

import com.liferay.faces.portlet.component.actionurl.ActionURL;


/**
 * @author  Kyle Stiemann
 */
//J-
@FacesRenderer(componentFamily = ActionURL.COMPONENT_FAMILY, rendererType = ActionURL.RENDERER_TYPE)
//J+
public class ActionURLRenderer extends ActionURLRendererBase {

	@Override
	protected PortletURL getPortletURL(MimeResponse mimeResponse, UIComponent uiComponent) {

		PortletURL actionURL = mimeResponse.createActionURL();
		ActionURL actionURLComponent = (ActionURL) uiComponent;
		String name = actionURLComponent.getName();

		if (name != null) {
			actionURL.setParameter(ActionRequest.ACTION_NAME, name);
		}

		return actionURL;
	}
}
