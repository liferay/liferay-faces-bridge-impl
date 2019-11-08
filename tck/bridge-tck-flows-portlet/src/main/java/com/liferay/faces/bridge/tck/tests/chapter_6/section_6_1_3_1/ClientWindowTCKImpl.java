/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.lifecycle.ClientWindowWrapper;


/**
 * @author  Neil Griffin
 */
public class ClientWindowTCKImpl extends ClientWindowWrapper {

	private Map<String, String> queryURLParameters;
	private ClientWindow wrappedClientWindow;

	public ClientWindowTCKImpl(ClientWindow clientWindow) {
		this.queryURLParameters = new HashMap<String, String>();
		this.queryURLParameters.put(ClientWindowTestUtil.TCK_CUSTOM_CLIENT_WINDOW_PARAM_NAME,
			ClientWindowTestUtil.TCK_CUSTOM_CLIENT_WINDOW_PARAM_VALUE);
		this.queryURLParameters = Collections.unmodifiableMap(queryURLParameters);
		this.wrappedClientWindow = clientWindow;
	}

	@Override
	public Map<String, String> getQueryURLParameters(FacesContext context) {
		return queryURLParameters;
	}

	@Override
	public ClientWindow getWrapped() {
		return wrappedClientWindow;
	}
}
