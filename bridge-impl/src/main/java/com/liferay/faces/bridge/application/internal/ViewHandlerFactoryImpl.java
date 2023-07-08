/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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

import javax.faces.application.ViewHandler;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class ViewHandlerFactoryImpl extends ViewHandlerFactory {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ViewHandlerFactoryImpl.class);

	// Private Constants
	private static final String MOJARRA_VIEW_HANDLER_FQCN = "com.sun.faces.application.ViewHandlerImpl";
	private static final String MYFACES_VIEW_HANDLER_FQCN_NEW = "org.apache.myfaces.application.ViewHandlerImpl";
	private static final Class<?> viewHandlerClass;

	static {

		Class<?> clazz = null;

		try {
			clazz = Class.forName(MOJARRA_VIEW_HANDLER_FQCN);
		}
		catch (Exception e1) {

			try {
				clazz = Class.forName(MYFACES_VIEW_HANDLER_FQCN_NEW);
			}
			catch (Exception e2) {
				logger.error("Classloader unable to find either the Mojarra or MyFaces ViewHandler implementations");
			}
		}

		viewHandlerClass = clazz;
	}

	@Override
	public ViewHandler getViewHandler() {

		ViewHandler viewHandler = null;

		if (viewHandlerClass != null) {

			try {
				viewHandler = (ViewHandler) viewHandlerClass.newInstance();
			}
			catch (Exception e) {
				logger.error(e);
			}
		}

		return viewHandler;
	}

	public ViewHandlerFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}

}
