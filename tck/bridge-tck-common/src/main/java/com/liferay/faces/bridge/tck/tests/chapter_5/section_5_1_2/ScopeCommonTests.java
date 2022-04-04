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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_1_2;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class ScopeCommonTests {

	private static final String REQUEST_ATTR_VALUE = "T1";

	// Test is MultiRequest -- Render/Action
	// Test #5.8
	@BridgeTest(test = "requestRenderRedisplayTest")
	public String requestRenderRedisplayTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		// If this method is reached in the ACTION_PHASE of the portlet lifecycle, then that means the
		// multiRequestTest.xhtml view was initially rendered properly, and that the "Run Test" button was clicked. In
		// this case, set an attribute in the bridge request scope that is to be made available in a subsequent
		// HEADER_PHASE, provided that the BRIDGE_REQUEST_SCOPE_ACTION_ENABLED context initialization parameter is true.
		if (portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) {

			requestMap.put("com.liferay.faces.bridge.tck.TestRequestScope_a", REQUEST_ATTR_VALUE);
		}

		// Otherwise, if this method is reached in the HEADER_PHASE of the portlet lifecycle, then
		else if (portletRequestPhase == Bridge.PortletPhase.HEADER_PHASE) {

			// If the page is being redisplayed due to clicking the "Run Test" link, then
			if (externalContext.getRequestParameterMap().get("org.apache.portlet.faces.tck.redisplay") != null) {

				testBean.setTestComplete(true);

				String bridgeRequestScopeActionEnabledParam = externalContext.getInitParameter(
						Bridge.BRIDGE_REQUEST_SCOPE_ACTION_ENABLED);
				boolean bridgeRequestScopeActionEnabled = "true".equalsIgnoreCase(bridgeRequestScopeActionEnabledParam);
				String requestScopedAttribute = (String) requestMap.get(
						"com.liferay.faces.bridge.tck.TestRequestScope_a");

				if (requestScopedAttribute != null) {

					if (bridgeRequestScopeActionEnabled) {

						testBean.setTestResult(true,
							"Request attribute retained (AS EXPECTED) through a redisplay since the bridge request " +
							"scope spans ACTION_PHASE -> ACTION_PHASE.");

						return Constants.TEST_SUCCESS;
					}
					else {

						testBean.setTestResult(false,
							"Request attribute retained through a redisplay EVEN THOUGH the bridge " +
							"request scope DOES NOT span ACTION_PHASE -> ACTION_PHASE.");

						return Constants.TEST_FAILED;
					}
				}
				else {

					if (bridgeRequestScopeActionEnabled) {

						testBean.setTestResult(false,
							"Request attribute NOT retained through a redisplay EVEN THOUGH the bridge " +
							"request scope spans ACTION_PHASE -> ACTION_PHASE.");

						return Constants.TEST_FAILED;
					}
					else {

						testBean.setTestResult(true,
							"Request attribute NOT retained (AS EXPECTED) through a redisplay since the bridge " +
							"request scope does not span ACTION_PHASE -> ACTION_PHASE.");

						return Constants.TEST_SUCCESS;
					}
				}
			}
		}

		return "requestRenderRedisplayTest";
	}
}
