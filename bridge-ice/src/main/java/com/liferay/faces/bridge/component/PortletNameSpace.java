/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.component;

import javax.faces.component.UIOutput;


/**
 * The default renderer for this component is {@link NameSpaceRenderer}. It conforms as closely as possible to the
 * requirements set forth in section PLT.26.5 of the JSR 286 Portlet Specification, Version 2.0.
 *
 * @author  Neil Griffin
 */
public class PortletNameSpace extends UIOutput {

	public static final String COMPONENT_TYPE = "com.liferay.faces.bridge.component.PortletNameSpace";
	public static final String DEFAULT_RENDERER_TYPE = "com.liferay.faces.bridge.renderkit.portlet.NameSpaceRenderer";

	public String getComponentType() {
		return COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return DEFAULT_RENDERER_TYPE;
	}

}
