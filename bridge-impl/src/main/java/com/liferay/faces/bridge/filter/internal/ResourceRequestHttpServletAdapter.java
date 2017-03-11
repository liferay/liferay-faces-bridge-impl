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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.portlet.PortletAsyncContext;
import javax.portlet.ResourceParameters;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * Provides a way to decorate a {@link ResourceRequest} as an {@link HttpServletRequest}. The methods signatures that
 * are unique to {@link HttpServletRequest} throw {@link UnsupportedOperationException} since they are never called
 * during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more
 * information, see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class ResourceRequestHttpServletAdapter extends ClientDataRequestHttpServletAdapter implements ResourceRequest {

	public ResourceRequestHttpServletAdapter(ResourceRequest resourceRequest) {
		super(resourceRequest, null);
	}

	@Override
	public String getCacheability() {
		return ((ResourceRequest) getRequest()).getCacheability();
	}

	@Override
	public String getCharacterEncoding() {
		return ((ResourceRequest) getRequest()).getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return ((ResourceRequest) getRequest()).getContentLength();
	}

	@Override
	public long getContentLengthLong() {
		return ((ResourceRequest) getRequest()).getContentLengthLong();
	}

	@Override
	public String getContentType() {
		return ((ResourceRequest) getRequest()).getContentType();
	}

	@Override
	public String getETag() {
		return ((ResourceRequest) getRequest()).getETag();
	}

	@Override
	public String getMethod() {
		return ((ResourceRequest) getRequest()).getMethod();
	}

	@Override
	public PortletAsyncContext getPortletAsyncContext() {
		return ((ResourceRequest) getRequest()).getPortletAsyncContext();
	}

	@Override
	public InputStream getPortletInputStream() throws IOException {
		return ((ResourceRequest) getRequest()).getPortletInputStream();
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		return ((ResourceRequest) getRequest()).getPrivateRenderParameterMap();
	}

	@Override
	public BufferedReader getReader() throws UnsupportedEncodingException {

		try {
			return ((ResourceRequest) getRequest()).getReader();
		}
		catch (IOException e) {
			throw new UnsupportedEncodingException(e.getMessage());
		}
	}

	@Override
	public String getResourceID() {
		return ((ResourceRequest) getRequest()).getResourceID();
	}

	@Override
	public ResourceParameters getResourceParameters() {
		return ((ResourceRequest) getRequest()).getResourceParameters();
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		((ResourceRequest) getRequest()).setCharacterEncoding(env);
	}

	@Override
	public PortletAsyncContext startPortletAsync() throws IllegalStateException {
		return ((ResourceRequest) getRequest()).startPortletAsync();
	}

	@Override
	public PortletAsyncContext startPortletAsync(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {
		return ((ResourceRequest) getRequest()).startPortletAsync(resourceRequest, resourceResponse);
	}
}
