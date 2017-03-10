/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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

	// Protected (Lazy-Initialized) Constants
	private static Boolean VIEW_PARAMETERS_ENABLED;

	// Private Data Members
	private PhaseId phaseId = PhaseId.RESTORE_VIEW;

	@Override
		public void afterPhase(PhaseEvent phaseEvent) {

			FacesContext facesContext = phaseEvent.getFacesContext();

			if (VIEW_PARAMETERS_ENABLED == null) {

				synchronized (this) {

					if (VIEW_PARAMETERS_ENABLED == null) {

						PortletConfig portletConfig = RequestMapUtil.getPortletConfig(facesContext);
						VIEW_PARAMETERS_ENABLED = isViewParametersEnabled(portletConfig);
					}
				}
			}

		// If the JSF 2 "View Parameters" feature is not enabled, then ensure that only the RESTORE_VIEW phase executes.
		if (!VIEW_PARAMETERS_ENABLED &&
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
		return phaseId;
	}

	private boolean isViewParametersEnabled(PortletConfig portletConfig) {
		return PortletConfigParam.ViewParametersEnabled.getBooleanValue(portletConfig);
	}
}
