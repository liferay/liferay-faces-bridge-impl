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

import javax.faces.context.ExternalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;


/**
 * This class provides a compatibility layer that isolates differences between JSF 2.0 and JSF 2.1.
 *
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_2_1_Impl extends ExternalContextCompat_2_0_Impl {

	public ExternalContextCompat_2_1_Impl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {
		super(portletContext, portletRequest, portletResponse);
	}

	/**
	 * @see    {@link ExternalContext#getSessionMaxInactiveInterval()}
	 * @since  JSF 2.1
	 */
	@Override
	public int getSessionMaxInactiveInterval() {

		PortletSession portletSession = (PortletSession) getSession(true);

		return portletSession.getMaxInactiveInterval();
	}

	/**
	 * @see    {@link ExternalContext#isSecure()}
	 * @since  JSF 2.1
	 */
	@Override
	public boolean isSecure() {
		return portletRequest.isSecure();
	}

	/**
	 * @see    {@link ExternalContext#setSessionMaxInactiveInterval(int)}
	 * @since  JSF 2.1
	 */
	@Override
	public void setSessionMaxInactiveInterval(int sessionMaxInactiveInterval) {
		PortletSession portletSession = (PortletSession) getSession(true);
		portletSession.setMaxInactiveInterval(sessionMaxInactiveInterval);
	}
}
