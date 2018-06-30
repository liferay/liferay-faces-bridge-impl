/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.util.faces.context;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.portlet.ActionRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.servlet.ServletRequest;


/**
 * A factory object that creates (if needed) and returns new FacesContext instance for running in portlet environment
 * (PortletFacesContextImpl) The class is defined in &lt;faces-context-factory&gt; tag in faces-config.xml
 */
public class TCK_FacesContextFactoryImpl extends FacesContextFactory {
	private FacesContextFactory mHandler;

	public TCK_FacesContextFactoryImpl(FacesContextFactory handler) {
		mHandler = handler;
	}

	@Override
	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
		throws FacesException {
		FacesContext facesContext = mHandler.getFacesContext(context, request, response, lifecycle);

		// Only wrap the FacesContext if running in a portlet request
		// Purpose of this wrapping is to test the various FacesContext requirements
		// including that the bridge doesn't depend on its impl class being the instance
		if (isPortletRequest(request)) {

			// Verify we were passed the right objects
			verifyPortletObjects(context, request, response);

			// verify portlet phase
			verifyPortletPhase((PortletRequest) request);

			// Verify we are using the right lifecycle
			verifyLifecycle((PortletContext) context, (PortletRequest) request, lifecycle);

			return new TCK_FacesContextImpl(facesContext);
		}
		else {
			return facesContext;
		}
	}

	private boolean isPortletRequest(Object request) {

		// could be either a servlet or portlet request object (or both)
		// Check servlet side first in case we are packaged in an application
		// that is running as a servlet in an environment that doesn't contain
		// a portlet container.
		if (request instanceof ServletRequest) {
			ServletRequest sr = (ServletRequest) request;
			Bridge.PortletPhase phase = (Bridge.PortletPhase) sr.getAttribute(Bridge.PORTLET_LIFECYCLE_PHASE);

			return (phase != null);
		}
		else if (request instanceof PortletRequest) {
			return true;
		}

		return false;
	}

	private void verifyLifecycle(PortletContext context, PortletRequest portletRequest, Lifecycle lifecycle) {
		String id = context.getInitParameter("javax.faces.LIFECYCLE_ID");
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(
				FactoryFinder.LIFECYCLE_FACTORY);

		if (id != null) {
			Lifecycle l = lifecycleFactory.getLifecycle(id);

			if (l == lifecycle) {
				portletRequest.setAttribute("javax.portlet.faces.tck.testLifecyclePass",
					"Correctly used the configured lifcycle: " + id);
			}
			else {
				portletRequest.setAttribute("javax.portlet.faces.tck.testLifecycleFail",
					"Didn't use the configured lifecycle: " + id);
			}
		}
		else {
			Lifecycle l = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			if (l == lifecycle) {
				portletRequest.setAttribute("javax.portlet.faces.tck.testLifecyclePass",
					"Correctly used the default lifcycle as none was configured.");
			}
			else {
				portletRequest.setAttribute("javax.portlet.faces.tck.testLifecycleFail",
					"Didn't use the default lifecycle when none was configured.");
			}
		}
	}

	private void verifyPortletObjects(Object context, Object request, Object response) {

		if ((context instanceof PortletContext) && (request instanceof PortletRequest) &&
				(response instanceof PortletResponse)) {
			((PortletRequest) request).setAttribute("javax.portlet.faces.tck.verifyPortletObjectsPass",
				"Correctly passed a PortletContext, PortletRequest, and PortletResponse in acquiring a FacesContext");
		}
		else {
			((PortletRequest) request).setAttribute("javax.portlet.faces.tck.verifyPortletObjectsFail",
				"At least one of the objects passed to acquire a FacesContext wasn't a  PortletContext, PortletRequest, or PortletResponse");
		}
	}

	private void verifyPortletPhase(PortletRequest portletRequest) {
		Bridge.PortletPhase phase = (Bridge.PortletPhase) portletRequest.getAttribute("javax.portlet.faces.phase");

		if (portletRequest instanceof ActionRequest) {

			if ((phase != null) && (phase == Bridge.PortletPhase.ACTION_PHASE)) {
				portletRequest.setAttribute("javax.portlet.faces.tck.verifyPortletPhaseDuringActionPass",
					"Phase attribute correctly set during action ... ");
			}
			else {
				portletRequest.setAttribute("javax.portlet.faces.tck.verifyPortletPhaseDuringActionFail",
					"Phase attribute either not set or has incorrect value during action ... ");
			}
		}
		else {

			if (Bridge.PortletPhase.RENDER_PHASE.equals(phase)) {
				portletRequest.setAttribute("javax.portlet.faces.tck.verifyPortletPhaseDuringRenderPass",
					"Phase attribute correctly set during render.");
			}
			else {
				portletRequest.setAttribute("javax.portlet.faces.tck.verifyPortletPhaseDuringRenderFail",
					"Phase attribute either not set or has incorrect value during render.");
			}
		}
	}
}
