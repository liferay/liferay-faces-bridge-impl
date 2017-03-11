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
package com.liferay.faces.bridge.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.faces.BridgeConfig;

import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.config.ConfiguredElement;
import com.liferay.faces.util.config.FacesConfig;


/**
 * @author  Neil Griffin
 */
public class BridgeConfigImpl implements BridgeConfig {

	// Private Constants
	private static final String EXCLUDED_ATTRIBUTE = "excluded-attribute";
	private static final String MODEL_EL = "model-el";
	private static final String PARAMETER = "parameter";
	private static final String RENDER_RESPONSE_WRAPPER_CLASS = "render-response-wrapper-class";
	private static final String RESOURCE_RESPONSE_WRAPPER_CLASS = "resource-response-wrapper-class";

	// Final Data Members
	private final Map<String, Object> bridgeConfigAttributeMap;
	private final Set<String> excludedRequestAttributes;
	private final Map<String, String[]> publicParameterMappings;
	private final String viewIdRenderParameterName;
	private final String viewIdResourceParameterName;

	public BridgeConfigImpl(PortletConfig portletConfig) {

		// bridgeConfigAttributeMap
		Map<String, Object> bridgeConfigAttributeMap = new BridgeConfigAttributeMap();

		// configuredFacesServletMappings
		String appConfigAttrName = ApplicationConfig.class.getName();
		PortletContext portletContext = portletConfig.getPortletContext();
		ApplicationConfig applicationConfig = (ApplicationConfig) portletContext.getAttribute(appConfigAttrName);
		FacesConfig facesConfig = applicationConfig.getFacesConfig();
		bridgeConfigAttributeMap.put(BridgeConfigAttributeMap.CONFIGURED_FACES_SERVLET_MAPPINGS,
			facesConfig.getConfiguredFacesServletMappings());

		// configuredSystemEventListeners
		bridgeConfigAttributeMap.put(BridgeConfigAttributeMap.CONFIGURED_SYSTEM_EVENT_LISTENERS,
			facesConfig.getConfiguredSystemEventListeners());

		// configuredSuffixes
		bridgeConfigAttributeMap.put(BridgeConfigAttributeMap.CONFIGURED_SUFFIXES, facesConfig.getConfiguredSuffixes());
		this.bridgeConfigAttributeMap = Collections.unmodifiableMap(bridgeConfigAttributeMap);

		// excludedRequestAttributes
		Set<String> excludedRequestAttributes = new HashSet<String>();

		List<ConfiguredElement> configuredApplicationExtensions = facesConfig.getConfiguredApplicationExtensions();

		for (ConfiguredElement configuredElement : configuredApplicationExtensions) {
			String configuredElementName = configuredElement.getName();

			if (EXCLUDED_ATTRIBUTE.equals(configuredElementName)) {
				String excludedAttributeName = configuredElement.getValue();
				excludedRequestAttributes.add(excludedAttributeName);
			}
		}

		this.excludedRequestAttributes = Collections.unmodifiableSet(excludedRequestAttributes);

		// publicParameterMappings
		Map<String, String[]> publicParameterMappings = new HashMap<String, String[]>();

		String parameter = null;
		String modelEL = null;

		for (ConfiguredElement configuredElement : configuredApplicationExtensions) {
			String configuredElementName = configuredElement.getName();

			if (PARAMETER.equals(configuredElementName)) {
				parameter = configuredElement.getValue();
			}
			else if (MODEL_EL.equals(configuredElementName)) {

				modelEL = configuredElement.getValue();

				if ((parameter != null) && (modelEL != null)) {

					if (modelEL.length() > 0) {
						String[] newValue = new String[] { modelEL };
						String[] existingValue = publicParameterMappings.get(parameter);

						if (existingValue != null) {
							int total = existingValue.length + 1;
							newValue = new String[total];

							for (int i = 0; i < existingValue.length; i++) {
								newValue[i] = existingValue[i];
							}

							newValue[total - 1] = modelEL;
						}

						publicParameterMappings.put(parameter, newValue);
					}
				}
			}
		}

		this.publicParameterMappings = Collections.unmodifiableMap(publicParameterMappings);

		// viewIdResourceParameterName
		this.viewIdResourceParameterName = PortletConfigParam.ViewIdResourceParameterName.getStringValue(portletConfig);

		// viewIdRenderParameterName
		this.viewIdRenderParameterName = PortletConfigParam.ViewIdRenderParameterName.getStringValue(portletConfig);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return bridgeConfigAttributeMap;
	}

	@Override
	public Set<String> getExcludedRequestAttributes() {
		return excludedRequestAttributes;
	}

	@Override
	public Map<String, String[]> getPublicParameterMappings() {
		return publicParameterMappings;
	}

	@Override
	public String getViewIdRenderParameterName() {
		return viewIdRenderParameterName;
	}

	@Override
	public String getViewIdResourceParameterName() {
		return viewIdResourceParameterName;
	}

}
