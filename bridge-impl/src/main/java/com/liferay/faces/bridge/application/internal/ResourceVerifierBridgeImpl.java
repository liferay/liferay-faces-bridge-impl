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

import java.util.Collections;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadManagedBean;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.application.ResourceVerifier;
import com.liferay.faces.util.application.ResourceVerifierWrapper;


/**
 * @author  Kyle Stiemann
 */
public class ResourceVerifierBridgeImpl extends ResourceVerifierWrapper {

	// Public Constants
	public static final String HEAD_RESOURCE_IDS = ResourceVerifierBridgeImpl.class.getName() + "HEAD_RESOURCE_IDS";

	// Private Members
	private ResourceVerifier wrappedResourceVerifier;

	public ResourceVerifierBridgeImpl(ResourceVerifier wrappedResourceVerifier) {
		this.wrappedResourceVerifier = wrappedResourceVerifier;
	}

	@Override
	public boolean isDependencySatisfied(FacesContext facesContext, UIComponent componentResource) {

		boolean dependencySatisfied;

		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);
		Set<String> headResourceIds = null;

		if (headManagedBean == null) {
			headResourceIds = Collections.EMPTY_SET;
		}
		else {
			headResourceIds = headManagedBean.getHeadResourceIds();
		}

		if (headResourceIds.contains(ResourceUtil.getResourceId(componentResource))) {
			dependencySatisfied = true;
		}
		else {
			dependencySatisfied = super.isDependencySatisfied(facesContext, componentResource);
		}

		return dependencySatisfied;
	}

	@Override
	public ResourceVerifier getWrapped() {
		return wrappedResourceVerifier;
	}
}
