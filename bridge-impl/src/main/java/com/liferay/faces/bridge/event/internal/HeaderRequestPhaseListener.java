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
package com.liferay.faces.bridge.event.internal;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.PortletConfig;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.util.internal.RequestMapUtil;


/**
 * Section 5.2.6 of the Spec indicates that the bridge must proactively ensure that only the RESTORE_VIEW phase
 * executes, and Section 6.4 indicates that a PhaseListener must be used.
 *
 * @author  Neil Griffin
 */
public class HeaderRequestPhaseListener implements PhaseListener {

	// serialVersionUID
	private static final long serialVersionUID = 8470095938465172618L;

	// Static field must be declared volatile in order for the double-check idiom to work (requires JRE 1.5+)
	private static volatile Boolean viewParametersEnabled;

	@Override
	public void afterPhase(PhaseEvent phaseEvent) {

		FacesContext facesContext = phaseEvent.getFacesContext();

		// First check without locking (not yet thread-safe)
		if (viewParametersEnabled == null) {

			synchronized (HeaderRequestPhaseListener.class) {

				// Second check with locking (thread-safe)
				if (viewParametersEnabled == null) {

					PortletConfig portletConfig = RequestMapUtil.getPortletConfig(facesContext);
					viewParametersEnabled = isViewParametersEnabled(portletConfig);
				}
			}
		}

		// If the JSF 2 "View Parameters" feature is not enabled, then ensure that only the RESTORE_VIEW phase executes.
		if (!viewParametersEnabled &&
				(BridgeUtil.getPortletRequestPhase(facesContext) == Bridge.PortletPhase.RENDER_PHASE)) {

			facesContext.renderResponse();
		}
	}

	@Override
	public void beforePhase(PhaseEvent phaseEvent) {
		// This method is required by the PhaseListener interfaces but is not used.
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	private boolean isViewParametersEnabled(PortletConfig portletConfig) {
		return PortletConfigParam.ViewParametersEnabled.getBooleanValue(portletConfig);
	}
}
