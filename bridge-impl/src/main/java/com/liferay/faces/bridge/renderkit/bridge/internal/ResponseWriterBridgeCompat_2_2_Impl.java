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
package com.liferay.faces.bridge.renderkit.bridge.internal;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.render.ResponseStateManager;


/**
 * @author  Neil Griffin
 */
public abstract class ResponseWriterBridgeCompat_2_2_Impl extends ResponseWriterBridgeCompat_2_0_Impl {

	// Protected Constants
	protected static final String CLIENT_WINDOW_PARAM = ResponseStateManager.CLIENT_WINDOW_PARAM;

	public ResponseWriterBridgeCompat_2_2_Impl() {
		super();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		// Note that this is no longer necessary for JSF 2.2, so simply delegate to the wrapped ResponseWriter rather
		// than executing the JSF 2.0 method in the parent class.
		getWrapped().write(cbuf, off, len);
	}

	@Override
	public void writePreamble(String preamble) throws IOException {
		// No-op for portlets: http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-1069
	}

	protected void writeClientWindowHiddenField() throws IOException {

		startElement("input", null);
		writeAttribute("type", "hidden", null);

		String clientWindowName = CLIENT_WINDOW_PARAM;

		if (namespacedParameters) {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			String namingContainerId = facesContext.getViewRoot().getContainerClientId(facesContext);
			clientWindowName = namingContainerId + clientWindowName;
		}

		writeAttribute("name", clientWindowName, null);

		// TODO: The following line is a workaround and needs to be fixed in FACES-1798.
		writeAttribute("id", clientWindowName, null);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ClientWindow clientWindow = externalContext.getClientWindow();

		if (clientWindow != null) {
			String clientWindowId = clientWindow.getId();
			writeAttribute("value", clientWindowId, null);
		}

		writeAttribute(ATTRIBUTE_AUTOCOMPLETE, VALUE_OFF, null);
		endElement("input");
	}
}
