/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.filter.internal;

import javax.portlet.PortletResponse;
import javax.portlet.filter.PortletResponseWrapper;


/**
 * @author  Neil Griffin
 */
public final class PortletContainerDetectorUtil {

	private PortletContainerDetectorUtil() {
		throw new AssertionError();
	}

	/**
	 * Determines whether or not the specified {@link javax.portlet.PortletResponse} is one created by Pluto Portal. If
	 * the specified {@link javax.portlet.PortletResponse} is an instance of {@link
	 * javax.portlet.filter.PortletResponseWrapper} then it will work with the wrapped {@link
	 * javax.portlet.PortletResponse}.
	 *
	 * @param   portletResponse  The current {@link javax.portlet.PortletResponse}.
	 *
	 * @return  true if the specified portletResponse was created by Pluto.
	 */
	public static boolean isPlutoPortletResponse(PortletResponse portletResponse) {

		if (portletResponse != null) {

			while (portletResponse instanceof PortletResponseWrapper) {
				PortletResponseWrapper portletResponseWrapper = (PortletResponseWrapper) portletResponse;
				portletResponse = portletResponseWrapper.getResponse();
			}

			return portletResponse.getClass().getName().startsWith("org.apache.pluto");
		}
		else {
			return false;
		}
	}
}
