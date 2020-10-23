/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.liferay.harness;

import java.util.Locale;

import org.junit.Assume;

import org.junit.runner.RunWith;

import com.liferay.faces.bridge.tck.harness.TckParameterized;
import com.liferay.faces.bridge.tck.harness.TckTestCase;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.TestUtil;


/**
 * @author  Michael Freedman
 * @author  Kyle Stiemann
 */
@RunWith(TckParameterized.class)
public class LiferayTckTestCase extends TckTestCase {

	// Private Constants
	private static final String TCK_CONTEXT = "/group/bridge-tck/";
	private static final String DEFAULT_LIFERAY_WINDOW_STATE = TestUtil.getSystemPropertyOrDefault(
			"integration.default.liferay.window.state", "exclusive");

	public LiferayTckTestCase(String pageName, String testName, String testPortletName, String testModule) {
		super(pageName, testName, testPortletName, testModule);
	}

	//J-
	private static final String USE_DEFAULT_LIFERAY_WINDOW_STATE_SCRIPT =
		"var forms = document.getElementsByTagName('form');" +
		"for (var i = 0; i < forms.length; i++) {" +
		"forms[i]['action'] =" +
		"forms[i]['action'].replace('p_p_state=normal', 'p_p_state=" + DEFAULT_LIFERAY_WINDOW_STATE + "');" +
		"}" +
		"var links = document.getElementsByTagName('a');" +
		"for (var i = 0; i < links.length; i++) {" +
		"links[i]['href'] =" +
		"links[i]['href'].replace('p_p_state=normal', 'p_p_state=" + DEFAULT_LIFERAY_WINDOW_STATE + "');" +
		"}";
	//J+

	@Override
	public void runBeforeEachTest() throws Exception {

		Assume.assumeTrue(TestUtil.getContainer("liferay").contains("liferay"));

		BrowserDriver browserDriver = getBrowserDriver();
		String query = "";

		if (useSpecialLiferayWindowState(DEFAULT_LIFERAY_WINDOW_STATE)) {
			query = "?p_p_state=" + DEFAULT_LIFERAY_WINDOW_STATE + "&p_p_id=" + getTestPortletName().replace("-", "") +
				"_WAR_" + getTestModule().replaceAll("/", "").replaceAll("[.]", "");
		}

		browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + TCK_CONTEXT + getPageName() + query);

		if (useSpecialLiferayWindowState(DEFAULT_LIFERAY_WINDOW_STATE)) {
			browserDriver.executeScriptInCurrentWindow(USE_DEFAULT_LIFERAY_WINDOW_STATE_SCRIPT);
		}
	}

	@Override
	protected void runAfterEachFullPageTestAction() {

		if (useSpecialLiferayWindowState(DEFAULT_LIFERAY_WINDOW_STATE)) {

			BrowserDriver browserDriver = getBrowserDriver();
			browserDriver.executeScriptInCurrentWindow(USE_DEFAULT_LIFERAY_WINDOW_STATE_SCRIPT);
		}
	}

	private boolean useSpecialLiferayWindowState(String defaultLiferayWindowState) {

		defaultLiferayWindowState = defaultLiferayWindowState.toLowerCase(Locale.ENGLISH);

		boolean useSpecialLiferayWindowState = !((defaultLiferayWindowState == null) ||
				defaultLiferayWindowState.equals("") || defaultLiferayWindowState.equals("normal"));

		if (useSpecialLiferayWindowState) {

			String pageName = getPageName();
			String testName = getTestName();
			useSpecialLiferayWindowState = !pageName.startsWith("jsr_329_testpage") &&
				!(testName.equals("resourcesRenderedInHeadTest") || testName.equals("lifecycleTest") ||
					testName.equals("encodeActionURLJSFViewResourceTest") ||
					testName.equals("encodeActionURLNonJSFViewWithInvalidWindowStateRenderTest") ||
					testName.equals("encodeActionURLNonJSFViewWithInvalidWindowStateResourceTest") ||
					testName.equals("encodeActionURLWithInvalidWindowStateActionTest") ||
					testName.equals("encodeActionURLWithInvalidWindowStateEventTest") ||
					testName.equals("encodeActionURLWithInvalidWindowStateRenderTest") ||
					testName.equals("encodePartialActionURLTest") ||
					testName.equals("encodeResourceURLWithWindowStateTest"));
		}

		return useSpecialLiferayWindowState;
	}
}
