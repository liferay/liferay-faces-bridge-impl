/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.faces.context.ResponseWriter;

import com.liferay.faces.bridge.renderkit.html_basic.internal.BodyRendererBridgeImpl;
import com.liferay.faces.util.context.FacesRequestContext;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is a portlet-compatible version of the PrimeFaces 5.x BodyRenderer. For more information, see: FACES-1977.
 * Note that reflection is used to access the PrimeFaces RequestContext in order to avoid a compile-time dependency on a
 * specific version of PrimeFaces.
 *
 * @author  Neil Griffin
 */
public class BodyRendererPrimeFacesImpl extends BodyRendererBridgeImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BodyRendererPrimeFacesImpl.class);

	// Private Constants
	private static final Method GET_CURRENT_INSTANCE_METHOD;
	private static final Method GET_SCRIPTS_TO_EXECUTE_METHOD;

	static {

		Method getCurrentInstanceMethod = null;
		Method getScriptsToExecuteMethod = null;

		try {
			Class<?> requestContextClass = Class.forName("org.primefaces.context.RequestContext");
			getCurrentInstanceMethod = requestContextClass.getDeclaredMethod("getCurrentInstance");
			getScriptsToExecuteMethod = requestContextClass.getMethod("getScriptsToExecute");
		}
		catch (Exception e) {
			logger.error(e);
		}

		GET_CURRENT_INSTANCE_METHOD = getCurrentInstanceMethod;
		GET_SCRIPTS_TO_EXECUTE_METHOD = getScriptsToExecuteMethod;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void encodeScripts(ResponseWriter responseWriter) throws IOException {

		Object requestContext = getRequestContextCurrentInstance();

		try {

			List<String> scriptsToExecute = (List<String>) GET_SCRIPTS_TO_EXECUTE_METHOD.invoke(requestContext);

			if (scriptsToExecute != null) {

				FacesRequestContext facesRequestContext = FacesRequestContext.getCurrentInstance();

				for (String scriptToExecute : scriptsToExecute) {
					facesRequestContext.addScript(scriptToExecute);
				}
			}
		}
		catch (Exception e) {
			logger.error(e);
		}

		// Allow the BodyRendererBridgeImpl to render the scripts.
		super.encodeScripts(responseWriter);
	}

	protected Object getRequestContextCurrentInstance() {

		Object requestContext = null;

		try {
			requestContext = GET_CURRENT_INSTANCE_METHOD.invoke(null);
		}
		catch (Exception e) {
			logger.error(e);
		}

		return requestContext;
	}
}
