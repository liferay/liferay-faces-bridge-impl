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
package com.liferay.faces.bridge.tck.tests.chapter_4.section_4_2_15;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.BridgeException;

import com.liferay.faces.bridge.tck.common.portlet.GenericFacesTestSuitePortlet;


/**
 * Checks that GenericFacesPortlet#getFacesBridge method works as stated in section 4.2.15. - There may be times, in
 * particular when dealing with events using the standard portlet event model, in which a subclass needs to dispatch a
 * request directly to the bridge. To support this the GenericFacesPortlet, via this method, returns a properly
 * initialized and active bridge which a subclass can use to call one of the doFacesRequest() methods.
 */
public class GetFacesBridgeMethodTestPortlet extends GenericFacesTestSuitePortlet {

	@Override
	public void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		String contentType = getResponseContentType(request);

		if (contentType != null) {
			String charSetEncoding = getResponseCharacterSetEncoding(request);

			if (charSetEncoding != null) {
				StringBuilder buf = new StringBuilder(contentType);
				buf.append(";");
				buf.append(charSetEncoding);
				response.setContentType(buf.toString());
			}
			else {
				response.setContentType(contentType);
			}
		}

		try {
			getFacesBridge(request, response).doFacesRequest(request, response);
		}
		catch (BridgeException be) {
			throw new PortletException(
				"getFacesBridge test failed:  error from Bridge while executing the render request", be);
		}
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

		try {
			getFacesBridge(request, response).doFacesRequest(request, response);
		}
		catch (BridgeException be) {
			throw new PortletException(
				"getFacesBridge test failed:  error from Bridge while executing the action request", be);
		}
	}
}
