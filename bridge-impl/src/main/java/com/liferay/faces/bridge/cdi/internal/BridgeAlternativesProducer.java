/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.cdi.internal;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;

import com.liferay.faces.bridge.util.internal.RequestMapUtil;


/**
 * Alternative to the producer provided by the portlet container.
 */
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION + 10)
public class BridgeAlternativesProducer {

	@Dependent
	@Named(value = "portletConfig")
	@Produces
	public PortletConfig getPortletConfig() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		return RequestMapUtil.getPortletConfig(facesContext);
	}

	@Dependent
	@Named(value = "portletContext")
	@Produces
	public PortletContext getPortletContext() {
		PortletConfig portletConfig = getPortletConfig();

		return portletConfig.getPortletContext();
	}

	@Dependent
	@Named(value = "portletMode")
	@Produces
	public PortletMode getPortletMode() {
		PortletRequest portletRequest = getPortletRequest();

		return portletRequest.getPortletMode();
	}

	@Dependent
	@Named(value = "portletPreferences")
	@Produces
	public PortletPreferences getPortletPreferences() {
		PortletRequest portletRequest = getPortletRequest();

		return portletRequest.getPreferences();
	}

	@Named("portletRequest")
	@Dependent
	@Produces
	public PortletRequest getPortletRequest() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		return (PortletRequest) externalContext.getRequest();
	}

	@Named("portletResponse")
	@Dependent
	@Produces
	public PortletResponse getPortletResponse() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		return (PortletResponse) externalContext.getResponse();
	}

	@Named("portletSession")
	@Dependent
	@Produces
	public PortletSession getPortletSession() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		return (PortletSession) externalContext.getSession(true);
	}

	@Named("renderRequest")
	@Dependent
	@Produces
	@Typed(RenderRequest.class)
	public RenderRequest getRenderRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if ((portletRequest != null) && (portletRequest instanceof RenderRequest)) {
			return (RenderRequest) portletRequest;
		}

		return null;
	}

	@Named("renderResponse")
	@Dependent
	@Produces
	@Typed(RenderResponse.class)
	public RenderResponse getRenderResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if ((portletResponse != null) && (portletResponse instanceof RenderResponse)) {
			return (RenderResponse) portletResponse;
		}

		return null;
	}

	@Named("resourceRequest")
	@Dependent
	@Produces
	@Typed(ResourceRequest.class)
	public ResourceRequest getResourceRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if ((portletRequest != null) && (portletRequest instanceof ResourceRequest)) {
			return (ResourceRequest) portletRequest;
		}

		return null;
	}

	@Named("resourceResponse")
	@Dependent
	@Produces
	@Typed(ResourceResponse.class)
	public ResourceResponse getResourceResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if ((portletResponse != null) && (portletResponse instanceof ResourceResponse)) {
			return (ResourceResponse) portletResponse;
		}

		return null;
	}

	@Named("stateAwareResponse")
	@Dependent
	@Produces
	@Typed(StateAwareResponse.class)
	public StateAwareResponse getStateAwareResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if ((portletResponse != null) && (portletResponse instanceof StateAwareResponse)) {
			return (StateAwareResponse) portletResponse;
		}

		return null;
	}
}
