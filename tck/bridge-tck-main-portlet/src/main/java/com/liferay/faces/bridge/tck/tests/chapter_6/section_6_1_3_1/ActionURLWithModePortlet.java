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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * @author  Neil Griffin
 */
public class ActionURLWithModePortlet extends GenericFacesTestSuitePortlet {

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
		IOException {

		String testName = getTestName();

		if ("encodeActionURLWithModeRenderTest".equals(getTestName())) {

			PortletMode portletMode = actionRequest.getPortletMode();
			String param1 = actionRequest.getParameter("param1");

			if (PortletMode.EDIT.equals(portletMode) && "testValue".equals(param1)) {
				actionResponse.setRenderParameter(testName + "-portletMode", "edit");
				actionResponse.setRenderParameter(testName + "-param1", param1);
			}

			super.processAction(actionRequest, actionResponse);
		}
		else {
			throw new PortletException("This portlet is only valid for use with encodeActionURLWithModeRenderTest");
		}
	}
}
