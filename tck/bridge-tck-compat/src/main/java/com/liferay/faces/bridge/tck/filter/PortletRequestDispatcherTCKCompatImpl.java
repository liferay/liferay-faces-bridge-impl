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
package com.liferay.faces.bridge.tck.filter;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


/**
 * @author  Neil Griffin
 */
public class PortletRequestDispatcherTCKCompatImpl implements PortletRequestDispatcher {

	// Private Data Members
	private PortletRequestDispatcher wrappedPortletRequestDispatcher;

	public PortletRequestDispatcherTCKCompatImpl(PortletRequestDispatcher portletRequestDispatcher) {
		this.wrappedPortletRequestDispatcher = portletRequestDispatcher;
	}

	@Override
	public void forward(PortletRequest portletRequest, PortletResponse portletResponse) throws PortletException,
		IOException {

		try {
			wrappedPortletRequestDispatcher.forward(portletRequest, portletResponse);
		}
		catch (IllegalStateException e) {

			// Pluto throws an IllegalStateException because TestSuiteViewHandlerImpl.renderSelf() calls
			// ExternalContext.dispatch(String) assuming that the bridge must render the view itself because the Faces
			// implementation is not able to render views in a portlet environment. When this happens, throw a
			// RenderSelfException so that ViewHandlerTCKImpl.renderView(FacesContext,UIViewRoot) can catch it and
			// proceed with invoking the Faces implementation to render the view.
			throw new RenderSelfException(e);
		}
	}

	@Override
	public void include(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		wrappedPortletRequestDispatcher.include(request, response);
	}

	@Override
	public void include(PortletRequest portletRequest, PortletResponse portletResponse) throws PortletException,
		IOException {
		wrappedPortletRequestDispatcher.include(portletRequest, portletResponse);
	}
}
