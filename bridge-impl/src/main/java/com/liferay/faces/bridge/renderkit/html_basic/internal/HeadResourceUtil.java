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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.portlet.PortalContext;

import com.liferay.faces.bridge.context.BridgePortalContext;


/**
 * @author  Kyle Stiemann
 */
public final class HeadResourceUtil {

	private HeadResourceUtil() {
		throw new AssertionError();
	}

	/* package-private */ static boolean canAddResourceToHead(PortalContext portalContext, UIComponent componentResource) {

		boolean canAddResourceToHead = false;

		if (isStyleSheetResource(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(
					BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT) != null;
		}
		else if (isScriptResource(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT) !=
				null;
		}
		else if (isInlineStyleSheet(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT) !=
				null;
		}
		else if (isInlineScript(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT) !=
				null;
		}

		return canAddResourceToHead;
	}

	/* package-private */ static boolean isScriptResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("js");
	}

	/* package-private */ static boolean isStyleSheetResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("css");
	}

	/* package-private */ static boolean isInlineScript(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String rendererType = componentResource.getRendererType();

		return (resourceName == null) && RenderKitBridgeImpl.SCRIPT_RENDERER_TYPE.equals(rendererType);
	}

	/* package-private */ static boolean isInlineStyleSheet(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String rendererType = componentResource.getRendererType();

		return (resourceName == null) && RenderKitBridgeImpl.STYLESHEET_RENDERER_TYPE.equals(rendererType);
	}
}
