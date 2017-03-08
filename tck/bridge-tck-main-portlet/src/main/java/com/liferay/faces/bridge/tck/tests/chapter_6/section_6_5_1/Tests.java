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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELResolver;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.preference.Preference;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  jhaley
 */
public class Tests {

	/**
	 * Testing JSF EL - implicits are in alpha order.
	 *
	 * @param   testRunner
	 *
	 * @return
	 */
	@BridgeTest(test = "JSF_ELTest")
	public String JSF_ELTest(TestRunnerBean testRunner) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext extCtx = ctx.getExternalContext();
		ELResolver facesResolver = ctx.getELContext().getELResolver();

		// Test that each implicit object is accessible and has the right value in
		// both the action phase and render phase
		if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.ACTION_PHASE) {

			// ActionRequest
			try {

				// First ensure there are entries in the various scopes
				ensureScopeEntries(extCtx);

				// JSF Implicit Objects:
				// application -> ExternalContext.getContext();
				testImplicitObject(testRunner, facesResolver, ctx, "application", extCtx.getContext());

				// applicationScope -> externalContext.getApplicationMap()
				testImplicitObject(testRunner, facesResolver, ctx, "applicationScope", extCtx.getApplicationMap());

				// cookie -> externalContext.getRequestCookieMap()
				testImplicitObject(testRunner, facesResolver, ctx, "cookie", extCtx.getRequestCookieMap());

				// facesContext -> the FacesContext for this request
				testImplicitObject(testRunner, facesResolver, ctx, "facesContext", ctx);

				// header -> externalContext.getRequestHeaderMap()
				testImplicitObject(testRunner, facesResolver, ctx, "header", extCtx.getRequestHeaderMap());

				// headerValues -> externalContext.getRequestHeaderValuesMap()
				testImplicitObject(testRunner, facesResolver, ctx, "headerValues", extCtx.getRequestHeaderValuesMap());

				// initParam -> externalContext.getInitParameterMap()
				testImplicitObject(testRunner, facesResolver, ctx, "initParam", extCtx.getInitParameterMap());

				// param -> externalContext.getRequestParameterMap()
				testImplicitObject(testRunner, facesResolver, ctx, "param", extCtx.getRequestParameterMap());

				// paramValues -> externalContext.getRequestParameterValuesMap()
				testImplicitObject(testRunner, facesResolver, ctx, "paramValues",
					extCtx.getRequestParameterValuesMap());

				// request -> externalContext.getRequest()
				testImplicitObject(testRunner, facesResolver, ctx, "request", extCtx.getRequest());

				// requestScope -> externalContext.getRequestScope()
				testImplicitObject(testRunner, facesResolver, ctx, "requestScope", extCtx.getRequestMap());

				// session -> externalContext.getSession()
				testImplicitObject(testRunner, facesResolver, ctx, "session", extCtx.getSession(true));

				// sessionScope -> externalContext.getSessionMap()
				testImplicitObject(testRunner, facesResolver, ctx, "sessionScope", extCtx.getSessionMap());

				// view -> facesContext.getViewRoot()
				testImplicitObject(testRunner, facesResolver, ctx, "view", ctx.getViewRoot());

				// Portlet implicit objects
				// httpSessionScope:  mutable Map containing PortletSession attribute/values at APPLICATION_SCOPE.
				testHttpSessionScope(testRunner, facesResolver, ctx, "httpSessionScope",
					(PortletSession) extCtx.getSession(true));

				// mutablePortletPreferencesValues: mutable Map of type Map<String,
				// javax.portlet.faces.preference.Preference>.
				testMutablePortletPreferencesValues(testRunner, facesResolver, ctx, "mutablePortletPreferencesValues",
					((PortletRequest) extCtx.getRequest()).getPreferences().getMap());

				// portletConfig -> object of type javax.portlet.PortletConfig
				testImplicitObject(testRunner, facesResolver, ctx, "portletConfig",
					extCtx.getRequestMap().get(Constants.PORTLET_CONFIG));

				// portletPreferences -> ExternalContext.getRequest().getPreferences() object.
				testImplicitObject(testRunner, facesResolver, ctx, "portletPreferences",
					((PortletRequest) extCtx.getRequest()).getPreferences());

				// portletPreferencesValues -> ExternalContext.getRequest()).getPreferences().getMap().
				testImplicitObjectArrayMaps(testRunner, facesResolver, ctx, "portletPreferencesValues",
					((PortletRequest) extCtx.getRequest()).getPreferences().getMap());

				// portletSession -> ExternalContext.getSession()
				testImplicitObject(testRunner, facesResolver, ctx, "portletSession", extCtx.getSession(true));

				// portletSessionScope -> ExternalContext.getSessionMap().
				testImplicitObject(testRunner, facesResolver, ctx, "portletSessionScope", extCtx.getSessionMap());
			}
			catch (Throwable t) {
				testRunner.setTestResult(false, "JSF EL failure in action request: " + t.getCause().toString());
			}

			return "JSF_ELTest";
		}

		// HeaderRequest / RenderRequest
		else {

			try {

				// JSF Implicit Objects
				// application -> ExternalContext.getContext();
				testImplicitObject(testRunner, facesResolver, ctx, "application", extCtx.getContext());

				// applicationScope -> externalContext.getApplicationMap()
				testImplicitObject(testRunner, facesResolver, ctx, "applicationScope", extCtx.getApplicationMap());

				// cookie -> externalContext.getRequestCookieMap()
				testImplicitObject(testRunner, facesResolver, ctx, "cookie", extCtx.getRequestCookieMap());

				// facesContext -> the FacesContext for this request
				testImplicitObject(testRunner, facesResolver, ctx, "facesContext", ctx);

				// header -> externalContext.getRequestHeaderMap()
				testImplicitObject(testRunner, facesResolver, ctx, "header", extCtx.getRequestHeaderMap());

				// headerValues -> externalContext.getRequestHeaderValuesMap()
				testImplicitObject(testRunner, facesResolver, ctx, "headerValues", extCtx.getRequestHeaderValuesMap());

				// initParam -> externalContext.getInitParameterMap()
				testImplicitObject(testRunner, facesResolver, ctx, "initParam", extCtx.getInitParameterMap());

				// param -> externalContext.getRequestParameterMap()
				testImplicitObject(testRunner, facesResolver, ctx, "param", extCtx.getRequestParameterMap());

				// paramValues -> externalContext.getRequestParameterValuesMap()
				testImplicitObject(testRunner, facesResolver, ctx, "paramValues",
					extCtx.getRequestParameterValuesMap());

				// request -> externalContext.getRequest()
				testImplicitObject(testRunner, facesResolver, ctx, "request", extCtx.getRequest());

				// requestScope -> externalContext.getRequestScope()
				testImplicitObject(testRunner, facesResolver, ctx, "requestScope", extCtx.getRequestMap());

				// session -> externalContext.getSession()
				testImplicitObject(testRunner, facesResolver, ctx, "session", extCtx.getSession(true));

				// sessionScope -> externalContext.getSessionMap()
				testImplicitObject(testRunner, facesResolver, ctx, "sessionScope", extCtx.getSessionMap());

				// view -> facesContext.getViewRoot()
				testImplicitObject(testRunner, facesResolver, ctx, "view", ctx.getViewRoot());

				// Portlet implicit objects
				// httpSessionScope:  mutable Map containing PortletSession attribute/values at APPLICATION_SCOPE.
				testHttpSessionScope(testRunner, facesResolver, ctx, "httpSessionScope",
					(PortletSession) extCtx.getSession(true));

				// mutablePortletPreferencesValues: mutable Map of type Map<String,
				// javax.portlet.faces.preference.Preference>.
				testMutablePortletPreferencesValues(testRunner, facesResolver, ctx, "mutablePortletPreferencesValues",
					((PortletRequest) extCtx.getRequest()).getPreferences().getMap());

				// portletConfig -> object of type javax.portlet.PortletConfig
				testImplicitObject(testRunner, facesResolver, ctx, "portletConfig",
					extCtx.getRequestMap().get(Constants.PORTLET_CONFIG));

				// portletPreferences -> ExternalContext.getRequest().getPreferences() object.
				testImplicitObject(testRunner, facesResolver, ctx, "portletPreferences",
					((PortletRequest) extCtx.getRequest()).getPreferences());

				// portletPreferencesValues -> ExternalContext.getRequest()).getPreferences().getMap().
				testImplicitObjectArrayMaps(testRunner, facesResolver, ctx, "portletPreferencesValues",
					((PortletRequest) extCtx.getRequest()).getPreferences().getMap());

				// portletSession -> ExternalContext.getSession()
				testImplicitObject(testRunner, facesResolver, ctx, "portletSession", extCtx.getSession(true));

				// portletSessionScope -> ExternalContext.getSessionMap().
				testImplicitObject(testRunner, facesResolver, ctx, "portletSessionScope", extCtx.getSessionMap());

				// RenderRequest
				if (BridgeUtil.getPortletRequestPhase(ctx) == Bridge.PortletPhase.RENDER_PHASE) {

					// renderRequest -> object of type javax.portlet.RenderRequest
					testImplicitObject(testRunner, facesResolver, ctx, "renderRequest", extCtx.getRequest());

					// renderResponse -> object of type javax.portlet.RenderResponse
					testImplicitObject(testRunner, facesResolver, ctx, "renderResponse", extCtx.getResponse());
				}

				// HeaderRequest
				else {

					// headerRequest -> object of type javax.portlet.HeaderRequest
					testImplicitObject(testRunner, facesResolver, ctx, "headerRequest", extCtx.getRequest());

					// headerResponse -> object of type javax.portlet.HeaderResponse
					testImplicitObject(testRunner, facesResolver, ctx, "headerResponse", extCtx.getResponse());
				}
			}
			catch (Throwable t) {
				fail(testRunner, "JSF EL failure in render request: " + t.getCause().toString());
			}

			if (!testRunner.isTestComplete()) {

				// Things completed successfully
				testRunner.setTestComplete(true);
				testRunner.setTestResult(true,
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
	 * @param   testRunner
	 *
	 * @return
	 */

	@BridgeTest(test = "JSP_ELTest")
	public String JSP_ELTest(TestRunnerBean testRunner) {
		testRunner.setTestComplete(true);

		Map<String, Object> m = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Boolean status = (Boolean) m.get("com.liferay.faces.bridge.TCK.status");

		testRunner.setTestResult(status.booleanValue(), (String) m.get("com.liferay.faces.bridge.TCK.detail"));

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

	private void ensureScopeEntries(ExternalContext extCtx) {

		// App Scope
		extCtx.getApplicationMap().put("com.liferay.faces.bridge.TCK.appScopeAttr", Boolean.TRUE);

		// Session scope (portlet scope)
		extCtx.getSessionMap().put("com.liferay.faces.bridge.TCK.portletSessionScopeAttr", Boolean.TRUE);

		// Session scope (app scope)
		((PortletSession) extCtx.getSession(true)).setAttribute(
			"com.liferay.faces.bridge.TCK.applicationSessionScopeAttr", Boolean.TRUE, PortletSession.APPLICATION_SCOPE);

		// Request scope
		extCtx.getRequestMap().put("com.liferay.faces.bridge.TCK.requestScopeAttr", Boolean.TRUE);
	}

	private void fail(TestRunnerBean testRunner, String message) {

		if (testRunner.isTestComplete()) {
			testRunner.appendTestDetail(message);
		}
		else {
			testRunner.setTestComplete(true);
			testRunner.setTestResult(false, message);
		}
	}

	private void testHttpSessionScope(TestRunnerBean testRunner, ELResolver resolver, FacesContext ctx,
		String implicitObject, PortletSession session) {
		Map<String, Object> objectFromFacesEL = (Map<String, Object>) resolver.getValue(ctx.getELContext(), null,
				implicitObject);

		if ((objectFromFacesEL == null) || !ctx.getELContext().isPropertyResolved()) {
			fail(testRunner, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");

			return;
		}

		// Now compare the result
		// iterate over the Map and make sure each is in the portlet session app scope
		for (Iterator<Map.Entry<String, Object>> i = objectFromFacesEL.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, Object> e = i.next();
			Object compareTo = session.getAttribute(e.getKey(), PortletSession.APPLICATION_SCOPE);

			if (compareTo == null) {
				fail(testRunner,
					"resolved implicit object httpSessionScope contains an entry that isn't in the portlet's session APPLICATION_SCOPE: " +
					e.getKey());
			}
			else if (!e.getValue().equals(compareTo)) {
				fail(testRunner,
					"resolved implicit object httpSessionScope contains an entry whose value differs from the one in the portlet's session APPLICATION_SCOPE" +
					e.getKey());
			}
		}

		// Now verify that the Map contained the correct number of entries
		Enumeration en = session.getAttributeNames(PortletSession.APPLICATION_SCOPE);
		int count = 0;

		while (en.hasMoreElements()) {
			en.nextElement();
			count++;
		}

		if (count != objectFromFacesEL.size()) {
			fail(testRunner,
				"resolved implicit object httpSessionScope doesn't contain the same number of entries as are in the portlet's session ApplicationScope.");
		}
	}

	private void testImplicitObject(TestRunnerBean testRunner, ELResolver resolver, FacesContext ctx,
		String implicitObject, Object compareTo) {
		Object objectFromFacesEL = resolver.getValue(ctx.getELContext(), null, implicitObject);

		if ((objectFromFacesEL == null) || !ctx.getELContext().isPropertyResolved()) {
			fail(testRunner, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");
		}
		else if ((objectFromFacesEL != compareTo) && !objectFromFacesEL.equals(compareTo)) {
			fail(testRunner,
				"implicit object  " + implicitObject +
				" resolved using the Faces EL resolver but its not equal to what is expected.");
		}
	}

	private void testImplicitObjectArrayMaps(TestRunnerBean testRunner, ELResolver resolver, FacesContext ctx,
		String implicitObject, Map<String, String[]> compareTo) {
		Map<String, String[]> objectFromFacesEL = (Map<String, String[]>) resolver.getValue(ctx.getELContext(), null,
				implicitObject);

		if ((objectFromFacesEL == null) || !ctx.getELContext().isPropertyResolved()) {
			fail(testRunner, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");
		}
		else if (!arrayMapsEquals(objectFromFacesEL, compareTo)) {
			fail(testRunner,
				"implicit object  " + implicitObject +
				" resolved using the Faces EL resolver but its not equal to what is expected.");
		}
	}

	private void testMutablePortletPreferencesValues(TestRunnerBean testRunner, ELResolver resolver, FacesContext ctx,
		String implicitObject, Map<String, String[]> prefMap) {
		Map<String, Preference> objectFromFacesEL = (Map<String, Preference>) resolver.getValue(ctx.getELContext(),
				null, implicitObject);

		if ((objectFromFacesEL == null) || !ctx.getELContext().isPropertyResolved()) {
			fail(testRunner, "implicit object " + implicitObject + " didn't resolve using the Faces EL resolver.");

			return;
		}

		if (objectFromFacesEL.size() != prefMap.size()) {
			fail(testRunner,
				"resolved implicit object mutablePortletPreferencesValues doesn't contain the same number of entries as are in the portlet's preference Map.");
		}

		// Now test that the Map contains the same entries as the immutable preference Map
		for (Iterator<Map.Entry<String, Preference>> i = objectFromFacesEL.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, Preference> e = i.next();
			List<String> portletPrefValues = Arrays.asList(prefMap.get(e.getKey()));

			if (portletPrefValues == null) {
				fail(testRunner,
					"resolved implicit object mutablePortletPreferencesValues contains an entry that isn't in the portlet's preferences map: " +
					e.getKey());
			}
			else if (!e.getValue().getValues().equals(portletPrefValues)) {
				fail(testRunner,
					"resolved implicit object mutablePortletPreferencesValues contains an entry whose value differs from the one in the portlet's preference" +
					e.getKey());
			}
		}

	}

}
