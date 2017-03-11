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

import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;


/**
 * @author  Neil Griffin
 */
public class ExternalContextRichFacesImpl extends ExternalContextImpl {

	public ExternalContextRichFacesImpl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {
		super(portletContext, portletRequest, portletResponse);
	}

	@Override
	public Object getContext() {

		Object context = super.getContext();
		Map<String, Object> requestMap = getRequestMap();

		if (requestMap.get("FACES-2638") != null) {
			return new ServletContextRichFacesImpl((PortletContext) context);
		}
		else {
			return context;
		}
	}
}
