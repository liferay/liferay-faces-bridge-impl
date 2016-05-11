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
package com.liferay.portal.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.cdi.portlet.bridge.filter.HttpServletRequestAdapter;


/**
 * Since the Liferay CDI Portlet Bridge has a dependency on the {@link PortalUtil#getHttpServletRequest(PortletRequest)}
 * method, this class is necessary to provide a runtime compatibility layer.
 *
 * @author  Neil Griffin
 */
public class PortalUtil {

	public static HttpServletRequest getHttpServletRequest(PortletRequest portletRequest) {
		return new HttpServletRequestAdapter(portletRequest);
	}
}
