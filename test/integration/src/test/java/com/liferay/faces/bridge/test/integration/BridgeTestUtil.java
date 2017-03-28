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
package com.liferay.faces.bridge.test.integration;

import com.liferay.faces.test.selenium.TestUtil;


/**
 * @author  Kyle Stiemann
 */
public final class BridgeTestUtil {

	// Private Constants
	private static final String DEFAULT_DEMO_CONTEXT;
	private static final String DEFAULT_ISSUE_CONTEXT;
	private static final int DEFAULT_PLUTO_MAJOR_VERSION = 3;

	static {

		String defaultDemoContext = "/group/bridge-demos";
		String defaultIssueContext = "/web/bridge-issues";

		if (isContainerPluto(TestUtil.getContainer())) {
			defaultDemoContext = TestUtil.DEFAULT_PLUTO_CONTEXT;
			defaultIssueContext = TestUtil.DEFAULT_PLUTO_CONTEXT;
		}

		DEFAULT_DEMO_CONTEXT = defaultDemoContext;
		DEFAULT_ISSUE_CONTEXT = defaultIssueContext;
	}

	private BridgeTestUtil() {
		throw new AssertionError();
	}

	public static String getDemoContext(String portletPageName) {
		return TestUtil.getSystemPropertyOrDefault("integration.demo.context", DEFAULT_DEMO_CONTEXT) + "/" +
			portletPageName;
	}

	public static String getDemoPageURL(String portletPageName) {
		return TestUtil.DEFAULT_BASE_URL + getDemoContext(portletPageName);
	}

	public static String getIssueContext(String portletPageName) {
		return TestUtil.getSystemPropertyOrDefault("integration.issue.context", DEFAULT_ISSUE_CONTEXT) + "/" +
			portletPageName;
	}

	public static String getIssuePageURL(String portletPageName) {
		return TestUtil.DEFAULT_BASE_URL + getIssueContext(portletPageName);
	}

	public static boolean isContainerPluto(String container) {
		return isContainerPluto(DEFAULT_PLUTO_MAJOR_VERSION, container);
	}

	public static boolean isContainerPluto(int plutoMajorVersion, String container) {

		String plutoContainer = "pluto";

		if (plutoMajorVersion != DEFAULT_PLUTO_MAJOR_VERSION) {
			plutoContainer += plutoMajorVersion;
		}

		return container.startsWith(plutoContainer);
	}
}
