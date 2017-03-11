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
package com.liferay.faces.bridge.scope.internal;

import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.ClientWindow;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeRequestScopeCompat_2_2_Impl extends BridgeRequestScopeCompatImpl {

	private static final String BRIDGE_REQ_SCOPE_ATTR_CLIENT_WINDOW = "com.liferay.faces.bridge.clientWindow";

	protected void restoreClientWindow(ExternalContext externalContext) {
		ClientWindow clientWindow = (ClientWindow) getAttribute(BRIDGE_REQ_SCOPE_ATTR_CLIENT_WINDOW);
		externalContext.setClientWindow(clientWindow);
	}

	protected void saveClientWindow(ExternalContext externalContext) {
		ClientWindow clientWindow = externalContext.getClientWindow();
		setAttribute(BRIDGE_REQ_SCOPE_ATTR_CLIENT_WINDOW, clientWindow);
	}
}
