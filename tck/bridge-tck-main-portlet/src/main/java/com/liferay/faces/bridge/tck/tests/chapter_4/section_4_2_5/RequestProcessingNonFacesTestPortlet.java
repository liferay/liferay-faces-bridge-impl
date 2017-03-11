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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_5;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * Non Faces request processing, section 4.2.5. Confirms that for a non Faces request, the response contentType is set,
 * if not already set, using the preferred contentType expressed by the portlet container.
 */
public class RequestProcessingNonFacesTestPortlet extends GenericFacesTestSuitePortlet {
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		if ((response.getContentType() == null) && (request.getParameter("_jsfBridgeNonFacesView") != null)) {

			// Set an attribute to be read by the associated jsp view.  The test
			// is considered to have passed if the response content type is the
			// same as the request content type.
			request.setAttribute("com.liferay.faces.bridge.tck.4_2_5_nonFacesTest", "1");
		}

		super.render(request, response);
	}
}
