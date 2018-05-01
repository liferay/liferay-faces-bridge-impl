/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.portlet.BaseURL;
import javax.portlet.MimeResponse;

import com.liferay.faces.portlet.component.resourceurl.ResourceURL;


/**
 * @author  Kyle Stiemann
 */

//J-
@FacesRenderer(componentFamily = ResourceURL.COMPONENT_FAMILY, rendererType = ResourceURL.RENDERER_TYPE)
//J+
public class ResourceURLRenderer extends ResourceURLRendererBase {

	@Override
	protected BaseURL createBaseURL(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ExternalContext externalContext = facesContext.getExternalContext();
		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
		javax.portlet.ResourceURL resourceURL = mimeResponse.createResourceURL();
		ResourceURL resourceURLComponent = (ResourceURL) uiComponent;
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
		return ((ResourceURL) uiComponent).getSecure();
	}

	@Override
	protected String getVar(UIComponent uiComponent) {
		return ((ResourceURL) uiComponent).getVar();
	}

	@Override
	protected boolean isEscapeXml(UIComponent uiComponent) {
		return ((ResourceURL) uiComponent).isEscapeXml();
	}
}
