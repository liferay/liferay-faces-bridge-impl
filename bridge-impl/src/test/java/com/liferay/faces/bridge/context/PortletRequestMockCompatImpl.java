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
package com.liferay.faces.bridge.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.RenderParameters;
import javax.portlet.faces.Bridge;


/**
 * @author  Kyle Stiemann
 */
public abstract class PortletRequestMockCompatImpl implements PortletRequest {

	@Override
	public PortletContext getPortletContext() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public RenderParameters getRenderParameters() {
		throw new UnsupportedOperationException("");
	}

	@Override
	public String getUserAgent() {
		throw new UnsupportedOperationException("");
	}

	protected final Map<String, String[]> initParameters(boolean ajaxRequest) {

		Map<String, String[]> parameters;

		if (ajaxRequest) {

			parameters = new HashMap<String, String[]>();
			parameters.put(Bridge.FACES_AJAX_PARAMETER, new String[] { "true" });
			parameters = Collections.unmodifiableMap(parameters);
		}
		else {
			parameters = Collections.emptyMap();
		}

		return parameters;
	}
}
