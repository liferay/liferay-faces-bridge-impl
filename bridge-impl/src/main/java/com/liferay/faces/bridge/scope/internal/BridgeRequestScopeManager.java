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

import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;


/**
 * This interface defines a contract for managing a cache of {@link BridgeRequestScope} instances.
 *
 * @author  Neil Griffin
 */
public interface BridgeRequestScopeManager {

	/**
	 * Removes all {@link BridgeRequestScope} instances that are associated with the specified {@link FacesContext}.
	 * This should be called if the portlet container unloads an portlet individually.
	 *
	 * @param  portletConfig  The current {@link PortletConfig}.
	 */
	public void removeBridgeRequestScopesByPortlet(PortletConfig portletConfig);

	/**
	 * Removes all of the {@link BridgeRequestScope} instances from the underlying cache that are associated with the
	 * specified {@link HttpSession}. This method is meant to be called from a {@link HttpSessionListener} when a
	 * session is invalidated or expires.
	 *
	 * @param  httpSession  The current {@link HttpSession}.
	 */
	void removeBridgeRequestScopesBySession(HttpSession httpSession);
}
