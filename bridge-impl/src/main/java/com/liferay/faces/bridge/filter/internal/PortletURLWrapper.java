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
package com.liferay.faces.bridge.filter.internal;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class PortletURLWrapper extends BaseURLWrapper implements PortletURL, Wrapper<PortletURL> {

	@Override
	public abstract PortletURL getWrapped();

	public PortletMode getPortletMode() {
		return getWrapped().getPortletMode();
	}

	public WindowState getWindowState() {
		return getWrapped().getWindowState();
	}

	public void removePublicRenderParameter(String name) {
		getWrapped().removePublicRenderParameter(name);
	}

	public void setPortletMode(PortletMode portletMode) throws PortletModeException {
		getWrapped().setPortletMode(portletMode);
	}

	public void setWindowState(WindowState windowState) throws WindowStateException {
		getWrapped().setWindowState(windowState);
	}
}
