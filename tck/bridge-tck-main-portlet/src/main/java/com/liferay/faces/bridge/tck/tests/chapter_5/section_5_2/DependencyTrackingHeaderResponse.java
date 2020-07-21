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

import java.util.HashSet;
import java.util.Set;

import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.filter.HeaderResponseWrapper;

import org.w3c.dom.Element;


/**
 * This class decorates a {@link HeaderResponse} in order to track which of the expected resources are added via the
 * {@link #addDependency(String, String, String, String)} or {@link #addDependency(String, String, String)} methods. It
 * also tracks whether or not the {@link #addProperty(String, Element)} or {@link #addProperty(String, String)} methods
 * were called.
 *
 * @author  Kyle Stiemann
 */
public class DependencyTrackingHeaderResponse extends HeaderResponseWrapper {

	// Private Data Members
	private boolean addPropertyMarkupHeadElementCalled = false;
	private Set<String> testHeadElementsAddedViaAddDependency = new HashSet<String>();

	public DependencyTrackingHeaderResponse(HeaderResponse headerResponse) {
		super(headerResponse);
	}

	@Override
	public void addDependency(String name, String scope, String version) {

		// The Liferay Faces Bridge Reference Implementation (RI) does not call this method. However, other
		// implementations (or extensions to the RI) might call it. Because of this, it is important for the TCK to
		// implement this method. Of the resources defined in ResourcesRenderedInHeadTestUtil, only jsf.js could fall
		// into the category of being added without markup. For example, if the portlet container provided jsf.js
		// out-of-the-box, then the bridge could elect to not provide the markup for the resource.
		if ("jsf.js".equals(name) && "javax.faces".equals(scope)) {
			testHeadElementsAddedViaAddDependency.add("jsf.js");
		}

		super.addDependency(name, scope, version);
	}

	@Override
	public void addDependency(String name, String scope, String version, String markup) {

		for (String[] testHeadElementId : ResourcesRenderedInHeadTestUtil.TEST_HEAD_ELEMENT_IDS) {

			// For each script and stylesheet check if the id contains the testHeadElementId. See
			// ResponseWriterResourceIdImpl for more details.
			String escapedTestHeadElementId = ResourcesRenderedInHeadTestUtil.escapeId(testHeadElementId[0]);
			String idContainsTestHeadElementIdRegex = ".*id=\"[^\"]*" + escapedTestHeadElementId + "\"(.|\n)*";

			if ((markup.contains("<script") || markup.contains("<style") || markup.contains("<link")) &&
					markup.matches(idContainsTestHeadElementIdRegex)) {

				// If the resource has a name and library, make sure that they were correctly specified as the name and
				// scope of the dependency.
				if ((testHeadElementId.length == 1) ||
						(testHeadElementId[0].equals(name) && testHeadElementId[1].equals(scope))) {
					testHeadElementsAddedViaAddDependency.add(testHeadElementId[0]);
				}

				break;
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
