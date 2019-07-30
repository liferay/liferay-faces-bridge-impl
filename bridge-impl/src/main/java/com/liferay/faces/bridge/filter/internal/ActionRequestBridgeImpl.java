/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.filter.internal;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortalContext;
import javax.portlet.filter.ActionRequestWrapper;


/**
 * @author  Neil Griffin
 */
public class ActionRequestBridgeImpl extends ActionRequestWrapper {

	// Private Data Members
	private PortalContext portalContext;

	public ActionRequestBridgeImpl(ActionRequest actionRequest, PortalContext portalContext) {
		super(actionRequest);
		this.portalContext = portalContext;
	}

	@Override
	public Object getAttribute(String name) {
		return RequestAttributeUtil.getAttribute(getRequest(), name);
	}

	@Override
	public String getParameter(String name) {
		return RequestParameterUtil.getPortlet2_0Parameter(name, this);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return RequestParameterUtil.getPortlet2_0ParameterMap(super.getParameterMap());
	}

	@Override
	public String[] getParameterValues(String name) {
		return RequestParameterUtil.getPortlet2_0ParameterValues(name, this);
	}

	@Override
	public PortalContext getPortalContext() {
		return portalContext;
	}

	@Override
	public Map<String, String[]> getPrivateParameterMap() {
		return RequestParameterUtil.getPortlet2_0ParameterMap(super.getPublicParameterMap());
	}

	@Override
	public Map<String, String[]> getPublicParameterMap() {
		return RequestParameterUtil.getPortlet2_0ParameterMap(super.getPrivateParameterMap());
	}
}
