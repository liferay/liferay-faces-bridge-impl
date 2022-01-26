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
package com.liferay.faces.bridge.demos.portlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.render.ResponseStateManager;
import javax.portlet.ActionParameters;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.GenericFacesPortlet;


/**
 * @author  Neil Griffin
 */
public class ActionURLDemoPortlet extends GenericFacesPortlet {

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
		IOException {

		ActionParameters actionParameters = actionRequest.getActionParameters();

		String viewState = null;

		for (String actionParameterName : actionParameters.getNames()) {

			// With JSF 2.3, the "javax.faces.ViewState" parameter name is prepended with the portlet response
			// namespace and the value of UINamingContainer.getSeparatorChar(facesContext), which is normally a colon.
			// For this reason, and since we're not running within the JSF lifecyle (no access to FacesContext), it is
			// necessary to iterate through the action parameter names in order to find the view state parameter.
			if (actionParameterName.contains(ResponseStateManager.VIEW_STATE_PARAM)) {
				viewState = actionParameters.getValue(actionParameterName);

				break;
			}
		}

		String nonFacesPostback = actionParameters.getValue("Non-Faces-Postback");

		if ((viewState == null) && ("true".equalsIgnoreCase(nonFacesPostback))) {

			MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();

			mutableRenderParameters.setValue("Non-Faces-Postback", nonFacesPostback);

			String foo = actionParameters.getValue("foo");

			if (foo != null) {
				mutableRenderParameters.setValue("foo", foo);
			}
		}
		else {
			super.processAction(actionRequest, actionResponse);
		}
	}

	@Override
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		RenderParameters renderParameters = renderRequest.getRenderParameters();
		String nonFacesPostback = renderParameters.getValue("Non-Faces-Postback");

		if (nonFacesPostback == null) {
			super.doView(renderRequest, renderResponse);
		}
		else {
			PrintWriter writer = renderResponse.getWriter();
			writer.write("<p>");
			writer.write("Successfully executed ");
			writer.write("<strong>non-JSF postback</strong> in ");
			writer.write(ActionURLDemoPortlet.class.getName());
			writer.write("</p>");

			String foo = renderParameters.getValue("foo");
			writer.write("<p>");
			writer.write("Action Parameter:<pre>foo=" + foo + "</pre>");
			writer.write("</p>");

			PortletURL renderURL = renderResponse.createRenderURL();
			MutableRenderParameters mutableRenderParameters = renderURL.getRenderParameters();
			mutableRenderParameters.setValue("componentPrefix", "portlet");
			mutableRenderParameters.setValue("componentName", "actionurl");
			mutableRenderParameters.setValue("componentUseCase", "general");
			writer.write("<p>");
			writer.write("<a href=\"");
			writer.write(renderURL.toString());
			writer.write("\">");
			writer.write("Return to portlet:actionURL in the Liferay Faces Showcase");
			writer.write("</a>");
			writer.write("</p>");
		}
	}
}
