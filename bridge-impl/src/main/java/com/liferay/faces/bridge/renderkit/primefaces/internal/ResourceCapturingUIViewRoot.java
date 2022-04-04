/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;


/**
 * <p>This class is used by {@link HeadRendererPrimeFacesImpl} in order to workaround FACES-2061. It does this by
 * tricking the {@link org.primefaces.renderkit.HeadRenderer} class into thinking that there are no component resources
 * in the ViewRoot. This is necessary because the {@link
 * com.sun.faces.renderkit.html_basic.ScriptRenderer#encodeEnd(FacesContext,UIComponent)} and {@link
 * com.sun.faces.renderkit.html_basic.StylesheetRenderer#encodeEnd(FacesContext,UIComponent)} methods keep track of
 * resources that have been rendered in a FacesContext attribute, which prevents the bridge's head renderer from
 * subsequently being able to render the component resources.</p>
 *
 * <p>In addition, this class captures (remembers) the component resources that were added by the PrimeFaces
 * HeadRenderer.</p>
 *
 * @author  Neil Griffin
 */
public class ResourceCapturingUIViewRoot extends UIViewRoot {

	Map<String, List<UIComponent>> capturedResourcesMap;

	public ResourceCapturingUIViewRoot() {
		capturedResourcesMap = new HashMap<String, List<UIComponent>>();
		capturedResourcesMap.put("head", new ArrayList<UIComponent>());
	}

	@Override
	public void addComponentResource(FacesContext facesContext, UIComponent componentResource, String target) {

		// Add the specified component resource to the list of those that have been captured (remembered).
		List<UIComponent> capturedResourcesList = capturedResourcesMap.get(target);

		if (capturedResourcesList == null) {
			capturedResourcesList = new ArrayList<UIComponent>();
			capturedResourcesMap.put(target, capturedResourcesList);
		}

		capturedResourcesList.add(componentResource);
	}

	public List<UIComponent> getCapturedComponentResources(String target) {

		// Return the list of component resources that were captured (remembered).
		return capturedResourcesMap.get(target);
	}

	@Override
	public List<UIComponent> getComponentResources(FacesContext facesContext, String target) {

		// Trick the PrimeFaces HeadRenderer into thinking that there are no resources to render, even though some may
		// have been added (captured).
		return Collections.emptyList();
	}
}
