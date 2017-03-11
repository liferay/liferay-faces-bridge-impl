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
package com.liferay.faces.bridge.application.internal;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.component.internal.UIViewRootBridgeImpl;
import com.liferay.faces.util.application.ApplicationUtil;
import com.liferay.faces.util.config.ConfiguredSystemEventListener;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class ApplicationCompatImpl extends ApplicationWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicationCompatImpl.class);

	// Private Data Members
	private Application wrappedApplication;

	public ApplicationCompatImpl(Application application) {
		this.wrappedApplication = application;
	}

	/**
	 * @deprecated  The JSF API JavaDoc indicates that this method has been deprecated in favor of {@link
	 *              #createComponent(ValueExpression, FacesContext, String)}. However, Mojarra and MyFaces both end up
	 *              calling through to this method, which is why it must be implemented here in the bridge.
	 */
	@Deprecated
	@Override
	public UIComponent createComponent(FacesContext facesContext, String componentType, String rendererType) {

		UIComponent uiComponent;

		if (BridgeUtil.isPortletRequest(facesContext)) {

			if (componentType.equals(UIViewRoot.COMPONENT_TYPE)) {

				// FACES-1967: Apache MyFaces calls this 3-arg overload of createComponent rather than the 1-arg version
				// when creating a UIViewRoot.
				uiComponent = createComponent(componentType);
			}
			else {
				uiComponent = super.createComponent(facesContext, componentType, rendererType);
			}
		}
		else {
			uiComponent = super.createComponent(facesContext, componentType, rendererType);
		}

		return uiComponent;
	}

	/**
	 * @see    {@link Application#getResourceHandler()}
	 * @since  JSF 2.0
	 */
	@Override
	public ResourceHandler getResourceHandler() {

		ResourceHandler resourceHandler = super.getResourceHandler();

		if (resourceHandler != null) {

			if (resourceHandler.getClass().getName().startsWith("org.richfaces.resource")) {
				resourceHandler = new ResourceHandlerRichfacesImpl(resourceHandler);
			}

			if (!ApplicationUtil.isStartupOrShutdown(FacesContext.getCurrentInstance())) {
				resourceHandler = new ResourceHandlerOuterImpl(resourceHandler);
			}
		}

		return resourceHandler;
	}

	/**
	 * @see  ApplicationWrapper#getWrapped()
	 */
	@Override
	public Application getWrapped() {
		return wrappedApplication;
	}

	protected void subscribeToJSF2SystemEvent(ConfiguredSystemEventListener configuredSystemEventListener) {

		try {
			@SuppressWarnings("unchecked")
			Class<? extends SystemEvent> systemEventClass = (Class<? extends SystemEvent>) Class.forName(
					configuredSystemEventListener.getSystemEventClass());
			@SuppressWarnings("unchecked")
			Class<? extends SystemEventListener> systemEventListenerClass = (Class<? extends SystemEventListener>) Class
				.forName(configuredSystemEventListener.getSystemEventListenerClass());
			SystemEventListener systemEventListener = systemEventListenerClass.newInstance();

			logger.debug("Subscribing UIViewRootBridgeImpl for systemEventClass=[{0}] systemEventListener=[{1}]",
				systemEventClass, systemEventListener);
			subscribeToEvent(systemEventClass, UIViewRootBridgeImpl.class, systemEventListener);
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
}
