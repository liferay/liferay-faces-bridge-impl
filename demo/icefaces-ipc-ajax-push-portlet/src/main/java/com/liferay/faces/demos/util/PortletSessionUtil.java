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
package com.liferay.faces.demos.util;

import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;


/**
 * <a href="PortletSessionUtil.java.html"><b><i>View Source</i></b></a>
 *
 * <p>This utility class is necessary to simulate JSF "session" scope across multiple portlets. By default, the JSF
 * framework defines session-scoped managed beans in faces-config.xml within PortletSession.PORTLET_SCOPE instead of
 * PortletSession.APPLICATION_SCOPE, which defines a scope that can share attribute values between portlets.</p>
 *
 * @author  Neil Griffin
 */
public class PortletSessionUtil {

	public static final String CUSTOMER_LIST = "CUSTOMER_LIST";
	public static final String SELECTED_CUSTOMER = "SELECTED_CUSTOMER";

	public static Object getSharedSessionAttribute(String key) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		PortletSession portletSession = (PortletSession) facesContext.getExternalContext().getSession(false);

		return portletSession.getAttribute(key, PortletSession.APPLICATION_SCOPE);
	}

	public static void setSharedSessionAttribute(String key, Object value) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		PortletSession portletSession = (PortletSession) facesContext.getExternalContext().getSession(false);
		portletSession.setAttribute(key, value, PortletSession.APPLICATION_SCOPE);
	}
}
