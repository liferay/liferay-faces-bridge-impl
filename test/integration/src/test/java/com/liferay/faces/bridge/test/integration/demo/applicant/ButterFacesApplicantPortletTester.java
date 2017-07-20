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
package com.liferay.faces.bridge.test.integration.demo.applicant;

import org.junit.Assume;
import org.junit.Before;


/**
 * @author  Kyle Stiemann
 */
public class ButterFacesApplicantPortletTester extends BridgeApplicantPortletTester {

	@Before
	public void skipButterFaces() {

		// Skip ButterFaces until certain issues are fixed and the error message tests are overridden to detect
		// ButterFaces tooltip error messages.
		Assume.assumeTrue(false);
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath +
			"/../../following-sibling::div[@role='tooltip']//div[contains(@class,'butter-component-tooltip')][contains(@class,'error')]/ul/li";
	}

	@Override
	protected String getPortletPageName() {
		return "butterfaces-applicant";
	}
}
