/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
/*
 *  Copyright 2009 .
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_5_1;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.preference.Preference;
import javax.portlet.filter.RenderRequestWrapper;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  jhaley
 */
public class Tests {

	private static final Map<String, Class<?>> EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES;

	static {

		Map<String, Class<?>> expectedElResolverFeatureDescriptorTypes = new HashMap<String, Class<?>>();
		expectedElResolverFeatureDescriptorTypes.put("portletConfig", javax.portlet.PortletConfig.class);
		expectedElResolverFeatureDescriptorTypes.put("actionRequest", javax.portlet.ActionRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("actionResponse", javax.portlet.ActionResponse.class);
		expectedElResolverFeatureDescriptorTypes.put("eventRequest", javax.portlet.EventRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("eventResponse", javax.portlet.EventResponse.class);
		addExpectedFeatureDescriptorTypeIfPossible("headerRequest", "javax.portlet.HeaderRequest",
			expectedElResolverFeatureDescriptorTypes);
		addExpectedFeatureDescriptorTypeIfPossible("headerResponse", "javax.portlet.HeaderResponse",
			expectedElResolverFeatureDescriptorTypes);
		expectedElResolverFeatureDescriptorTypes.put("renderRequest", javax.portlet.RenderRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("renderResponse", javax.portlet.RenderResponse.class);
		expectedElResolverFeatureDescriptorTypes.put("resourceRequest", javax.portlet.ResourceRequest.class);
		expectedElResolverFeatureDescriptorTypes.put("resourceResponse", javax.portlet.ResourceResponse.class);
		expectedElResolverFeatureDescriptorTypes.put("portletSession", javax.portlet.PortletSession.class);
		expectedElResolverFeatureDescriptorTypes.put("portletSessionScope", Map.class);
		expectedElResolverFeatureDescriptorTypes.put("httpSessionScope", Map.class);
		expectedElResolverFeatureDescriptorTypes.put("portletPreferences", javax.portlet.PortletPreferences.class);
		expectedElResolverFeatureDescriptorTypes.put("portletPreferencesValues", Map.class);
		expectedElResolverFeatureDescriptorTypes.put("mutablePortletPreferencesValues", Map.class);
		EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES = Collections.unmodifiableMap(
				expectedElResolverFeatureDescriptorTypes);
	}

	private static void addExpectedFeatureDescriptorTypeIfPossible(String key, String classNameValue,
		Map<String, Class<?>> map) {

		try {
			map.put(key, Class.forName(classNameValue));
		}
		catch (ClassNotFoundException e) {
			// no-op
		}
		catch (NoClassDefFoundError e) {
			// no-op
		}
	}

	//J-
//	// Unfortunately ELResolver.getFeatureDescriptors() cannot be tested in the TCK due to JSF implementation
//	// bugs. If you are an implementor, consider adding a unit test that tests the features below instead.
//	private static boolean isFeatureDescriptorValid(String name, FeatureDescriptor featureDescriptor, Class<?> clazz) {
//
//		return (featureDescriptor != null) && clazz.equals(featureDescriptor.getValue(ELResolver.TYPE)) &&
//			Boolean.TRUE.equals(featureDescriptor.getValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME)) &&
//			name.equals(featureDescriptor.getName()) && name.equals(featureDescriptor.getName()) &&
//			(featureDescriptor.getShortDescription() != null) && !featureDescriptor.isExpert() &&
//			!featureDescriptor.isHidden() && featureDescriptor.isPreferred();
//	}
	//J+

	/**
	 * Testing JSF EL - implicits are in alpha order.
	 *
	 * @param   testBean  the bean for testing
	 *
	 * @return
	 */
	@BridgeTest(test = "JSF_ELTest")
	public String JSF_ELTest(TestBean testBean) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		ELContext elContext = facesContext.getELContext();
		ELResolver facesResolver = elContext.getELResolver();

		// Test that each implicit object is accessible and has the right value in
		// both the action phase and render phase
		if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.ACTION_PHASE) {

			// ActionRequest
			try {

				// First ensure there are entries in the various scopes
				ensureScopeEntries(externalContext);

				// JSF Implicit Objects:
				// application -> ExternalContext.getContext();
				testImplicitObject(testBean, facesResolver, facesContext, "application", externalContext.getContext());

				// applicationScope -> externalContext.getApplicationMap()
				testImplicitObject(testBean, facesResolver, facesContext, "applicationScope",
					externalContext.getApplicationMap());

				// cookie -> externalContext.getRequestCookieMap()
				testImplicitObject(testBean, facesResolver, facesContext, "cookie",
					externalContext.getRequestCookieMap());

				// facesContext -> the FacesContext for this request
				testImplicitObject(testBean, facesResolver, facesContext, "facesContext", facesContext);

				// header -> externalContext.getRequestHeaderMap()
				testImplicitObject(testBean, facesResolver, facesContext, "header",
					externalContext.getRequestHeaderMap());

				// headerValues -> externalContext.getRequestHeaderValuesMap()
				testImplicitObject(testBean, facesResolver, facesContext, "headerValues",
					externalContext.getRequestHeaderValuesMap());

				// initParam -> externalContext.getInitParameterMap()
				testImplicitObject(testBean, facesResolver, facesContext, "initParam",
					externalContext.getInitParameterMap());

				// param -> externalContext.getRequestParameterMap()
				testImplicitObject(testBean, facesResolver, facesContext, "param",
					externalContext.getRequestParameterMap());

				// paramValues -> externalContext.getRequestParameterValuesMap()
				testImplicitObject(testBean, facesResolver, facesContext, "paramValues",
					externalContext.getRequestParameterValuesMap());

				// request -> externalContext.getRequest()
				testImplicitObject(testBean, facesResolver, facesContext, "request", externalContext.getRequest());

				// requestScope -> externalContext.getRequestScope()
				testImplicitObject(testBean, facesResolver, facesContext, "requestScope",
					externalContext.getRequestMap());

				// session -> externalContext.getSession()
				testImplicitObject(testBean, facesResolver, facesContext, "session", externalContext.getSession(true));

				// sessionScope -> externalContext.getSessionMap()
				testImplicitObject(testBean, facesResolver, facesContext, "sessionScope",
					externalContext.getSessionMap());

				// view -> facesContext.getViewRoot()
				testImplicitObject(testBean, facesResolver, facesContext, "view", facesContext.getViewRoot());

				// Portlet implicit objects
				// httpSessionScope:  mutable Map containing PortletSession attribute/values at APPLICATION_SCOPE.
				testHttpSessionScope(testBean, facesResolver, facesContext, "httpSessionScope",
					(PortletSession) externalContext.getSession(true));

				// mutablePortletPreferencesValues: mutable Map of type Map<String,
				// javax.portlet.faces.preference.Preference>.
				testMutablePortletPreferencesValues(testBean, facesResolver, facesContext,
					"mutablePortletPreferencesValues",
					((PortletRequest) externalContext.getRequest()).getPreferences().getMap());

				// portletConfig -> object of type javax.portlet.PortletConfig
				testImplicitObject(testBean, facesResolver, facesContext, "portletConfig",
					externalContext.getRequestMap().get(Constants.PORTLET_CONFIG));

				// portletPreferences -> ExternalContext.getRequest().getPreferences() object.
				testImplicitObject(testBean, facesResolver, facesContext, "portletPreferences",
					((PortletRequest) externalContext.getRequest()).getPreferences());

				// portletPreferencesValues -> ExternalContext.getRequest()).getPreferences().getMap().
				testImplicitObjectArrayMaps(testBean, facesResolver, facesContext, "portletPreferencesValues",
					((PortletRequest) externalContext.getRequest()).getPreferences().getMap());

				// portletSession -> ExternalContext.getSession()
				testImplicitObject(testBean, facesResolver, facesContext, "portletSession",
					externalContext.getSession(true));

				// portletSessionScope -> ExternalContext.getSessionMap().
				testImplicitObject(testBean, facesResolver, facesContext, "portletSessionScope",
					externalContext.getSessionMap());
			}
			catch (Throwable t) {
				testBean.setTestResult(false, "JSF EL failure in action request: " + t.getCause().toString());
			}

			return "JSF_ELTest";
		}

		// HeaderRequest / RenderRequest
		else {

			try {

				// JSF Implicit Objects
				// application -> ExternalContext.getContext();
				testImplicitObject(testBean, facesResolver, facesContext, "application", externalContext.getContext());

				// applicationScope -> externalContext.getApplicationMap()
				testImplicitObject(testBean, facesResolver, facesContext, "applicationScope",
					externalContext.getApplicationMap());

				// cookie -> externalContext.getRequestCookieMap()
				testImplicitObject(testBean, facesResolver, facesContext, "cookie",
					externalContext.getRequestCookieMap());

				// facesContext -> the FacesContext for this request
				testImplicitObject(testBean, facesResolver, facesContext, "facesContext", facesContext);

				// header -> externalContext.getRequestHeaderMap()
				testImplicitObject(testBean, facesResolver, facesContext, "header",
					externalContext.getRequestHeaderMap());

				// headerValues -> externalContext.getRequestHeaderValuesMap()
				testImplicitObject(testBean, facesResolver, facesContext, "headerValues",
					externalContext.getRequestHeaderValuesMap());

				// initParam -> externalContext.getInitParameterMap()
				testImplicitObject(testBean, facesResolver, facesContext, "initParam",
					externalContext.getInitParameterMap());

				// param -> externalContext.getRequestParameterMap()
				testImplicitObject(testBean, facesResolver, facesContext, "param",
					externalContext.getRequestParameterMap());

				// paramValues -> externalContext.getRequestParameterValuesMap()
				testImplicitObject(testBean, facesResolver, facesContext, "paramValues",
					externalContext.getRequestParameterValuesMap());

				// request -> externalContext.getRequest()
				testImplicitObject(testBean, facesResolver, facesContext, "request", externalContext.getRequest());

				// requestScope -> externalContext.getRequestScope()
				testImplicitObject(testBean, facesResolver, facesContext, "requestScope",
					externalContext.getRequestMap());

				// session -> externalContext.getSession()
				testImplicitObject(testBean, facesResolver, facesContext, "session", externalContext.getSession(true));

				// sessionScope -> externalContext.getSessionMap()
				testImplicitObject(testBean, facesResolver, facesContext, "sessionScope",
					externalContext.getSessionMap());

				// view -> facesContext.getViewRoot()
				testImplicitObject(testBean, facesResolver, facesContext, "view", facesContext.getViewRoot());

				// Portlet implicit objects
				// httpSessionScope:  mutable Map containing PortletSession attribute/values at APPLICATION_SCOPE.
				testHttpSessionScope(testBean, facesResolver, facesContext, "httpSessionScope",
					(PortletSession) externalContext.getSession(true));

				// mutablePortletPreferencesValues: mutable Map of type Map<String,
				// javax.portlet.faces.preference.Preference>.
				testMutablePortletPreferencesValues(testBean, facesResolver, facesContext,
					"mutablePortletPreferencesValues",
					((PortletRequest) externalContext.getRequest()).getPreferences().getMap());

				// portletConfig -> object of type javax.portlet.PortletConfig
				testImplicitObject(testBean, facesResolver, facesContext, "portletConfig",
					externalContext.getRequestMap().get(Constants.PORTLET_CONFIG));

				// portletPreferences -> ExternalContext.getRequest().getPreferences() object.
				testImplicitObject(testBean, facesResolver, facesContext, "portletPreferences",
					((PortletRequest) externalContext.getRequest()).getPreferences());

				// portletPreferencesValues -> ExternalContext.getRequest()).getPreferences().getMap().
				testImplicitObjectArrayMaps(testBean, facesResolver, facesContext, "portletPreferencesValues",
					((PortletRequest) externalContext.getRequest()).getPreferences().getMap());

				// portletSession -> ExternalContext.getSession()
				testImplicitObject(testBean, facesResolver, facesContext, "portletSession",
					externalContext.getSession(true));

				// portletSessionScope -> ExternalContext.getSessionMap().
				testImplicitObject(testBean, facesResolver, facesContext, "portletSessionScope",
					externalContext.getSessionMap());

				// RenderRequest
				if (BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RENDER_PHASE) {

					// renderRequest -> object of type javax.portlet.RenderRequest
					testImplicitObject(testBean, facesResolver, facesContext, "renderRequest",
						externalContext.getRequest());

					// renderResponse -> object of type javax.portlet.RenderResponse
					testImplicitObject(testBean, facesResolver, facesContext, "renderResponse",
						externalContext.getResponse());
				}

				// HeaderRequest
				else {

					// headerRequest -> object of type javax.portlet.HeaderRequest
					testImplicitObject(testBean, facesResolver, facesContext, "headerRequest",
						externalContext.getRequest());

					// headerResponse -> object of type javax.portlet.HeaderResponse
					testImplicitObject(testBean, facesResolver, facesContext, "headerResponse",
						externalContext.getResponse());
				}
			}
			catch (Throwable t) {
				fail(testBean, "JSF EL failure in render request: " + t.getCause().toString());
			}

			// Test ELResolver.getType()
			try {

				facesResolver.getType(elContext, null, null);
				fail(testBean, "PropertyNotFoundException not thrown when calling getType(elContext, null, null). ");
			}
			catch (PropertyNotFoundException e) {
				// Passed.
			}

			Set<Map.Entry<String, Class<?>>> entrySet = EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES.entrySet();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(
				"getType() didn't return null or elContext.setPropertyResolved(true) wasn't called for the following values: ");

			boolean first = true;

			for (Map.Entry<String, Class<?>> entry : entrySet) {

				String name = entry.getKey();

				if ((facesResolver.getType(elContext, null, name) != null) || !elContext.isPropertyResolved()) {

					if (!first) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(name);
					first = false;
				}
			}

			if (!first) {

				stringBuilder.append(". ");
				fail(testBean, stringBuilder.toString());
			}

			// Test ELResolver.setValue()
			try {

				facesResolver.setValue(elContext, null, null, "facesContext");
				fail(testBean,
					"PropertyNotFoundException not thrown when calling setValue(elContext, null, null, value). ");
			}
			catch (PropertyNotFoundException e) {
				// Passed.
			}

			Set<String> keySet = EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES.keySet();
			stringBuilder.setLength(0);
			stringBuilder.append(
				"setValue(elContext, null, name, value) didn't throw PropertyNotWritableException when name was set to the following values: ");

			first = true;

			for (String name : keySet) {

				if ((name.endsWith("Request") || name.endsWith("Response")) &&
						!(name.startsWith("render") || name.startsWith("header"))) {

					// Cannot test EL keywords for different portlet phases.
					continue;
				}

				Object originalValue = facesResolver.getValue(elContext, null, name);

				try {

					facesResolver.setValue(elContext, null, name, name);

					// Reset the original value so other parts of the test don't fail.
					facesResolver.setValue(elContext, null, name, originalValue);

					if (!first) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(name);
					first = false;
				}
				catch (PropertyNotWritableException e) {
					// Passed.
				}
			}

			if (!first) {

				stringBuilder.append(". ");
				fail(testBean, stringBuilder.toString());
			}

			// Test ELResolver.isReadOnly()
			try {

				facesResolver.isReadOnly(elContext, "facesContext", "facesContext");
				fail(testBean,
					"PropertyNotFoundException not thrown when calling isReadOnly(elContext, base, value). ");
			}
			catch (PropertyNotFoundException e) {
				// Passed.
			}

			stringBuilder.setLength(0);
			stringBuilder.append(
				"isReadOnly(elContext, null, name) didn't return true and/or call elContext.setPropertyResolved() when name was set to the following values: ");

			first = true;

			for (String name : keySet) {

				if (!facesResolver.isReadOnly(elContext, null, name) || !elContext.isPropertyResolved()) {

					if (!first) {
						stringBuilder.append(", ");
					}

					stringBuilder.append(name);
					first = false;
				}
			}

			if (!first) {

				stringBuilder.append(". ");
				fail(testBean, stringBuilder.toString());
			}

			//J-
//			// Unfortunately ELResolver.getFeatureDescriptors() cannot be tested in the TCK due to JSF implementation
//			// bugs. If you are an implementor, consider adding a unit test that tests the features below instead.
//			if (facesResolver.getFeatureDescriptors(elContext, "facesContext") != null) {
//				fail(testBean,
//					"ELResolver.getFeatureDescriptors() did not return null when passed a non-null value for base. ");
//			}
//
//			RenderRequest renderRequest = (RenderRequest) externalContext.getRequest();
//
//			// Workaround the fact that certain request attributes are null in a portlet environment. JSF
//			// implementations expect certain request attributes such as "javax.servlet.include.path_info" to be
//			// non-null so their classes can be obtained.
//			externalContext.setRequest(new RenderRequestELImpl(renderRequest));
//
//			Iterator<FeatureDescriptor> iterator = facesResolver.getFeatureDescriptors(elContext, null);
//			Map<String, FeatureDescriptor> featureDescriptors = new HashMap<String, FeatureDescriptor>();
//
//			while (iterator.hasNext()) {
//
//				FeatureDescriptor featureDescriptor = iterator.next();
//				featureDescriptors.put(featureDescriptor.getName(), featureDescriptor);
//			}
//
//			featureDescriptors = Collections.unmodifiableMap(featureDescriptors);
//			externalContext.setRequest(renderRequest);
//			stringBuilder.setLength(0);
//			stringBuilder.append("The following EL key words had invalid feature descriptors:<br />");
//
//			first = true;
//
//			for (Map.Entry<String, Class<?>> entry : entrySet) {
//
//				String name = entry.getKey();
//				FeatureDescriptor featureDescriptor = featureDescriptors.get(name);
//
//				if (!isFeatureDescriptorValid(name, featureDescriptor, entry.getValue())) {
//
//					if (!first) {
//						stringBuilder.append(",<br />");
//					}
//
//					stringBuilder.append(name);
//					stringBuilder.append(" (expected ELResolver.TYPE \"");
//					stringBuilder.append(EXPECTED_EL_RESOLVER_FEATURE_DESCRIPTOR_TYPES.get(name));
//					stringBuilder.append("\")");
//					first = false;
//				}
//			}
//
//			if (!first) {
//
//				stringBuilder.append(
//					".<br />ELResolver.RESOLVABLE_AT_DESIGN_TIME must be Boolean.TRUE. getName() and getDisplayName() must return the same value. getShortDescription() must not be null. isExpert() and isHidden() must return false. isPreferred() must return true. ");
//				fail(testBean, stringBuilder.toString());
//			}
//
//			// Test ELResolver.getCommonPropertyType()
//			if ((facesResolver.getCommonPropertyType(elContext, "facesContext") != null) ||
//					!String.class.equals(facesResolver.getCommonPropertyType(elContext, null))) {
//				fail(testBean,
//					"ELResolver.getCommonPropertyType() should return String.class if base is null and null otherwise. ");
//			}
			//J+

			if (!testBean.isTestComplete()) {

				// Things completed successfully
				testBean.setTestComplete(true);
				testBean.setTestResult(true,
					"JSF EL impicit objects correctly resolved in both action and header/render phases.");

				return Constants.TEST_SUCCESS;
			}
			else {
				return Constants.TEST_FAILED;
			}

		}
	}

	/**
	 * Testing JSP EL.
	 *
	 * @param   testBean  the bean for testing
	 *
	 * @return
	 */

	@BridgeTest(test = "JSP_ELTest")
	public String JSP_ELTest(TestBean testBean) {
		testBean.setTestComplete(true);

		Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Boolean status = (Boolean) requestMap.get("com.liferay.faces.bridge.TCK.status");

		testBean.setTestResult(status.booleanValue(), (String) requestMap.get("com.liferay.faces.bridge.TCK.detail"));

		if (status.booleanValue()) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;
		}

	}

	private boolean arrayMapsEquals(Map<String, String[]> a, Map<String, String[]> b) {

		if (a == b)
			return true;

		if (a.size() != b.size())
			return false;

		for (Iterator<Map.Entry<String, String[]>> i = a.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, String[]> e = i.next();

			if (!Arrays.equals(e.getValue(), b.get(e.getKey())))
				return false;
		}

		return true;
	}

	private void ensureScopeEntries(ExternalContext externalContext) {

		// App Scope
		externalContext.getApplicationMap().put("com.liferay.faces.bridge.TCK.appScopeAttr", Boolean.TRUE);

		// Session scope (portlet scope)
		externalContext.getSessionMap().put("com.liferay.faces.bridge.TCK.portletSessionScopeAttr", Boolean.TRUE);

		// Session scope (app scope)
		((PortletSession) externalContext.getSession(true)).setAttribute(
			"com.liferay.faces.bridge.TCK.applicationSessionScopeAttr", Boolean.TRUE, PortletSession.APPLICATION_SCOPE);

		// Request scope
		externalContext.getRequestMap().put("com.liferay.faces.bridge.TCK.requestScopeAttr", Boolean.TRUE);
	}

	private void fail(TestBean testBean, String message) {

		if (testBean.isTestComplete()) {
			testBean.appendTestDetail(message);
		}
		else {
			testBean.setTestComplete(true);
			testBean.setTestResult(false, message);
		}
	}

	private void testHttpSessionScope(TestBean testBean, ELResolver resolver, FacesContext facesContext,
		String implicitObject, PortletSession portletSession) {
		Map<String, Object> objectFromFacesEL = (Map<String, Object>) resolver.getValue(facesContext.getELContext(),
				null, implicitObject);

		if ((objectFromFacesEL == null) || !facesContext.getELContext().isPropertyResolved()) {
			fail(testBean, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");

			return;
		}

		// Now compare the result
		// iterate over the Map and make sure each is in the portlet session app scope
		for (Iterator<Map.Entry<String, Object>> i = objectFromFacesEL.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, Object> e = i.next();
			Object compareTo = portletSession.getAttribute(e.getKey(), PortletSession.APPLICATION_SCOPE);

			if (compareTo == null) {
				fail(testBean,
					"resolved implicit object httpSessionScope contains an entry that isn't in the portlet's session APPLICATION_SCOPE: " +
					e.getKey());
			}
			else if (!e.getValue().equals(compareTo)) {
				fail(testBean,
					"resolved implicit object httpSessionScope contains an entry whose value differs from the one in the portlet's session APPLICATION_SCOPE" +
					e.getKey());
			}
		}

		// Now verify that the Map contained the correct number of entries
		Enumeration en = portletSession.getAttributeNames(PortletSession.APPLICATION_SCOPE);
		int count = 0;

		while (en.hasMoreElements()) {
			en.nextElement();
			count++;
		}

		if (count != objectFromFacesEL.size()) {
			fail(testBean,
				"resolved implicit object httpSessionScope doesn't contain the same number of entries as are in the portlet's session ApplicationScope.");
		}
	}

	private void testImplicitObject(TestBean testBean, ELResolver resolver, FacesContext facesContext,
		String implicitObject, Object compareTo) {
		Object objectFromFacesEL = resolver.getValue(facesContext.getELContext(), null, implicitObject);

		if ((objectFromFacesEL == null) || !facesContext.getELContext().isPropertyResolved()) {
			fail(testBean, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");
		}
		else if ((objectFromFacesEL != compareTo) && !objectFromFacesEL.equals(compareTo)) {
			fail(testBean,
				"implicit object  " + implicitObject +
				" resolved using the Faces EL resolver but its not equal to what is expected.");
		}
	}

	private void testImplicitObjectArrayMaps(TestBean testBean, ELResolver resolver, FacesContext facesContext,
		String implicitObject, Map<String, String[]> compareTo) {
		Map<String, String[]> objectFromFacesEL = (Map<String, String[]>) resolver.getValue(facesContext.getELContext(),
				null, implicitObject);

		if ((objectFromFacesEL == null) || !facesContext.getELContext().isPropertyResolved()) {
			fail(testBean, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");
		}
		else if (!arrayMapsEquals(objectFromFacesEL, compareTo)) {
			fail(testBean,
				"implicit object  " + implicitObject +
				" resolved using the Faces EL resolver but its not equal to what is expected.");
		}
	}

	private void testMutablePortletPreferencesValues(TestBean testBean, ELResolver resolver, FacesContext facesContext,
		String implicitObject, Map<String, String[]> prefMap) {
		Map<String, Preference> objectFromFacesEL = (Map<String, Preference>) resolver.getValue(
				facesContext.getELContext(), null, implicitObject);

		if ((objectFromFacesEL == null) || !facesContext.getELContext().isPropertyResolved()) {
			fail(testBean, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");

			return;
		}

		if (objectFromFacesEL.size() != prefMap.size()) {
			fail(testBean,
				"resolved implicit object mutablePortletPreferencesValues doesn't contain the same number of entries as are in the portlet's preference Map.");
		}

		// Now test that the Map contains the same entries as the immutable preference Map
		for (Iterator<Map.Entry<String, Preference>> i = objectFromFacesEL.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, Preference> e = i.next();
			List<String> portletPrefValues = Arrays.asList(prefMap.get(e.getKey()));

			if (portletPrefValues == null) {
				fail(testBean,
					"resolved implicit object mutablePortletPreferencesValues contains an entry that isn't in the portlet's preferences map: " +
					e.getKey());
			}
			else if (!e.getValue().getValues().equals(portletPrefValues)) {
				fail(testBean,
					"resolved implicit object mutablePortletPreferencesValues contains an entry whose value differs from the one in the portlet's preference" +
					e.getKey());
			}
		}

	}

	private static final class RenderRequestELImpl extends RenderRequestWrapper {

		public RenderRequestELImpl(RenderRequest wrappedRenderRequest) {
			super(wrappedRenderRequest);
		}

		@Override
		public Object getAttribute(String name) {

			if ("javax.servlet.include.path_info".equals(name) || "javax.servlet.include.servlet_path".equals(name)) {
				return "";
			}
			else {
				return super.getAttribute(name);
			}
		}
	}
}
