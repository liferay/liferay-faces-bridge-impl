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
package com.liferay.faces.bridge.application.view.internal;

import java.io.IOException;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageWrapper;


/**
 * @author  Neil Griffin
 */
public class ViewDeclarationLanguageBridgeBase extends ViewDeclarationLanguageWrapper {

	// Private Data Members
	private ViewDeclarationLanguage wrappedViewDeclarationLanguage;

	public ViewDeclarationLanguageBridgeBase(ViewDeclarationLanguage viewDeclarationLanguage) {
		this.wrappedViewDeclarationLanguage = viewDeclarationLanguage;
	}

	@Override
	public void buildView(FacesContext facesContext, UIViewRoot uiViewRoot) throws IOException {

		String queryString = null;
		String viewId = uiViewRoot.getViewId();

		if (viewId != null) {

			int pos = viewId.indexOf("?");

			if (pos > 0) {
				queryString = viewId.substring(pos);
				viewId = viewId.substring(0, pos);
				uiViewRoot.setViewId(viewId);
			}
		}

		// Delegate to the Mojarra/MyFaces ViewHandler.
		super.buildView(facesContext, uiViewRoot);

		if (queryString != null) {
			uiViewRoot.setViewId(viewId.concat(queryString));
		}
	}

	@Override
	public ViewDeclarationLanguage getWrapped() {
		return wrappedViewDeclarationLanguage;
	}
}
