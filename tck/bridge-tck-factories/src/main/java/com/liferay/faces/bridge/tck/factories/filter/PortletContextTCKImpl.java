/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.factories.filter;

import javax.portlet.PortletContext;
import javax.portlet.filter.PortletContextWrapper;


/**
 * @author  Neil Griffin
 */
public class PortletContextTCKImpl extends PortletContextWrapper {

	private String portletName;

	public PortletContextTCKImpl(PortletContext portletContext, String portletName) {
		super(portletContext);

		this.portletName = portletName;
	}

	@Override
	public Object getAttribute(String name) {

		if ("tck".equals(name)) {

			// portletContextAlternativeTest
			return "true";
		}

		return super.getAttribute(name);
	}

	@Override
	public String getContextPath() {

		if (portletName.contains("contextPathAlternativeTest")) {
			return "tckPortletContextPath";
		}

		return super.getContextPath();
	}
}
