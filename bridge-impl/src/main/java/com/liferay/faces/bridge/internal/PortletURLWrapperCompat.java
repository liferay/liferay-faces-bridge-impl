/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.internal;

import javax.portlet.PortletURL;

import com.liferay.faces.bridge.filter.internal.PortletURLWrapper;


/**
 * This class provides a compatibility layer that isolates differences related Portlet 3.0 and 2.0.
 *
 * @author  Neil Griffin
 */
public class PortletURLWrapperCompat extends PortletURLWrapper {

	// Private Data Members
	private PortletURL portletURL;

	public PortletURLWrapperCompat(PortletURL portletURL) {
		this.portletURL = portletURL;
	}

	@Override
	public PortletURL getWrapped() {
		return portletURL;
	}
}
