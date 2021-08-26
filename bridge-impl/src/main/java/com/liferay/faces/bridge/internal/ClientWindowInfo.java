/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.internal;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;


/**
 * This class provides a facade over the JSF 2.2 API in order to minimize diffs across branches.
 *
 * @author  Neil Griffin
 */
public class ClientWindowInfo {

	// Private Data Members
	private ClientWindow clientWindow;

	public ClientWindowInfo(ExternalContext externalContext) {
		this.clientWindow = externalContext.getClientWindow();
	}

	public String getId() {

		if (clientWindow == null) {
			return null;
		}
		else {
			return clientWindow.getId();
		}
	}

	public Map<String, String> getUrlParameters(FacesContext facesContext) {

		if (clientWindow == null) {
			return null;
		}
		else {
			return clientWindow.getQueryURLParameters(facesContext);
		}
	}

	public boolean isRenderModeEnabled(FacesContext facesContext) {

		if (clientWindow == null) {
			return false;
		}
		else {
			return clientWindow.isClientWindowRenderModeEnabled(facesContext);
		}
	}
}
