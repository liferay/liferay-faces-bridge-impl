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
package com.liferay.faces.portlet.component.actionurl.internal;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.portlet.ActionURL;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;
import javax.portlet.MutablePortletParameters;
import javax.portlet.faces.component.PortletActionURL;
import javax.portlet.faces.component.PortletParam;


/**
 * @author  Neil Griffin
 */
public abstract class ActionURLRendererCompat extends ActionURLRendererBase {

	protected enum ParamCopyOption {
		ALL_PUBLIC_PRIVATE, NONE
	}

	protected ActionURL createActionURL(ExternalContext externalContext, ParamCopyOption paramCopyOption) {

		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();

		if (paramCopyOption == ParamCopyOption.ALL_PUBLIC_PRIVATE) {
			return mimeResponse.createActionURL(MimeResponse.Copy.ALL);
		}
		else {
			return mimeResponse.createActionURL(MimeResponse.Copy.NONE);
		}
	}

	@Override
	protected void processParamChildren(UIComponent uiComponent, BaseURL baseURL) {

		MutablePortletParameters mutablePortletParameters;

		ActionURL actionURL = (ActionURL) baseURL;
		PortletActionURL portletActionURL = (PortletActionURL) uiComponent;

		if ("render".equalsIgnoreCase(portletActionURL.getType())) {
			mutablePortletParameters = actionURL.getRenderParameters();
		}
		else {
			mutablePortletParameters = actionURL.getActionParameters();
		}

		List<UIComponent> children = uiComponent.getChildren();

		for (UIComponent child : children) {

			if (child instanceof PortletParam) {

				PortletParam portletParam = (PortletParam) child;
				String name = portletParam.getName();
				String value = portletParam.getValue();

				mutablePortletParameters.setValue(name, value);
			}
		}
	}
}
