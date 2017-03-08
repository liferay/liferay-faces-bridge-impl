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

import static com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2.HeaderResponseResourcesRenderedInHeadTestImpl.TEST_HEAD_ELEMENT_IDS;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean
@RequestScoped
public class ResourcesRenderedInHeadTestBean {

	// Private Constants
	private static final String TEST_HEAD_ELEMENT_IDS_JS_ARRAY;

	static {

		String testHeadElementIdsJSArray = "";

		for (String testHeadElementId : HeaderResponseResourcesRenderedInHeadTestImpl.TEST_HEAD_ELEMENT_IDS) {

			if (testHeadElementIdsJSArray.length() > 0) {
				testHeadElementIdsJSArray += ", ";
			}

			testHeadElementIdsJSArray += "'" + testHeadElementId + "'";
		}

		TEST_HEAD_ELEMENT_IDS_JS_ARRAY = "[ " + testHeadElementIdsJSArray + " ]";
	}

	public String getTestHeadElementIdsJSArray() {
		return TEST_HEAD_ELEMENT_IDS_JS_ARRAY;
	}

	public String getTestHeadElementsNotAddedViaAddDependency() {

		String testHeadElementsAddedViaAddDependencyString = "";
		Set<String> testHeadElementsAddedViaAddDependency = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		while (portletResponse instanceof PortletResponseWrapper) {

			if (portletResponse instanceof HeaderResponseResourcesRenderedInHeadTestImpl) {

				HeaderResponseResourcesRenderedInHeadTestImpl headerResponseResourcesRenderedInHeadTestImpl =
					(HeaderResponseResourcesRenderedInHeadTestImpl) portletResponse;
				testHeadElementsAddedViaAddDependency =
					headerResponseResourcesRenderedInHeadTestImpl.getTestHeadElementsAddedViaAddDependency();

				break;
			}

			PortletResponseWrapper portletResponseWrapper = (PortletResponseWrapper) portletResponse;
			portletResponse = portletResponseWrapper.getResponse();
		}

		for (String testHeadElementId : TEST_HEAD_ELEMENT_IDS) {

			if ((testHeadElementsAddedViaAddDependency == null) ||
					!testHeadElementsAddedViaAddDependency.contains(testHeadElementId)) {

				if (testHeadElementsAddedViaAddDependencyString.length() > 0) {
					testHeadElementsAddedViaAddDependencyString += ", ";
				}

				testHeadElementsAddedViaAddDependencyString += testHeadElementId;
			}
		}

		return testHeadElementsAddedViaAddDependencyString;
	}

	public boolean isAddPropertyMarkupHeadElementCalled() {

		boolean addPropertyMarkupHeadElementCalled = false;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		while (portletResponse instanceof PortletResponseWrapper) {

			if (portletResponse instanceof HeaderResponseResourcesRenderedInHeadTestImpl) {

				HeaderResponseResourcesRenderedInHeadTestImpl headerResponseResourcesRenderedInHeadTestImpl =
					(HeaderResponseResourcesRenderedInHeadTestImpl) portletResponse;
				addPropertyMarkupHeadElementCalled =
					headerResponseResourcesRenderedInHeadTestImpl.isAddPropertyMarkupHeadElementCalled();

				break;
			}

			PortletResponseWrapper portletResponseWrapper = (PortletResponseWrapper) portletResponse;
			portletResponse = portletResponseWrapper.getResponse();
		}

		return addPropertyMarkupHeadElementCalled;
	}
}
