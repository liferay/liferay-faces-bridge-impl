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
package com.liferay.faces.portlet.component.baseurl.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.component.PortletActionURL;
import javax.portlet.faces.component.PortletParam;
import javax.portlet.faces.component.PortletProperty;

import com.liferay.faces.bridge.util.internal.URLUtil;


/**
 * @author  Kyle Stiemann
 */
public abstract class BaseURLRenderer extends BaseURLRendererBase {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		// no-op
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// no-op
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		BaseURL baseURL = createBaseURL(facesContext, uiComponent);
		Boolean secure = getSecure(uiComponent);

		if (secure != null) {

			try {
				baseURL.setSecure(secure);
			}
			catch (PortletSecurityException e) {
				throw new IOException(e);
			}
		}

		processParamChildren(uiComponent, baseURL);
		processPropertyChildren(uiComponent, baseURL);

		String varName = getVar(uiComponent);
		String url = baseURL.toString();

		if (isEscapeXml(uiComponent)) {
			url = URLUtil.escapeXML(url);
		}

		// If the user didn't specify a value for the "var" attribute, then write the URL to the response.
		if (varName == null) {
			ResponseWriter responseWriter = facesContext.getResponseWriter();
			responseWriter.write(url);
		}

		// Otherwise, place the url into the request scope so that it can be resolved via EL with the name
		// specified in the "var" attribute.
		else {

			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put(varName, url);
		}
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	protected abstract BaseURL createBaseURL(FacesContext facesContext, UIComponent uiComponent) throws IOException;

	protected abstract Boolean getSecure(UIComponent uiComponent);

	protected abstract String getVar(UIComponent uiComponent);

	protected abstract boolean isEscapeXml(UIComponent uiComponent);

	protected void processParamChildren(UIComponent uiComponent, BaseURL baseURL) {

		Map<String, String[]> parameterMap = new HashMap<String, String[]>(baseURL.getParameterMap());
		Map<String, String[]> initialParameterMap = new HashMap<String, String[]>(parameterMap);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		boolean RESOURCE_PHASE = BridgeUtil.getPortletRequestPhase(facesContext).equals(
				Bridge.PortletPhase.RESOURCE_PHASE);

		List<UIComponent> children = uiComponent.getChildren();

		for (UIComponent child : children) {

			if (child instanceof PortletParam) {

				PortletParam portletParam = (PortletParam) child;
				String name = portletParam.getName();
				String value = portletParam.getValue();

				if (parameterMap.containsKey(name)) {

					// According to the Java Portlet Specification (version 2.0 (2008-01-11)), when rendering a resource
					// URL, parameters set by the portlet container should not be removed.
					final boolean PARAM_SET_BY_PORTLET_CONTAINER = initialParameterMap.containsKey(name);
					final boolean REMOVE_EMPTY_PARAMETER = (!RESOURCE_PHASE || !PARAM_SET_BY_PORTLET_CONTAINER);

					if ("".equals(value) && REMOVE_EMPTY_PARAMETER) {
						parameterMap.remove(name);
					}
					else if ("".equals(value) && !REMOVE_EMPTY_PARAMETER) {
						// do nothing
					}
					else {

						String[] paramValueArray = parameterMap.get(name);
						String[] newParamValueArray = new String[paramValueArray.length + 1];
						System.arraycopy(paramValueArray, 0, newParamValueArray, 0, paramValueArray.length);
						newParamValueArray[newParamValueArray.length - 1] = value;
						parameterMap.put(name, newParamValueArray);
					}
				}
				else {
					parameterMap.put(name, new String[] { value });
				}
			}
		}

		baseURL.setParameters(parameterMap);
	}

	protected void processPropertyChildren(UIComponent uiComponent, BaseURL baseURL) {

		List<UIComponent> children = uiComponent.getChildren();

		for (UIComponent child : children) {

			if (child instanceof PortletProperty) {

				PortletProperty portletProperty = (PortletProperty) child;
				String value = portletProperty.getValue();

				if (value != null) {
					baseURL.addProperty(portletProperty.getName(), value);
				}
			}
		}

	}
}
