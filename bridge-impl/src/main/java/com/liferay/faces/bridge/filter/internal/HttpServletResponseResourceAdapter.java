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

import java.util.Locale;

import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate an {@link HttpServletResponse} as a portlet {@link ResourceResponse}. The methods
 * signatures that are unique to {@link ResourceResponse} throw {@link UnsupportedOperationException} since they are
 * never called during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written).
 * For more information, see {@link com.liferay.faces.bridge.context.internal.ExternalContextImpl#setResponse(Object)}.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseResourceAdapter extends HttpServletResponseMimeAdapter implements ResourceResponse {

	public HttpServletResponseResourceAdapter(HttpServletResponse httpServletResponse, String namespace) {
		super(httpServletResponse, namespace);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLength(int len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLengthLong(long len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();
	}
}
