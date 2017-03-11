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
package com.liferay.faces.bridge.application.internal;

import java.io.Serializable;
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
public class ResourceVerifierBridgeImpl extends ResourceVerifierWrapper implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7367617042346492537L;

	// Private Members
	private ResourceVerifier wrappedResourceVerifier;

	public ResourceVerifierBridgeImpl(ResourceVerifier wrappedResourceVerifier) {
		this.wrappedResourceVerifier = wrappedResourceVerifier;
	}

	@Override
	public ResourceVerifier getWrapped() {
		return wrappedResourceVerifier;
	}

	@Override
	public boolean isDependencySatisfied(FacesContext facesContext, UIComponent componentResource) {

		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);

		if (headManagedBean == null) {
			return super.isDependencySatisfied(facesContext, componentResource);
		}
		else {
			Set<String> headResourceIds = headManagedBean.getHeadResourceIds();
			String resourceId = ResourceUtil.getResourceId(componentResource);

			if (headResourceIds.contains(resourceId)) {
				return true;
			}
			else {
				return super.isDependencySatisfied(facesContext, componentResource);
			}
		}
	}
}
