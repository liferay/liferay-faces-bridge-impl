/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.faces.bridge.renderkit.primefaces.internal.FormRendererPrimeFacesImpl;


/**
 * This class provides a compatibility layer that contains JSF 1.0/1.1/1.2 public methods that subclasses need to
 * override.
 *
 * @author  Neil Griffin
 */
public abstract class ExternalContextCompat_1_2_Impl extends ExternalContextBridgeBase {

	// Protected Data Members

	public ExternalContextCompat_1_2_Impl(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse) {
		super(portletContext, portletRequest, portletResponse);
	}

	protected boolean isEncodingFormWithPrimeFacesAjaxFileUpload() {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		return facesContext.getAttributes().get(FormRendererPrimeFacesImpl.AJAX_FILE_UPLOAD) != null;
	}
}
