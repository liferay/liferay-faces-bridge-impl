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
package com.liferay.faces.bridge.filter.internal;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.RenderResponse;
import javax.portlet.filter.RenderResponseWrapper;

import com.liferay.faces.bridge.context.BridgeContext;


/**
 * @author  Neil Griffin
 */
public class RenderResponseBridgeImpl extends RenderResponseWrapper {

	// Private Data Members
	private String namespace;
	private String namespaceWSRP;

	public RenderResponseBridgeImpl(RenderResponse renderResponse) {
		super(renderResponse);
	}

	@Override
	public String getNamespace() {

		if (namespace == null) {

			namespace = super.getNamespace();

			if (namespace.startsWith("wsrp_rewrite")) {

				BridgeContext bridgeContext = BridgeContext.getCurrentInstance();
				namespace = getNamespaceWSRP(bridgeContext);
			}
		}

		return namespace;
	}

	protected String getNamespaceWSRP(BridgeContext bridgeContext) {

		if (namespaceWSRP == null) {

			PortletConfig portletConfig = bridgeContext.getPortletConfig();
			String portletName = portletConfig.getPortletName();
			PortletContext portletContext = bridgeContext.getPortletContext();
			String portletContextName = portletContext.getPortletContextName();

			namespaceWSRP = portletName + portletContextName;
		}

		return namespaceWSRP;
	}
}
