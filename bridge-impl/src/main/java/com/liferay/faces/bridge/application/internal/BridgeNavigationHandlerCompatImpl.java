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

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;


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

	@Override
	public NavigationCase getNavigationCase(FacesContext facesContext, String fromAction, String outcome) {

		if (wrappedNavigationHandler instanceof ConfigurableNavigationHandler) {
			ConfigurableNavigationHandler wrappedConfigurableNavigationHandler = (ConfigurableNavigationHandler)
				wrappedNavigationHandler;

			return wrappedConfigurableNavigationHandler.getNavigationCase(facesContext, fromAction, outcome);
		}
		else {

			// So as not to reinvent the wheel, we currently rely on the default NavigationHandler provided by
			// Mojarra/MyFaces being an instance of ConfigurableNavigationHandler. If that's not the case for some
			// reason, then throw an exception.
			throw new UnsupportedOperationException(
				"JSF runtime does not provide an instance of ConfigurableNavigationHandler");
		}
	}

	@Override
	public Map<String, Set<NavigationCase>> getNavigationCases() {

		if (wrappedNavigationHandler instanceof ConfigurableNavigationHandler) {
			ConfigurableNavigationHandler wrappedConfigurableNavigationHandler = (ConfigurableNavigationHandler)
				wrappedNavigationHandler;

			return wrappedConfigurableNavigationHandler.getNavigationCases();
		}
		else {

			// So as not to reinvent the wheel, we currently rely on the default NavigationHandler provided by
			// Mojarra/MyFaces being an instance of ConfigurableNavigationHandler. If that's not the case for some
			// reason, then throw an exception.
			throw new UnsupportedOperationException(
				"JSF runtime does not provide an instance of ConfigurableNavigationHandler");
		}
	}

	protected NavigationHandler getWrappedNavigationHandler() {
		return wrappedNavigationHandler;
	}

	protected void partialViewContextRenderAll(FacesContext facesContext) {
		PartialViewContext partialViewContext = facesContext.getPartialViewContext();
		partialViewContext.setRenderAll(true);
	}
}
