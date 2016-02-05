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

import javax.portlet.PortalContext;
import javax.portlet.ResourceRequest;
import javax.portlet.filter.ResourceRequestWrapper;

import com.liferay.faces.bridge.context.BridgePortalContext;


/**
 * @author  Neil Griffin
 */
public class ResourceRequestBridgeImpl extends ResourceRequestWrapper {

	// Private Data Members
	private BridgePortalContext bridgePortalContext;

	public ResourceRequestBridgeImpl(ResourceRequest resourceRequest, BridgePortalContext bridgePortalContext) {
		super(resourceRequest);
		this.bridgePortalContext = bridgePortalContext;
	}

	@Override
	public Object getAttribute(String name) {

		// If the specified name is a Servlet-API reserved attribute name, then the JSF runtime is attempting to
		// determine the viewId for a webapp environment. Because of this, it is necessary to return null so that the
		// JSF runtime will attempt to determine the viewId a different way, namely by calling
		// ExternalContext#getRequestPathInfo() or ExternalContext#getRequestServletPath().
		if ("javax.servlet.include.path_info".equals(name) || "javax.servlet.include.servlet_path".equals(name)) {
			return null;
		}
		else {
			return super.getAttribute(name);
		}
	}

	@Override
	public PortalContext getPortalContext() {
		return bridgePortalContext;
	}
}
