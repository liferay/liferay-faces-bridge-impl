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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class TestEventHandler implements BridgeEventHandler {

	public static final String EVENT_TEST_RESULT = "com.liferay.faces.bridge.tck.eventTestResult";

	public TestEventHandler() {
	}

	public EventNavigationResult handleEvent(FacesContext context, Event event) {
		Map<String, Object> m = context.getExternalContext().getRequestMap();
		String portletTestName = (String) m.get(Constants.TEST_NAME);
		String testName = (String) event.getValue();

		// Not our test so ignore this event (since we pass the same event to many portlets/test
		if ((testName == null) || (portletTestName == null) || !testName.equals(portletTestName))
			return null;

		if (testName.equalsIgnoreCase("getRequestHeaderMapEventTest")) {
			getRequestHeaderMapEventTest();
		}
		else if (testName.equalsIgnoreCase("getRequestHeaderValuesMapEventTest")) {
			getRequestHeaderValuesMapEventTest();
		}
		else if (testName.equalsIgnoreCase("getRequestCharacterEncodingEventTest")) {
			getRequestCharacterEncodingEventTest();
		}
		else if (testName.equalsIgnoreCase("getRequestContentTypeEventTest")) {
			getRequestContentTypeEventTest();
		}
		else if (testName.equalsIgnoreCase("getResponseCharacterEncodingEventTest")) {
			getResponseCharacterEncodingEventTest();
		}
		else if (testName.equalsIgnoreCase("getResponseContentTypeEventTest")) {
			getResponseContentTypeEventTest();
		}
		else if (testName.equalsIgnoreCase("redirectEventTest")) {
			redirectEventTest();
		}

		// URL encoded in the faces-config.xml -- based on the testname
		return new EventNavigationResult(null, testName);
	}

	private void getRequestCharacterEncodingEventTest() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		String charEncoding = extCtx.getRequestCharacterEncoding();

		if (charEncoding != null) {
			requestMap.put(EVENT_TEST_RESULT,
				"extCtx.getRequestCharacterEncoding() incorrectly returned non-null value when called during the event phase: " +
				charEncoding);
		}
		else {
			requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
		}
	}

	private void getRequestContentTypeEventTest() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		String contentType = extCtx.getRequestContentType();

		if (contentType != null) {
			requestMap.put(EVENT_TEST_RESULT,
				"extCtx.getRequestContentType() incorrectly returned non-null value when called during the event phase: " +
				contentType);
		}
		else {
			requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
		}
	}

	private void getRequestHeaderMapEventTest() {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains properties from the portlet request (that it should)
		// 2. Doesn't contain the Content-Type property
		// 3. Does include the Accept and Accept-Language

		Map headerMap = extCtx.getRequestHeaderMap();
		Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

		// Test for immutability
		try {
			String s = (String) headerMap.put("TestKey", "TestValue");
			requestMap.put(EVENT_TEST_RESULT, "Failed: The Map returned from getRequestHeaderMap isn't immutable.");

			return;
		}
		catch (Exception e) {
			// we expect to get an exception
		}

		Set<Map.Entry<String, String>> set = headerMap.entrySet();
		int propsFound = 0;

		for (Iterator<Map.Entry<String, String>> headers = set.iterator(); headers.hasNext();) {
			Map.Entry<String, String> e = headers.next();
			String key = e.getKey();

			if (key.equalsIgnoreCase("content-type")) {
				requestMap.put(EVENT_TEST_RESULT,
					"Failed: The Map returned from getRequestHeaderMap contains a content-type header but shouldn't.");

				return;
			}
			else if (key.equalsIgnoreCase("content-length")) {
				requestMap.put(EVENT_TEST_RESULT,
					"Failed: The Map returned from getRequestHeaderMap contains a content-length header but shouldn't.");

				return;
			}
			else if (key.equalsIgnoreCase("accept")) {
				boolean found = false;

				// parse the accept header into its parts
				String[] accepts = trim(e.getValue().split(","));

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

				// Now ensure that all entries in the getResponseContentTypes enum are in the property
				while (em.hasMoreElements()) {
					String rct = ((String) em.nextElement());
					found = false;

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found = true;

							break;
						}
					}

					if (!found) {
						requestMap.put(EVENT_TEST_RESULT,
							"Failed: The Map returned from getRequestHeaderMap is missing a key returned in request.getResponseContentTypes: " +
							rct);

						return;
					}
				}
			}
			else if (key.equalsIgnoreCase("accept-language")) {

				// parse the accept header into its parts
				String[] accepts = trim(e.getValue().split(","));

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

				// Now ensure the two match
				int found = 0;

				while (em.hasMoreElements()) {
					String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found += 1;

							break;
						}
					}
				}

				if (found != accepts.length) {
					requestMap.put(EVENT_TEST_RESULT,
						"Failed: The Map returned from getRequestHeaderMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

					return;
				}
			}
		}

		requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
	}

	private void getRequestHeaderValuesMapEventTest() {

		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains properties from the portlet request (that it should)
		// 2. Doesn't contain the Content-Type property
		// 3. Does include the Accept and Accept-Language

		Map<String, String[]> headerMap = extCtx.getRequestHeaderValuesMap();
		Enumeration propertyNames = ((PortletRequest) extCtx.getRequest()).getPropertyNames();

		// Test for immutability
		try {
			String[] s = (String[]) headerMap.put("TestKey", new String[] { "TestValue" });

			requestMap.put(EVENT_TEST_RESULT,
				"Failed: The Map returned from getRequestHeaderValuesMap isn't immutable.");

			return;
		}
		catch (Exception e) {
			// we expect to get an exception
		}

		Set<Map.Entry<String, String[]>> set = headerMap.entrySet();
		int propsFound = 0;

		for (Iterator<Map.Entry<String, String[]>> headers = set.iterator(); headers.hasNext();) {
			Map.Entry<String, String[]> e = headers.next();
			String key = e.getKey();

			if (key.equalsIgnoreCase("content-type")) {
				requestMap.put(EVENT_TEST_RESULT,
					"Failed: The Map returned from getRequestHeaderValuesMap contains a content-type header but shouldn't.");

				return;
			}
			else if (key.equalsIgnoreCase("content-length")) {
				requestMap.put(EVENT_TEST_RESULT,
					"Failed: The Map returned from getRequestHeaderValuesMap contains a content-length header but shouldn't.");

				return;
			}
			else if (key.equalsIgnoreCase("accept")) {
				boolean found = false;

				// parse the accept header into its parts
				String[] acceptsValues = e.getValue();
				String[] accepts = null;
				int count = 0;

				for (int i = 0; i < acceptsValues.length; i++) {
					String[] temp = trim(acceptsValues[i].split(","));

					if (accepts == null) {
						accepts = new String[temp.length];
					}
					else {
						String[] acceptsTemp = new String[accepts.length + temp.length];
						System.arraycopy(accepts, 0, acceptsTemp, 0, count);
						accepts = acceptsTemp;
					}

					System.arraycopy(temp, 0, accepts, count, temp.length);
					count += temp.length;
				}

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getResponseContentTypes();

				// Now ensure that all entries in the getResponseContentTypes enum are in the property
				while (em.hasMoreElements()) {
					String rct = ((String) em.nextElement());
					found = false;

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found = true;

							break;
						}
					}

					if (!found) {
						requestMap.put(EVENT_TEST_RESULT,
							"Failed: The Map returned from getRequestHeaderValuesMap is missing a key returned in request.getResponseContentTypes: " +
							rct);

						return;
					}
				}
			}
			else if (key.equalsIgnoreCase("accept-language")) {

				// parse the accept header into its parts
				String[] acceptLangValues = e.getValue();
				String[] accepts = null;
				int count = 0;

				for (int i = 0; i < acceptLangValues.length; i++) {
					String[] temp = trim(acceptLangValues[i].split(","));

					if (accepts == null) {
						accepts = new String[temp.length];
					}
					else {
						String[] acceptsTemp = new String[accepts.length + temp.length];
						System.arraycopy(accepts, 0, acceptsTemp, 0, count);
						accepts = acceptsTemp;
					}

					System.arraycopy(temp, 0, accepts, count, temp.length);
				}

				Enumeration em = ((PortletRequest) extCtx.getRequest()).getLocales();

				// Now ensure the two match
				int found = 0;

				while (em.hasMoreElements()) {
					String rct = ((Locale) em.nextElement()).toString().replace('_', '-');

					for (int i = 0; i < accepts.length; i++) {

						if (rct.regionMatches(true, 0, accepts[i], 0, rct.length())) {
							found += 1;

							break;
						}
					}
				}

				if (found != accepts.length) {
					requestMap.put(EVENT_TEST_RESULT,
						"Failed: The Map returned from getRequestHeaderValuesMap didn't contain an Accept-Language key with a value containing each of the locales returned from request.getResponseLocales segmented by a comma.");

					return;
				}
			}
		}

		requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
	}

	private void getResponseCharacterEncodingEventTest() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		try {
			String encoding = extCtx.getResponseCharacterEncoding();
			requestMap.put(EVENT_TEST_RESULT,
				"extCtx.getResponseCharacterEncoding() didn't throw an IllegalStateException when called during an Event.");
		}
		catch (IllegalStateException e) {
			requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
		}
	}

	private void getResponseContentTypeEventTest() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		try {
			String encoding = extCtx.getResponseContentType();
			requestMap.put(EVENT_TEST_RESULT,
				"extCtx.getResponseContentType() didn't throw an IllegalStateException when called during an Event.");
		}
		catch (IllegalStateException e) {
			requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
		}

	}

	private void redirectEventTest() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		Map<String, Object> requestMap = extCtx.getRequestMap();

		String target = ctx.getApplication().getViewHandler().getActionURL(ctx,
				"/tests/redirectTestResultRenderCheck.xhtml");

		try {
			extCtx.redirect(target);
			requestMap.put(EVENT_TEST_RESULT, Constants.TEST_SUCCESS);
		}
		catch (Exception e) {
			requestMap.put(EVENT_TEST_RESULT, "Calling extCtx.redirect() threw an exception: " + e.getMessage());
		}
	}

	private String[] trim(String[] toTrim) {

		for (int i = 0; i < toTrim.length; i++) {
			toTrim[i] = toTrim[i].trim();
		}

		return toTrim;
	}
}
