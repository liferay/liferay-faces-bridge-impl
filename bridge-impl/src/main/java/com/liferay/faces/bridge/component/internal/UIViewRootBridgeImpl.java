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
package com.liferay.faces.bridge.component.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.util.application.ResourceVerifier;
import com.liferay.faces.util.application.ResourceVerifierFactory;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class overrides the behavior of the {@link PortletNamingContainerUIViewRoot} standard class.
 *
 * @author  Neil Griffin
 */
public class UIViewRootBridgeImpl extends PortletNamingContainerUIViewRoot {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(UIViewRootBridgeImpl.class);

	// serialVersionUID
	private static final long serialVersionUID = 1523062041951774729L;

	@Override
	public List<UIComponent> getComponentResources(FacesContext facesContext, String target) {

		// Get the list of all component resources.
		List<UIComponent> allComponentResources = super.getComponentResources(facesContext, target);

		// Determine which of the component resources are unsatisfied.
		List<UIComponent> unsatisfiedComponentResources = new ArrayList<UIComponent>(allComponentResources.size());
		ResourceVerifier resourceVerifier = ResourceVerifierFactory.getResourceDependencyHandlerInstance();

		for (UIComponent componentResource : allComponentResources) {

			if (resourceVerifier.isDependencySatisfied(facesContext, componentResource)) {

				if (logger.isDebugEnabled()) {

					Map<String, Object> componentResourceAttributes = componentResource.getAttributes();

					logger.debug(
						"Resource dependency already satisfied: name=[{0}] library=[{1}] rendererType=[{2}] value=[{3}] className=[{4}]",
						componentResourceAttributes.get("name"), componentResourceAttributes.get("library"),
						componentResource.getRendererType(), ComponentUtil.getComponentValue(componentResource),
						componentResource.getClass().getName());
				}
			}
			else {
				unsatisfiedComponentResources.add(componentResource);
			}
		}

		// Return an immutable list of unsatisfied resources.
		return Collections.unmodifiableList(unsatisfiedComponentResources);
	}

	/**
	 * <p>This method fixes a problem with {@link UIComponent#findComponent(String)} where sometimes it is unable to
	 * find components due to incorrect clientId values. For more info, see the following issues:
	 *
	 * <ul>
	 *   <li>http://issues.liferay.com/browse/FACES-198</li>
	 *   <li>http://jira.icesoft.org/browse/ICE-6659</li>
	 *   <li>http://jira.icesoft.org/browse/ICE-6667</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public void setId(String id) {

		if (BridgeUtil.isPortletRequest()) {
			super.setId(getContainerClientId(FacesContext.getCurrentInstance()));
		}
	}
}
