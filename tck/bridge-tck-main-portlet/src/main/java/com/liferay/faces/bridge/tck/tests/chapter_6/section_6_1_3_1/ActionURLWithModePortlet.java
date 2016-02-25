package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_1;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import java.io.IOException;

public class ActionURLWithModePortlet extends GenericFacesTestSuitePortlet {

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException {

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
