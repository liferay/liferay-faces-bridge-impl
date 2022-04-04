/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;

import com.liferay.faces.bridge.scope.internal.BridgeRequestScope;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;


/**
 * @author  Neil Griffin
 */
public class ActionResponseBridgeImpl extends ActionResponseBridgeCompatImpl {

	// Private Data Members
	private PortletMode initialPortletMode;

	public ActionResponseBridgeImpl(ActionResponse actionResponse) {
		super(actionResponse);

		PortletMode portletMode = actionResponse.getPortletMode();

		if (portletMode == null) {
			this.initialPortletMode = PortletMode.VIEW;
		}
		else {
			this.initialPortletMode = portletMode;
		}
	}

	@Override
	public void sendRedirect(String location) throws IOException {

		prepareForRedirect();
		super.sendRedirect(location);
	}

	@Override
	public void sendRedirect(String location, String renderUrlParamName) throws IOException {

		prepareForRedirect();
		super.sendRedirect(location, renderUrlParamName);
	}

	@Override
	public void setPortletMode(PortletMode portletMode) throws PortletModeException {

		super.setPortletMode(portletMode);

		if (!initialPortletMode.equals(portletMode)) {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			BridgeRequestScope bridgeRequestScope = RequestMapUtil.getBridgeRequestScope(facesContext);
			bridgeRequestScope.setPortletModeChanged(true);
		}
	}

	protected void prepareForRedirect() {

		// Update the PartialViewContext.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		partialViewContextRenderAll(facesContext);

		// Set the response as "complete" in the FacesContext.
		facesContext.responseComplete();

		// Set a flag on the {@link BridgeRequestScope} indicating that a <redirect />
		// occurred which means that the request attributes should not be preserved.
		BridgeRequestScope bridgeRequestScope = RequestMapUtil.getBridgeRequestScope(facesContext);
		bridgeRequestScope.setRedirectOccurred(true);
	}
}
