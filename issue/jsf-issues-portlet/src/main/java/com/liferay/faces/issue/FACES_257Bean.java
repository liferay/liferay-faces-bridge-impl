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

import java.lang.reflect.Field;

import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.portlet.PortletRequest;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "FACES_257Bean")
@RequestScoped
public class FACES_257Bean {

	public static final String WEB_KEYS_THEME_DISPLAY_KEY;

	static {

		String themeDisplayKey;

		try {

			Class<?> themeDisplayClass = Class.forName("com.liferay.portal.kernel.util.WebKeys");
			Field declaredField = themeDisplayClass.getDeclaredField("THEME_DISPLAY");
			themeDisplayKey = (String) declaredField.get(null);
		}
		catch (Exception e) {
			themeDisplayKey = null;
		}

		WEB_KEYS_THEME_DISPLAY_KEY = themeDisplayKey;
	}

	public Object getThemeDisplay() {

		Object themeDisplay = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		if (WEB_KEYS_THEME_DISPLAY_KEY != null) {
			themeDisplay = portletRequest.getAttribute(WEB_KEYS_THEME_DISPLAY_KEY);
		}

		if (themeDisplay == null) {
			themeDisplay = portletRequest.getAttribute("LIFERAY_SHARED_THEME_DISPLAY");
		}

		if (themeDisplay == null) {
			themeDisplay = portletRequest.getAttribute("THEME_DISPLAY");
		}

		return themeDisplay;
	}
}
