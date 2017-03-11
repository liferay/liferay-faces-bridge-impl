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
package com.liferay.faces.bridge.renderkit.icefaces.internal;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.component.internal.ResourceComponent;
import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadRendererBridgeImpl;


/**
 * @author  Neil Griffin
 */
public class HeadRendererICEfacesImpl extends HeadRendererBridgeImpl {

	// Private Constants
	private static final String ICEFACES_LIBRARY_NAME_ACE = "icefaces.ace";
	private static final String ICEFACES_THEME_NAME_SAM = "sam";
	private static final String ICEFACES_THEME_NAME_RIME = "rime";
	private static final String ICEFACES_THEME_DEFAULT = ICEFACES_THEME_NAME_SAM;
	private static final String ICEFACES_THEME_PARAM = "org.icefaces.ace.theme";
	private static final String ICEFACES_THEME_NONE = "none";
	private static final String ICEFACES_THEME_PREFIX = "ace-";
	private static final String ICEFACES_THEME_RESOURCE_NAME = "theme.css";
	private static final String ICEFACES_THEME_DIR = "themes";

	@Override
	protected List<UIComponent> getFirstResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = super.getFirstResources(facesContext, uiComponent);

		// ICEfaces Theme
		ExternalContext externalContext = facesContext.getExternalContext();
		String iceFacesThemeName = externalContext.getInitParameter(ICEFACES_THEME_PARAM);

		if (iceFacesThemeName != null) {
			ELContext elContext = facesContext.getELContext();
			ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
			ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, iceFacesThemeName,
					String.class);
			iceFacesThemeName = (String) valueExpression.getValue(elContext);

		}
		else {
			iceFacesThemeName = ICEFACES_THEME_DEFAULT;
		}

		if ((iceFacesThemeName != null) && !iceFacesThemeName.equals(ICEFACES_THEME_NONE)) {

			if (resources == null) {
				resources = new ArrayList<UIComponent>();
			}

			String resourceName = ICEFACES_THEME_RESOURCE_NAME;
			String resourceLibrary = ICEFACES_THEME_PREFIX + iceFacesThemeName;

			if (iceFacesThemeName.equals(ICEFACES_THEME_NAME_SAM) ||
					iceFacesThemeName.equals(ICEFACES_THEME_NAME_RIME)) {
				StringBuilder buf = new StringBuilder();
				buf.append(ICEFACES_THEME_DIR);
				buf.append("/");
				buf.append(iceFacesThemeName);
				buf.append("/");
				buf.append(ICEFACES_THEME_RESOURCE_NAME);
				resourceName = buf.toString();
				resourceLibrary = ICEFACES_LIBRARY_NAME_ACE;
			}

			ResourceComponent iceFacesStyleSheet = new ResourceComponent(facesContext, resourceName, resourceLibrary,
					"head");
			resources.add(iceFacesStyleSheet);
		}

		return resources;
	}
}
