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
package com.liferay.faces.bridge.util.internal;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeConfig;

import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;


/**
 * @author  Neil Griffin
 */
public class RequestMapUtil {

	public static BridgeConfig getBridgeConfig(FacesContext facesContext) {
		return getBridgeConfig(facesContext.getExternalContext());
	}

	public static BridgeConfig getBridgeConfig(ExternalContext externalContext) {
		return getBridgeConfig((PortletRequest) externalContext.getRequest());
	}

	public static BridgeConfig getBridgeConfig(PortletRequest portletRequest) {
		return (BridgeConfig) portletRequest.getAttribute(BridgeConfig.class.getName());
	}

	public static BridgeRequestScope getBridgeRequestScope(FacesContext facesContext) {
		return getBridgeRequestScope(facesContext.getExternalContext());
	}

	public static BridgeRequestScope getBridgeRequestScope(ExternalContext externalContext) {
		return getBridgeRequestScope((PortletRequest) externalContext.getRequest());
	}

	public static BridgeRequestScope getBridgeRequestScope(PortletRequest portletRequest) {
		return (BridgeRequestScope) portletRequest.getAttribute(BridgeRequestScope.class.getName());
	}

	public static PortletConfig getPortletConfig(FacesContext facesContext) {
		return getPortletConfig(facesContext.getExternalContext());
	}

	public static PortletConfig getPortletConfig(ExternalContext externalContext) {
		return getPortletConfig((PortletRequest) externalContext.getRequest());
	}

	public static PortletConfig getPortletConfig(PortletRequest portletRequest) {
		return (PortletConfig) portletRequest.getAttribute(PortletConfig.class.getName());
	}
}
