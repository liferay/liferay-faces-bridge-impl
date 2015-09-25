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
package com.liferay.faces.demos.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class FacesMessageUtil {

	private static final Logger logger = LoggerFactory.getLogger(FacesMessageUtil.class);
	private static ResourceBundle resourceBundle;

	public static void addErrorMessage(FacesContext facesContext, String clientId, String key, Object[] args) {
		addMessage(facesContext, clientId, FacesMessage.SEVERITY_ERROR, key, args);
	}

	public static void addGlobalErrorMessage(FacesContext facesContext, String key, Object[] args) {
		String clientId = null;
		addErrorMessage(facesContext, clientId, key, args);
	}

	public static void addGlobalInfoMessage(FacesContext facesContext, String key, Object[] args) {
		String clientId = null;
		addInfoMessage(facesContext, clientId, key, args);
	}

	public static void addGlobalInfoMessage(FacesContext facesContext, String key, Object arg) {
		addGlobalInfoMessage(facesContext, key, new Object[] { arg });
	}

	public static void addGlobalSuccessInfoMessage(FacesContext facesContext) {
		String key = "your-request-processed-successfully";
		Object[] args = null;
		addGlobalInfoMessage(facesContext, key, args);
	}

	public static void addGlobalUnexpectedErrorMessage(FacesContext facesContext) {
		String key = "an-unexpected-error-occurred";
		Object[] args = null;
		addGlobalErrorMessage(facesContext, key, args);
	}

	public static void addInfoMessage(FacesContext facesContext, String clientId, String key, Object[] args) {
		addMessage(facesContext, clientId, FacesMessage.SEVERITY_INFO, key, args);
	}

	public static void addMessage(FacesContext facesContext, String clientId, FacesMessage.Severity severity,
		String key, Object[] args) {
		String message = getMessage(key, args);
		FacesMessage facesMessage = new FacesMessage(severity, message, message);
		facesContext.addMessage(clientId, facesMessage);
	}

	public static String getMessage(String key, Object[] args) {
		String message = key;

		try {

			if (resourceBundle == null) {
				resourceBundle = ResourceBundle.getBundle("i18n");
			}

			message = resourceBundle.getString(key);

			if (args != null) {
				message = MessageFormat.format(message, args);
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return message;
	}
}
