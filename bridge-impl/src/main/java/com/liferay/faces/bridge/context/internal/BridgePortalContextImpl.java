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
package com.liferay.faces.bridge.context.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import com.liferay.faces.bridge.config.internal.PortletConfigParam;
import com.liferay.faces.bridge.context.BridgePortalContext;


/**
 * @author  Neil Griffin
 */
public class BridgePortalContextImpl implements BridgePortalContext {

	// Private Data Members
	private String ableToSetHttpStatusCode;
	private List<String> propertyNameList;
	private PortalContext wrappedPortalContext;

	public BridgePortalContextImpl(PortalContext portalContext) {

		this.wrappedPortalContext = portalContext;

		propertyNameList = new ArrayList<String>();

		Enumeration<String> propertyNames = portalContext.getPropertyNames();

		while (propertyNames.hasMoreElements()) {
			propertyNameList.add(propertyNames.nextElement());
		}

		propertyNameList.add(ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT);
		propertyNameList.add(ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT);
		propertyNameList.add(ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT);
		propertyNameList.add(CREATE_RENDER_URL_DURING_ACTION_PHASE_SUPPORT);
		propertyNameList.add(POST_REDIRECT_GET_SUPPORT);
		propertyNameList.add(SET_HTTP_STATUS_CODE_SUPPORT);
		propertyNameList.add(SET_RESOURCE_RESPONSE_BUFFER_SIZE_SUPPORT);
		propertyNameList.add(STRICT_NAMESPACED_PARAMETERS_SUPPORT);
	}

	protected String getAddScriptResourceToHead() {
		return getMarkupHeadElementSupported();
	}

	protected String getAddScriptTextToHead() {
		return getMarkupHeadElementSupported();
	}

	protected String getAddStyleSheetResourceToHead() {
		return getMarkupHeadElementSupported();
	}

	public String getCreateRenderUrlDuringActionPhase() {

		// Portlet 2.0 does not support this feature but perhaps Portlet 3.0 will.
		// https://java.net/jira/browse/PORTLETSPEC3-49
		return null;
	}

	protected String getMarkupHeadElementSupported() {
		return wrappedPortalContext.getProperty(PortalContext.MARKUP_HEAD_ELEMENT_SUPPORT);
	}

	protected String getNamespacedParametersRequired() {
		return null;
	}

	@Override
	public String getPortalInfo() {
		return wrappedPortalContext.getPortalInfo();
	}

	protected String getPostRedirectGetSupported() {
		return null;
	}

	@Override
	public String getProperty(String name) {

		if (ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT.equals(name)) {
			return getAddScriptResourceToHead();
		}
		else if (ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT.equals(name)) {
			return getAddScriptTextToHead();
		}
		else if (ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT.equals(name)) {
			return getAddStyleSheetResourceToHead();
		}
		else if (CREATE_RENDER_URL_DURING_ACTION_PHASE_SUPPORT.equals(name)) {
			return getCreateRenderUrlDuringActionPhase();
		}
		else if (STRICT_NAMESPACED_PARAMETERS_SUPPORT.equals(name)) {
			return getNamespacedParametersRequired();
		}
		else if (POST_REDIRECT_GET_SUPPORT.equals(name)) {
			return getPostRedirectGetSupported();
		}
		else if (SET_HTTP_STATUS_CODE_SUPPORT.equals(name)) {
			return getSetHttpStatusCode();
		}
		else if (SET_RESOURCE_RESPONSE_BUFFER_SIZE_SUPPORT.equals(name)) {
			return getSetResourceResponseBufferSize();
		}
		else {
			return wrappedPortalContext.getProperty(name);
		}
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.enumeration(propertyNameList);
	}

	protected String getSetHttpStatusCode() {

		if (ableToSetHttpStatusCode == null) {

			// Although it's not the most performant option, it's safest to assume that the portlet container has not
			// implemented this feature. That way, the ResourceHandlerImpl will always deliver stuff like jsf.js back
			// to the browser. As we support more portlet containers in the future (Pluto, etc.) we can create
			// implementations that override this.
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			ableToSetHttpStatusCode = externalContext.getInitParameter(
					PortletConfigParam.ContainerAbleToSetHttpStatusCode.getName());

			if (ableToSetHttpStatusCode == null) {
				ableToSetHttpStatusCode = externalContext.getInitParameter(
						PortletConfigParam.ContainerAbleToSetHttpStatusCode.getAlternateName());
			}
		}

		return ableToSetHttpStatusCode;
	}

	protected String getSetResourceResponseBufferSize() {
		return "true";
	}

	@Override
	public Enumeration<PortletMode> getSupportedPortletModes() {
		return wrappedPortalContext.getSupportedPortletModes();
	}

	@Override
	public Enumeration<WindowState> getSupportedWindowStates() {
		return wrappedPortalContext.getSupportedWindowStates();
	}
}
