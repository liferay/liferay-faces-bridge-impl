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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.ClientWindow;


/**
 * This is a utility class that acts as a compatibility layer in order to minimize diffs across branches.
 *
 * @author  Neil Griffin
 */
final class ClientWindowTestUtil {

	static String TCK_CUSTOM_CLIENT_WINDOW_PARAM_NAME = "tckCustomClientWindowParam";
	static String TCK_CUSTOM_CLIENT_WINDOW_PARAM_VALUE = "1234";

	private ClientWindowTestUtil() {
		throw new AssertionError();
	}

	static String getClientWindowId(ExternalContext externalContext) {

		ClientWindow clientWindow = externalContext.getClientWindow();

		if (clientWindow != null) {
			return clientWindow.getId();
		}
		else {
			return null;
		}
	}

	static boolean isClientWindowEnabled(ExternalContext externalContext) {
		return "url".equals(externalContext.getInitParameter(ClientWindow.CLIENT_WINDOW_MODE_PARAM_NAME));
	}
}
