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
package com.liferay.faces.bridge.i18n.internal;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;

import com.liferay.faces.util.i18n.I18n;
import com.liferay.faces.util.i18n.I18nUtil;
import com.liferay.faces.util.i18n.I18nWrapper;


/**
 * @author  Neil Griffin
 */
public class I18nBridgeImpl extends I18nWrapper implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 3720968042955596126L;

	// Private Data Members
	private I18n wrappedI18n;

	public I18nBridgeImpl(I18n i18n) {
		this.wrappedI18n = i18n;
	}

	@Override
	public FacesMessage getFacesMessage(FacesContext facesContext, Locale locale, FacesMessage.Severity severity,
		String messageId) {
		return I18nUtil.getFacesMessage(this, facesContext, locale, severity, messageId);
	}

	@Override
	public FacesMessage getFacesMessage(FacesContext facesContext, Locale locale, FacesMessage.Severity severity,
		String messageId, Object... arguments) {
		return I18nUtil.getFacesMessage(this, facesContext, locale, severity, messageId, arguments);
	}

	@Override
	public String getMessage(FacesContext facesContext, Locale locale, String messageId) {

		String value = null;
		ResourceBundle resourceBundle = getPortletConfigResourceBundle(facesContext, locale);

		if ((resourceBundle != null) && resourceBundle.containsKey(messageId)) {
			value = resourceBundle.getString(messageId);
		}

		if ((value == null) || value.equals(messageId)) {
			value = super.getMessage(facesContext, locale, messageId);
		}

		return value;
	}

	@Override
	public String getMessage(FacesContext facesContext, Locale locale, String messageId, Object... arguments) {

		String value = null;
		ResourceBundle resourceBundle = getPortletConfigResourceBundle(facesContext, locale);

		if ((resourceBundle != null) && resourceBundle.containsKey(messageId)) {

			value = resourceBundle.getString(messageId);

			// Give the delegate the opportunity to format the arguments since that is the default functionality
			// of the base implementation.
			value = super.getMessage(facesContext, locale, value, arguments);
		}

		if ((value == null) || value.equals(messageId)) {
			value = super.getMessage(facesContext, locale, messageId, arguments);
		}

		return value;
	}

	@Override
	public I18n getWrapped() {
		return wrappedI18n;
	}

	private ResourceBundle getPortletConfigResourceBundle(FacesContext facesContext, Locale locale) {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		PortletConfig portletConfig = (PortletConfig) requestMap.get(PortletConfig.class.getName());

		if (portletConfig != null) {
			return portletConfig.getResourceBundle(locale);
		}
		else {
			return null;
		}
	}
}
