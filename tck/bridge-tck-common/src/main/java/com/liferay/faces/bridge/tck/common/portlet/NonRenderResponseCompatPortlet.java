/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.portlet;

import java.io.IOException;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletException;


/**
 * This class serves as a compatibility layer in order to minimize diffs across branches for TCK portlets that do not
 * rely on executing the RENDER_RESPONSE phase of the JSF lifecycle for rendering test results to the response.
 *
 * @author  Kyle Stiemann
 */
public class NonRenderResponseCompatPortlet extends GenericFacesTestSuitePortlet {

	@Override
	public void renderHeaders(HeaderRequest headerRequest, HeaderResponse headerResponse) throws PortletException,
		IOException {
		// no-op
	}
}
