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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_3;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.filter.ResourceResponseWrapper;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;
import com.liferay.faces.bridge.tck.factories.filter.ResourceResponseTCKImpl;


/**
 * @author  Neil Griffin
 */
public class Tests {

	// Test 6.151
	@BridgeTest(test = "setResponseStatusTest")
	public String setResponseStatusTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RESOURCE_PHASE) {
			int[] statusCodes = new int[] {
					HttpServletResponse.SC_ACCEPTED, HttpServletResponse.SC_BAD_GATEWAY,
					HttpServletResponse.SC_BAD_REQUEST, HttpServletResponse.SC_CONFLICT,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HttpServletResponse.SC_CONTINUE,
					HttpServletResponse.SC_CREATED, HttpServletResponse.SC_EXPECTATION_FAILED,
					HttpServletResponse.SC_FORBIDDEN, HttpServletResponse.SC_FOUND,
					HttpServletResponse.SC_GATEWAY_TIMEOUT, HttpServletResponse.SC_GONE,
					HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED, HttpServletResponse.SC_LENGTH_REQUIRED,
					HttpServletResponse.SC_METHOD_NOT_ALLOWED, HttpServletResponse.SC_MOVED_PERMANENTLY,
					HttpServletResponse.SC_MOVED_TEMPORARILY, HttpServletResponse.SC_MULTIPLE_CHOICES,
					HttpServletResponse.SC_NO_CONTENT, HttpServletResponse.SC_NO_CONTENT,
					HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION, HttpServletResponse.SC_NOT_ACCEPTABLE,
					HttpServletResponse.SC_NOT_FOUND, HttpServletResponse.SC_NOT_IMPLEMENTED,
					HttpServletResponse.SC_NOT_MODIFIED, HttpServletResponse.SC_OK,
					HttpServletResponse.SC_PARTIAL_CONTENT, HttpServletResponse.SC_PAYMENT_REQUIRED,
					HttpServletResponse.SC_PRECONDITION_FAILED, HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED,
					HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, HttpServletResponse.SC_REQUEST_TIMEOUT,
					HttpServletResponse.SC_REQUEST_URI_TOO_LONG, HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE,
					HttpServletResponse.SC_RESET_CONTENT, HttpServletResponse.SC_SEE_OTHER,
					HttpServletResponse.SC_SERVICE_UNAVAILABLE, HttpServletResponse.SC_SWITCHING_PROTOCOLS,
					HttpServletResponse.SC_TEMPORARY_REDIRECT, HttpServletResponse.SC_UNAUTHORIZED,
					HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, HttpServletResponse.SC_USE_PROXY
				};

			ExternalContext externalContext = facesContext.getExternalContext();

			ResourceResponse resourceResponse = (ResourceResponse) externalContext.getResponse();

			while ((resourceResponse instanceof ResourceResponseWrapper) &&
					!(resourceResponse instanceof ResourceResponseTCKImpl)) {
				ResourceResponseWrapper resourceResponseWrapper = (ResourceResponseWrapper) resourceResponse;
				resourceResponse = resourceResponseWrapper.getResponse();
			}

			if (resourceResponse instanceof ResourceResponseTCKImpl) {

				ResourceResponseTCKImpl resourceResponseTCKImpl = (ResourceResponseTCKImpl) resourceResponse;
				resourceResponseTCKImpl.getStatus();

				int originalStatus = resourceResponseTCKImpl.getStatus();

				boolean pass = true;

				for (int statusCode : statusCodes) {
					externalContext.setResponseStatus(statusCode);

					if (resourceResponseTCKImpl.getStatus() != statusCode) {
						pass = false;
						testBean.setTestResult(false,
							"externalContext.setResponseStatus(int) did not set the underlying status on the ResourceResponse");
					}
				}

				if (pass) {
					testBean.setTestResult(true,
						"externalContext.setResponseStatus(int) correctly set the underlying status on the ResourceResponse");
				}

				// A value of zero indicates that the status was never set.
				if (originalStatus != 0) {
					externalContext.setResponseStatus(originalStatus);
				}

				testBean.setTestComplete(true);

				if (testBean.getTestStatus()) {
					return Constants.TEST_SUCCESS;
				}
				else {
					return Constants.TEST_FAILED;
				}
			}
			else {
				testBean.setTestComplete(true);

				testBean.setTestResult(false, "resourceResponse is not an instance of ResourceResponseTCKImpl");

				return Constants.TEST_FAILED;
			}
		}

		return "";
	}
}
