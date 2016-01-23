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
package com.liferay.faces.bridge.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.portlet.MimeResponse;

import com.sun.facelets.FaceletViewHandler;


/**
 * This class is a portlet-compatible implementation of the Facelets {@link ViewHandler}.
 *
 * @author  Neil Griffin
 */
public class PortletFaceletViewHandler extends FaceletViewHandler {

	// Private Constants
	private static final String FACELETS_CONTENT_TYPE = "facelets.ContentType";
	private static final String FACELETS_ENCODING = "facelets.Encoding";

	public PortletFaceletViewHandler(ViewHandler parent) {
		super(parent);
	}

	@Override
	protected ResponseWriter createResponseWriter(FacesContext facesContext) throws IOException, FacesException {

		ExternalContext externalContext = facesContext.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		String contentType = (String) requestMap.get(FACELETS_CONTENT_TYPE);

		if (contentType == null) {
			contentType = externalContext.getResponseContentType();
		}

		String encoding = (String) requestMap.get(FACELETS_ENCODING);

		if (encoding == null) {
			encoding = externalContext.getResponseCharacterEncoding();
		}

		MimeResponse mimeResponse = (MimeResponse) externalContext.getResponse();
		PrintWriter printWriter = mimeResponse.getWriter();
		RenderKit renderKit = facesContext.getRenderKit();
		ResponseWriter responseWriter = renderKit.createResponseWriter(printWriter, contentType, encoding);

		return responseWriter;
	}
}
