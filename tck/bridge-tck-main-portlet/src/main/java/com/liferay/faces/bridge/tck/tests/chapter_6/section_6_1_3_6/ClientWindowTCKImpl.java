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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_6;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;


/**
 * @author  Neil Griffin
 */
public class ClientWindowTCKImpl extends ClientWindow {

	private ClientWindow wrappedClientWindow;

	public ClientWindowTCKImpl(ClientWindow clientWindow) {
		this.wrappedClientWindow = clientWindow;
	}

	@Override
	public void decode(FacesContext facesContext) {
		wrappedClientWindow.decode(facesContext);
	}

	@Override
	public String getId() {
		return wrappedClientWindow.getId();
	}

	@Override
	public Map<String, String> getQueryURLParameters(FacesContext facesContext) {
		return wrappedClientWindow.getQueryURLParameters(facesContext);
	}
}
