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

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.portlet.filter.PortletResponseWrapper;


/**
 * This class is a JSF managed-bean that is used by resourcesRenderedInHeadTest.xhtml in order to display test results.
 *
 * @author  Kyle Stiemann
 */
@ManagedBean
@RequestScoped
public class ResourcesRenderedInHeadTestBean {

	// Private Constants
	private static final String TEST_HEAD_ELEMENT_IDS_JS_ARRAY;

	static {

		String testHeadElementIdsJSArray = "";

		for (String testHeadElementId : DependencyTrackingHeaderResponse.TEST_HEAD_ELEMENT_IDS) {

			if (testHeadElementIdsJSArray.length() > 0) {
				testHeadElementIdsJSArray += ", ";
			}

			testHeadElementIdsJSArray += "'" + testHeadElementId + "'";
		}

		TEST_HEAD_ELEMENT_IDS_JS_ARRAY = "[ " + testHeadElementIdsJSArray + " ]";
	}

	// Private Data Members
	private Boolean addPropertyMarkupHeadElementCalled;
	private String testHeadElementsNotAddedViaAddDependency;

	public String getTestHeadElementIdsJSArray() {
		return TEST_HEAD_ELEMENT_IDS_JS_ARRAY;
	}

	/**
	 * This method returns the elements that were EXPECTED to be added via {@link
	 * javax.portlet.HeaderResponse#addDependency(String, String, String)} or {@link
	 * javax.portlet.HeaderResponse#addDependency(String, String, String, String)} but were not. If this method returns
	 * a non-empty value then that would indicate a test condition failure.
	 */
	public String getTestHeadElementsNotAddedViaAddDependency() {

		if (testHeadElementsNotAddedViaAddDependency == null) {

			StringBuilder buf = new StringBuilder();
			DependencyTrackingHeaderResponse dependencyTrackingHeaderResponse = getDependencyTrackingHeaderResponse();
			Set<String> testHeadElementsAddedViaAddDependency =
				dependencyTrackingHeaderResponse.getTestHeadElementsAddedViaAddDependency();

			for (String testHeadElementId : DependencyTrackingHeaderResponse.TEST_HEAD_ELEMENT_IDS) {

				if ((testHeadElementsAddedViaAddDependency == null) ||
						!testHeadElementsAddedViaAddDependency.contains(testHeadElementId)) {

					if (buf.length() > 0) {
						buf.append(", ");
					}

					buf.append(testHeadElementId);
				}
			}

			testHeadElementsNotAddedViaAddDependency = buf.toString();
		}

		return testHeadElementsNotAddedViaAddDependency;
	}

	/**
	 * Determines whether or not the {@link javax.portlet.HeaderResponse#addProperty(String, org.w3c.dom.Element)}
	 * method was called in order to add a resource to the <head>...</head> section of the page. Since the FacesBridge
	 * is not supposed to do this, returning a value of <code>true</code> would indicate a test condition failure.
	 */
	public boolean isAddPropertyMarkupHeadElementCalled() {

		if (addPropertyMarkupHeadElementCalled == null) {

			DependencyTrackingHeaderResponse dependencyTrackingHeaderResponse = getDependencyTrackingHeaderResponse();
			addPropertyMarkupHeadElementCalled =
				dependencyTrackingHeaderResponse.isAddPropertyMarkupHeadElementCalled();
		}

		return addPropertyMarkupHeadElementCalled;
	}

	private DependencyTrackingHeaderResponse getDependencyTrackingHeaderResponse() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		return getDependencyTrackingHeaderResponse(portletResponse);
	}

	private DependencyTrackingHeaderResponse getDependencyTrackingHeaderResponse(PortletResponse portletResponse) {

		if (portletResponse instanceof DependencyTrackingHeaderResponse) {
			return (DependencyTrackingHeaderResponse) portletResponse;
		}
		else if (portletResponse instanceof PortletResponseWrapper) {
			PortletResponseWrapper portletResponseWrapper = (PortletResponseWrapper) portletResponse;

			return getDependencyTrackingHeaderResponse(portletResponseWrapper.getResponse());
		}
		else {

			// Unexpected error
			return null;
		}
	}
}
