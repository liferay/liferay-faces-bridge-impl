/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
