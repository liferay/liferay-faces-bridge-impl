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
package com.liferay.faces.bridge.application.internal;

import java.util.Map;

import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.scope.BridgeRequestScope;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class BridgeNavigationHandlerCompatImpl extends BridgeNavigationHandler {

	// Private Data Members
	private NavigationHandler wrappedNavigationHandler;

	public BridgeNavigationHandlerCompatImpl(NavigationHandler navigationHandler) {
		this.wrappedNavigationHandler = navigationHandler;
	}

	protected void partialViewContextRenderAll(FacesContext facesContext) {
		// No-op for JSF 1.2
	}

	public NavigationCase getNavigationCase(FacesContext facesContext, String fromAction, String outcome) {

		BridgeRequestScope bridgeRequestScope = RequestMapUtil.getBridgeRequestScope(facesContext);

		return new NavigationCase(bridgeRequestScope.isRedirectOccurred());
	}

	protected NavigationHandler getWrappedNavigationHandler() {
		return wrappedNavigationHandler;
	}

	protected static class NavigationCase {

		private boolean redirect;

		public NavigationCase(boolean redirect) {
			this.redirect = redirect;
		}

		public boolean isRedirect() {
			return redirect;
		}

		public String getToViewId(FacesContext facesContext) {

			UIViewRoot viewRoot = facesContext.getViewRoot();

			return viewRoot.getViewId();
		}
	}
}
