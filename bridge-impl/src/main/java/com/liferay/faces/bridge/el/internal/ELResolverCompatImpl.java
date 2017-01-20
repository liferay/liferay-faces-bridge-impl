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
package com.liferay.faces.bridge.el.internal;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;


/**
 * This class provides a compatibility layer that isolates differences between JSF versions.
 *
 * @author  Neil Griffin
 */
public abstract class ELResolverCompatImpl extends ELResolver {

	// Protected Constants
	protected static final List<FeatureDescriptor> FEATURE_DESCRIPTORS_COMPAT = Collections.emptyList();

	protected static FeatureDescriptor getFeatureDescriptor(String featureName, Class<?> classType) {

		FeatureDescriptor featureDescriptor = new FeatureDescriptor();
		featureDescriptor.setName(featureName);
		featureDescriptor.setDisplayName(featureName);
		featureDescriptor.setShortDescription(featureName);
		featureDescriptor.setExpert(false);
		featureDescriptor.setHidden(false);
		featureDescriptor.setPreferred(true);
		featureDescriptor.setValue(ELResolver.TYPE, classType);
		featureDescriptor.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, true);

		return featureDescriptor;
	}

	protected abstract PortletRequest getPortletRequest(FacesContext facesContext);

	protected abstract PortletResponse getPortletResponse(FacesContext facesContext);

	protected Object getFlash(FacesContext facesContext) {
		return facesContext.getExternalContext().getFlash();
	}

	protected boolean isFacesContextVar(String varName) {
		return false;
	}

	protected Object resolveVariable(ELContext elContext, String varName) {
		return null;
	}
}
