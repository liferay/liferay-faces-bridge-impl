/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeException;

import com.liferay.faces.bridge.BridgeConfig;
import com.liferay.faces.bridge.context.internal.WriterOperation;
import com.liferay.faces.bridge.filter.BridgePortletRequestFactory;
import com.liferay.faces.bridge.filter.BridgePortletResponseFactory;


/**
 * This class provides a compatibility layer that isolates differences related to Portlet 2.0/3.0 and helps to minimize
 * diffs across branches.
 *
 * @author  Neil Griffin
 */
public abstract class BridgePhaseRenderCompatImpl extends BridgePhaseHeaderRenderCommon {

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

	@Override
	protected MimeResponse getMimeResponse() {
		return renderResponse;
	}

	@Override
	protected RenderRequest getRenderRequest() {
		return renderRequest;
	}

	protected void execute(String renderRedirectViewId) throws BridgeException, IOException {
		executeRender(null, Bridge.PortletPhase.RENDER_PHASE);
	}

	@Override
	protected void renderCapturedOperations(List<WriterOperation> writerOperations, Writer writer) throws IOException {

		for (WriterOperation writerOperation : writerOperations) {
			writerOperation.invoke(writer);
		}
	}
}
