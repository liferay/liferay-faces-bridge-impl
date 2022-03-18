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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.portlet.ActionParameters;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.annotations.ContextPath;
import javax.portlet.annotations.Namespace;
import javax.portlet.annotations.PortletName;
import javax.portlet.annotations.WindowId;
import javax.servlet.http.Cookie;

import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * Alternative to the producer provided by the portlet container.
 */
@Alternative
@Dependent
@Priority(Interceptor.Priority.APPLICATION + 10)
public class BridgeAlternativesProducer {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeAlternativesProducer.class);

	@Inject
	private BeanManager beanManager;

	@Named("actionParams")
	@Dependent
	@Produces
	@Typed(ActionParameters.class)
	public ActionParameters getActionParameters() {
		ActionRequest actionRequest = getActionRequest();

		if (actionRequest != null) {
			return actionRequest.getActionParameters();
		}

		return null;
	}

	@Named("actionRequest")
	@Dependent
	@Produces
	@Typed(ActionRequest.class)
	public ActionRequest getActionRequest() {
		PortletRequest portletRequest = _getPortletRequest(ActionRequest.class);

		if ((portletRequest != null) && (portletRequest instanceof ActionRequest)) {

			ActionRequest actionRequest = (ActionRequest) portletRequest;

			return actionRequest;
		}

		return null;
	}

	@Named("actionResponse")
	@Dependent
	@Produces
	@Typed(ActionResponse.class)
	public ActionResponse getActionResponse() {
		PortletResponse portletResponse = _getPortletResponse(ActionResponse.class);

		if ((portletResponse != null) && (portletResponse instanceof ActionResponse)) {
			return (ActionResponse) portletResponse;
		}

		return null;
	}

	@Named("clientDataRequest")
	@Dependent
	@Produces
	@Typed(ClientDataRequest.class)
	public ClientDataRequest getClientDataRequest() {
		PortletRequest portletRequest = _getPortletRequest(ClientDataRequest.class);

		if ((portletRequest != null) && (portletRequest instanceof ClientDataRequest)) {

			ClientDataRequest clientDataRequest = (ClientDataRequest) portletRequest;

			return clientDataRequest;
		}

		return null;
	}

	@Dependent
	@Named("contextPath")
	@ContextPath
	@Produces
	public String getContextPath() {
		PortletContext portletContext = getPortletContext();

		if (portletContext != null) {
			return portletContext.getContextPath();
		}

		return null;
	}

	@Dependent
	@Named("cookies")
	@Produces
	public List<Cookie> getCookies() {
		PortletRequest portletRequest = _getPortletRequest();

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
	@Dependent
	@Produces
	@Typed(EventRequest.class)
	public EventRequest getEventRequest() {
		PortletRequest portletRequest = _getPortletRequest(EventRequest.class);

		if ((portletRequest != null) && (portletRequest instanceof EventRequest)) {
			return (EventRequest) portletRequest;
		}

		return null;
	}

	@Named("eventResponse")
	@Dependent
	@Produces
	@Typed(EventResponse.class)
	public EventResponse getEventResponse() {
		PortletResponse portletResponse = _getPortletResponse(EventResponse.class);

		if ((portletResponse != null) && (portletResponse instanceof EventResponse)) {
			return (EventResponse) portletResponse;
		}

		return null;
	}

	@Named("headerRequest")
	@Dependent
	@Produces
	@Typed(HeaderRequest.class)
	public HeaderRequest getHeaderRequest() {
		PortletRequest portletRequest = _getPortletRequest(HeaderRequest.class);

		if ((portletRequest != null) && (portletRequest instanceof HeaderRequest)) {
			return (HeaderRequest) portletRequest;
		}

		return null;
	}

	@Named("headerResponse")
	@Dependent
	@Produces
	@Typed(HeaderResponse.class)
	public HeaderResponse getHeaderResponse() {
		PortletResponse portletResponse = _getPortletResponse(HeaderResponse.class);

		if ((portletResponse != null) && (portletResponse instanceof HeaderResponse)) {
			return (HeaderResponse) portletResponse;
		}

		return null;
	}

	@Dependent
	@Named("locales")
	@Produces
	public List<Locale> getLocales() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return Collections.list(portletRequest.getLocales());
	}

	@Named("mimeResponse")
	@Dependent
	@Produces
	@Typed(MimeResponse.class)
	public MimeResponse getMimeResponse() {
		PortletResponse portletResponse = _getPortletResponse(MimeResponse.class);

		if ((portletResponse != null) && (portletResponse instanceof MimeResponse)) {
			return (MimeResponse) portletResponse;
		}

		return null;
	}

	@Named("mutableRenderParams")
	@Dependent
	@Produces
	@Typed(MutableRenderParameters.class)
	public MutableRenderParameters getMutableRenderParameters() {
		StateAwareResponse stateAwareResponse = getStateAwareResponse();

		if (stateAwareResponse != null) {
			return stateAwareResponse.getRenderParameters();
		}

		return null;
	}

	@Dependent
	@Named("namespace")
	@Namespace
	@Produces
	public String getNamespace() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse == null) {
			return null;
		}

		return portletResponse.getNamespace();
	}

	@Dependent
	@Named(value = "portletConfig")
	@Produces
	public PortletConfig getPortletConfig() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext == null) {
			return _getNonAlternativeDefaultBean(PortletConfig.class);
		}

		return RequestMapUtil.getPortletConfig(facesContext);
	}

	@Dependent
	@Named(value = "portletContext")
	@Produces
	public PortletContext getPortletContext() {
		PortletConfig portletConfig = getPortletConfig();

		if (portletConfig == null) {
			return _getNonAlternativeDefaultBean(PortletContext.class);
		}

		return portletConfig.getPortletContext();
	}

	@Dependent
	@Named(value = "portletMode")
	@Produces
	public PortletMode getPortletMode() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPortletMode();
	}

	@Dependent
	@Named("portletName")
	@PortletName
	@Produces
	public String getPortletName() {
		PortletConfig portletConfig = getPortletConfig();

		if (portletConfig == null) {
			return null;
		}

		return portletConfig.getPortletName();
	}

	@Dependent
	@Named(value = "portletPreferences")
	@Produces
	public PortletPreferences getPortletPreferences() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPreferences();
	}

	@Named("portletRequest")
	@Dependent
	@Produces
	public PortletRequest getPortletRequest() {
		return _getPortletRequest();
	}

	@Named("portletResponse")
	@Dependent
	@Produces
	public PortletResponse getPortletResponse() {
		return _getPortletResponse();
	}

	@Named("portletSession")
	@Dependent
	@Produces
	public PortletSession getPortletSession() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext == null) {
			return _getNonAlternativeDefaultBean(PortletSession.class);
		}

		ExternalContext externalContext = facesContext.getExternalContext();

		return (PortletSession) externalContext.getSession(true);
	}

	@Named("renderParams")
	@Dependent
	@Produces
	@Typed(RenderParameters.class)
	public RenderParameters getRenderParameters() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getRenderParameters();
	}

	@Named("renderRequest")
	@Dependent
	@Produces
	@Typed(RenderRequest.class)
	public RenderRequest getRenderRequest() {
		PortletRequest portletRequest = _getPortletRequest(RenderRequest.class);

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
		PortletResponse portletResponse = _getPortletResponse(RenderResponse.class);

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
		PortletRequest portletRequest = _getPortletRequest(ResourceRequest.class);

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
		PortletResponse portletResponse = _getPortletResponse(ResourceResponse.class);

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
		PortletResponse portletResponse = _getPortletResponse(StateAwareResponse.class);

		if ((portletResponse != null) && (portletResponse instanceof StateAwareResponse)) {
			return (StateAwareResponse) portletResponse;
		}

		return null;
	}

	@Dependent
	@Named("windowId")
	@Produces
	@WindowId
	public String getWindowID() {

		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getWindowID();
	}

	@Dependent
	@Named(value = "windowState")
	@Produces
	public WindowState getWindowState() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getWindowState();
	}

	/**
	 * When the Liferay Faces Bridge Impl jar module is deployed in the global classpath (such as with Apache
	 * Pluto+Tomcat), the alternative producers in this class are invoked in favor of the non-alternative (default)
	 * producers that are provided by the portal. When a JSF portlet is causing these producers to be invoked, all is
	 * well since FacesContext.getCurrentInstance() returns a non-null value. But when a plain Portlet 3.0 bean portlet
	 * causes these producers to be invoked, FacesContext.getCurrentInstance() returns a null value. In this case, it is
	 * necessary to programatically acquire the non-alternative (default) producer.
	 */
	private <T> T _getNonAlternativeDefaultBean(Class<T> clazz) {
		Set<Bean<?>> beans = beanManager.getBeans(clazz, DefaultAnnotation.INSTANCE);

		for (Bean<?> bean : beans) {
			Class<?> beanClass = bean.getBeanClass();
			String beanClassName = beanClass.getName();

			if (!beanClassName.contains(getClass().getPackage().getName())) {
				CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);

				Object beanInstance = beanManager.getReference(bean, clazz, creationalContext);

				logger.debug("beanClassName=[{}] beanInstance=[{}]", beanClassName, beanInstance);

				return (T) beanInstance;
			}
		}

		return null;
	}

	private PortletRequest _getPortletRequest() {
		return _getPortletRequest(PortletRequest.class);
	}

	private <T extends PortletRequest> T _getPortletRequest(Class<T> clazz) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext == null) {
			return _getNonAlternativeDefaultBean(clazz);
		}

		ExternalContext externalContext = facesContext.getExternalContext();

		return (T) externalContext.getRequest();
	}

	private PortletResponse _getPortletResponse() {
		return _getPortletResponse(PortletResponse.class);
	}

	private <T extends PortletResponse> T _getPortletResponse(Class<T> clazz) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext == null) {
			return _getNonAlternativeDefaultBean(clazz);
		}

		ExternalContext externalContext = facesContext.getExternalContext();

		return (T) externalContext.getResponse();
	}

	private static class DefaultAnnotation extends AnnotationLiteral<Default> implements Default {
		public static final DefaultAnnotation INSTANCE = new DefaultAnnotation();
	}
}
