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
package com.liferay.faces.bridge.filter.internal;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.portlet.ActionResponse;
import javax.portlet.filter.ActionResponseWrapper;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class ActionResponseBridgeCompatImpl extends ActionResponseWrapper {

	public ActionResponseBridgeCompatImpl(ActionResponse response) {
		super(response);
	}

	protected void partialViewContextRenderAll(FacesContext facesContext) {

		PartialViewContext partialViewContext = facesContext.getPartialViewContext();

		if (!partialViewContext.isRenderAll()) {
			partialViewContext.setRenderAll(true);
		}
	}
}
