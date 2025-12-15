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
package com.liferay.faces.bridge.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import jakarta.faces.context.ExternalContext;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeConfig;
import javax.portlet.faces.BridgeException;
import javax.portlet.faces.filter.BridgePortletRequestFactory;
import javax.portlet.faces.filter.BridgePortletResponseFactory;

import com.liferay.faces.bridge.context.internal.WriterOperation;


/**
 * This class provides a compatibility layer that isolates differences related to Portlet 2.0/3.0 and helps to minimize
 * diffs across branches.
 *
 * @author  Neil Griffin
 */
public abstract class BridgePhaseRenderCompatImpl extends BridgePhaseCompat_2_2_Impl {

	// Private Data Members
	protected RenderRequest renderRequest;
	protected RenderResponse renderResponse;

	public BridgePhaseRenderCompatImpl(RenderRequest renderRequest, RenderResponse renderResponse,
		PortletConfig portletConfig, BridgeConfig bridgeConfig) {

		super(portletConfig, bridgeConfig);
		this.renderRequest = BridgePortletRequestFactory.getRenderRequestInstance(renderRequest, renderResponse,
				portletConfig, bridgeConfig);
		this.renderResponse = BridgePortletResponseFactory.getRenderResponseInstance(renderRequest, renderResponse,
				portletConfig, bridgeConfig);
	}

	protected void execute(String renderRedirectViewId) throws BridgeException, IOException {

		init(renderRequest, renderResponse, Bridge.PortletPhase.RENDER_PHASE);

		// Spec 6.6 (Namespacing)
		indicateNamespacingToConsumers(facesContext.getViewRoot(), renderResponse);

		// If a captured list of writer operations was saved in the HEADER_PHASE, then invoke each writer operation so
		// that the markup will be written to the response now in the RENDER_PHASE.
		ExternalContext externalContext = facesContext.getExternalContext();
		List<WriterOperation> writerOperations = (List<WriterOperation>) renderRequest.getAttribute(
				Bridge.RENDER_RESPONSE_OUTPUT);
		renderRequest.removeAttribute(Bridge.RENDER_RESPONSE_OUTPUT);

		if (writerOperations != null) {

			Writer responseOutputWriter = getResponseOutputWriter(externalContext);

			for (WriterOperation writerOperation : writerOperations) {
				writerOperation.invoke(responseOutputWriter);
			}
		}
	}
}
