/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.application.internal;

import java.util.List;
import java.util.Map;

import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.util.internal.URLUtil;


/**
 * @author  Neil Griffin
 */
public class BridgeNavigationCaseImpl implements BridgeNavigationCase {

	// Private Data Members
	private Map<String, List<String>> parameters;
	private String toViewId;

	public BridgeNavigationCaseImpl(String toViewId) {
		this.toViewId = toViewId;
	}

	public Map<String, List<String>> getParameters() {

		if (parameters == null) {
			parameters = URLUtil.parseParameterMapValuesList(toViewId);
		}

		return parameters;
	}

	public String getPortletMode() {
		return getParameter(Bridge.PORTLET_MODE_PARAMETER);
	}

	public String getWindowState() {
		return getParameter(Bridge.PORTLET_WINDOWSTATE_PARAMETER);
	}

	protected String getParameter(String parameterName) {

		String parameter = null;

		Map<String, List<String>> parameterMap = getParameters();

		if (parameterMap != null) {
			List<String> values = parameterMap.get(parameterName);

			if ((values != null) && (values.size() > 0)) {
				parameter = values.get(0);
			}
		}

		return parameter;
	}
}
