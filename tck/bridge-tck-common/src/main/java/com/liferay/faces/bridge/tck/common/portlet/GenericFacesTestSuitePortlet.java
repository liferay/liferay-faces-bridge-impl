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
package com.liferay.faces.bridge.tck.common.portlet;

import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.GenericFacesPortlet;

import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class GenericFacesTestSuitePortlet extends GenericFacesPortlet {

	private String mTestBeanName;
	private String mTestName;

	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {
		initTestRequest(renderRequest);
		super.doDispatch(renderRequest, renderResponse);
	}

	public Bridge getFacesBridge(PortletRequest request, PortletResponse response) throws PortletException {
		initTestRequest(request);

		return super.getFacesBridge(request, response);
	}

	public String getTestName() {
		return mTestName;
	}

	public void init(PortletConfig config) throws PortletException {
		String portletConfigWrapperFQCN = config.getPortletContext().getInitParameter("portletConfigWrapperClass");

		if (portletConfigWrapperFQCN != null) {

			try {
				Class<?> portletConfigWrapperClass = (Class<?>) Class.forName(portletConfigWrapperFQCN);
				Constructor<?> constructor = portletConfigWrapperClass.getConstructor(PortletConfig.class);
				config = (PortletConfig) constructor.newInstance(config);
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		// parse the portlet name to extract the testName and bean we use to during this test
		// This information is subsequently attached to each request (as an attribute).
		// The generic TestRunnerBean uses this information to execute the bean and properly
		// reference the test in any result.

		// portletName syntax: testGroup-testName-portlet
		String portletName = config.getPortletName();
		String[] parts = portletName.split("-");

		if (parts.length != 3) {
			throw new IllegalStateException(
				"Incorrect portletName syntax for a test:  should be testGroup-testName-portlet");
		}

		mTestBeanName = parts[0];
		mTestName = parts[1];

		if (!mTestName.endsWith("Test")) {
			mTestName = mTestName.concat("Test");
		}

		super.init(config);
	}

	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
		IOException {
		initTestRequest(actionRequest);

		// add an additional attribute -- so one of the tests can verify its excluded in render
		actionRequest.setAttribute("verifyPreBridgeExclusion", "avalue");
		super.processAction(actionRequest, actionResponse);
		actionRequest.removeAttribute("verifyPreBridgeExclusion");
	}

	private void initTestRequest(PortletRequest portletRequest) {
		portletRequest.setAttribute(Constants.TEST_BEAN_NAME, mTestBeanName);
		portletRequest.setAttribute(Constants.TEST_NAME, mTestName);
		portletRequest.setAttribute(Constants.PORTLET_CONFIG, this.getPortletConfig());

		if (portletRequest.getParameter(Bridge.VIEW_ID) != null) {
			portletRequest.setAttribute(Bridge.VIEW_ID, portletRequest.getParameter(Bridge.VIEW_ID));
		}
		else if (portletRequest.getParameter(Bridge.VIEW_PATH) != null) {
			portletRequest.setAttribute(Bridge.VIEW_PATH, portletRequest.getParameter(Bridge.VIEW_PATH));
		}
	}
}
