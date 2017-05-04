/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_6;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.annotation.PortletNamingContainer;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	// Test is MultiRequest -- Action/Render
	// Test #6.92
	@BridgeTest(test = "portletNamingContainerClientIdConsistentTest")
	public String portletNamingContainerClientIdConsistentTest(TestBean testBean) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		UIViewRoot viewRoot = ctx.getViewRoot();
		String clientId = viewRoot.getContainerClientId(ctx);

		// In the action portion create/attach things to request scope that should either be preserved or
		// are explicitly excluded -- test for presence/absence in render
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {
			extCtx.getRequestMap().put("com.liferay.faces.bridge.tck.clientIdInAction", clientId);

			return "portletNamingContainerClientIdConsistentTest"; // action Navigation result
		}
		else {
			testBean.setTestComplete(true);

			// Values set by portlet at end of action
			String clientIdInAction = (String) extCtx.getRequestMap().get(
					"com.liferay.faces.bridge.tck.clientIdInAction");
			String namespace = ((MimeResponse) ctx.getExternalContext().getResponse()).getNamespace();

			if (clientId.indexOf(namespace) < 0) {
				testBean.setTestResult(false,
					"UIViewRoot getClientContainerId doesn not includes the portlet namespace id.");

				return Constants.TEST_FAILED;
			}

			if (clientIdInAction.indexOf(namespace) >= 0) {
				testBean.setTestResult(true,
					"getContainerClientId encoded the same namespace in both the action and the render.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"getContainerClientId returned a different namespace encoding between an action and the render.  ClientId action: " +
					clientIdInAction + " while in render: " + clientId);

				return Constants.TEST_FAILED;
			}
		}
	}

	// Test is SingleRequest -- Render
	// Test #6.91
	@BridgeTest(test = "portletNamingContainerClientIdTest")
	public String portletNamingContainerClientIdTest(TestBean testBean) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = ctx.getViewRoot();
		String namespace = ((MimeResponse) ctx.getExternalContext().getResponse()).getNamespace();

		testBean.setTestComplete(true);

		if (viewRoot.getClass().getAnnotation(PortletNamingContainer.class) != null) {

			if (viewRoot.getContainerClientId(ctx).indexOf(namespace) >= 0) {
				testBean.setTestResult(true, "UIViewRoot getClientContainerId includes the portlet namespace id.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"UIViewRoot getClientContainerId doesn not includes the portlet namespace id.");

				return Constants.TEST_FAILED;
			}
		}
		else {
			testBean.setTestResult(false,
				"UIViewRoot is not annotated with javax.portlet.faces.annotation.PortletNamingContainer.");

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render
	// Test #6.96
	@BridgeTest(test = "portletNamingContainerUIViewRootClientIdTest")
	public String portletNamingContainerUIViewRootClientIdTest(TestBean testBean) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		String namespace = ((MimeResponse) ctx.getExternalContext().getResponse()).getNamespace();

		testBean.setTestComplete(true);

		try {
			PortletNamingContainerUIViewRoot vr = new PortletNamingContainerUIViewRoot();

			// ensure it has an id
			vr.setId("nc");

			if (vr.getContainerClientId(ctx).indexOf(namespace) >= 0) {

				testBean.setTestResult(true,
					"class PortletNamingContainerUIViewRoot correctly encodes portlet namespace id in the result from getContainerClientId");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"class PortletNamingContainerUIViewRoot doesn't encodes portlet namespace id in the result from getContainerClientId. namespaceId: " +
					namespace + " getContainerClientId returned: " + vr.getContainerClientId(ctx));

				return Constants.TEST_FAILED;
			}
		}
		catch (Exception e) {
			testBean.setTestResult(false,
				"class PortletNamingContainerUIViewRoot doesn't exist or can't be instantiated. Received exception: " +
				e.getMessage());

			return Constants.TEST_FAILED;
		}
	}

	// Test is SingleRequest -- Render
	// Test #6.94
	@BridgeTest(test = "portletNamingContainerUIViewRootTest")
	public String portletNamingContainerUIViewRootTest(TestBean testBean) {

		testBean.setTestComplete(true);

		try {
			PortletNamingContainerUIViewRoot vr = new PortletNamingContainerUIViewRoot();

			if (vr.getClass().getAnnotation(PortletNamingContainer.class) != null) {
				testBean.setTestResult(true,
					"class PortletNamingContainerUIViewRoot exists and can be instantiated and is properly annotated.");

				return Constants.TEST_SUCCESS;
			}
			else {
				testBean.setTestResult(false,
					"class PortletNamingContainerUIViewRoot exists and can be instantiated but isn't properly annotated.");

				return Constants.TEST_FAILED;
			}
		}
		catch (Exception e) {
			testBean.setTestResult(false,
				"class PortletNamingContainerUIViewRoot doesn't exist or can't be instantiated. Received exception: " +
				e.getMessage());

			return Constants.TEST_FAILED;
		}
	}

}
