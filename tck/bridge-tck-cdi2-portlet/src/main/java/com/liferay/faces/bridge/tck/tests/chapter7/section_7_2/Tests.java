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
	private CDIRequestScopedBeanExtension cdiRequestScopedBeanExtension;

	@BridgeTest(test = "cdiRequestScopedBeanExtensionTest")
	public String cdiRequestScopedBeanExtensionTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletPhase == Bridge.PortletPhase.ACTION_PHASE) {
			cdiRequestScopedBeanExtension.setFoo("setInActionPhase");

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletPhase == Bridge.PortletPhase.RENDER_PHASE) {
			testBean.setTestComplete(true);

			if ("setInActionPhase".equals(cdiRequestScopedBeanExtension.getFoo())) {
				testBean.setTestResult(true,
					"CDI @RequestScoped is behaving like faces-config &lt;managed-bean&gt; &lt;scope&gt;request&lt/scope&gt; (bridge request scope)");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"CDI @RequestScoped is behaving like @PortletRequestScoped rather than @BridgeRequestScoped");

				return Constants.TEST_FAILED;
			}
		}

		testBean.setTestResult(false, "Unexpected portletPhase=" + portletPhase);

		return Constants.TEST_FAILED;
	}

	@BridgeTest(test = "portletRequestScopedBeanExtensionTest")
	public String portletRequestScopedBeanExtensionTest(TestBean testBean) {

		// The @PortletRequestScoped annotation is not available in the Portlet 2.0 API, so return the same result as
		// the cdiRequestScopedBeanExtensionTest.
		return cdiRequestScopedBeanExtensionTest(testBean);
	}

}
