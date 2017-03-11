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
package com.liferay.faces.bridge.context.internal;

import java.util.Iterator;

import javax.faces.application.ProjectStage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * The purpose of this class is to wrap the JSF implementation's Ajax exception handler so that exceptions that occur
 * during Ajax are logged to the console. For some reason Mojarra doesn't do that, which makes it very hard for the
 * developer to find out what went wrong.
 *
 * @author  Neil Griffin
 */
public class ExceptionHandlerBridgeImpl extends ExceptionHandlerWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerBridgeImpl.class);

	// Private Data Members
	private ExceptionHandler wrappedExceptionHandler;

	public ExceptionHandlerBridgeImpl(ExceptionHandler exceptionHandler) {
		this.wrappedExceptionHandler = exceptionHandler;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrappedExceptionHandler;
	}

	@Override
	public void handle() {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext.getPartialViewContext().isAjaxRequest()) {

			// Before delegating, log all exceptions to the console.
			Iterable<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents();
			Iterator<ExceptionQueuedEvent> itr = unhandledExceptionQueuedEvents.iterator();

			boolean isDevelopment = facesContext.isProjectStage(ProjectStage.Development);

			while (itr.hasNext()) {
				ExceptionQueuedEvent exceptionQueuedEvent = itr.next();
				ExceptionQueuedEventContext exceptionQueuedEventContext = exceptionQueuedEvent.getContext();

				if (exceptionQueuedEventContext != null) {
					Throwable throwable = exceptionQueuedEventContext.getException();

					if (throwable != null) {

						if (isDevelopment) {
							logger.error(throwable);
						}
						else {
							logger.error(throwable.getMessage());
						}
					}
					else {
						logger.error("Unable to get exception from exceptionQueuedEventContext");
					}
				}
				else {
					logger.error("Unable to get exceptionQueuedEventContext from exceptionQueuedEvent");
				}
			}
		}

		// Delegate to the wrapped JSF implementation's ExceptionHandler.
		super.handle();
	}
}
