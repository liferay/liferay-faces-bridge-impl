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
package com.liferay.faces.portlet.component.namespace.internal;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;
import jakarta.portlet.faces.component.PortletNamespace;


/**
 * @author  Neil Griffin
 */

//J-
@FacesRenderer(componentFamily = PortletNamespace.COMPONENT_FAMILY, rendererType = "jakarta.portlet.faces.Namespace")
//J+
public class NamespaceRenderer extends NamespaceRendererBase {

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		// no-op
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		// no-op
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		String namespace = facesContext.getExternalContext().encodeNamespace("");
		PortletNamespace portletNamespaceComponent = (PortletNamespace) uiComponent;
		String var = portletNamespaceComponent.getVar();

		if (var == null) {

			ResponseWriter responseWriter = facesContext.getResponseWriter();
			responseWriter.write(namespace);
		}
		else {

			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.getRequestMap().put(var, namespace);
		}
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
