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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.application.internal.ResourceRichFacesImpl;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ final class RenderKitUtil {

	// Package-Private Constants
	/* package-private */ static final String HEAD_RESOURCES_TO_RENDER_IN_BODY = "headResourcesToRenderInBody";
	/* package-private */ static final String SCRIPT_RENDERER_TYPE = "javax.faces.resource.Script";
	/* package-private */ static final String STYLE_CLASS_PORTLET_BODY = "liferay-faces-bridge-body";
	/* package-private */ static final String STYLESHEET_RENDERER_TYPE = "javax.faces.resource.Stylesheet";

	private RenderKitUtil() {
		throw new AssertionError();
	}

	/* package-private */ static Set<String> getHeadResourceIds(FacesContext facesContext) {

		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);
		Set<String> headResourceIds;

		if (headManagedBean == null) {
			headResourceIds = new HashSet<String>();
		}
		else {
			headResourceIds = headManagedBean.getHeadResourceIds();
		}

		return headResourceIds;
	}

	/* package-private */ static boolean isScriptResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String resourceLibrary = (String) componentResourceAttributes.get("library");

		return ((resourceName != null) && resourceName.endsWith("js")) ||
			isRichFacesReslibResource(resourceName, resourceLibrary);
	}

	/* package-private */ static boolean isStyleSheetResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("css");
	}

	private static boolean isRichFacesReslibResource(String resourceName, String resourceLibrary) {
		return ((resourceName != null) && resourceName.endsWith("reslib")) &&
			((resourceLibrary != null) && resourceLibrary.startsWith(ResourceRichFacesImpl.ORG_RICHFACES));
	}
}
