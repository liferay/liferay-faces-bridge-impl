/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.liferay.faces.bridge.client.internal.BridgeScriptUtil;
import com.liferay.faces.bridge.render.BodyScriptEncoder;
import com.liferay.faces.util.client.Script;
import com.liferay.faces.util.context.FacesRequestContext;


/**
 * @author  Neil Griffin
 */
public class BodyScriptEncoderImpl implements BodyScriptEncoder {

	@Override
	public void encodeScripts(FacesContext facesContext, ResponseWriter responseWriter, UIComponent uiComponent)
		throws IOException {

		FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();
		List<Script> scripts = facesRequestContext.getScripts();

		responseWriter.startElement("script", uiComponent);
		responseWriter.writeAttribute("type", "text/javascript", null);
		BridgeScriptUtil.writeScripts(responseWriter, scripts);
		responseWriter.endElement("script");
	}
}
