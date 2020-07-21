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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author  Kyle Stiemann
 */
public final class ResourcesRenderedInHeadTestUtil {

	// Package-Private Constants
	public static final Set<String[]> TEST_HEAD_ELEMENT_IDS;
	public static final String TEST_HEAD_ELEMENT_IDS_JS_ARRAY;

	static {

		Set<String[]> testHeadResourceIds = new HashSet<String[]>();
		testHeadResourceIds.add(new String[] { "jsf.js", "javax.faces" });
		testHeadResourceIds.add(new String[] { "resourcesRenderedInHeadTest.js", "test" });
		testHeadResourceIds.add(new String[] { "resource1.css", "test" });
		testHeadResourceIds.add(new String[] { "resource1.js" });
		testHeadResourceIds.add(new String[] { "inlineScript_js" });
		TEST_HEAD_ELEMENT_IDS = Collections.unmodifiableSet(testHeadResourceIds);

		String testHeadElementIdsJSArray = "";

		for (String[] testHeadElementId : TEST_HEAD_ELEMENT_IDS) {

			if (testHeadElementIdsJSArray.length() > 0) {
				testHeadElementIdsJSArray += ", ";
			}

			testHeadElementIdsJSArray += "'" + escapeId(testHeadElementId[0]) + "'";
		}

		TEST_HEAD_ELEMENT_IDS_JS_ARRAY = "[ " + testHeadElementIdsJSArray + " ]";
	}

	private ResourcesRenderedInHeadTestUtil() {
		throw new AssertionError();
	}

	public static String escapeId(String string) {

		// Replace illegal (JSF/HTML) id characters with underscores.
		return string.replaceAll("[^A-Za-z0-9-_]", "_");
	}
}
