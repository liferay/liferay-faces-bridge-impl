/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.el.internal;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.filter.PortletConfigWrapper;


/**
 * This class provides a compatibility layer that isolates differences between JSF versions.
 *
 * @author  Neil Griffin
 */
public abstract class ELResolverCompatImpl extends ELResolver {

	// Private Constants
	private static final String HEADER_REQUEST = "headerRequest";
	private static final String HEADER_RESPONSE = "headerResponse";

	// Protected Constants
	protected static final List<FeatureDescriptor> FEATURE_DESCRIPTORS_COMPAT;

	static {

		// Initialize the list of static feature descriptors.
		List<FeatureDescriptor> featureDescriptors = new ArrayList<FeatureDescriptor>();
		featureDescriptors.add(getFeatureDescriptor(HEADER_REQUEST, HeaderRequest.class));
		featureDescriptors.add(getFeatureDescriptor(HEADER_RESPONSE, HeaderResponse.class));
		featureDescriptors.addAll(featureDescriptors);
		FEATURE_DESCRIPTORS_COMPAT = Collections.unmodifiableList(featureDescriptors);
	}

	protected static FeatureDescriptor getFeatureDescriptor(String featureName, Class<?> classType) {
		FeatureDescriptor featureDescriptor = new FeatureDescriptor();
		featureDescriptor.setName(featureName);
		featureDescriptor.setDisplayName(featureName);
		featureDescriptor.setShortDescription(featureName);
		featureDescriptor.setExpert(false);
		featureDescriptor.setHidden(false);
		featureDescriptor.setPreferred(true);
		featureDescriptor.setValue(ELResolver.TYPE, classType);
		featureDescriptor.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);

		return featureDescriptor;
	}

	protected abstract PortletRequest getPortletRequest(FacesContext facesContext);

	protected abstract PortletResponse getPortletResponse(FacesContext facesContext);

	protected Object getFlash(FacesContext facesContext) {
		return facesContext.getExternalContext().getFlash();
	}

	protected boolean isFacesContextVar(String varName) {
		return HEADER_REQUEST.equals(varName) || HEADER_RESPONSE.equals(varName);
	}

	protected Object resolveVariable(ELContext elContext, String varName) {

		Object value = null;

		if (varName != null) {

			if (varName.equals(HEADER_REQUEST)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {
					value = getPortletRequest(facesContext);
				}
				else {
					throw new ELException("Unable to get renderRequest during " + portletPhase);
				}
			}
			else if (varName.equals(HEADER_RESPONSE)) {

				FacesContext facesContext = FacesContext.getCurrentInstance();
				Bridge.PortletPhase portletPhase = BridgeUtil.getPortletRequestPhase(facesContext);

				if (portletPhase == Bridge.PortletPhase.HEADER_PHASE) {
					value = getPortletResponse(facesContext);
				}
				else {
					throw new ELException("Unable to get renderResponse during " + portletPhase);
				}
			}
		}

		return value;
	}

	protected PortletConfig unwrapPortletConfig(PortletConfig portletConfig) {

		// Unwrap the PortletConfigWrapper to conform to the TCK's expectations. Note that this is not necessary with
		// JSR 378 since the TCK was modified to take BridgePortletConifigFactory into account. For more information,
		// see: https://issues.liferay.com/browse/FACES-3108
		while (portletConfig instanceof PortletConfigWrapper) {
			PortletConfigWrapper portletConfigWrapper = (PortletConfigWrapper) portletConfig;
			portletConfig = portletConfigWrapper.getWrapped();
		}

		return portletConfig;
	}
}
