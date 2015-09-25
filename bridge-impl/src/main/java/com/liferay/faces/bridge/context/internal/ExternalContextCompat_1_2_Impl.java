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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.faces.bridge.config.internal.PortletConfigParam;
import com.liferay.faces.bridge.context.BridgeContext;
import com.liferay.faces.bridge.context.IncongruityContext;


/**
 * This class provides a compatibility layer that contains JSF 1.0/1.1/1.2 public methods that subclasses need to
 * override.
 *
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_1_2_Impl extends ExternalContext {

	// Private Constants
	private static final String TRINIDAD_DISABLE_DIALOG_OUTCOMES =
		"org.apache.myfaces.trinidad.DISABLE_DIALOG_OUTCOMES";

	// Protected Data Members
	protected BridgeContext bridgeContext;
	protected IncongruityContext incongruityContext;
	protected boolean manageIncongruities;
	protected PortletContext portletContext;
	protected PortletRequest portletRequest;
	protected PortletResponse portletResponse;

	public ExternalContextCompat_1_2_Impl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		this.portletContext = portletContext;
		this.portletRequest = portletRequest;
		this.portletResponse = portletResponse;

		// Get the BridgeContext.
		this.bridgeContext = BridgeContext.getCurrentInstance();

		this.incongruityContext = bridgeContext.getIncongruityContext();

		// Determine whether or not lifecycle incongruities should be managed.
		PortletConfig portletConfig = bridgeContext.getPortletConfig();
		this.manageIncongruities = PortletConfigParam.ManageIncongruities.getBooleanValue(portletConfig);

		// Disable the Apache Trinidad 1.2.x "dialog:" URL feature as it causes navigation-handler failures during the
		// EVENT_PHASE of the portlet lifecycle. For more information on the feature, see:
		// http://jsfatwork.irian.at/book_de/trinidad.html
		portletContext.setAttribute(TRINIDAD_DISABLE_DIALOG_OUTCOMES, Boolean.TRUE);
	}

	/**
	 * Note: The reason why this method appears here in {@link ExternalContextCompat_1_2_Impl} is because the method was
	 * first introduced with JSF 1.0 and and also because it needs to be overridden by {@link
	 * ExternalContextCompat_2_2_Impl} since it has special requirements for JSF 2.2.
	 *
	 * @see    {@link ExternalContext#encodeActionURL(String, Map)}
	 * @since  JSF 1.0
	 */
	@Override
	public String encodeActionURL(String url) {
		return bridgeContext.encodeActionURL(url).toString();
	}
}
