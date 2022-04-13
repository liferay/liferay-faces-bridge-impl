/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.demos.portlet;

import java.io.IOException;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderParameters;
import javax.portlet.faces.Bridge;


/**
 * @author  Neil Griffin
 */
public class ShowcasePortlet extends ActionURLDemoPortlet {

	@Override
	public void renderHeaders(HeaderRequest headerRequest, HeaderResponse headerResponse) throws PortletException,
		IOException {

		RenderParameters renderParameters = headerRequest.getRenderParameters();
		String componentPrefix = renderParameters.getValue("componentPrefix");
		String componentName = renderParameters.getValue("componentName");

		if ((componentPrefix != null) && (componentName != null)) {
			String viewId = "/views/component.xhtml";
			headerRequest.setAttribute(Bridge.VIEW_ID, viewId);
		}

		super.renderHeaders(headerRequest, headerResponse);
	}
}
