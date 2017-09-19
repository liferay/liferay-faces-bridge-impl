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
import javax.portlet.faces.Bridge;

import com.liferay.faces.util.helper.BooleanHelper;


/**
 * @author  Kyle Stiemann
 */
public abstract class PortalContextBridgeCompatImpl implements PortalContext {

	// Private Data Members
	private boolean ajaxRequest;

	public PortalContextBridgeCompatImpl(PortletRequest portletRequest) {

		String facesAjaxParam = portletRequest.getParameter(Bridge.FACES_AJAX_PARAMETER);

		this.ajaxRequest = BooleanHelper.isTrueToken(facesAjaxParam);
	}

	protected String getAddToHeadSupport(String addToHeadPropertyName, PortalContext wrappedPortalContext) {

		if (ajaxRequest) {

			// During a Faces ajax request no elements can be added to the <head> section via the portlet API.
			return null;
		}
		else {

			// The only way to add elements to the <head> section is via the optional Portlet 2.0 MARKUP_HEAD_ELEMENT
			// feature, so for all BridgePortalContext.ADD_*_TO_HEAD_SUPPORT property names (such as
			// ADD_ELEMENT_TO_HEAD_SUPPORT) and the MARKUP_HEAD_ELEMENT_SUPPORT property name, delegate to the portlet
			// container to determine if the optional MARKUP_HEAD_ELEMENT feature is supported.
			return wrappedPortalContext.getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT);
		}
	}
}
