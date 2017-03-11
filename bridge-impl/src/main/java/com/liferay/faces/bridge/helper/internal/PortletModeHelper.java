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
package com.liferay.faces.bridge.helper.internal;

import javax.portlet.PortletMode;


/**
 * @author  Neil Griffin
 */
public class PortletModeHelper {

	// Public Constants
	public static final String PORTLET_MODE_VIEW = PortletMode.VIEW.toString();
	public static final String PORTLET_MODE_EDIT = PortletMode.EDIT.toString();
	public static final String PORTLET_MODE_HELP = PortletMode.HELP.toString();
	public static final String[] PORTLET_MODE_NAMES = new String[] {
			PORTLET_MODE_VIEW, PORTLET_MODE_EDIT, PORTLET_MODE_HELP
		};

}
