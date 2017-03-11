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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_1;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.BridgePublicRenderParameterHandler;

import com.liferay.faces.bridge.tck.annotation.BridgeTest;
import com.liferay.faces.bridge.tck.beans.TestRunnerBean;
import com.liferay.faces.bridge.tck.common.Constants;


/**
 * @author  Michael Freedman
 */
public class Tests extends Object {
	public boolean checkAttrs(Map<String, Object> attributes, String namespace, Map<String, Object> expectedInitParams,
		StringBuilder notFoundMsg) {

		if (attributes == null) {
			buildMsg(notFoundMsg, "There were no portlet context parameters");

			return false;
		}

		boolean pass = true;

		for (String key : expectedInitParams.keySet()) {
			// Run through the expected init param attributes.  They will either be key=string, value=object, or
			// key=defaultViewIdMap, value=map of modes (where key = string and value=string)

			if ("defaultViewIdMap".equals(key)) {
				Map<String, String> defaultViewIdMap = (Map<String, String>) expectedInitParams.get("defaultViewIdMap");
				Map<String, String> attributeViewIdMap = (Map<String, String>) attributes.get(namespace +
						"defaultViewIdMap");

				if (attributeViewIdMap == null) {
					buildMsg(notFoundMsg, "init-param name = " + key);
					pass = false;

					break;
				}

				for (String mode : defaultViewIdMap.keySet()) {

					if (!(defaultViewIdMap.get(mode).equals(attributeViewIdMap.get(mode)))) {
						pass = false;
					}
				}
			}
			else {
				Object param = expectedInitParams.get(key);

				try {

					Object val = attributes.get(namespace + key);

					if ((val instanceof String) && !(param.equals((String) val))) {
						buildMsg(notFoundMsg,
							"init-param name = " + key + ", expected value = " + expectedInitParams.get(key) +
							",  value = " + val);
						pass = false;
					}
					else if ((val instanceof Boolean) && !(param.equals((Boolean) val))) {
						buildMsg(notFoundMsg,
							"init-param name = " + key + ", expected value = " + expectedInitParams.get(key) +
							",  value = " + val);
						pass = false;
					}
					else if (((val instanceof BridgePublicRenderParameterHandler) ||
								(val instanceof BridgeEventHandler)) &&
							val.getClass().getSimpleName().equals((String) param)) {
						buildMsg(notFoundMsg,
							"init-param name = " + key + ", expected value = " + expectedInitParams.get(key) +
							",  value = " + val.getClass().getSimpleName());
						pass = false;
					}

				}
				catch (Exception e) {
					;
				}
			}

		}

		return pass;
	}

	@BridgeTest(test = "portletInitializationParametersTest")
	public String portletInitializationParametersTest(TestRunnerBean testRunner) {

		// Section 4.1
		// Tests whether the GenericFacesPortlet portlet initialization
		// parameters have been correctly set as Bridge attributes.
		String PARAM_NAMESPACE = "javax.portlet.faces.chapter4_1Tests-portletInitializationParametersTest-portlet.";
		Map<String, Object> expectedInitParams = new HashMap<String, Object>();
		Map<String, String> viewIds = new HashMap<String, String>();
		viewIds.put("view", "/tests/singleRequestTest.xhtml");
		viewIds.put("edit", "/tests/singleRequestTest.xhtml");
		viewIds.put("help", "/tests/singleRequestTest.xhtml");
		expectedInitParams.put("defaultViewIdMap", viewIds);

		expectedInitParams.put("excludedRequestAttributes", "exclude1,exclude2");
		expectedInitParams.put("preserveActionParams", Boolean.TRUE);
		expectedInitParams.put("bridgeEventHandler",
			"com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2.TestEventHandler");
		expectedInitParams.put("bridgePublicRenderParameterHandler",
			"com.liferay.faces.bridge.tck.tests.chapter_5.section_5_3.Tests");
		expectedInitParams.put("defaultRenderKitId", "HTML_BASIC");

		ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();

		StringBuilder notFoundMsg = new StringBuilder();

		boolean pass = checkAttrs(extCtx.getApplicationMap(), PARAM_NAMESPACE, expectedInitParams, notFoundMsg);

		if (notFoundMsg.length() != 0) {
			notFoundMsg.append(".  ");
		}

		StringBuilder msg = new StringBuilder();

		if (pass) {
			msg.append(
				"GenericFacesPortlet properly sets appropriate portlet init parameters as the spec defined corresponding portlet context attributes.");
		}
		else {
			msg.append(
				"GenericFacesPortlet didn't properly set the appropriate portlet init parameters as the spec defined corresponding portlet context attributes. Missing or incorrect portlet context attributes: " +
				notFoundMsg);
		}

		testRunner.setTestResult(pass, msg.toString());

		if (pass) {
			return Constants.TEST_SUCCESS;
		}
		else {
			return Constants.TEST_FAILED;
		}

	}

	private void buildMsg(StringBuilder completeMsg, String msg) {

		if (completeMsg.length() != 0) {
			completeMsg.append(", ");
		}

		completeMsg.append(msg);
	}
}
