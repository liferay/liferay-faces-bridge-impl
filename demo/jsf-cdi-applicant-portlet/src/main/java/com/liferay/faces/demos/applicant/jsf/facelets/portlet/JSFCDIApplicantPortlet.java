/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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

package com.liferay.faces.demos.applicant.jsf.facelets.portlet;

import javax.portlet.annotations.InitParameter;
import javax.portlet.annotations.LocaleString;
import javax.portlet.annotations.PortletConfiguration;
import javax.portlet.annotations.Preference;
import javax.portlet.annotations.SecurityRoleRef;
import javax.portlet.annotations.Supports;
import javax.portlet.faces.GenericFacesPortlet;

import com.liferay.bean.portlet.LiferayPortletConfiguration;


/**
 * @author  Neil Griffin
 */
@PortletConfiguration(
	portletName = "1", displayName = { @LocaleString("jsf-cdi-applicant") },
	initParams = {
			@InitParameter(
				name = "javax.portlet.faces.defaultViewId.view", value = "/WEB-INF/views/portletViewMode.xhtml"
			),
			@InitParameter(
				name = "javax.portlet.faces.defaultViewId.edit", value = "/WEB-INF/views/portletEditMode.xhtml"
			),
			@InitParameter(
				name = "javax.portlet.faces.defaultViewId.help", value = "/WEB-INF/views/portletHelpMode.xhtml"
			),
		}, keywords = { @LocaleString("jsf-cdi-applicant") }, prefs = {
			@Preference(name = "datePattern", values = "MM/dd/yyyy"),
			@Preference(name = "recipientEmailAddress", values = "humanresources@some-company-domain.com")
		}, roleRefs = {
			@SecurityRoleRef(roleName = "administrator"), @SecurityRoleRef(roleName = "guest"),
			@SecurityRoleRef(roleName = "power-user"), @SecurityRoleRef(roleName = "user")
		}, shortTitle = { @LocaleString("jsf-cdi-applicant") }, supports =
		@Supports(portletModes = { "view", "edit", "help" }), title = { @LocaleString("jsf-cdi-applicant") }
)
@LiferayPortletConfiguration(
	portletName = "1", properties = {
			"com.liferay.portlet.ajaxable=false", "com.liferay.portlet.display-category=category.sample",
			"com.liferay.portlet.instanceable=true", "com.liferay.portlet.requires-namespaced-parameters=false"
		}
)
public class JSFCDIApplicantPortlet extends GenericFacesPortlet {
}
