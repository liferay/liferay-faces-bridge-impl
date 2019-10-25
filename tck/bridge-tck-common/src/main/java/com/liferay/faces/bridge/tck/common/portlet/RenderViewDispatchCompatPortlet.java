/**
 * Copyright (c) 2000-2019 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.common.portlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.HeaderRequest;
import javax.portlet.HeaderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.Bridge;

import com.liferay.faces.bridge.tck.common.util.BridgeTCKResultWriter;


/**
 * This class serves as a compatibility layer in order to minimize diffs across branches for TCK portlets by providing a
 * way to render JSF views dispatch to JSF views in the appropriate portlet lifecycle.
 *
 * @author  Neil Griffin
 */
public abstract class RenderViewDispatchCompatPortlet extends GenericFacesTestSuitePortlet {

	public void doBridgeFacesRequest(Bridge bridge, RenderRequest renderRequest, MimeResponse mimeResponse) {
		bridge.doFacesRequest((HeaderRequest) renderRequest, (HeaderResponse) mimeResponse);
	}

	@Override
	public void renderHeaders(HeaderRequest headerRequest, HeaderResponse headerResponse) throws PortletException,
		IOException {
		renderView(headerRequest, headerResponse);
	}

	protected abstract void renderView(RenderRequest renderRequest, MimeResponse mimeResponse) throws PortletException,
		IOException;

	protected void dispatchToView(RenderRequest renderRequest, MimeResponse mimeResponse) throws PortletException,
		IOException {
		super.renderHeaders((HeaderRequest) renderRequest, (HeaderResponse) mimeResponse);
	}

	@Override
	protected void doEdit(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		super.doEdit(renderRequest, renderResponse);
		outputResultWriter(renderRequest, renderResponse);
	}

	@Override
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException,
		IOException {

		super.doView(renderRequest, renderResponse);
		outputResultWriter(renderRequest, renderResponse);
	}

	private void outputResultWriter(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException {

		BridgeTCKResultWriter bridgeTCKResultWriter = (BridgeTCKResultWriter) renderRequest.getAttribute(
				BridgeTCKResultWriter.class.getName());

		if (bridgeTCKResultWriter != null) {
			PrintWriter printWriter = renderResponse.getWriter();
			printWriter.write(bridgeTCKResultWriter.toString());
		}
	}
}
