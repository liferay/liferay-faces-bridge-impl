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
package com.liferay.faces.bridge.scope.internal;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.faces.Bridge;


/**
 * Section 5.1.2 of the JSR 329 Spec describes a concept called the "bridge request scope," the purpose of which is to
 * bridge the gap between the portlet lifecycle's ACTION_PHASE, EVENT_PHASE, and RENDER_PHASE. When the user invokes an
 * HTTP POST operation by clicking on a Submit button, the portlet lifecycle's ACTION_PHASE will be invoked, and the
 * bridge will execute all the phases of the JSF lifecycle except for RENDER_RESPONSE -- which is to be done later in
 * the portlet lifecycle's RENDER_PHASE. The problem is that there might be FacesContext attributes or {@link
 * javax.faces.bean.RequestScoped} managed-beans (request attributes) created during in the ACTION_PHASE that also need
 * to be referenced in the RENDER_PHASE. Technically, the {@link ActionRequest} created for the ACTION_PHASE and the
 * {@link RenderRequest} created for the RENDER_PHASE are two different objects, and so the BridgeRequestScope exists to
 * maintain the existence of scoped data between the two phases.
 *
 * @author  Neil Griffin
 */
public interface BridgeRequestScope {

	/**
	 * @author  Neil Griffin
	 */
	public enum Transport {

		/**
		 * Indicates that the bridge request scope should be carried from the ACTION_PHASE/EVENT_PHASE into the
		 * RENDER_PHASE by setting a render parameter.
		 */
		RENDER_PARAMETER,

		/**
		 * Indicates that the bridge request scope should be carried from the ACTION_PHASE/EVENT_PHASE into the
		 * RENDER_PHASE by setting a portlet session attribute.
		 */
		PORTLET_SESSION_ATTRIBUTE
	}

	/**
	 * Determines the {@link javax.portlet.faces.Bridge.PortletPhase} in which the bridge request scope instance was
	 * created.
	 *
	 * @return  The {@link javax.portlet.faces.Bridge.PortletPhase} in which the bridge request scope instance was
	 *          created.
	 */
	public Bridge.PortletPhase getBeganInPhase();

	/**
	 * Returns the unique identifier for this bridge request scope.
	 */
	public String getId();

	/**
	 * Returns the {@link PortletMode} that was set via {@link #setPortletMode(PortletMode)}.
	 */
	public PortletMode getPortletMode();

	/**
	 * If the javax.portlet.faces.preserveActionParams init-param is set to <code>true</code> in WEB-INF/portlet.xml
	 * then this returns the action parameters that were found in the ACTION_PHASE.
	 */
	public Map<String, String> getPreservedActionParameterMap();

	/**
	 * Returns the value of the {@link javax.faces.render.ResponseStateManager#VIEW_STATE_PARAM} parameter that was
	 * found in the ACTION_PHASE.
	 */
	public String getPreservedViewStateParam();

	/**
	 * Returns the flag indicating whether or not the Faces Lifecycle was executed.
	 *
	 * @return  <code>true</code> if the Faces Lifecycle was executed, otherwise <code>false</code>.
	 */
	public boolean isFacesLifecycleExecuted();

	/**
	 * Returns the flag indicating whether or not a navigation-rule fired.
	 *
	 * @return  <code>true</code> indicates that a navigation-rule fired, otherwise <code>false</code>.
	 */
	public boolean isNavigationOccurred();

	/**
	 * Returns a flag indicating whether or not the PortletMode has changed.
	 *
	 * @return  <code>true</code> if the portlet mode has changed, otherwise <code>false</code>
	 */
	public boolean isPortletModeChanged();

	/**
	 * Returns a flag indicating whether or not a <redirect/> was encountered in a navigation-rule.
	 *
	 * @return  <code>true</code> indicates that <redirect/> was encountered in a navigation-rule, otherwise <code>
	 *          false</code>.
	 */
	public boolean isRedirectOccurred();

	/**
	 * This method should be called in order to release resources and/or scoped data that is not to be maintained from
	 * the ACTION_PHASE/EVENT_PHASE to the RENDER_PHASE.
	 *
	 * @param  facesContext  The current {@link FacesContext}.
	 */
	public void release(FacesContext facesContext);

	/**
	 * This method restores the scoped data that was preserved by the call to {@link #saveState(FacesContext)} method as
	 * required by section 5.1.2 of the Bridge Spec. This method is designed to be called during the EVENT_PHASE and
	 * RENDER_PHASE of the portlet lifecycle.
	 *
	 * @param  facesContext  The current {@link FacesContext}.
	 */
	public void restoreState(FacesContext facesContext);

	/**
	 * This method preserves the scoped data (as defined in Section 5.1.2 of the Bridge Spec). It should only be called
	 * during the {@link javax.portlet.PortletRequest#ACTION_PHASE} and {@link javax.portlet.PortletRequest#EVENT_PHASE}
	 * of the portlet lifecycle.
	 *
	 * @param  facesContext  The current {@link FacesContext}.
	 */
	public void saveState(FacesContext facesContext);

	/**
	 * Sets the flag indicating whether or not the Faces lifecycle was executed.
	 *
	 * @param  facesLifecycleExecuted  <code>true</code> indicates that the Faces lifecycle was executed, otherwise
	 *                                 <code>false</code>.
	 */
	public void setFacesLifecycleExecuted(boolean facesLifecycleExecuted);

	/**
	 * Sets the flag indicating whether or not a navigation-rule fired.
	 *
	 * @param  navigationOccurred  <code>true</code> indicates that a navigation-rule fired, otherwise <code>
	 *                             false</code>.
	 */
	public void setNavigationOccurred(boolean navigationOccurred);

	/**
	 * Sets the {@link PortletMode} that can be retrieved by calling {@link #getPortletMode()}.
	 */
	public void setPortletMode(PortletMode portletMode);

	/**
	 * Sets a flag indicating whether or not the PortletMode has changed. If <code>true</code> then request attributes
	 * will not be preserved when the {@link #saveState(FacesContext)} method is called.
	 */
	public void setPortletModeChanged(boolean portletModeChanged);

	/**
	 * Sets a flag indicating whether or not a <redirect/> was encountered in a navigation-rule.
	 *
	 * @param  redirectOccurred  <code>true</code> indicates that a <redirect/> was encountered in a navigation-rule,
	 *                           otherwise <code>false</code>.
	 */
	public void setRedirectOccurred(boolean redirectOccurred);
}
