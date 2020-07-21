/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
@ManagedBean(name = "chapter6_1_3_1EncodePartialActionURLTests")
@RequestScoped
public class EncodePartialActionURLTests {

	@BridgeTest(test = "encodePartialActionURLTest")
	public String encodePartialActionURLTest(TestBean testBean) {

		// Test is invoked from resourceAjaxResult.xhtml which simply submits a form via Ajax.
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// If executing in the RESOURCE_PHASE of the JSF lifecycle, and the "_jsfBridgeAjax" parameter is "true", then
		// that means the form was submitted via Ajax with a URL that caused the bridge to invoke the JSF lifecycle.
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			ExternalContext externalContext = facesContext.getExternalContext();

			Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
			String jsfBridgeAjax = requestParameterMap.get(Bridge.FACES_AJAX_PARAMETER);

			if ("true".equals(jsfBridgeAjax)) {

				String clientWindowParam = requestParameterMap.get(ResponseStateManager.CLIENT_WINDOW_URL_PARAM);

				if (ClientWindowTestUtil.isClientWindowEnabled(externalContext)) {

					String clientWindowId = ClientWindowTestUtil.getClientWindowId(externalContext);

					if ((clientWindowParam != null) && (clientWindowParam.length() > 0)) {

						if (clientWindowParam.equals(clientWindowId)) {
							testBean.setTestResult(true,
								"encodePartialActionURL returned a URL that correctly caused the bridge to invoke the JSF lifecycle " +
								"in the RESOURCE_PHASE of the portlet lifecycle and the " +
								ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " request parameter value=[" +
								clientWindowParam + "] is equal to ClientWindow.getId().");
						}
						else {
							testBean.setTestResult(false,
								"encodePartialActionURL returned a URL such that the " +
								ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " request parameter value=[" +
								clientWindowParam + "] did NOT equal ClientWindow.getId()=[" + clientWindowId + "].");
						}
					}
					else {
						testBean.setTestResult(false,
							"encodePartialActionURL returned a URL that contained a null value for the " +
							ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " request parameter.");
					}
				}
				else {

					if (clientWindowParam == null) {
						testBean.setTestResult(true,
							"encodePartialActionURL returned a URL that correctly caused the bridge to invoke the JSF lifecycle " +
							"in the RESOURCE_PHASE of the portlet lifecycle.");
					}
					else {
						testBean.setTestResult(false,
							"encodePartialActionURL returned a URL that incorrectly included the " +
							ResponseStateManager.CLIENT_WINDOW_URL_PARAM + " request parameter with value=[" +
							clientWindowParam + "] even though the client window feature is disabled.");
					}
				}
			}
			else {
				testBean.setTestResult(false,
					"encodePartialActionURL returned a URL that did not contain the " + Bridge.FACES_AJAX_PARAMETER +
					" request parameter.");
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}
		else {
			return "";
		}
	}
}
