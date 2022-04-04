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
package com.liferay.faces.bridge.event.internal;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;
import com.liferay.faces.bridge.util.internal.ViewUtil;
import com.liferay.faces.util.lang.ThreadSafeAccessor;


/**
 * Section 5.2.6 of the Spec indicates that the bridge must proactively ensure that only the RESTORE_VIEW phase
 * executes, and Section 6.4 indicates that a PhaseListener must be used.
 *
 * @author  Neil Griffin
 */
public class HeaderRequestPhaseListener implements PhaseListener {

	// serialVersionUID
	private static final long serialVersionUID = 8470095938465172618L;

	// Private Final Data Members
	private final ViewParametersEnabledAccessor viewParametersEnabledAccessor = new ViewParametersEnabledAccessor();

	@Override
	public void afterPhase(PhaseEvent phaseEvent) {

		FacesContext facesContext = phaseEvent.getFacesContext();

		// If the JSF 2 "View Parameters" feature is not enabled, then ensure that only the RESTORE_VIEW phase executes.
		if (!viewParametersEnabledAccessor.get(facesContext) &&
				(BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RENDER_PHASE)) {

			facesContext.renderResponse();
		}
	}

	@Override
	public void beforePhase(PhaseEvent phaseEvent) {

		FacesContext facesContext = phaseEvent.getFacesContext();
		ExternalContext externalContext = facesContext.getExternalContext();
		String viewId = externalContext.getRequestPathInfo();

		if (viewId == null) {
			viewId = externalContext.getRequestServletPath();
		}

		if (viewId != null) {
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			PortletConfig portletConfig = RequestMapUtil.getPortletConfig(portletRequest);
			Map<String, String> defaultViewIdMap = ViewUtil.getDefaultViewIdMap(portletConfig);

			if (viewId.equals(defaultViewIdMap.get(PortletMode.EDIT.toString()))) {
				boolean portletModeAllowed = portletRequest.isPortletModeAllowed(PortletMode.EDIT);

				if (!portletModeAllowed) {
					throw new BridgeException("Portlet EDIT_MODE is not allowed");
				}
			}
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	private static final class ViewParametersEnabledAccessor extends ThreadSafeAccessor<Boolean, FacesContext> {

		@Override
		protected Boolean computeValue(FacesContext facesContext) {

			PortletConfig portletConfig = RequestMapUtil.getPortletConfig(facesContext);

			return PortletConfigParam.ViewParametersEnabled.getBooleanValue(portletConfig);
		}
	}
}
