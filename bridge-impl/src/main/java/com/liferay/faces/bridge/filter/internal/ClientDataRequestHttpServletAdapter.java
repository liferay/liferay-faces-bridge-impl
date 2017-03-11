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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletException;
import javax.servlet.http.Part;


/**
 * @author  Neil Griffin
 */
public class ClientDataRequestHttpServletAdapter extends PortletRequestHttpServletAdapter implements ClientDataRequest {

	public ClientDataRequestHttpServletAdapter(ClientDataRequest clientDataRequest, String characterEncoding) {
		super(clientDataRequest, characterEncoding);
	}

	@Override
	public long getContentLengthLong() {
		return ((ClientDataRequest) getRequest()).getContentLengthLong();
	}

	// Since HttpServletRequest.getPart(String) throws IOException,ServletException and
	// ClientDataRequest.getPart(String) throws IOException,PortletException the signature below must only throw
	// IOException in order to compile.
	@Override
	public Part getPart(String name) throws IOException {

		try {
			return ((ClientDataRequest) getRequest()).getPart(name);
		}
		catch (PortletException e) {
			throw new IOException(e);
		}
	}

	// Since HttpServletRequest.getParts() throws IOException,ServletException and
	// ClientDataRequest.getParts() throws IOException,PortletException the signature below must only throw
	// IOException in order to compile.
	@Override
	public Collection<Part> getParts() throws IOException {

		try {
			return ((ClientDataRequest) getRequest()).getParts();
		}
		catch (PortletException e) {
			throw new IOException(e);
		}
	}

	@Override
	public InputStream getPortletInputStream() throws IOException {
		return ((ClientDataRequest) getRequest()).getPortletInputStream();
	}
}
