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
package com.liferay.faces.bridge.component.internal;

import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;


/**
 * This class represents a JSF 2 component with a resource dependency. For example, if the <code>resourceName</code>
 * ends with CSS, then this component would be rendered with the Mojarra {@link
 * com.sun.faces.renderkit.html_basic.StyleSheetRenderer} class.
 *
 * @author  Neil Griffin
 */
public class ResourceComponent extends UIOutput {

	public ResourceComponent(FacesContext facesContext, String resourceName, String resourceLibrary, String target) {
		ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
		setRendererType(resourceHandler.getRendererTypeForResourceName(resourceName));

		Map<String, Object> attributes = this.getAttributes();
		attributes.put("name", resourceName);
		attributes.put("library", resourceLibrary);
		attributes.put("target", target);
	}
}
