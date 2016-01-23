/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import javax.portlet.PortletMode;

import com.liferay.faces.bridge.PortletModeValidator;


/**
 * @author  Neil Griffin
 */
public class PortletModeValidatorImpl implements PortletModeValidator {

	// Private Constants
	private static final String[] PORTLET_MODES = new String[] {
			PortletMode.VIEW.toString(), PortletMode.EDIT.toString(), PortletMode.HELP.toString()
		};

	public boolean isValid(String portletMode) {

		boolean valid = false;

		for (String curPortletMode : PORTLET_MODES) {

			if (curPortletMode.equals(portletMode)) {
				valid = true;

				break;
			}
		}

		return valid;
	}
}
