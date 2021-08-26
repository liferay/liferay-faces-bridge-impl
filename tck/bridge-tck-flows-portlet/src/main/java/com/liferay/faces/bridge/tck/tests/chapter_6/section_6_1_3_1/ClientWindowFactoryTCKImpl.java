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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.lifecycle.ClientWindowFactory;


/**
 * @author  Neil Griffin
 */
public class ClientWindowFactoryTCKImpl extends ClientWindowFactory {

	private ClientWindowFactory wrappedClientWindowFactory;

	public ClientWindowFactoryTCKImpl(ClientWindowFactory clientWindowFactory) {
		this.wrappedClientWindowFactory = clientWindowFactory;
	}

	@Override
	public ClientWindow getClientWindow(FacesContext facesContext) {

		ClientWindow wrappedClientWindow = wrappedClientWindowFactory.getClientWindow(facesContext);

		return new ClientWindowTCKImpl(wrappedClientWindow);
	}
}
