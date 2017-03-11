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
package com.liferay.faces.demos.bean;

import java.util.Enumeration;
import java.util.Map;

import javax.el.ELResolver;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.faces.preference.Preference;

import com.liferay.faces.util.context.FacesContextHelperUtil;


/**
 * @author  Neil Griffin
 */

@Named
@RequestScoped
public class PortletPreferencesBackingBean {

	/**
	 * Resets/restores the values in the portletPreferences.xhtml Facelet composition with portlet preference default
	 * values.
	 */
	public void reset() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortletPreferences portletPreferences = portletRequest.getPreferences();

		try {
			Enumeration<String> preferenceNames = portletPreferences.getNames();

			while (preferenceNames.hasMoreElements()) {
				String preferenceName = preferenceNames.nextElement();
				portletPreferences.reset(preferenceName);
			}

			portletPreferences.store();

			// Switch the portlet mode back to VIEW.
			ActionResponse actionResponse = (ActionResponse) externalContext.getResponse();
			actionResponse.setPortletMode(PortletMode.VIEW);
			actionResponse.setWindowState(WindowState.NORMAL);

			FacesContextHelperUtil.addGlobalSuccessInfoMessage();
		}
		catch (Exception e) {
			FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
		}
	}

	/**
	 * Saves the values in the portletPreferences.xhtml Facelet composition as portlet preferences.
	 */
	public void submit() {

		// The JSR 329 specification defines an EL variable named mutablePortletPreferencesValues that is being used in
		// the portletPreferences.xhtml Facelet composition. This object is of type Map<String, Preference> and is
		// designed to be a model managed-bean (in a sense) that contain preference values. However the only way to
		// access this from a Java class is to evaluate an EL expression (effectively self-injecting) the map into
		// this backing bean.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		String elExpression = "mutablePortletPreferencesValues";
		ELResolver elResolver = facesContext.getApplication().getELResolver();
		@SuppressWarnings("unchecked")
		Map<String, Preference> mutablePreferenceMap = (Map<String, Preference>) elResolver.getValue(
				facesContext.getELContext(), null, elExpression);

		// Get a list of portlet preference names.
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		PortletPreferences portletPreferences = portletRequest.getPreferences();
		Enumeration<String> preferenceNames = portletPreferences.getNames();

		try {

			// For each portlet preference name:
			while (preferenceNames.hasMoreElements()) {

				// Get the value specified by the user.
				String preferenceName = preferenceNames.nextElement();
				String preferenceValue = mutablePreferenceMap.get(preferenceName).getValue();

				// Prepare to save the value.
				if (!portletPreferences.isReadOnly(preferenceName)) {
					portletPreferences.setValue(preferenceName, preferenceValue);
				}
			}

			// Save the preference values.
			portletPreferences.store();

			// Switch the portlet mode back to VIEW.
			ActionResponse actionResponse = (ActionResponse) externalContext.getResponse();
			actionResponse.setPortletMode(PortletMode.VIEW);
			actionResponse.setWindowState(WindowState.NORMAL);

			// Report a successful message back to the user as feedback.
			FacesContextHelperUtil.addGlobalSuccessInfoMessage();
		}
		catch (Exception e) {
			FacesContextHelperUtil.addGlobalUnexpectedErrorMessage();
		}
	}
}
