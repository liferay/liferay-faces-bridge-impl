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
package com.liferay.faces.issue.primefaces;

import javax.portlet.annotations.InitParameter;
import javax.portlet.annotations.PortletConfiguration;
import javax.portlet.annotations.SecurityRoleRef;
import javax.portlet.faces.GenericFacesPortlet;


/**
 * @author  Neil Griffin
 */
@PortletConfiguration(
	portletName = "FACES3031", initParams = {
			@InitParameter(
				name = "javax.portlet.faces.defaultViewId.view", value = "/WEB-INF/views/FACES-3031.xhtml"
			)
		}, roleRefs = {
			@SecurityRoleRef(roleName = "administrator"), @SecurityRoleRef(roleName = "guest"),
			@SecurityRoleRef(roleName = "power-user"), @SecurityRoleRef(roleName = "user")
		}
)
public class FACES_3031Portlet extends GenericFacesPortlet {
}
