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
package com.liferay.faces.bridge.tck.common.util.faces.render;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.Renderer;


/**
 * @author  Michael Freedman
 */
public class TCK_PortletUrlRenderer extends Renderer {
	private Renderer mWrapped;

	public TCK_PortletUrlRenderer(Renderer r) {
		mWrapped = r;
	}

	public String convertClientId(FacesContext facesContext, String clientId) {
		return mWrapped.convertClientId(facesContext, clientId);
	}

	public void decode(FacesContext facesContext, UIComponent component) {
		mWrapped.decode(facesContext, component);
	}

	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.write("<tck-portlet><portlet-url>");
		mWrapped.encodeBegin(facesContext, component);
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		mWrapped.encodeChildren(facesContext, component);
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		mWrapped.encodeEnd(facesContext, component);

		ResponseWriter writer = facesContext.getResponseWriter();
		writer.write("</portlet-url></tck-portlet>");
	}

	public Object getConvertedValue(FacesContext facesContext, UIComponent component, Object submittedValue)
		throws ConverterException {
		return mWrapped.getConvertedValue(facesContext, component, submittedValue);
	}

	public boolean getRendersChildren() {
		return mWrapped.getRendersChildren();
	}

}
