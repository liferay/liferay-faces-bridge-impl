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

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.portlet.PortletMode;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.faces.Bridge;


/**
 * <p>This abstract class defines the contract for a brige-specific {@link NavigationHandler} that fortifies the JSF
 * runtime with the ability to handle to-view-id entries in navigaion-case blocks that respect the {@link
 * Bridge#PORTLET_MODE_PARAMETER} parameter for switching to a different {@link PortletMode} and the {@link
 * Bridge#PORTLET_WINDOWSTATE_PARAMETER} parameter for switching to a different {@link WindowState}. It also has the
 * ability to react to changes in portlet modes that were done programattically by portlet developers that called {@link
 * StateAwareResponse#setWindowState(WindowState)} during the INVOKE_APPLICATION phase of the JSF lifecycle.</p>
 *
 * @author  Neil Griffin
 */
public abstract class BridgeNavigationHandler extends ConfigurableNavigationHandler {

	/**
	 * This method is defined in the {@link NavigationHandler} superclass and must be overridden in the bridge
	 * implementation so that it can handle to-view-id entries in navigaion-case blocks that respect the {@link
	 * Bridge#PORTLET_MODE_PARAMETER} parameter for switching to a different {@link PortletMode} and the {@link
	 * Bridge#PORTLET_WINDOWSTATE_PARAMETER} parameter for switching to a different {@link WindowState}.
	 *
	 * @see  {@link NavigationHandler#handleNavigation(FacesContext, String, String)}
	 */
	@Override
	public abstract void handleNavigation(FacesContext facesContext, String fromAction, String outcome);

	/**
	 * This method must react to changes in portlet modes that were done programatically by portlet developers that
	 * called {@link StateAwareResponse#setWindowState(WindowState)} during the INVOKE_APPLICATION phase of the JSF
	 * lifecycle. The viewId to be rendered is the current viewId in the UIViewRoot.
	 *
	 * @param  facesContext     The current FacesContext.
	 * @param  fromPortletMode  The PortletMode that was rendered prior to the developer calling {@link
	 *                          StateAwareResponse#setWindowState(WindowState)}.
	 * @param  toPortletMode    The PortletMode that is to be switched to.
	 */
	public abstract void handleNavigation(FacesContext facesContext, PortletMode fromPortletMode,
		PortletMode toPortletMode);
}
