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
package com.liferay.faces.bridge.tck.application.view;

import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageFactory;


/**
 * @author  Neil Griffin
 */
public class ViewDeclarationLanguageFactoryTCKImpl extends ViewDeclarationLanguageFactory {

	// Private Data Members
	private ViewDeclarationLanguageFactory wrappedFactory;

	public ViewDeclarationLanguageFactoryTCKImpl(ViewDeclarationLanguageFactory wrappedFactory) {
		this.wrappedFactory = wrappedFactory;
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(String viewId) {
		ViewDeclarationLanguage viewDeclarationLanguage = getWrapped().getViewDeclarationLanguage(viewId);

		if (viewDeclarationLanguage.getClass().getName().contains("ViewDeclarationLanguageBridgeJspImpl")) {
			viewDeclarationLanguage = new ViewDeclarationLanguageJspTCKImpl(viewDeclarationLanguage);
		}

		return viewDeclarationLanguage;
	}

	@Override
	public ViewDeclarationLanguageFactory getWrapped() {
		return wrappedFactory;
	}

}
