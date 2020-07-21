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
package com.liferay.faces.issue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean(name = "FACES_3300Bean")
@RequestScoped
public class FACES_3300Bean {

	// Private Constants
	private static final List<String> BRIDGE_FACES_VIEW_PARAMETER_NAMES = Collections.unmodifiableList(Arrays.asList(
				Bridge.FACES_VIEW_ID_PARAMETER, Bridge.FACES_VIEW_PATH_PARAMETER,

				// default value of bridgeConfig.getViewIdRenderParameterName()
				"_facesViewIdRender"));
	private static final String RESULT_XHTML_VIEW = "WEB-INF/views/FACES-3300/result.xhtml";
	private static final List<String> VALID_VIEWS = Collections.unmodifiableList(Arrays.asList("/" + RESULT_XHTML_VIEW,
				RESULT_XHTML_VIEW));
	private static final String NON_EXISTENT_VIEW = "nonExistentView.xhtml";
	private static final List<String> INVALID_VIEWS = Collections.unmodifiableList(Arrays.asList(
				"/" + NON_EXISTENT_VIEW, NON_EXISTENT_VIEW));

	public List<String> getBridgeFacesViewParameterNames() {
		return BRIDGE_FACES_VIEW_PARAMETER_NAMES;
	}

	public List<String> getInvalidViews() {
		return INVALID_VIEWS;
	}

	public List<String> getValidViews() {
		return VALID_VIEWS;
	}

	public boolean isFailed(PortletRequest portletRequest) {

		boolean failed = false;
		List<String> bridgeFacesViewParameterNames = getBridgeFacesViewParameterNames();

		for (String bridgeFacesViewParameterName : bridgeFacesViewParameterNames) {

			failed = (portletRequest.getParameter(bridgeFacesViewParameterName) != null);

			if (failed) {
				break;
			}
		}

		return failed;
	}
}
