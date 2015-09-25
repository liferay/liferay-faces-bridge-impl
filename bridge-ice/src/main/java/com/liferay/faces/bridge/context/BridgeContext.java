/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;

import com.liferay.faces.bridge.config.BridgeConfig;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeContext {

	public static BridgeContext getCurrentInstance() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String elExpression = "bridgeContext";
		ELResolver elResolver = facesContext.getApplication().getELResolver();

		return (BridgeContext) elResolver.getValue(facesContext.getELContext(), null, elExpression);
	}

	public abstract BridgeConfig getBridgeConfig();

	public abstract PortletConfig getPortletConfig();
}
