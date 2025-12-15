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
package com.liferay.faces.portlet.component.resourceurl.internal;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;
import jakarta.portlet.BaseURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.faces.component.PortletResourceURL;


/**
 * @author  Kyle Stiemann
 */

//J-
@FacesRenderer(componentFamily = PortletResourceURL.COMPONENT_FAMILY, rendererType = "jakarta.portlet.faces.ResourceURL")
//J+
public class ResourceURLRenderer extends ResourceURLRendererBase {

	@Override
	protected BaseURL createBaseURL(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
		jakarta.portlet.ResourceURL resourceURL = mimeResponse.createResourceURL();
		PortletResourceURL resourceURLComponent = (PortletResourceURL) uiComponent;
		String cacheability = resourceURLComponent.getCacheability();
		resourceURL.setCacheability(cacheability);

		String id = resourceURLComponent.getId();

		if (id != null) {
			resourceURL.setResourceID(id);
		}

		return resourceURL;
	}

	@Override
	protected Boolean getSecure(UIComponent uiComponent) {
		return ((PortletResourceURL) uiComponent).getSecure();
	}

	@Override
	protected String getVar(UIComponent uiComponent) {
		return ((PortletResourceURL) uiComponent).getVar();
	}

	@Override
	protected boolean isEscapeXml(UIComponent uiComponent) {
		return ((PortletResourceURL) uiComponent).isEscapeXml();
	}
}
