/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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

import java.util.Locale;

import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate an {@link HttpServletResponse} as a portlet {@link ResourceResponse}.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseResourceAdapter extends HttpServletResponseMimeAdapter implements ResourceResponse {

	public HttpServletResponseResourceAdapter(ResourceResponse resourceResponse,
		HttpServletResponse httpServletResponse) {
		super(resourceResponse, httpServletResponse);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		getResourceResponse().setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int length) {
		getResourceResponse().setContentLength(length);
	}

	@Override
	public void setLocale(Locale locale) {
		getResourceResponse().setLocale(locale);
	}

	private ResourceResponse getResourceResponse() {
		return (ResourceResponse) getResponse();
	}
}
