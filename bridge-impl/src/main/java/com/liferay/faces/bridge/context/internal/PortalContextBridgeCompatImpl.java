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
package com.liferay.faces.bridge.context.internal;

import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;


/**
 * @author  Kyle Stiemann
 */
public abstract class PortalContextBridgeCompatImpl implements PortalContext {

	public PortalContextBridgeCompatImpl(PortletRequest portletRequest) {
		// no-op
	}

	protected String getAddToHeadSupport(String addToHeadPropertyName, PortalContext wrappedPortalContext) {

		if (PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT.equals(addToHeadPropertyName)) {

			// Delegate to the portlet container to determine if the optional MARKUP_HEAD_ELEMENT feature is supported.
			return wrappedPortalContext.getProperty(addToHeadPropertyName);
		}
		else {

			// JSF 1.2 does not support any <head> section features, so return null for all
			// BridgePortalContext.ADD_*_TO_HEAD_SUPPORT property names (such as ADD_ELEMENT_TO_HEAD_SUPPORT).
			return null;
		}
	}
}
