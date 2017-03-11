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
package com.liferay.faces.bridge.demos.portlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.render.ResponseStateManager;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
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

		String viewState = actionRequest.getParameter(ResponseStateManager.VIEW_STATE_PARAM);

		String nonFacesPostback = actionRequest.getParameter("Non-Faces-Postback");

		if ((viewState == null) && ("true".equalsIgnoreCase(nonFacesPostback))) {

			actionResponse.setRenderParameter("Non-Faces-Postback", nonFacesPostback);

			String foo = actionRequest.getParameter("foo");

			if (foo != null) {
				actionResponse.setRenderParameter("foo", foo);
			}
		}
		else {
			super.processAction(actionRequest, actionResponse);
		}
	}

	@Override
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		String nonFacesPostback = renderRequest.getParameter("Non-Faces-Postback");

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

			String foo = renderRequest.getParameter("foo");
			writer.write("<p>");
			writer.write("Action Parameter:<pre>foo=" + foo + "</pre>");
			writer.write("</p>");

			PortletURL renderURL = renderResponse.createRenderURL();
			renderURL.setParameter("componentPrefix", "portlet");
			renderURL.setParameter("componentName", "actionurl");
			renderURL.setParameter("componentUseCase", "general");
			writer.write("<p>");
			writer.write("<a href=\"" + renderURL.toString() + "\">");
			writer.write("Return to portlet:actionURL in the Liferay Faces Showcase");
			writer.write("</a>");
			writer.write("</p>");
		}
	}
}
