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
package com.liferay.faces.bridge.context.internal;

import javax.faces.FactoryFinder;
import javax.faces.context.Flash;
import javax.faces.context.FlashFactory;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_2_0_FlashImpl extends ExternalContextCompat_1_2_Impl {

	// Lazy-Initialized Data Members
	private Flash flash;

	public ExternalContextCompat_2_0_FlashImpl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {
		super(portletContext, portletRequest, portletResponse);
	}

	/**
	 * @see    {@link ExternalContext#getFlash()}
	 * @since  JSF 2.0
	 */
	@Override
	public Flash getFlash() {

		if (flash == null) {
			FlashFactory flashFactory = (FlashFactory) FactoryFinder.getFactory(FactoryFinder.FLASH_FACTORY);
			flash = flashFactory.getFlash(true);
		}

		return flash;
	}

	// NOTE: PROPOSED-FOR-JSR344-API
	// http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-1070
	// NOTE: PROPOSED-FOR-BRIDGE3-API (Called by BridgeRequestScope in order to restore the Flash scope)
	// https://issues.apache.org/jira/browse/PORTLETBRIDGE-207
	public void setFlash(Flash flash) {
		this.flash = flash;
	}

	protected HttpServletResponse createFlashHttpServletResponse() {

		// JSF 2.2 version of the bridge does not have a BridgeFlash.
		return null;
	}

	protected boolean isBridgeFlashServletResponseRequired() {

		// JSF 2.2 version of the bridge does not have a BridgeFlash.
		return false;
	}
}
