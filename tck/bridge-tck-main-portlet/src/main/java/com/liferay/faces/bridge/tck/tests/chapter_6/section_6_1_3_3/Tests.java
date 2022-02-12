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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class Tests {

	// Test 6.138
	@BridgeTest(test = "addResponseCookieTest")
	public String addResponseCookieTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		Bridge.PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase(facesContext);

		if (portletRequestPhase == Bridge.PortletPhase.ACTION_PHASE) {

			ExternalContext externalContext = facesContext.getExternalContext();

			Map<String, Object> properties = new HashMap<>();

			// Note: There is no guarantee that the portlet container will carry any of the properties like comment,
			// path, etc. through from the ACTION_PHASE to the HEADER_PHASE so that can't be tested. Instead, can only
			// try to pass the property map to the addResponseCookie method without an exception being thrown.
			properties.put("comment", "tckComment");
			properties.put("domain", "tckDomain");
			properties.put("httpOnly", true);
			properties.put("maxAge", new Integer(1234));
			properties.put("secure", Boolean.TRUE);
			properties.put("path", "tckPath");

			externalContext.addResponseCookie("tckCookie", "tck1234", properties);

			return "multiRequestTestResultRenderCheck";
		}
		else if (portletRequestPhase == Bridge.PortletPhase.HEADER_PHASE) {

			ExternalContext externalContext = facesContext.getExternalContext();

			Map<String, Object> requestCookieMap = externalContext.getRequestCookieMap();

			Object tckCookie = requestCookieMap.get("tckCookie");

			if ((tckCookie != null) && (tckCookie instanceof Cookie)) {
				Cookie cookie = (Cookie) tckCookie;

				if ("tckCookie".equals(cookie.getName()) && "tck1234".equals(cookie.getValue())) {
					testBean.setTestResult(true, "externalContext.addResponseCookie() set the expected cookie");
				}
				else {
					testBean.setTestResult(false,
						"externalContext.addResponseCookie() did not set the expected cookie value");
				}
			}
			else {
				testBean.setTestResult(false, "externalContext.addResponseCookie() did not set the expected cookie");
			}

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}

		return "";
	}

	// Test 6.139
	@BridgeTest(test = "getMimeTypeTest")
	public String getMimeTypeTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		PortletContext portletContext = (PortletContext) externalContext.getContext();

		String mimeType = portletContext.getMimeType("index.html");

		if (mimeType.equals("text/html") && mimeType.equals(externalContext.getMimeType("index.html"))) {
			testBean.setTestResult(true, "ExternalContext.getMimeType() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getMimeType() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

	// Test 6.143
	@BridgeTest(test = "getRealPathTest")
	public String getRealPathTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		PortletContext portletContext = (PortletContext) externalContext.getContext();

		String realPath = portletContext.getRealPath("/WEB-INF/faces-config.xml");

		if (Objects.equals(realPath, externalContext.getRealPath("/WEB-INF/faces-config.xml"))) {
			testBean.setTestResult(true, "ExternalContext.getRealPath() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getRealPath() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

	// Test 6.140
	@BridgeTest(test = "getRequestSchemeTest")
	public String getRequestSchemeTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		String scheme = portletRequest.getScheme();

		if (scheme.equals("http") && scheme.equals(externalContext.getRequestScheme())) {
			testBean.setTestResult(true, "ExternalContext.getRequestScheme() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getRequestScheme() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

	// Test 6.141
	@BridgeTest(test = "getRequestServerNameTest")
	public String getRequestServerNameTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		String serverName = portletRequest.getServerName();

		if (serverName.equals("localhost") && serverName.equals(externalContext.getRequestServerName())) {
			testBean.setTestResult(true, "ExternalContext.getRequestServerName() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getRequestServerName() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

	// Test 6.142
	@BridgeTest(test = "getRequestServerPortTest")
	public String getRequestServerPortTest(TestBean testBean) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();

		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

		if (portletRequest.getServerPort() == externalContext.getRequestServerPort()) {
			testBean.setTestResult(true, "ExternalContext.getRequestServerPort() returned the correct value");

			return Constants.TEST_SUCCESS;
		}

		testBean.setTestResult(false, "ExternalContext.getRequestServerPort() returned an incorrect value");

		return Constants.TEST_FAILED;
	}

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

			int originalStatus = resourceResponse.getStatus();

			boolean pass = true;

			for (int statusCode : statusCodes) {
				externalContext.setResponseStatus(statusCode);

				if (resourceResponse.getStatus() != statusCode) {
					pass = false;
					testBean.setTestResult(false,
						"externalContext.setResponseStatus(int) did not set the underlying status on the ResourceResponse");
				}
			}

			if (pass) {
				testBean.setTestResult(true,
					"externalContext.setResponseStatus(int) correctly set the underlying status on the ResourceResponse");
			}

			externalContext.setResponseStatus(originalStatus);

			testBean.setTestComplete(true);

			if (testBean.getTestStatus()) {
				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}
		}

		return "";
	}
}
