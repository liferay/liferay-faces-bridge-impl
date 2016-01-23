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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.portlet.ResourceResponse;

import com.liferay.faces.bridge.context.BridgeContext;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class BridgeContextCompatImpl extends BridgeContext {
	protected void partialViewContextRenderAll(FacesContext facesContext) {

		PartialViewContext partialViewContext = facesContext.getPartialViewContext();

		if (!partialViewContext.isRenderAll()) {
			partialViewContext.setRenderAll(true);
		}
	}

	protected void redirectJSF2PartialResponse(FacesContext facesContext, ResourceResponse resourceResponse, String url)
		throws IOException {
		resourceResponse.setContentType("text/xml");
		resourceResponse.setCharacterEncoding("UTF-8");

		PartialResponseWriter partialResponseWriter;
		ResponseWriter responseWriter = facesContext.getResponseWriter();

		if (responseWriter instanceof PartialResponseWriter) {
			partialResponseWriter = (PartialResponseWriter) responseWriter;
		}
		else {
			partialResponseWriter = facesContext.getPartialViewContext().getPartialResponseWriter();
		}

		partialResponseWriter.startDocument();
		partialResponseWriter.redirect(url);
		partialResponseWriter.endDocument();
		facesContext.responseComplete();
	}

	protected boolean isJSF2PartialRequest(FacesContext facesContext) {
		return facesContext.getPartialViewContext().isPartialRequest();
	}
}
