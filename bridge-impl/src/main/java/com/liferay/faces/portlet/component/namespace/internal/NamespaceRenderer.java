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
package com.liferay.faces.portlet.component.namespace.internal;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.portlet.faces.component.PortletNamespace;


/**
 * @author  Neil Griffin
 */

//J-
@FacesRenderer(componentFamily = PortletNamespace.COMPONENT_FAMILY, rendererType = "javax.portlet.faces.Namespace")
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
