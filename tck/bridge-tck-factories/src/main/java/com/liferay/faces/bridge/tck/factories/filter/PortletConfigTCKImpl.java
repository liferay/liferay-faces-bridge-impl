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

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.filter.PortletConfigWrapper;


/**
 * @author  Neil Griffin
 */
public class PortletConfigTCKImpl extends PortletConfigWrapper {

	private PortletContext portletContext;

	public PortletConfigTCKImpl(PortletConfig portletConfig) {
		super(portletConfig);
		this.portletContext = new PortletContextTCKImpl(portletConfig.getPortletContext());
	}

	@Override
	public String getInitParameter(String name) {

		if ("tck".equals(name)) {

			// portletConfigAlternativeTest
			return "true";
		}

		return super.getInitParameter(name);
	}

	@Override
	public PortletContext getPortletContext() {
		return portletContext;
	}

	@Override
	public String getPortletName() {

		String portletName = super.getPortletName();

		if (portletName.contains("portletNameAlternativeTest")) {
			return "tckPortletName";
		}

		return portletName;
	}
}
