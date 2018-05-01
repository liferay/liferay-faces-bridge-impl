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

import java.util.Collection;

import javax.portlet.PortletMode;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate an {@link HttpServletResponse} as a portlet {@link RenderResponse}.
 *
 * @author  Neil Griffin
 */
public class HttpServletResponseRenderAdapter extends HttpServletResponseMimeAdapter implements RenderResponse {

	public HttpServletResponseRenderAdapter(RenderResponse renderResponse, HttpServletResponse httpServletResponse) {
		super(renderResponse, httpServletResponse);
	}

	@Override
	public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
		getRenderResponse().setNextPossiblePortletModes(portletModes);
	}

	@Override
	public void setTitle(String title) {
		getRenderResponse().setTitle(title);
	}

	private RenderResponse getRenderResponse() {
		return (RenderResponse) getResponse();
	}
}
