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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import java.util.HashSet;
import java.util.Set;

import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.filter.HeaderResponseWrapper;

import org.w3c.dom.Element;


/**
 * @author  Kyle Stiemann
 */
public class HeaderResponseResourcesRenderedInHeadTestImpl extends HeaderResponseWrapper {

	// Private Constants
	private static final String INLINE_SCRIPT_JS = "inlineScript_js";

	// Package-Private Constants
	/* package-private */ static final String[] TEST_HEAD_ELEMENT_IDS = new String[] {
			"jsf.js", "resourcesRenderedInHeadTest.js", "resource1.js", "resource1.css", INLINE_SCRIPT_JS
		};

	// Private Data Members
	private boolean addPropertyMarkupHeadElementCalled = false;
	private Set<String> testHeadElementsAddedViaAddDependency = new HashSet<String>();

	public HeaderResponseResourcesRenderedInHeadTestImpl(HeaderResponse headerResponse) {
		super(headerResponse);
	}

	@Override
	public void addDependency(String name, String scope, String version) {

		for (String testHeadElementId : TEST_HEAD_ELEMENT_IDS) {

			if (name.contains(testHeadElementId)) {
				testHeadElementsAddedViaAddDependency.add(testHeadElementId);
			}
		}

		super.addDependency(name, scope, version);
	}

	@Override
	public void addDependency(String name, String scope, String version, String markup) {

		for (String testHeadElementId : TEST_HEAD_ELEMENT_IDS) {

			if ((markup.contains("src=\"") || markup.contains("href=\"")) &&
					(name.contains(testHeadElementId) || markup.contains(testHeadElementId))) {
				testHeadElementsAddedViaAddDependency.add(testHeadElementId);
			}
			else if (markup.contains(INLINE_SCRIPT_JS)) {
				testHeadElementsAddedViaAddDependency.add(testHeadElementId);
			}
		}

		super.addDependency(name, scope, version, markup);
	}

	@Override
	public void addProperty(String key, Element element) {

		if (MimeResponse.MARKUP_HEAD_ELEMENT.equals(key)) {
			addPropertyMarkupHeadElementCalled = true;
		}

		super.addProperty(key, element);
	}

	@Override
	public void addProperty(String key, String value) {

		if (MimeResponse.MARKUP_HEAD_ELEMENT.equals(key)) {
			addPropertyMarkupHeadElementCalled = true;
		}

		super.addProperty(key, value);
	}

	public Set<String> getTestHeadElementsAddedViaAddDependency() {
		return testHeadElementsAddedViaAddDependency;
	}

	public boolean isAddPropertyMarkupHeadElementCalled() {
		return addPropertyMarkupHeadElementCalled;
	}
}
