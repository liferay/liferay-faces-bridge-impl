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
package com.liferay.faces.support.thick.cdi.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
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
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Note: The source for class is mostly taken from the {@link
 * com.liferay.bean.portlet.cdi.extension.internal.scope.JSR362CDIBeanProducer} class.
 *
 * @author  Neil Griffin
 */
public class NonAlternativeProducer {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(NonAlternativeProducer.class);

	@Named("actionRequest")
	@RequestScoped
	@Produces
	@Typed(ActionRequest.class)
	public ActionRequest getActionRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest instanceof ActionRequest) {
			return (ActionRequest) portletRequest;
		}

		return null;
	}

	@Named("actionResponse")
	@RequestScoped
	@Produces
	@Typed(ActionResponse.class)
	public ActionResponse getActionResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if (portletResponse instanceof ActionResponse) {
			return (ActionResponse) portletResponse;
		}

		return null;
	}

	@Named("clientDataRequest")
	@RequestScoped
	@Produces
	@Typed(ClientDataRequest.class)
	public ClientDataRequest getClientDataRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest instanceof ClientDataRequest) {
			return (ClientDataRequest) portletRequest;
		}

		return null;
	}

	@Named("cookies")
	@RequestScoped
	@Produces
	public List<Cookie> getCookies() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		Cookie[] cookies = portletRequest.getCookies();

		if (cookies == null) {
			return null;
		}

		return Arrays.asList(cookies);
	}

	@Named("eventRequest")
	@RequestScoped
	@Produces
	@Typed(EventRequest.class)
	public EventRequest getEventRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest instanceof EventRequest) {
			return (EventRequest) portletRequest;
		}

		return null;
	}

	@Named("eventResponse")
	@RequestScoped
	@Produces
	@Typed(EventResponse.class)
	public EventResponse getEventResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if (portletResponse instanceof EventResponse) {
			return (EventResponse) portletResponse;
		}

		return null;
	}

	@Named("locales")
	@RequestScoped
	@Produces
	public List<Locale> getLocales() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return Collections.list(portletRequest.getLocales());
	}

	@Named("mimeResponse")
	@RequestScoped
	@Produces
	@Typed(MimeResponse.class)
	public MimeResponse getMimeResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if (portletResponse instanceof MimeResponse) {
			return (MimeResponse) portletResponse;
		}

		return null;
	}

	@Named("portletConfig")
	@RequestScoped
	@Produces
	public PortletConfig getPortletConfig() {

		PortletRequest portletRequest = getPortletRequest();

		return (PortletConfig) portletRequest.getAttribute(PortletConfig.class.getName());
	}

	@Named("portletContext")
	@RequestScoped
	@Produces
	public PortletContext getPortletContext() {
		PortletConfig portletConfig = getPortletConfig();

		if (portletConfig == null) {
			return null;
		}

		return portletConfig.getPortletContext();
	}

	@Named("portletMode")
	@RequestScoped
	@Produces
	public PortletMode getPortletMode() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPortletMode();
	}

	@Named("portletPreferences")
	@RequestScoped
	@Produces
	public PortletPreferences getPortletPreferences() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPreferences();
	}

	@Named("portletRequest")
	@RequestScoped
	@Produces
	public PortletRequest getPortletRequest() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		return (PortletRequest) externalContext.getRequest();
	}

	@Named("portletResponse")
	@RequestScoped
	@Produces
	public PortletResponse getPortletResponse() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		return (PortletResponse) externalContext.getResponse();
	}

	@Named("portletSession")
	@RequestScoped
	@Produces
	public PortletSession getPortletSession() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPortletSession();
	}

	@Named("renderRequest")
	@RequestScoped
	@Produces
	@Typed(RenderRequest.class)
	public RenderRequest getRenderRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest instanceof RenderRequest) {

			return (RenderRequest) portletRequest;
		}

		return null;
	}

	@Named("renderResponse")
	@RequestScoped
	@Produces
	@Typed(RenderResponse.class)
	public RenderResponse getRenderResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if (portletResponse instanceof RenderResponse) {
			return (RenderResponse) portletResponse;
		}

		return null;
	}

	@Named("resourceRequest")
	@RequestScoped
	@Produces
	@Typed(ResourceRequest.class)
	public ResourceRequest getResourceRequest() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest instanceof ResourceRequest) {
			return (ResourceRequest) portletRequest;
		}

		return null;
	}

	@Named("resourceResponse")
	@RequestScoped
	@Produces
	@Typed(ResourceResponse.class)
	public ResourceResponse getResourceResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if (portletResponse instanceof ResourceResponse) {
			return (ResourceResponse) portletResponse;
		}

		return null;
	}

	@Named("stateAwareResponse")
	@RequestScoped
	@Produces
	@Typed(StateAwareResponse.class)
	public StateAwareResponse getStateAwareResponse() {
		PortletResponse portletResponse = getPortletResponse();

		if (portletResponse instanceof StateAwareResponse) {
			return (StateAwareResponse) portletResponse;
		}

		return null;
	}

	@Named("windowState")
	@RequestScoped
	@Produces
	@Typed(WindowState.class)
	public WindowState getWindowState() {
		PortletRequest portletRequest = getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getWindowState();
	}
}
