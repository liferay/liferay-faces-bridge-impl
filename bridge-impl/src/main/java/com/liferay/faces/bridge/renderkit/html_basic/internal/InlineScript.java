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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


/**
 * @author  Neil Griffin
 */
public class InlineScript extends UIComponentBase {

	// Private Data Members
	private String script;

	public InlineScript() {
		// Zero-arg constructor is necessary to support restoration of saved state by the state manager.
	}

	public InlineScript(String script, String libraryName) {
		this.script = script;

		Map<String, Object> attributes = getAttributes();
		attributes.put("name", libraryName + "inlinescript" + script.hashCode());
		attributes.put("library", libraryName);
	}

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		ResponseWriter responseWriter = facesContext.getResponseWriter();
		responseWriter.startElement("script", this);
		responseWriter.writeAttribute("type", "text/javascript", null);
		responseWriter.writeText(script, null);
	}

	@Override
	public void encodeEnd(FacesContext facesContext) throws IOException {
		ResponseWriter responseWriter = facesContext.getResponseWriter();
		responseWriter.endElement("script");
	}

	@Override
	public String getFamily() {
		return "facelets.LiteralText";
	}
}
