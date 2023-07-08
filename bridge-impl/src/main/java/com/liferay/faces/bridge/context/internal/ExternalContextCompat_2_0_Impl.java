/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.portlet.ClientDataRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;


/**
 * This class provides a compatibility layer that isolates differences between JSF 1.2 and JSF 2.0.
 *
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_2_0_Impl extends ExternalContextCompat_2_0_FlashImpl {

	// Protected Data Members
	protected Bridge.PortletPhase portletPhase;

	public ExternalContextCompat_2_0_Impl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		super(portletContext, portletRequest, portletResponse);
	}

	protected boolean isICEfacesLegacyMode(ClientDataRequest clientDataRequest) {

		// no-op for JSF 1.2
		return false;
	}

	protected boolean isJSF2PartialRequest(FacesContext facesContext) {

		// no-op for JSF 1.2
		return false;
	}

	protected void partialViewContextRenderAll(FacesContext facesContext) {

		// no-op for JSF 1.2
	}

	protected void redirectJSF2PartialResponse(FacesContext facesContext, ResourceResponse resourceResponse, String url)
		throws IOException {

		// no-op for JSF 1.2
	}
}
