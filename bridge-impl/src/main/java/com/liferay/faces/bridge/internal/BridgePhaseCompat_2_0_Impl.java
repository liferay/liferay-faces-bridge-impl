/**
 * Copyright (c) 2000-2023 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletResponse;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.context.internal.CapturingWriterImpl;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class BridgePhaseCompat_2_0_Impl extends BridgePhaseCompat_1_2_Impl {

	public BridgePhaseCompat_2_0_Impl(PortletConfig portletConfig, BridgeConfig bridgeConfig) {
		super(portletConfig, bridgeConfig);
	}

	public Throwable getJSF2HandledException(FacesContext facesContext) {

		// no-op for JSF 1.x
		return null;
	}

	public Throwable getJSF2UnhandledException(FacesContext facesContext) {

		// no-op for JSF 1.x
		return null;
	}

	public Writer getResponseOutputWriter(ExternalContext externalContext) throws IOException {

		Writer responseOutputWriter = null;

		PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();

		if (portletResponse instanceof MimeResponse) {

			MimeResponse mimeResponse = (MimeResponse) portletResponse;

			Map<String, Object> requestMap = externalContext.getRequestMap();

			Bridge.PortletPhase portletPhase = (Bridge.PortletPhase) requestMap.get(Bridge.PORTLET_LIFECYCLE_PHASE);

			if (portletPhase == Bridge.PortletPhase.RENDER_PHASE) {

				// If the render-redirect feature is enabled, then return a capturing writer so that the
				// bridge has the opportunity to discard output in the case that a render-redirect actually
				// occurs.
				boolean renderRedirectEnabled = PortletConfigParam.RenderRedirectEnabled.getBooleanValue(portletConfig);

				if (renderRedirectEnabled) {
					responseOutputWriter = new CapturingWriterImpl();
				}

				// Otherwise, return a writer that will write directly to the response.
				else {
					responseOutputWriter = mimeResponse.getWriter();
				}
			}
			else {
				responseOutputWriter = mimeResponse.getWriter();
			}
		}

		return responseOutputWriter;
	}

	public void handleJSF2ResourceRequest(FacesContext facesContext) throws IOException {
		// no-op for JSF 1.x
	}

	public boolean isJSF2AjaxRequest(FacesContext facesContext) {

		// no-op for JSF 1.x
		return false;
	}

	public boolean isJSF2ResourceRequest(FacesContext facesContext) {

		// no-op for JSF 1.x
		return false;
	}

	protected void clearHeadManagedBeanResources(FacesContext facesContext) {
		// no-op for JSF 1.x
	}
}
