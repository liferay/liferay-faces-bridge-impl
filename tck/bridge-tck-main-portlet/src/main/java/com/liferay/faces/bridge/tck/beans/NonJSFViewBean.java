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
package com.liferay.faces.bridge.tck.beans;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class NonJSFViewBean {

	public NonJSFViewBean() {
	}

	public String getUrl() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		Map<String, Object> requestMap = externalContext.getRequestMap();
		String testName = (String) requestMap.get(Constants.TEST_NAME);

		if (testName.equals("encodeActionURLNonJSFViewRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewResourceTest")) {
			return externalContext.getRequestContextPath() +
				"/nonFacesViewTestPortlet.ptlt?javax.portlet.faces.ViewLink=true&amp;invokeTest=true";
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithParamRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithParamResourceTest")) {
			return externalContext.getRequestContextPath() +
				"/nonFacesViewTestPortlet.ptlt?javax.portlet.faces.ViewLink=true&amp;invokeTest=true&amp;param1=testValue";
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithModeRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithModeResourceTest")) {
			return externalContext.getRequestContextPath() +
				"/nonFacesViewTestPortlet.ptlt?javax.portlet.faces.ViewLink=true&amp;invokeTest=true&amp;javax.portlet.faces.PortletMode=edit&amp;param1=testValue";
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithInvalidModeRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithInvalidModeResourceTest")) {
			return externalContext.getRequestContextPath() +
				"/nonFacesViewTestPortlet.ptlt?javax.portlet.faces.ViewLink=true&amp;invokeTest=true&amp;javax.portlet.faces.PortletMode=blue&amp;param1=testValue";
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithWindowStateRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithWindowStateResourceTest")) {
			return externalContext.getRequestContextPath() +
				"/nonFacesViewTestPortlet.ptlt?javax.portlet.faces.ViewLink=true&amp;invokeTest=true&amp;javax.portlet.faces.WindowState=maximized&amp;param1=testValue";
		}
		else if (testName.equals("encodeActionURLNonJSFViewWithInvalidWindowStateRenderTest") ||
				testName.equals("encodeActionURLNonJSFViewWithInvalidWindowStateResourceTest")) {
			return externalContext.getRequestContextPath() +
				"/nonFacesViewTestPortlet.ptlt?javax.portlet.faces.ViewLink=true&amp;invokeTest=true&amp;javax.portlet.faces.WindowState=blue&amp;param1=testValue";
		}

		return null;
	}

}
