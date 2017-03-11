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
package com.liferay.faces.bridge.tck.common.util.faces.render;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;


/**
 * @author  Michael Freedman
 */
public class TCK_PortletUrlRenderer extends Renderer {
	private Renderer mWrapped;

	public TCK_PortletUrlRenderer(Renderer r) {
		mWrapped = r;
	}

	public String convertClientId(FacesContext context, String clientId) {
		return mWrapped.convertClientId(context, clientId);
	}

	public void decode(FacesContext context, UIComponent component) {
		mWrapped.decode(context, component);
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.write("<tck-portlet><portlet-url>");
		mWrapped.encodeBegin(context, component);
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		mWrapped.encodeChildren(context, component);
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		mWrapped.encodeEnd(context, component);

		ResponseWriter writer = context.getResponseWriter();
		writer.write("</portlet-url></tck-portlet>");
	}

	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
		throws ConverterException {
		return mWrapped.getConvertedValue(context, component, submittedValue);
	}

	public boolean getRendersChildren() {
		return mWrapped.getRendersChildren();
	}

}
