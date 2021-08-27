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
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.annotation.BridgeRequestScoped;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
@Named("chapter7_2CDITests")
@BridgeRequestScoped
public class Tests {

	@Inject
	private BridgeRequestScopedBean bridgeRequestScopedBean;

	@Inject
	private CDIRequestScopedBean cdiRequestScopedBean;

	@Inject
	private PortletRequestScopedBean portletRequestScopedBean;

	@BridgeTest(test = "bridgeRequestScopedBeanTest")
	public String bridgeRequestScopedBeanTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
			bridgeRequestScopedBean.setFoo("setInActionPhase");

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.RENDER_PHASE) {
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
		else if (portletPhase == Bridge.PortletPhase.RENDER_PHASE) {
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

	@BridgeTest(test = "portletRequestScopedBeanTest")
	public String portletRequestScopedBeanTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
			portletRequestScopedBean.setFoo("setInActionPhase");

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.RENDER_PHASE) {
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
}
