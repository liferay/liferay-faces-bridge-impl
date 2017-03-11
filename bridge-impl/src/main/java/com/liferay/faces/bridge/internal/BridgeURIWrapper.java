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
package com.liferay.faces.bridge.internal;

import java.util.Map;

import javax.faces.FacesWrapper;
import javax.portlet.faces.Bridge;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeURIWrapper implements BridgeURI, FacesWrapper<BridgeURI> {

	@Override
	public abstract BridgeURI getWrapped();

	@Override
	public String getContextRelativePath(String contextPath) {
		return getWrapped().getContextRelativePath(contextPath);
	}

	@Override
	public String getParameter(String name) {
		return getWrapped().getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return getWrapped().getParameterMap();
	}

	@Override
	public String getPath() {
		return getWrapped().getPath();
	}

	@Override
	public Bridge.PortletPhase getPortletPhase() {
		return getWrapped().getPortletPhase();
	}

	@Override
	public String getQuery() {
		return getWrapped().getQuery();
	}

	@Override
	public boolean isAbsolute() {
		return getWrapped().isAbsolute();
	}

	@Override
	public boolean isEscaped() {
		return getWrapped().isEscaped();
	}

	@Override
	public boolean isExternal(String contextPath) {
		return getWrapped().isExternal(contextPath);
	}

	@Override
	public boolean isHierarchical() {
		return getWrapped().isHierarchical();
	}

	@Override
	public boolean isOpaque() {
		return getWrapped().isOpaque();
	}

	@Override
	public boolean isPathRelative() {
		return getWrapped().isPathRelative();
	}

	@Override
	public boolean isPortletScheme() {
		return getWrapped().isPortletScheme();
	}

	@Override
	public boolean isRelative() {
		return getWrapped().isRelative();
	}

	@Override
	public String removeParameter(String name) {
		return getWrapped().removeParameter(name);
	}

	@Override
	public void setParameter(String name, String value) {
		getWrapped().setParameter(name, value);
	}
}
