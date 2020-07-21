/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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

import javax.portlet.HeaderResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate an {@link HttpServletResponse} as a portlet {@link HeaderResponse}.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseHeaderAdapter extends HttpServletResponseMimeAdapter implements HeaderResponse {

	public HttpServletResponseHeaderAdapter(HeaderResponse headerResponse, HttpServletResponse httpServletResponse) {
		super(headerResponse, httpServletResponse);
	}

	@Override
	public void addDependency(String name, String scope, String version) {
		getHeaderResponse().addDependency(name, scope, version);
	}

	@Override
	public void addDependency(String name, String scope, String version, String markup) {
		getHeaderResponse().addDependency(name, scope, version, markup);
	}

	@Override
	public void setTitle(String title) {
		getHeaderResponse().setTitle(title);
	}

	private HeaderResponse getHeaderResponse() {
		return (HeaderResponse) getResponse();
	}
}
