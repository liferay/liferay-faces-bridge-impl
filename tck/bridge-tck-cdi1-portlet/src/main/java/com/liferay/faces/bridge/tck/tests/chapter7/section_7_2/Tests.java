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
package com.liferay.faces.bridge.tck.tests.chapter7.section_7_2;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.annotations.PortletName;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.BridgeRequestScoped;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
@Named("chapter7_2CDITests")
@BridgeRequestScoped
public class Tests {

	// Private Constants
	private static final String TEST_REQUIRES_PORTLET2 =
		"This test only applies to Portlet 2.0 and is a no-op PASS for Portlet 3.0";

	@Inject
	private BridgeRequestScopedBean bridgeRequestScopedBean;

	@Inject
	private CDIRequestScopedBean cdiRequestScopedBean;

	@Inject
	private PortletConfig portletConfig;

	@Inject
	private PortletContext portletContext;

	@Inject
	private PortletMode portletMode;

	@Inject
	@PortletName
	private String portletName;

	@Inject
	private PortletPreferences portletPreferences;

	@Inject
	private PortletRequest portletRequest;

	@Inject
	private PortletRequestScopedBean portletRequestScopedBean;

	@Inject
	private PortletResponse portletResponse;

	@Inject
	private PortletSession portletSession;

	@Inject
	private RenderParameters renderParams;

	@Inject
	private RenderRequest renderRequest;

	@Inject
	private ResourceRequest resourceRequest;

	@Inject
	private ResourceResponse resourceResponse;

	@Inject
	private StateAwareResponse stateAwareResponse;

	@BridgeTest(test = "bridgeRequestScopedBeanTest")
	public String bridgeRequestScopedBeanTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
			bridgeRequestScopedBean.setFoo("setInActionPhase");

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {
			testBean.setTestComplete(true);

			if ("setInActionPhase".equals(bridgeRequestScopedBean.getFoo())) {
				testBean.setTestResult(true,
					"@BridgeRequestScoped is behaving like faces-config &lt;managed-bean&gt; &lt;scope&gt;request&lt/scope&gt; (bridge request scope)");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "@BridgeRequestScoped is behaving like @PortletRequestScoped");

				return Constants.TEST_FAILED;
			}
		}

		testBean.setTestResult(false, "Unexpected portletPhase=" + portletPhase);

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "cdiRequestScopedBeanTest")
	public String cdiRequestScopedBeanTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
			cdiRequestScopedBean.setFoo("setInActionPhase");

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {
			testBean.setTestComplete(true);

			if (cdiRequestScopedBean.getFoo() == null) {
				testBean.setTestResult(true,
					"CDI @RequestScoped is behaving like @PortletRequestScoped and not like @BridgeRequestScoped");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "CDI @RequestScoped is behaving like @BridgeRequestScoped");

				return Constants.TEST_FAILED;
			}
		}

		testBean.setTestResult(false, "Unexpected portletPhase=" + portletPhase);

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletConfigAlternativeTest")
	public String portletConfigAlternativeTest(TestBean testBean) {
		String value = portletConfig.getInitParameter("tck");

		// PortletConfigTCKImpl.getInitParameter(String) expects this condition.
		if ("true".equals(value)) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletConfig was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletConfig was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletContextAlternativeTest")
	public String portletContextAlternativeTest(TestBean testBean) {

		String value = String.valueOf(portletContext.getAttribute("tck"));

		// PortletContextTCKImpl.getAttribute(String) expects this condition.
		if ("true".equals(value)) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletContext was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletContext was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletModeAlternativeTest")
	public String portletModeAlternativeTest(TestBean testBean) {

		// HeaderRequestTCKImpl.getPortletMode() expects this condition.
		if (portletMode.getClass().getName().contains("PortletModeTCKViewImpl")) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletMode was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletMode was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletNameAlternativeTest")
	public String portletNameAlternativeTest(TestBean testBean) {

		// PortletConfigTCKImpl.getPortletName() expects this condition.
		if (portletName.equals("tckPortletName")) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletName was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletName was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletPreferencesAlternativeTest")
	public String portletPreferencesAlternativeTest(TestBean testBean) {

		// HeaderRequestTCKImpl.getPortletPreferences() expects this condition.
		if (portletPreferences.getClass().getName().contains("PortletPreferencesTCKImpl")) {

			testBean.setTestResult(true,
				"The bridge's alternative producer for PortletPreferences was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletPreferences was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletRequestAlternativeTest")
	public String portletRequestAlternativeTest(TestBean testBean) {

		if (portletRequest.getClass().getName().contains("HeaderRequestTCKImpl")) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletRequest was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletRequest was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletRequestScopedBeanTest")
	public String portletRequestScopedBeanTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
			portletRequestScopedBean.setFoo("setInActionPhase");

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {
			testBean.setTestComplete(true);

			if (portletRequestScopedBean.getFoo() == null) {
				testBean.setTestResult(true, "@PortletRequestScoped is not behaving like @BridgeRequestScoped");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false, "@PortletRequestScoped is behaving like @BridgeRequestScoped");

				return Constants.TEST_FAILED;
			}
		}

		testBean.setTestResult(false, "Unexpected portletPhase=" + portletPhase);

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletResponseAlternativeTest")
	public String portletResponseAlternativeTest(TestBean testBean) {

		if (portletResponse.getClass().getName().contains("HeaderResponseTCKImpl")) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletResponse was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletResponse was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletSessionAlternativeTest")
	public String portletSessionAlternativeTest(TestBean testBean) {

		if (portletSession.getClass().getName().contains("PortletSessionTCKImpl")) {

			testBean.setTestResult(true, "The bridge's alternative producer for PortletSession was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for PortletSession was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "renderParamsAlternativeTest")
	public String renderParamsAlternativeTest(TestBean testBean) {

		if (renderParams.getClass().getName().contains("RenderParametersTCKImpl")) {

			testBean.setTestResult(true, "The bridge's alternative producer for RenderParameters was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for RenderParameters was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "renderRequestAlternativeTest")
	public String renderRequestAlternativeTest(TestBean testBean) {

		if (renderRequest.getClass().getName().contains("HeaderRequestTCKImpl")) {

			testBean.setTestResult(true, "The bridge's alternative producer for RenderRequest was properly invoked");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "The bridge's alternative producer for RenderRequest was not invoked");

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "renderResponseAlternativeTest")
	public String renderResponseAlternativeTest(TestBean testBean) {

		if (portletConfig.getPortletName().equals(portletConfig.getPortletName())) {

			testBean.setTestResult(true, TEST_REQUIRES_PORTLET2);

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, TEST_REQUIRES_PORTLET2);

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "resourceRequestAlternativeTest")
	public String resourceRequestAlternativeTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			if (resourceRequest.getClass().getName().contains("ResourceRequestTCKImpl")) {

				testBean.setTestResult(true,
					"The bridge's alternative producer for ResourceRequest was properly invoked");

				return Constants.TEST_SUCCESS;
			}

			testBean.setTestResult(false, "The bridge's alternative producer for ResourceRequest was not invoked");

			return Constants.TEST_FAILED;
		}

		return "";
	}

	@BridgeTest(test = "resourceResponseAlternativeTest")
	public String resourceResponseAlternativeTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {

			if (resourceResponse.getClass().getName().contains("ResourceResponseTCKImpl")) {

				testBean.setTestResult(true,
					"The bridge's alternative producer for ResourceResponse was properly invoked");

				return Constants.TEST_SUCCESS;
			}

			testBean.setTestResult(false, "The bridge's alternative producer for ResourceResponse was not invoked");

			return Constants.TEST_FAILED;
		}

		return "";
	}

	@BridgeTest(test = "stateAwareResponseAlternativeTest")
	public String stateAwareResponseAlternativeTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {

			bridgeRequestScopedBean.setFoo(stateAwareResponse.getClass().getSimpleName());

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {

			if ("ActionResponseTCKImpl".equals(bridgeRequestScopedBean.getFoo())) {

				testBean.setTestResult(true,
					"The bridge's alternative producer for StateAwareResponse was properly invoked");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"The bridge's alternative producer for StateAwareResponse was not invoked");

				return Constants.TEST_FAILED;
			}
		}

		testBean.setTestResult(false, "Unexpected portletPhase=" + portletPhase);

		return Constants.TEST_FAILED;
	}
}
