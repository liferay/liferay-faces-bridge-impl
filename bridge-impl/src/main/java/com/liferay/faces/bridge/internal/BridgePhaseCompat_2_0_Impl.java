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
package com.liferay.faces.bridge.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

import javax.faces.application.ResourceHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeInvalidViewPathException;

import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadManagedBean;


/**
 * This class provides a compatibility layer that isolates differences related to JSF 2.0.
 *
 * @author  Neil Griffin
 */
public abstract class BridgePhaseCompat_2_0_Impl extends BridgePhaseCompat_1_2_Impl {

	public BridgePhaseCompat_2_0_Impl(PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		super(portletConfig, bridgeConfig);
	}

	public Writer getResponseOutputWriter(ExternalContext externalContext) throws IOException {
		return externalContext.getResponseOutputWriter();
	}

	protected void clearHeadManagedBeanResources(FacesContext facesContext) {
		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);

		if (headManagedBean != null) {
			Set<String> headResourceIds = headManagedBean.getHeadResourceIds();

			if (headResourceIds != null) {
				headResourceIds.clear();
			}
		}
	}

	protected void handleJSF2ResourceRequest(FacesContext facesContext) throws IOException {
		ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
		resourceHandler.handleResourceRequest(facesContext);
	}

	protected boolean isJSF2AjaxRequest(FacesContext facesContext) {
		return facesContext.getPartialViewContext().isAjaxRequest();
	}

	protected boolean isJSF2ResourceRequest(FacesContext facesContext) {
		ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();

		return resourceHandler.isResourceRequest(facesContext);
	}

	@Override
	protected void queueHandleableException(PortletRequest portletRequest, FacesContext facesContext, Exception e) {

		ExceptionHandler exceptionHandler = facesContext.getExceptionHandler();
		ExceptionQueuedEventContext exceptionQueuedEventContext = new ExceptionQueuedEventContext(facesContext, e);
		ExceptionQueuedEvent exceptionQueuedEvent = new ExceptionQueuedEvent(exceptionQueuedEventContext);
		exceptionHandler.processEvent(exceptionQueuedEvent);
	}

	protected void throwQueuedExceptionIfNecessary(FacesContext facesContext) throws BridgeException {

		// If there were any "handled" exceptions queued, then throw a BridgeException.
		ExceptionHandler exceptionHandler = facesContext.getExceptionHandler();
		Throwable t = getQueuedException(exceptionHandler, true);

		if (t == null) {

			// Otherwise, if there were any "unhandled" exceptions queued, then throw a BridgeException.
			t = getQueuedException(exceptionHandler, false);
		}

		if (t != null) {

			if (t instanceof BridgeException) {
				throw (BridgeException) t;
			}
			else {

				Throwable cause = t.getCause();

				if ((cause != null) && (cause instanceof BridgeInvalidViewPathException)) {
					throw (BridgeException) cause;
				}
				else {
					throw new BridgeException(t);
				}
			}
		}
	}

	private Throwable getQueuedException(ExceptionHandler exceptionHandler, boolean handled) {

		Throwable t = null;
		Iterable<ExceptionQueuedEvent> exceptionQueuedEvents;

		if (handled) {
			exceptionQueuedEvents = exceptionHandler.getHandledExceptionQueuedEvents();
		}
		else {
			exceptionQueuedEvents = exceptionHandler.getUnhandledExceptionQueuedEvents();
		}

		if (exceptionQueuedEvents != null) {
			Iterator<ExceptionQueuedEvent> itr = exceptionQueuedEvents.iterator();

			while (itr.hasNext()) {
				ExceptionQueuedEvent exceptionQueuedEvent = itr.next();
				ExceptionQueuedEventContext exceptionQueuedEventContext = exceptionQueuedEvent.getContext();
				t = exceptionQueuedEventContext.getException();

				break;
			}
		}

		return t;
	}
}
