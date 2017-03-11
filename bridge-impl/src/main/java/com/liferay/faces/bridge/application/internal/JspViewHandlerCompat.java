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

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public class JspViewHandlerCompat extends ViewHandlerWrapper {

	// Private Data Members
	private ViewHandler wrappedViewHandler;

	public JspViewHandlerCompat(ViewHandler viewHandler) {
		this.wrappedViewHandler = viewHandler;
	}

	public static boolean isJspView(UIViewRoot uiViewRoot) {

		// Return false for JSF 2.x since JSP detection is done inside ViewDeclarationLanguageBridgeJspImpl.java
		// (which is a more appropriate integration point for JSF 2.x).
		return false;
	}

	@Override
	public ViewHandler getWrapped() {
		return wrappedViewHandler;
	}
}
