/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import java.io.IOException;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * @author  Kyle Stiemann
 */
public abstract class ManualBridgeInvokeCompatPortlet extends GenericFacesTestSuitePortlet {

	private static final String EXCEPTIONTHROWN_NODEFAULTVIEWID_TEST = "exceptionThrownWhenNoDefaultViewIdTest";
	private static final String VIEWIDWITHPARAM_TEST = "viewIdWithParam_1_Test";

	@Override
	public void renderHeaders(HeaderRequest headerRequest, HeaderResponse headerResponse) throws PortletException,
		IOException {

		if (getTestName().equals(EXCEPTIONTHROWN_NODEFAULTVIEWID_TEST)) {

			Bridge bridge = super.getFacesBridge(headerRequest, headerResponse);

			try {
				bridge.doFacesRequest(headerRequest, headerResponse);
			}
			catch (BridgeDefaultViewNotSpecifiedException e) {
				outputTestResult(headerResponse, Boolean.TRUE,
					"Correctly threw BridgeDefaultViewNotSpecifiedException when no default defined.");
			}
			catch (Exception e) {
				outputTestResult(headerResponse, Boolean.FALSE,
					"Didn't throw BridgeDefaultViewNotSpecifiedException when no default defined.");
			}
		}
		else {

			if (getTestName().equals(VIEWIDWITHPARAM_TEST)) {
				headerRequest.setAttribute(Bridge.VIEW_ID, "/tests/singleRequestTest.xhtml?param1=testValue");
			}

			super.renderHeaders(headerRequest, headerResponse);
		}
	}

	protected abstract void outputTestResult(MimeResponse response, Boolean pass, String detail) throws IOException;
}
