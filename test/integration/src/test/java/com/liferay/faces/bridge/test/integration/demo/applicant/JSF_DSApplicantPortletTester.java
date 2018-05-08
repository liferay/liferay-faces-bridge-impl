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
package com.liferay.faces.bridge.test.integration.demo.applicant;

import org.junit.Assume;
import org.junit.Before;

import com.liferay.faces.test.selenium.browser.TestUtil;


/**
 * @author  Liferay Faces Team
 */
public class JSF_DSApplicantPortletTester extends ApplicantTesterBase {

	@Before
	public void onlyTestOnLiferay7_0OrGreater() {

		String container = TestUtil.getContainer();
		Assume.assumeFalse(container.contains("pluto") || "liferay62".equals(container));
	}

	@Override
	protected String getPortletPageName() {
		return "jsf-ds-applicant";
	}
}
