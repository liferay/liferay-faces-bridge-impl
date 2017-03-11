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
package com.liferay.faces.bridge.scope.internal;

import java.io.Serializable;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.RequestAttributeInspector;
import javax.portlet.faces.RequestAttributeInspectorFactory;


/**
 * @author  Neil Griffin
 */
public class RequestAttributeInspectorFactoryImpl extends RequestAttributeInspectorFactory implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 1830965518049901309L;

	@Override
	public RequestAttributeInspector getRequestAttributeInspector(PortletRequest portletRequest,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		return new RequestAttributeInspectorImpl(portletRequest, portletConfig, bridgeConfig);
	}

	@Override
	public RequestAttributeInspectorFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
