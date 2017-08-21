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
package com.liferay.faces.bridge.scope.internal;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.PortletMode;
import javax.portlet.faces.Bridge.PortletPhase;

import com.liferay.faces.util.helper.Wrapper;


/**
 * @author  Neil Griffin
 */
public abstract class BridgeRequestScopeWrapper implements BridgeRequestScope, Wrapper<BridgeRequestScope> {

	@Override
	public abstract BridgeRequestScope getWrapped();

	@Override
	public PortletPhase getBeganInPhase() {
		return getWrapped().getBeganInPhase();
	}

	@Override
	public String getId() {
		return getWrapped().getId();
	}

	@Override
	public PortletMode getPortletMode() {
		return getWrapped().getPortletMode();
	}

	@Override
	public Map<String, String> getPreservedActionParameterMap() {
		return getWrapped().getPreservedActionParameterMap();
	}

	@Override
	public String getPreservedViewStateParam() {
		return getWrapped().getPreservedViewStateParam();
	}

	@Override
	public boolean isFacesLifecycleExecuted() {
		return getWrapped().isFacesLifecycleExecuted();
	}

	@Override
	public boolean isNavigationOccurred() {
		return getWrapped().isNavigationOccurred();
	}

	@Override
	public boolean isPortletModeChanged() {
		return getWrapped().isPortletModeChanged();
	}

	@Override
	public boolean isRedirectOccurred() {
		return getWrapped().isRedirectOccurred();
	}

	@Override
	public void release(FacesContext facesContext) {
		getWrapped().release(facesContext);
	}

	@Override
	public void restoreState(FacesContext facesContext) {
		getWrapped().restoreState(facesContext);
	}

	@Override
	public void saveState(FacesContext facesContext) {
		getWrapped().saveState(facesContext);
	}

	@Override
	public void setFacesLifecycleExecuted(boolean facesLifecycleExecuted) {
		getWrapped().setFacesLifecycleExecuted(facesLifecycleExecuted);
	}

	@Override
	public void setNavigationOccurred(boolean navigationOccurred) {
		getWrapped().setNavigationOccurred(navigationOccurred);
	}

	@Override
	public void setPortletMode(PortletMode portletMode) {
		getWrapped().setPortletMode(portletMode);
	}

	@Override
	public void setPortletModeChanged(boolean portletModeChanged) {
		getWrapped().setPortletModeChanged(portletModeChanged);
	}

	@Override
	public void setRedirectOccurred(boolean redirectOccurred) {
		getWrapped().setRedirectOccurred(redirectOccurred);
	}
}
