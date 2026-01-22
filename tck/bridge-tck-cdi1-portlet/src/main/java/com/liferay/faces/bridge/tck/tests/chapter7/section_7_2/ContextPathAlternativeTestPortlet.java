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
package com.liferay.faces.bridge.tck.tests.chapter7.section_7_2;

import jakarta.portlet.annotations.InitParameter;
import jakarta.portlet.annotations.PortletConfiguration;
import jakarta.portlet.annotations.SecurityRoleRef;

import com.liferay.bean.portlet.LiferayPortletConfiguration;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * @author  Neil Griffin
 */
@PortletConfiguration(
	portletName = "chapter7_2CDITests-contextPathAlternativeTest-portlet",
	initParams = {
			@InitParameter(
				name = "jakarta.portlet.faces.defaultViewId.view", value = "/WEB-INF/views/singleRequestTest.xhtml"
			)
		}, roleRefs = {
			@SecurityRoleRef(roleName = "administrator"), @SecurityRoleRef(roleName = "guest"),
			@SecurityRoleRef(roleName = "power-user"), @SecurityRoleRef(roleName = "user")
		}
)
@LiferayPortletConfiguration(
	portletName = "chapter7_2CDITests-contextPathAlternativeTest-portlet",
	properties = {
			"com.liferay.portlet.header-request-attribute-prefix=false",
			"com.liferay.portlet.requires-namespaced-parameters=false"
		}
)
public class ContextPathAlternativeTestPortlet extends GenericFacesTestSuitePortlet {
}
