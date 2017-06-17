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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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

		// Test the following:
		// 1. Map is immutable
		// 2. Map contains attributes in the underlying portlet context
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletContext portletContext = (PortletContext) externalContext.getContext();
		Map<String, String> externalContextInitParamMap = externalContext.getInitParameterMap();
		Enumeration<String> initParameterNames = portletContext.getInitParameterNames();

		if (!containsIdenticalInitParamEntries(externalContextInitParamMap, initParameterNames, portletContext)) {
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
		testBean.appendTestDetail("The initParameterMap Map correctly expresses attributes in the underlying context.");

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
}
