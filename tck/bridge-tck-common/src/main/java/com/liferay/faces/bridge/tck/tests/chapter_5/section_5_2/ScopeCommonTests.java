/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class ScopeCommonTests {

	// Test is MultiRequest -- Render/Action
	// Test # -- 5.66
	@BridgeTest(test = "scopeAfterRedisplayResourcePPRTest")
	public String scopeAfterRedisplayResourcePPRTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		// If this method is reached in the RESOURCE_PHASE of the portlet lifecycle, then that means the
		// redisplayResourceAjaxResult.xhtml view was initially rendered properly, and that the ajaxified "Run Test"
		// button was clicked. In this case, set an attribute in the bridge request scope that is to be made available
		// in a subsequent HEADER_PHASE, provided that the BRIDGE_REQUEST_SCOPE_AJAX_ENABLED context initialization
		// parameter is true.
		if (portletRequestPhase == Bridge.PortletPhase.RESOURCE_PHASE) {
			requestMap.put("testAttr", testBean.getTestName());

			return "";
		}

		// Otherwise, if this method is reached in the HEADER_PHASE of the portlet lifecycle, then
		else if (Bridge.PortletPhase.HEADER_PHASE.equals(portletRequestPhase)) {

			String bridgeRequestScopeAjaxEnabledParam = externalContext.getInitParameter(
					Bridge.BRIDGE_REQUEST_SCOPE_AJAX_ENABLED);

			boolean bridgeRequestScopeAjaxEnabled = "true".equalsIgnoreCase(bridgeRequestScopeAjaxEnabledParam);

			// If the "redisplay" link was clicked, then this is not the initial render of the page. Instead, this is
			// a re-render of the page which was invoked by a RenderURL.
			if (externalContext.getRequestParameterMap().get("org.apache.portlet.faces.tck.redisplay") != null) {

				testBean.setTestComplete(true);

				// If the "pprSubmitted" request attribute if present, then that means the
				// scopeAfterRedisplayResourcePPRTestActionListener properly set the attribute in the RESOURCE_PHASE of
				// the portlet lifecycle when the "Run Test" button was clicked.
				if (requestMap.get("com.liferay.faces.bridge.tck.pprSubmitted") != null) {

					requestMap.remove("com.liferay.faces.bridge.tck.pprSubmitted");

					// If the "pprSubmitted" request attribute has the expected value, then that means that this test
					// method was properly called during the RESOURCE_PHASE of the portlet lifecycle.
					Object testAttr = requestMap.get("testAttr");

					if ((testAttr != null) && testAttr.equals(testBean.getTestName())) {

						// If the bridge request scope is supposed to include Ajax requests, then the test has passed.
						if (bridgeRequestScopeAjaxEnabled) {
							testBean.setTestResult(true,
								"Redisplay after resource request CORRECTLY restored scoped attr that was added " +
								"during resource execution since the bridge request scope is supposed to include " +
								"Ajax requests.");

							return Constants.TEST_SUCCESS;
						}

						// Otherwise, since the bridge request scope is not supposed to include Ajax requests, then
						// the test has failed.
						else {
							testBean.setTestResult(false,
								"Redisplay after resource request INCORRECTLY restored scoped attr that was added " +
								"during resource execution since the bridge request scope is not supposed to include " +
								"Ajax requests. NOTE: MAKE SURE YOU USED A NEW BROWSER TAB!");

							return Constants.TEST_FAILED;
						}
					}

					// Otherwise, the test has failed since the "pprSubmitted" request attribute does not have the
					// expected value.
					else {
						testBean.setTestResult(false,
							"Redisplay after resource request didn't restore the scoped attr that was added during " +
							"resource execution. NOTE: MAKE SURE YOU USED A NEW BROWSER TAB!");

						return Constants.TEST_FAILED;
					}
				}

				// Otherwise, since the "pprSubmitted" request attribute is not present, then
				else {

					// If the bridge request scope is supposed to include Ajax requests, then the test has failed
					// because the "pprSubmitted" attribute should have been preserved from the RESOURCE_PHASE of the
					// portlet lifecycle into the RENDER_PHASE.
					if (bridgeRequestScopeAjaxEnabled) {

						testBean.setTestResult(false,
							"Redisplay after resource request INCORRECTLY did not restore the scoped attr that was " +
							"added during resource execution since the bridge request scope is supposed to include " +
							"Ajax requests. NOTE: MAKE SURE YOU USED A NEW BROWSER TAB!");

						return Constants.TEST_FAILED;
					}

					// Otherwise, since the bridge request scope is not supposed to include Ajax requests, then the test
					// is successful because the "pprSubmitted" attribute should not be preserved from the
					// RESOURCE_PHASE of the portlet lifecycle into the RENDER_PHASE.
					else {

						testBean.setTestResult(true,
							"Redisplay after resource request CORRECTLY did not restore the scoped attr that was added during " +
							"resource execution since the bridge request scope is not supposed to include Ajax requests.");

						return Constants.TEST_SUCCESS;
					}
				}
			}
		}

		// Otherwise -- no output
		return "";
	}

	// ActionListener
	@BridgeTest(test = "scopeAfterRedisplayResourcePPRTestActionListener")
	public void scopeAfterRedisplayResourcePPRTestListener(TestBean testBean, ActionEvent action) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();

		requestMap.put("com.liferay.faces.bridge.tck.pprSubmitted", Boolean.TRUE);
	}
}
