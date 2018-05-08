/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.portlet;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.faces.GenericFacesPortlet;
import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@Component(
	immediate = true, property = {
			"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.instanceable=true",
			"com.liferay.portlet.ajaxable=false", "javax.portlet.name=jsf_ds_applicant",
			"javax.portlet.display-name=jsf-ds-applicant",
			"javax.portlet.init-param.javax.portlet.faces.defaultViewId.view=/WEB-INF/views/portletViewMode.xhtml",
			"javax.portlet.init-param.javax.portlet.faces.defaultViewId.edit=/WEB-INF/views/portletEditMode.xhtml",
			"javax.portlet.init-param.javax.portlet.faces.defaultViewId.help=/WEB-INF/views/portletHelpMode.xhtml",
			"javax.portlet.preferences=classpath:/META-INF/preferences.xml",
			"javax.portlet.security-role-ref=power-user,user", "javax.portlet.portlet-mode=text/html;view,edit,help"
		}, service = Portlet.class
)
public class ApplicantFacesPortlet extends GenericFacesPortlet {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantFacesPortlet.class);

	// Workaround for LPS-66225:
	// This reference (along with the corresponding init-param in web.xml) is necessary in order to ensure that the
	// context listeners have been called before the init(PortletConfig) method is called.
	@Reference(target = "(servlet.init.portlet-class=com.liferay.faces.demos.portlet.ApplicantFacesPortlet)")
	private Servlet servlet;

	@Activate
	public void activate() {
		logger.debug("activate() called");
	}

	@Deactivate
	public void deactivate() {
		logger.debug("deactivate() called");
	}

	@Override
	public void destroy() {
		logger.debug("destroy() called");
		super.destroy();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		logger.debug("init(PortletConfig) called");
		super.init(portletConfig);
	}
}
