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
package com.liferay.faces.demos.bean;

import javax.faces.context.FacesContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;


/**
 * @author  Kyle Stiemann
 */
public class URLBean {

	private PortletURL viewRenderURL;
	private PortletURL editRenderURL;
	private PortletURL helpRenderURL;

	public PortletURL getEditRenderURL() {

		if (editRenderURL == null) {
			editRenderURL = createRenderURL(PortletMode.EDIT);
		}

		return editRenderURL;
	}

	public PortletURL getHelpRenderURL() {

		if (helpRenderURL == null) {
			helpRenderURL = createRenderURL(PortletMode.HELP);
		}

		return helpRenderURL;
	}

	public PortletURL getViewRenderURL() {

		if (viewRenderURL == null) {

			viewRenderURL = createRenderURL(PortletMode.VIEW);
			viewRenderURL.setParameter("foo", "bar");
		}

		return viewRenderURL;
	}

	private PortletURL createRenderURL(PortletMode portletMode) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		RenderResponse renderResponse = (RenderResponse) facesContext.getExternalContext().getResponse();
		PortletURL renderURL = renderResponse.createRenderURL();

		try {
			renderURL.setPortletMode(portletMode);
		}
		catch (PortletModeException e) {
			throw new IllegalArgumentException(e);
		}

		return renderURL;
	}
}
