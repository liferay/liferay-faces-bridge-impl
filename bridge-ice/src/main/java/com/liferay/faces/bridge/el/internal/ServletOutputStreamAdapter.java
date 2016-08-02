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
package com.liferay.faces.bridge.el.internal;

import java.io.IOException;
import java.io.OutputStream;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.servlet.ServletOutputStream;


/**
 * This class provides an {@link ServletOutputStream} adapter/wrapper around the {@link OutputStream} provided by the
 * current {@link PortletResponse}. This is necessary in order to hack around a Servlet-API dependencies in the Mojarra
 * implementation of JSF. Refer to usage in {@link HttpServletResponseAdapter#getOutputStream()} for more details.
 *
 * @author  Neil Griffin
 */
public class ServletOutputStreamAdapter extends ServletOutputStream {

	PortletResponse portletResponse;

	public ServletOutputStreamAdapter(PortletResponse portletResponse) {
		this.portletResponse = portletResponse;
	}

	@Override
	public void write(int b) throws IOException {

		if (portletResponse instanceof MimeResponse) {
			MimeResponse mimeResponse = (MimeResponse) portletResponse;
			mimeResponse.getPortletOutputStream().write(b);
		}
	}

}
