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
package com.liferay.faces.bridge.application.internal;

import java.util.List;
import java.util.Map;

import javax.faces.application.NavigationCase;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.portlet.faces.Bridge;


/**
 * @author  Neil Griffin
 */
public interface BridgeNavigationCase {

	/**
	 * Whereas the JSF API {@link NavigationCase#getParameters()} documentation states that this method returns
	 * parameters that are to be included for a navigation-case that contains a redirect, in a portlet environment it
	 * must also return parameters in the non-redirect case.
	 *
	 * @return  The {@link Map} of parameter values.
	 */
	public Map<String, List<String>> getParameters();

	/**
	 * Returns the string representation of the {@link PortletMode} associated with the {@link
	 * Bridge#PORTLET_MODE_PARAMETER} in the to-view-id value of the navigation-case.
	 */
	String getPortletMode();

	/**
	 * Returns the string representation of the {@link WindowState} associated with the {@link
	 * Bridge#PORTLET_WINDOWSTATE_PARAMETER} in the to-view-id value of the navigation-case.
	 */
	String getWindowState();
}
