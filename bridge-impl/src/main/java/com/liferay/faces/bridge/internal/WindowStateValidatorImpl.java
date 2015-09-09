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
package com.liferay.faces.bridge.internal;

import javax.portlet.WindowState;

import com.liferay.faces.bridge.WindowStateValidator;


/**
 * @author  Neil Griffin
 */
public class WindowStateValidatorImpl implements WindowStateValidator {

	// Private Constants
	private static final String[] WINDOW_STATES = new String[] {
			WindowState.MAXIMIZED.toString(), WindowState.MINIMIZED.toString(), WindowState.NORMAL.toString()
		};

	public boolean isValid(String windowState) {

		boolean valid = false;

		for (String curWindowState : WINDOW_STATES) {

			if (curWindowState.equals(windowState)) {
				valid = true;

				break;
			}
		}

		return valid;
	}
}
