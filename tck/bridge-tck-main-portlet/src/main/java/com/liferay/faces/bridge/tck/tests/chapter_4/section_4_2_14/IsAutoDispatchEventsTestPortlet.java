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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_14;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * Checks that GenericFacesPortlet#isAutoDispatchEvents method works as stated in section 4.2.14. - The
 * GenericFacesPortlet implements isAutoDispatchEvents by returning a boolean corresponding to the configured parameter
 * or true otherwise.
 */
public class IsAutoDispatchEventsTestPortlet extends GenericFacesTestSuitePortlet {

	private static String TEST_FAIL_PREFIX = "test.fail.";
	private static String TEST_PASS_PREFIX = "test.pass.";

	public boolean isAutoDispatchEvents() {
		boolean isAutoDispatchEvents = super.isAutoDispatchEvents();

		try {

			if (getTestName().equals("isAutoDispatchEventsSetTest")) {

				if (isAutoDispatchEvents) {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"isAutoDispatchEvents returned true but is configured as false.");
				}
				else {
					getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
						"isAutoDispatchEvents correctly returned the configured value 'false'.");
				}

			}
			else if (getTestName().equals("isAutoDispatchEventsNotSetTest")) {

				if (isAutoDispatchEvents) {
					getPortletContext().setAttribute(TEST_PASS_PREFIX + getPortletName(),
						"isAutoDispatchEvents correctly returned true as there isn't a configured value.");
				}
				else {
					getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
						"isAutoDispatchEvents incorrectly returned false though no value was configured and hence should have returned the default true.");
				}
			}
			else {
				getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
					"Unexpected Test invocation: " + getTestName());
			}
		}
		catch (Exception e) {
			getPortletContext().setAttribute(TEST_FAIL_PREFIX + getPortletName(),
				"isAutoDispatchEvents unexpected Exception: " + e.toString());
		}

		return isAutoDispatchEvents;
	}

	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		// Move messages to the request to renderer can get at them

		String attr = (String) getPortletContext().getAttribute(TEST_FAIL_PREFIX + getPortletName());

		if (attr != null) {
			request.setAttribute(TEST_FAIL_PREFIX, attr);
		}

		attr = (String) getPortletContext().getAttribute(TEST_PASS_PREFIX + getPortletName());

		if (attr != null) {
			request.setAttribute(TEST_PASS_PREFIX, attr);
		}

		super.render(request, response);
	}
}
