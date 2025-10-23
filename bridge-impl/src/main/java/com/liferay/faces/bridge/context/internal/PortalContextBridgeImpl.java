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
package com.liferay.faces.bridge.context.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;

import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.internal.PortletConfigParamUtil;


/**
 * @author  Neil Griffin
 */
public class PortalContextBridgeImpl extends PortalContextBridgeCompatImpl {

	// Private Data Members
	private String ableToSetHttpStatusCode;
	private List<String> propertyNameList;

	public PortalContextBridgeImpl(PortletRequest portletRequest) {

		super(portletRequest);

		PortalContext portalContext = portletRequest.getPortalContext();
		propertyNameList = new ArrayList<String>();

		Enumeration<String> propertyNames = portalContext.getPropertyNames();

		while (propertyNames.hasMoreElements()) {
			propertyNameList.add(propertyNames.nextElement());
		}

		propertyNameList.add(BridgePortalContext.ADD_ELEMENT_TO_HEAD_SUPPORT);
		propertyNameList.add(BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT);
		propertyNameList.add(BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT);
		propertyNameList.add(BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT);
		propertyNameList.add(BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT);
		propertyNameList.add(BridgePortalContext.CREATE_RENDER_URL_DURING_ACTION_PHASE_SUPPORT);
		propertyNameList.add(BridgePortalContext.POST_REDIRECT_GET_SUPPORT);
		propertyNameList.add(BridgePortalContext.SET_HTTP_STATUS_CODE_SUPPORT);
		propertyNameList.add(BridgePortalContext.SET_RESOURCE_RESPONSE_BUFFER_SIZE_SUPPORT);
		propertyNameList.add(BridgePortalContext.STRICT_NAMESPACED_PARAMETERS_SUPPORT);
	}

	@Override
	public String getProperty(String name) {

		if (PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT.equals(name) ||
				BridgePortalContext.ADD_ELEMENT_TO_HEAD_SUPPORT.equals(name) ||
				BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT.equals(name) ||
				BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT.equals(name) ||
				BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT.equals(name) ||
				BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT.equals(name)) {
			return getAddToHeadSupport(name, getWrapped());
		}
		else if (BridgePortalContext.CREATE_RENDER_URL_DURING_ACTION_PHASE_SUPPORT.equals(name)) {

			// Portlet 2.0 does not support this feature but perhaps Portlet 3.0 will.
			// https://java.net/jira/browse/PORTLETSPEC3-49
			return null;
		}
		else if (BridgePortalContext.STRICT_NAMESPACED_PARAMETERS_SUPPORT.equals(name)) {
			return null;
		}
		else if (BridgePortalContext.POST_REDIRECT_GET_SUPPORT.equals(name)) {
			return "true";
		}
		else if (BridgePortalContext.SET_HTTP_STATUS_CODE_SUPPORT.equals(name)) {
			return getSetHttpStatusCode();
		}
		else if (BridgePortalContext.SET_RESOURCE_RESPONSE_BUFFER_SIZE_SUPPORT.equals(name)) {
			return "true";
		}
		else {
			return getWrapped().getProperty(name);
		}
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.enumeration(propertyNameList);
	}

	private String getSetHttpStatusCode() {

		if (ableToSetHttpStatusCode == null) {

			// Although it's not the most performant option, it's safest to assume that the portlet container has not
			// implemented this feature. That way, the ResourceHandlerImpl will always deliver stuff like jsf.js back
			// to the browser. As we support more portlet containers in the future (Pluto, etc.) we can create
			// implementations that override this.
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			ableToSetHttpStatusCode = PortletConfigParamUtil.getStringValue(externalContext,
					PortletConfigParam.ContainerAbleToSetHttpStatusCode);
		}

		return ableToSetHttpStatusCode;
	}
}
