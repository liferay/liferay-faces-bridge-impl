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
package com.liferay.faces.bridge.tck.tests.chapter_6.section_6_1_3_2;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Neil Griffin
 */
public class InitParameterTests {

	/**
	 * getInitParameterMap Tests
	 */
	// Test #6.72
	@BridgeTest(test = "getInitParameterMapTest")
	public String getInitParameterMapTest(TestBean testBean) {

		testBean.setTestComplete(true);

		// Verify that the init parameters are the union of init-params from portlet.xml and context-params from
		// web.xml.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletContext portletContext = (PortletContext) externalContext.getContext();
		Map<String, String> externalContextInitParamMap = externalContext.getInitParameterMap();
		ELContext elContext = facesContext.getELContext();
		ELResolver elResolver = elContext.getELResolver();
		PortletConfig portletConfig = (PortletConfig) elResolver.getValue(elContext, null, "portletConfig");
		Enumeration<String> portletConfigInitParameterNames = portletConfig.getInitParameterNames();
		List<String> initParamList = Collections.list(portletConfigInitParameterNames);
		Enumeration<String> portletContextInitParameterNames = portletContext.getInitParameterNames();

		while (portletContextInitParameterNames.hasMoreElements()) {
			String portletContextInitParameterName = portletContextInitParameterNames.nextElement();

			if (!initParamList.contains(portletContextInitParameterName)) {
				initParamList.add(portletContextInitParameterName);
			}
		}

		Enumeration<String> combinedInitParamNames = Collections.enumeration(initParamList);

		if (!containsIdenticalInitParamEntries(externalContextInitParamMap, combinedInitParamNames, portletConfig)) {
			testBean.setTestResult(false,
				"Failed: Portlet context initParams and the externalContext initParameterMap entries aren't identical.");

			return Constants.TEST_FAILED;
		}

		// Verify that the init parameter map is immutable.
		try {
			externalContextInitParamMap.put("Test0Key", "Test0Value");
			testBean.setTestResult(false,
				"Failed: ExternalContext's initParameterMap isn't immutable -- a put() suceeded.");

			return Constants.TEST_FAILED;
		}
		catch (Exception e) {
			// Expected condition
		}

		// Verify that the param tckParam1 has the same value found in web.xml.
		String tckParam1Value = externalContextInitParamMap.get("tckParam1");

		if (!"web-xml-tck-param1-value".equals(tckParam1Value)) {
			testBean.setTestResult(false,
				"Failed: ExternalContext's initParameterMap contained an incorrect value for tckParam1=[" +
				tckParam1Value + "].");

			return Constants.TEST_FAILED;
		}

		// Verify that the param tckParam2 has the same value as the overridden one in portlet.xml.
		String tckParam2Value = externalContextInitParamMap.get("tckParam2");

		if (!"portlet-xml-tck-param2-value".equals(tckParam2Value)) {
			testBean.setTestResult(false,
				"Failed: ExternalContext's initParameterMap contained an incorrect value for tckParam2=[" +
				tckParam2Value + "].");

			return Constants.TEST_FAILED;
		}

		testBean.setTestResult(true, "The Map returned from initParameterMap is immutable.");
		testBean.appendTestDetail("The initParameterMap Map correctly expresses attributes in the underlying context.");
		testBean.appendTestDetail("The value of tckParam1 is the same as the context-param value in web.xml.");
		testBean.appendTestDetail(
			"The value of tckParam2 is the same as the overridden init-param value in portlet.xml.");

		return Constants.TEST_SUCCESS;
	}

	// Test #6.71
	@BridgeTest(test = "getInitParameterTest")
	public String getInitParameterTest(TestBean testBean) {

		testBean.setTestComplete(true);

		// Verify that each init-param value from portlet.xml is the same as the value from ExternalContext.
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletContext portletContext = (PortletContext) externalContext.getContext();
		ELContext elContext = facesContext.getELContext();
		ELResolver elResolver = elContext.getELResolver();
		PortletConfig portletConfig = (PortletConfig) elResolver.getValue(elContext, null, "portletConfig");
		Enumeration<String> portletConfigInitParameterNames = portletConfig.getInitParameterNames();

		while (portletConfigInitParameterNames.hasMoreElements()) {
			String portletConfigInitParameterName = portletConfigInitParameterNames.nextElement();
			String portletConfigValue = portletConfig.getInitParameter(portletConfigInitParameterName);
			String externalContextValue = externalContext.getInitParameter(portletConfigInitParameterName);

			if (portletConfigValue != null) {

				if (!portletConfigValue.equals(externalContextValue)) {

					testBean.setTestResult(false,
						"externalContext.getInitParameter(\"" + portletConfigInitParameterName + "\")=[" +
						externalContextValue + "] but it should be portlet.xml init-param value=[" +
						portletConfigValue + "].");

					return Constants.TEST_FAILED;
				}
			}
		}

		// Verify that each context-param value from web.xml is the same as the value from ExternalContext except for
		// "tckParam2" which is expected to be different.
		Enumeration<String> portletContextInitParameterNames = portletContext.getInitParameterNames();

		while (portletContextInitParameterNames.hasMoreElements()) {
			String portletContextInitParameterName = portletContextInitParameterNames.nextElement();
			String portletContextValue = portletContext.getInitParameter(portletContextInitParameterName);
			String externalContextValue = externalContext.getInitParameter(portletContextInitParameterName);

			if (portletContextValue != null) {

				if (!portletContextValue.equals(externalContextValue) &&
						!"tckParam2".equals(portletContextInitParameterName)) {

					testBean.setTestResult(false,
						"externalContext.getInitParameter(\"" + portletContextInitParameterName + "\")=[" +
						externalContextValue + "] but it should be web.xml context-param value=[" +
						portletContextValue + "].");

					return Constants.TEST_FAILED;
				}
			}
		}

		// Verify that the init parameter value for "tckParam1" from ExternalContext is equal to the context-param
		// value from web.xml.
		String tckParam1Value = externalContext.getInitParameter("tckParam1");
		String nonOverriddenValue = portletContext.getInitParameter("tckParam1");

		if ((tckParam1Value != null) && !tckParam1Value.equals(nonOverriddenValue)) {

			testBean.setTestResult(false,
				"externalContext.getInitParameter(\"tckParam1\")=[" + tckParam1Value +
				"] but it should be the web.xml context-param value=[" + nonOverriddenValue + "]");

			return Constants.TEST_FAILED;
		}

		// Verify that the init parameter value for "tckParam2" from ExternalContext is equal to the init-param value
		// from portlet.xml (which verifies that the init-param value from portlet.xml overrides the context-param value
		// from web.xml).
		String tckParam2Value = externalContext.getInitParameter("tckParam2");
		String overriddenParam2Value = portletConfig.getInitParameter("tckParam2");

		if ((tckParam2Value != null) && tckParam2Value.equals(overriddenParam2Value)) {
			testBean.setTestResult(true,
				"externalContext.getInitParameter(\"tckParam2\") correctly returned the overridden portlet.xml init-param value=[" +
				overriddenParam2Value + "].");

			testBean.appendTestDetail(
				"externalContext.getInitParameter(\"tckParam1\") correctly returned the non-overridden web.xml context-param value=[" +
				nonOverriddenValue + "].");
			testBean.appendTestDetail(
				"All init-param values from portlet.xml are the same as those values from ExternalContext.");
			testBean.appendTestDetail(
				"All context-param values from web.xml are the same as those values from ExternalContext except for tckParam2 " +
				"which is expected to be different.");

			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;
		}
	}

	private boolean containsIdenticalInitParamEntries(Map<String, String> m, Enumeration<String> eNames,
		PortletConfig portletConfig) {

		// For each entry in m ensure there is an identical one in the context
		PortletContext portletContext = portletConfig.getPortletContext();

		for (Iterator<Map.Entry<String, String>> entries = m.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<String, String> e = entries.next();
			String value = portletConfig.getInitParameter(e.getKey());

			if (value == null) {
				value = portletContext.getInitParameter(e.getKey());
			}

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
			Object value = portletConfig.getInitParameter(key);

			if (value == null) {
				value = portletContext.getInitParameter(key);
			}

			Object mapObj = m.get(key);

			if ((mapObj == null) && (value == null))
				continue; // technically shouldn't have this but some container do

			if ((mapObj == null) || (value == null) || !mapObj.equals(value)) {
				return false;
			}
		}

		return true;
	}
}
