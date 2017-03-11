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
package com.liferay.faces.bridge.test.integration.issue;

import org.junit.Assume;
import org.junit.Test;

import com.liferay.faces.test.selenium.IntegrationTesterBase;


/**
 * @author  Kyle Stiemann
 */
public class FACES_1470PortletTester extends IntegrationTesterBase {

	@Test
	public void runFACES_1470PortletTest() {

		Assume.assumeTrue("The FACES-1470 test must be run manually.", false);

		//J-
		// TODO
		// Add a portlet to the FACES-1470 portlet war which shows all AS7Leak instances (if possible).
		// Add this portlet to a page called FACES-1470-AS7Leak-tracker.
		//
		// Write a test that accomplishes the following:
		// 1. Sign in.
		// 2. Navigate to the FACES-1470-tracker portlet and assert that no AS7Leak instances exist.
		// 3. Navigate to the FACES-1470 portlet.
		// 4. Navigate to view2.xhtml via Ajax and non-Ajax multiple times.
		// 5. Navigate to the FACES-1470-tracker portlet and assert that several AS7Leak instances exist.
		// 6. Sign out.
		// 7. Navigate to the FACES-1470-tracker portlet and assert that no AS7Leak instances exist.
		//
		// Old test: https://github.com/liferay/liferay-faces-bridge-impl/blob/4.0.0/test/integration/issues/bridge/FACES-1470-portlet/src/test/java/com/liferay/faces/test/FACES1470PortletTest.java
		//
		// Perhaps use this: https://github.com/ronmamo/reflections
		//J+
	}
}
