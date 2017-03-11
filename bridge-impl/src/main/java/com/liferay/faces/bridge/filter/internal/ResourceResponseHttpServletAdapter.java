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

import javax.annotation.Resource;
import javax.portlet.ActionURL;
import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate a {@link ResourceResponse} as an {@link HttpServletResponse}. The methods signatures that
 * are unique to {@link HttpServletResponse} throw {@link UnsupportedOperationException} since they are never called
 * during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more
 * information, see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class ResourceResponseHttpServletAdapter extends MimeResponseHttpServletAdapter implements ResourceResponse {

	public ResourceResponseHttpServletAdapter(ResourceResponse resourceResponse) {
		super(resourceResponse);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		((ResourceResponse) getResponse()).setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		((ResourceResponse) getResponse()).setContentLength(len);
	}

	@Override
	public void setContentLengthLong(long len) {
		((ResourceResponse) getResponse()).setContentLengthLong(len);
	}

	@Override
	public void setLocale(Locale loc) {
		((ResourceResponse) getResponse()).setLocale(loc);
	}
}
