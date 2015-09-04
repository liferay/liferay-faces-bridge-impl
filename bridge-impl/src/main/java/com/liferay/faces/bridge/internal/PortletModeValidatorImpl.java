/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
