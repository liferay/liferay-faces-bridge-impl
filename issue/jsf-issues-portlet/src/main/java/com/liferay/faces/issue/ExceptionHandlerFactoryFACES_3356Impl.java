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
package com.liferay.faces.issue;

import java.util.Iterator;

import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.faces.BridgeInvalidViewPathException;

import com.liferay.faces.util.helper.BooleanHelper;


/**
 * @author  Kyle Stiemann
 */
public class ExceptionHandlerFactoryFACES_3356Impl extends ExceptionHandlerFactory {

	// Private Final Data Members
	private final ExceptionHandlerFactory wrappedExceptionHandlerFactory;

	public ExceptionHandlerFactoryFACES_3356Impl(ExceptionHandlerFactory wrappedExceptionHandlerFactory) {
		this.wrappedExceptionHandlerFactory = wrappedExceptionHandlerFactory;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new ExceptionHandlerFACES_3356Impl(wrappedExceptionHandlerFactory.getExceptionHandler());
	}

	@Override
	public ExceptionHandlerFactory getWrapped() {
		return wrappedExceptionHandlerFactory;
	}

	private static final class ExceptionHandlerFACES_3356Impl extends ExceptionHandlerWrapper {

		// Private Final Data Members
		private final ExceptionHandler wrappedExceptionHandler;

		public ExceptionHandlerFACES_3356Impl(ExceptionHandler wrappedExceptionHandler) {
			this.wrappedExceptionHandler = wrappedExceptionHandler;
		}

		@Override
		public ExceptionHandler getWrapped() {
			return wrappedExceptionHandler;
		}

		@Override
		public void handle() throws FacesException {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			String faces_3356Parameter = portletRequest.getParameter("FACES-3356");

			if (BooleanHelper.isTrueToken(faces_3356Parameter)) {

				Iterable<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents();
				Iterator<ExceptionQueuedEvent> iterator = unhandledExceptionQueuedEvents.iterator();

				while (iterator.hasNext()) {

					ExceptionQueuedEvent exceptionQueuedEvent = iterator.next();
					ExceptionQueuedEventContext exceptionQueuedEventContext = exceptionQueuedEvent.getContext();
					Throwable t = exceptionQueuedEventContext.getException();

					if (t instanceof BridgeInvalidViewPathException) {

						Application application = facesContext.getApplication();
						ViewHandler viewHandler = application.getViewHandler();
						UIViewRoot uiViewRoot = viewHandler.createView(facesContext,
								"/WEB-INF/views/FACES-3356/success.xhtml");
						facesContext.setViewRoot(uiViewRoot);
						facesContext.renderResponse();
						iterator.remove();
					}
				}
			}

			super.handle();
		}
	}
}
