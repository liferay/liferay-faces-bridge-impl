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
package com.liferay.faces.bridge.tck.beans;

import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.faces.annotation.BridgePreDestroy;
import javax.portlet.faces.annotation.BridgeRequestScopeAttributeAdded;


/**
 * @author  Michael Freedman
 */
public class PreDestroyBean2 {

	PortletRequest mRequest;
	boolean mInBridgeRequestScope = false;

	public PreDestroyBean2() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		mRequest = (PortletRequest) ctx.getExternalContext().getRequest();
		mInBridgeRequestScope = true;
	}

	@BridgeRequestScopeAttributeAdded
	public void attributeAddedToBridgeRequestScope() {
		mRequest.setAttribute("PreDestroyBean2.attributeAdded", Boolean.TRUE);

	}

	@BridgePreDestroy
	public void bridgePredestroy() {
		mRequest.setAttribute("PreDestroyBean2.bridgePreDestroy", Boolean.TRUE);
		mInBridgeRequestScope = false;
	}

	public Boolean isInBridgeRequestScope() {

		if (mInBridgeRequestScope) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	@PreDestroy
	public void servletPredestroy() {

		if (!mInBridgeRequestScope) {
			mRequest.setAttribute("PreDestroyBean2.servletPreDestroy", Boolean.TRUE); // indicates we would do the
																					  // destroy
		}
		else {
			mRequest.setAttribute("PreDestroyBean2.servletPreDestroy", Boolean.FALSE); // indicates we would not do the
																					   // destroy
		}

	}
}
