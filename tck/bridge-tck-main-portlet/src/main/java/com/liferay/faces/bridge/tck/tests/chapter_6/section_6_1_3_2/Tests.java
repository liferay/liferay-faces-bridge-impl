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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {

	// Test is SingleRequest -- Render only

	// Test #6.67
	@BridgeTest(test = "encodeNamespaceTest")
	public String encodeNamespaceTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		MimeResponse response = (MimeResponse) externalContext.getResponse();

		String encodedNamespace = externalContext.encodeNamespace("");

		if (encodedNamespace == null) {
			testBean.setTestResult(false, "externalContext.encodeNamespace() failed:  it returned a null value");
		}
		else if (encodedNamespace.equals(response.getNamespace())) {
			testBean.setTestResult(true,
				"externalContext.encodeNamespace() correctly returned the same value as response.encodeNamespace when encoding the empty string.");
		}
		else {
			testBean.setTestResult(false,
				"externalContext.encodeNamespace() failed:  it returned a different value than response.encodeNamespace when encoding the empty string: it returned: " +
				encodedNamespace + " but we expected: " + response.getNamespace());
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	/**
	 * getApplicationMap Tests
	 */
	// Test #6.68
	@BridgeTest(test = "getApplicationMapTest")
	public String getApplicationMapTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is mutable
		// 2. Map contains attributes in the underlying portlet context
		// a) set on portlet context -- get via map
		// b) set on map -- get via app context
		// 3. Remove in request -- gone from Map
		// 4. Remove from Map -- gone in portlet context

		PortletContext portletCtx = (PortletContext) externalContext.getContext();
		Map<String, Object> externalContextAppMap = externalContext.getApplicationMap();

		// ensure they start out identical
		if (
			!containsIdenticalAttributeEntries(externalContextAppMap, (Enumeration<String>) portletCtx.getAttributeNames(),
					portletCtx)) {
			testBean.setTestResult(false,
				"Failed: Portlet context attributes and the externalContext applicationMap entries aren't identical.");

			return Constants.TEST_FAILED;
		}

		// Test for mutability
		try {
			externalContextAppMap.put("Test0Key", "Test0Value");
			portletCtx.setAttribute("Test1Key", "Test1Value");
		}
		catch (Exception e) {
			testBean.setTestResult(false,
				"Failed: Putting an attribute on the ExternalContext's applicationMap threw an exception: " +
				e.toString());

			return Constants.TEST_FAILED;
		}

		// test that we can read an attribute set on the portlet request via this Map
		// and vice-versa -- as we have just written an attribute on the externalContext and
		// the test portlet wrote one on the portlet request -- the act of verifying
		// the Maps contain the same keys/values should do the trick.
		if (
			!containsIdenticalAttributeEntries(externalContextAppMap, (Enumeration<String>) portletCtx.getAttributeNames(),
					portletCtx)) {
			testBean.setTestResult(false,
				"Failed: After setting an attribute on the portlet context and the externalContext applicationMap they no longer contain identical entries.");

			return Constants.TEST_FAILED;
		}

		// Now remove the attribute we put in the  -- do the remove on the opposite object
		externalContextAppMap.remove("Test1Key");
		portletCtx.removeAttribute("Test0Key");

		if (
			!containsIdenticalAttributeEntries(externalContextAppMap, (Enumeration<String>) portletCtx.getAttributeNames(),
					portletCtx)) {
			testBean.setTestResult(false,
				"Failed: After removing an attribute on the portlet context and the externalContext applicationMap they no longer contain identical entries.");

			return Constants.TEST_FAILED;
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true, "The Map returned from getApplicationMap is mutable.");
		testBean.appendTestDetail(
			"The getApplicationMap Map correctly expresses attributes in the underlying portletContext that have been added there.");
		testBean.appendTestDetail(
			"The getApplicationMap Map correctly reflects attrbiutes into the underlying portletContext that have been added to it.");
		testBean.appendTestDetail(
			"The getApplicationMap Map correctly doesn't express attrbiutes that have been removed from the underlying portletContext");
		testBean.appendTestDetail(
			"The getApplicationMap Map correctly cause the underlying portletContext to remove any attributes removed from it");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.69
	@BridgeTest(test = "getAuthTypeTest")
	public String getAuthTypeTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		RenderRequest request = (RenderRequest) externalContext.getRequest();

		String authType = externalContext.getAuthType();
		String requestAuthType = request.getAuthType();

		if (((authType == null) && (requestAuthType == null)) || authType.equals(requestAuthType)) {
			testBean.setTestResult(true,
				"externalContext.getAuthType() correctly returned the same value as request.getAuthType()");
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getAuthType() failed:  it returned a different value than request.getAuthType(): it returned: " +
				authType + " but we expected: " + requestAuthType);
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	// Test #6.70
	@BridgeTest(test = "getContextTest")
	public String getContextTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Object portletContext = externalContext.getContext();

		if ((portletContext != null) && (portletContext instanceof PortletContext)) {
			testBean.setTestResult(true, "externalContext.getContext() correctly returned an object of type PortletContext.");
		}
		else {
			testBean.setTestResult(false, "externalContext.getContext() didn't return an object of type PortletContext.");
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	/**
	 * getInitParameterMap Tests
	 */
	// Test #6.72
	@BridgeTest(test = "getInitParameterMapTest")
	public String getInitParameterMapTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains attributes in the underlying portlet context

		PortletContext portletCtx = (PortletContext) externalContext.getContext();
		Map<String, String> externalContextInitParamMap = externalContext.getInitParameterMap();

		// ensure they start out identical
		if (
			!containsIdenticalInitParamEntries(externalContextInitParamMap,
					(Enumeration<String>) portletCtx.getInitParameterNames(), portletCtx)) {
			testBean.setTestResult(false,
				"Failed: Portlet context initParams and the externalContext initParameterMap entries aren't identical.");

			return Constants.TEST_FAILED;
		}

		// Test for immutability
		try {
			externalContextInitParamMap.put("Test0Key", "Test0Value");
			testBean.setTestResult(false,
				"Failed: ExternalContext's initParameterMap isn't immutable -- a put() suceeded.");

			return Constants.TEST_FAILED;
		}
		catch (Exception e) {
			// this is what we expect -- just forge ahead;
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true, "The Map returned from initParameterMap is immutable.");
		testBean.appendTestDetail(
			"The initParameterMap Map correctly expresses attributes in the underlying context.");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.71
	@BridgeTest(test = "getInitParameterTest")
	public String getInitParameterTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletContext portletContext = (PortletContext) externalContext.getContext();

		// Get the enum of initparameter names -- then get one of the parameters
		// and make sure the same object is returned.
		Enumeration<String> e = portletContext.getInitParameterNames();

		if (!e.hasMoreElements()) {
			testBean.setTestResult(false, "externalContext.getInitParameter() failed: there are no initParameters");
		}
		else {
			String name = e.nextElement();
			String externalContextInitParam = externalContext.getInitParameter(name);
			String portletContextInitParam = portletContext.getInitParameter(name);

			if (externalContextInitParam.equals(portletContextInitParam)) {
				testBean.setTestResult(true,
					"externalContext.getInitParameter() correctly returned the same value as PortletContext.getInitParameter.");
			}
			else {
				testBean.setTestResult(false,
					"externalContext.getInitParameter() failed: it returned a different value than PortletContext.getInitParameter.  Expected: " +
					portletContextInitParam + " but received: " + externalContextInitParam);
			}
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	// Test #6.73
	@BridgeTest(test = "getRemoteUserTest")
	public String getRemoteUserTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		RenderRequest request = (RenderRequest) externalContext.getRequest();

		String remoteUser = externalContext.getRemoteUser();
		String requestRemoteUser = request.getRemoteUser();

		if (((remoteUser == null) && (requestRemoteUser == null)) || remoteUser.equals(requestRemoteUser)) {
			testBean.setTestResult(true,
				"externalContext.getRemoteUser() correctly returned the same value as request.getRemoteUser()");
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getRemoteUser() failed:  it returned a different value than request.getRemoteUser(): it returned: " +
				remoteUser + " but we expected: " + requestRemoteUser);
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	// Test #6.74
	@BridgeTest(test = "getRequestContextPathTest")
	public String getRequestContextPathTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		RenderRequest request = (RenderRequest) externalContext.getRequest();

		String contextPath = externalContext.getRequestContextPath();
		String requestContextPath = request.getContextPath();

		if (contextPath.equals(requestContextPath)) {
			testBean.setTestResult(true,
				"externalContext.getRequestContextPath() correctly returned the same value as request.getRequestContextPath()");
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getRequestContextPath() failed:  it returned a different value than request.getRequestContextPath(): it returned: " +
				contextPath + " but we expected: " + requestContextPath);
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	/**
	 * getRequestCookieMap Tests
	 */
	// Test #6.75
	@BridgeTest(test = "getRequestCookieMapTest")
	public String getRequestCookieMapTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. We don't expect the Map to contain anything as cookies are hidden in JSR 168

		Map<String, Object> cookieMap = externalContext.getRequestCookieMap();

		// ensure they start out identical
		if (cookieMap == null) {
			testBean.setTestResult(false, "Failed: externalContext.getRequestCookieMap() returned null.");

			return Constants.TEST_FAILED;
		}

		// Test for immutability
		try {
			cookieMap.put("Test0Key", "Test0Value");
			testBean.setTestResult(false,
				"Failed: ExternalContext's getRequestCookieMap isn't immutable -- a put() suceeded.");

			return Constants.TEST_FAILED;
		}
		catch (Exception e) {
			// this is what we expect -- just forge ahead;
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true, "The Map returned from getRequestCookieMap is immutable.");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.77
	@BridgeTest(test = "getRequestLocalesTest")
	public String getRequestLocalesTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		RenderRequest request = (RenderRequest) externalContext.getRequest();
		Iterator<Locale> locales = externalContext.getRequestLocales();
		Enumeration<Locale> requestLocales = request.getLocales();

		while (requestLocales.hasMoreElements() && locales.hasNext()) {
			Locale requestLocale = requestLocales.nextElement();
			Locale locale = locales.next();

			if (!locale.equals(requestLocale)) {
				testBean.setTestResult(false,
					"Failed: Portlet request locales and the externalContext getRequestLocales entries aren't identical.");

				return Constants.TEST_FAILED;
			}
		}

		if (requestLocales.hasMoreElements() || locales.hasNext()) {
			testBean.setTestResult(false,
				"Failed: Size of Portlet request locales enumeration and the externalContext getRequestLocales Iterator aren't identical.");

			return Constants.TEST_FAILED;
		}

		// Otherwise all out tests passed:

		testBean.setTestResult(true,
			"The Iterator returned from externalContext.getRequestLocales contains identical entries as the request.getLocales.");

		return Constants.TEST_SUCCESS;

	}

	// Test #6.76
	@BridgeTest(test = "getRequestLocaleTest")
	public String getRequestLocaleTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		RenderRequest request = (RenderRequest) externalContext.getRequest();

		Locale locale = externalContext.getRequestLocale();
		Locale requestLocale = request.getLocale();

		if (locale.equals(requestLocale)) {
			testBean.setTestResult(true,
				"externalContext.getRequestLocale() correctly returned the same Locale as request.getLocale()");
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getRequestLocale() failed:  it returned a different value than request.getRequestLocale(): it returned: " +
				locale + " but we expected: " + requestLocale);
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;

		}
	}

	// Test #6.79
	@BridgeTest(test = "getResourceAsStreamTest")
	public String getResourceAsStreamTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletContext pCtx = (PortletContext) externalContext.getContext();

		InputStream externalContextStream = externalContext.getResourceAsStream("/images/liferay-logo.png");
		InputStream pCtxStream = pCtx.getResourceAsStream("/images/liferay-logo.png");

		if ((externalContextStream == null) || (pCtxStream == null)) {
			testBean.setTestResult(false,
				"Failed: externalContext.getResourceAsStream failed:  returned InputStream is unexpectedly null.");

			return Constants.TEST_FAILED;
		}

		// compare the bytes in each stream
		byte[] stream1 = new byte[1024];
		byte[] stream2 = new byte[1024];
		boolean done = false;
		boolean success = true;

		try {

			while (!done) {

				try {
					int c1 = externalContextStream.read(stream1);
					int c2 = pCtxStream.read(stream2);

					if (c1 < 1024)
						done = true;

					if ((c1 != c2) || !Arrays.equals(stream1, stream2)) {
						success = false;
						done = true;
					}
				}
				catch (IOException e) {
					testBean.setTestResult(false,
						"Failed: Unexpected IOException thrown when reading stream: " + e.getMessage());

					return Constants.TEST_FAILED;
				}
				catch (Throwable t) {
					testBean.setTestResult(false,
						"Failed: Unexpected Throwable thrown when reading stream: " + t.getMessage());

					return Constants.TEST_FAILED;
				}
			}
		}
		finally {

			try {
				externalContextStream.close();
				pCtxStream.close();
			}
			catch (IOException e) {
				// ignore
			}
		}

		if (success) {
			testBean.setTestResult(true,
				"Stream returned from call through externalContext is the same as the one returned by the portletContext.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"Stream returned from call through externalContext is different than the one returned by the portletContext.");

			return Constants.TEST_FAILED;
		}

	}

	// Test #6.80
	@BridgeTest(test = "getResourcePathsTest")
	public String getResourcePathsTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletContext pCtx = (PortletContext) externalContext.getContext();

		Set<String> externalContextSet = externalContext.getResourcePaths("/tests/");
		Set<String> pCtxSet = pCtx.getResourcePaths("/tests/");

		if ((pCtxSet == null) || (externalContextSet == null)) {
			testBean.setTestResult(false,
				"Failed: externalContext.getResourcePaths failed:  returned path Set is unexpectedly null.");

			return Constants.TEST_FAILED;
		}
		else if (pCtxSet.equals(externalContextSet)) {
			testBean.setTestResult(true,
				"Path set returned from call through externalContext is the same as the one returned by the portletContext.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"Path set returned from call through externalContext is different than the one returned by the portletContext.");

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.78
	@BridgeTest(test = "getResourceTest")
	public String getResourceTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletContext pCtx = (PortletContext) externalContext.getContext();

		try {
			URL externalContextURL = externalContext.getResource("/images/liferay-logo.png");
			URL pCtxURL = pCtx.getResource("/images/liferay-logo.png");

			if ((externalContextURL == null) || (pCtxURL == null) || !pCtxURL.equals(externalContextURL)) {
				testBean.setTestResult(false,
					"Failed: externalContext.getResource failed:  URL returned from call through externalContext isn't the same as the one returned by the portletContext.");

				return Constants.TEST_FAILED;
			}

			// Otherwise all out tests passed:

			testBean.setTestResult(true,
				"URL returned from call through externalContext is the same as the one returned by the portletContext.");

			return Constants.TEST_SUCCESS;
		}
		catch (MalformedURLException e) {
			testBean.setTestResult(false,
				"Failed: Unexpected MalformedURLException thrown when called getResource().");

			return Constants.TEST_FAILED;
		}

	}

	// Test #6.83
	@BridgeTest(test = "getSessionMapTest")
	public String getSessionMapTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains the same entries as in the underlying session

		Map<String, Object> sessionMap = externalContext.getSessionMap();
		PortletSession portletSession = ((PortletRequest) externalContext.getRequest()).getPortletSession();
		Enumeration<String> requestSessionAttrNames = portletSession.getAttributeNames();

		// Test for mutability
		try {
			sessionMap.put("TestKey", "TestValue");
		}
		catch (Exception e) {
			testBean.setTestResult(false, "The Map returned from getSessionMap is immutable.");

			return "getSessionMapTest";
		}

		if (containsIdenticalSessionEntries(sessionMap, requestSessionAttrNames, portletSession)) {
			testBean.setTestResult(true,
				"externalContext.getSessionMap() correctly contains same attributes as the underlying portlet session.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getSessionMap() incorrectly contains different attributes than the underlying portlet session.");

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.82
	@BridgeTest(test = "getSessionTest")
	public String getSessionTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletRequest request = (PortletRequest) externalContext.getRequest();

		if (request.getPortletSession(true).equals(externalContext.getSession(true))) {
			testBean.setTestResult(true,
				"externalContext.getSession() correctly returned the same session object as the underlying request.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getSession() incorrectly returned a different session object than the underlying request.");

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.85
	@BridgeTest(test = "getUserPrincipalTest")
	public String getUserPrincipalTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletRequest request = (PortletRequest) externalContext.getRequest();

		Principal externalContextUP = externalContext.getUserPrincipal();
		Principal requestUP = request.getUserPrincipal();

		if (((externalContextUP == null) && (requestUP == null)) ||
				((externalContextUP != null) && (requestUP != null) && externalContextUP.equals(requestUP))) {
			testBean.setTestResult(true,
				"externalContext.getUserPrinicpal() correctly returned the same Principal as is in the underlying portlet request.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.getUserPrinicpal() unexpectedly returned a different Principal than is in the underlying portlet request.");

			return Constants.TEST_FAILED;
		}
	}

	// Test #6.84
	@BridgeTest(test = "sessionMapPreDestroyRemoveTest")
	public String sessionMapPreDestroyRemoveTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Application app = facesContext.getApplication();

		// ensure the managed beans come into existence
		Boolean isIn = (Boolean) app.evaluateExpressionGet(facesContext, "#{predestroySessionBean.inBridgeRequestScope}",
				Object.class);
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.remove("predestroySessionBean");

		// Now verify that things worked correctly
		// We expect that the beans were not added to the bridge scope (yet) and hence only the Predestroy was called
		Boolean notifiedAddedToBridgeScope = (Boolean) requestMap.get("PreDestroyBean1.attributeAdded");
		Boolean notifiedPreDestroy = (Boolean) requestMap.get("PreDestroyBean1.servletPreDestroy");
		Boolean notifiedBridgePreDestroy = (Boolean) requestMap.get("PreDestroyBean1.bridgePreDestroy");

		if ((notifiedAddedToBridgeScope == null) && (notifiedBridgePreDestroy == null) &&
				(notifiedPreDestroy != null) && notifiedPreDestroy.equals(Boolean.TRUE)) {

			// Only the regular PreDestroy was called and so it would have cleaned itself up
			testBean.setTestResult(true,
				"The bridge session scope behaved correctly in handling preDestroy of a removed attribute prior to it being added to the bridge's scope in that:");
			testBean.appendTestDetail("     a) the bean wasn't notified it had been added to the session scope.");
			testBean.appendTestDetail("     b) the bean didn't have its BridgePreDestroy called.");
			testBean.appendTestDetail("     c) the bean did have its Predestroy called.");
		}
		else {
			testBean.setTestResult(false,
				"The bridge session scope didn't behave correctly in handling preDestroy of a removed attribute:");

			if (notifiedAddedToBridgeScope != null)
				testBean.appendTestDetail("::::: it notified the bean it was added to the bridge session scope.");

			if (notifiedBridgePreDestroy != null)
				testBean.appendTestDetail("::::: it notified the bean it was removed from the bridge request scope.");

			if (notifiedPreDestroy == null)
				testBean.appendTestDetail("::::: it didn't notify the bean's PreDestroy.");

			if ((notifiedPreDestroy != null) && notifiedPreDestroy.equals(Boolean.FALSE))
				testBean.appendTestDetail(
					"::::: the bean's Predestroy was called but it thought it had been added to the bridge request scope.");
		}

		if (testBean.getTestStatus()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;
		}

	}

	// Test #6.81
	@BridgeTest(test = "setResponseCharacterEncodingTest")
	public String setResponseCharacterEncodingTest(TestBean testBean) {
		testBean.setTestComplete(true);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		PortletResponse response = (PortletResponse) externalContext.getResponse();

		String curEncoding = externalContext.getResponseCharacterEncoding();

		if (curEncoding.equalsIgnoreCase("utf-8")) {
			externalContext.setResponseCharacterEncoding("ISO-8859-1");
		}
		else {
			externalContext.setResponseCharacterEncoding("UTF-8");
		}

		if (curEncoding.equalsIgnoreCase(externalContext.getResponseCharacterEncoding())) {
			testBean.setTestResult(true, "externalContext.setResponseCharacterEncoding() correctly had no effect.");

			return Constants.TEST_SUCCESS;
		}
		else {
			testBean.setTestResult(false,
				"externalContext.setResponseCharacterEncoding() unexpectedly changed the response character encoding.");

			return Constants.TEST_FAILED;
		}
	}

	private boolean containsIdenticalAttributeEntries(Map<String, Object> m, Enumeration<String> eNames,
		PortletContext portletContext) {

		// For each entry in m ensure there is an identical one in the context
		for (Iterator<Map.Entry<String, Object>> entries = m.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<String, Object> e = entries.next();
			Object attrValue = portletContext.getAttribute(e.getKey());
			Object mapObj = e.getValue();

			if ((mapObj == null) && (attrValue == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (attrValue == null) || !mapObj.equals(attrValue)) {
				return false;
			}
		}

		// For each entry in the context -- ensure there is an identical one in the map
		while (eNames.hasMoreElements()) {
			String key = eNames.nextElement();
			Object attrValue = portletContext.getAttribute(key);
			Object mapObj = m.get(key);

			if ((mapObj == null) && (attrValue == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (attrValue == null) || !mapObj.equals(attrValue)) {
				return false;
			}
		}

		return true;
	}

	private boolean containsIdenticalInitParamEntries(Map<String, String> m, Enumeration<String> eNames,
		PortletContext portletContext) {

		// For each entry in m ensure there is an identical one in the context
		for (Iterator<Map.Entry<String, String>> entries = m.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<String, String> e = entries.next();
			String value = portletContext.getInitParameter(e.getKey());
			String mapObj = e.getValue();

			if ((mapObj == null) && (value == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (value == null) || !mapObj.equals(value)) {
				return false;
			}
		}

		// For each entry in the context -- ensure there is an identical one in the map
		while (eNames.hasMoreElements()) {
			String key = eNames.nextElement();
			Object value = portletContext.getInitParameter(key);
			Object mapObj = m.get(key);

			if ((mapObj == null) && (value == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (value == null) || !mapObj.equals(value)) {
				return false;
			}
		}

		return true;
	}

	private boolean containsIdenticalSessionEntries(Map<String, Object> m, Enumeration<String> eNames,
		PortletSession r) {

		// For each entry in m ensure there is an idenitcal one in the request
		for (Iterator<Map.Entry<String, Object>> entries = m.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<String, Object> e = entries.next();
			Object requestObj = r.getAttribute(e.getKey());
			Object mapObj = e.getValue();

			if ((mapObj == null) && (requestObj == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (requestObj == null) || !mapObj.equals(requestObj)) {
				return false;
			}
		}

		return true;
	}

//   @BridgeTest(test = "isUserInRoleTest") -- Not testable
//   @BridgeTest(test = "logTest") -- Not testable

}
