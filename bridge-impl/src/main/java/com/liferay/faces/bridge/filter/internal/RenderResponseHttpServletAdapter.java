/**
 * Copyright (c) 2000-2021 Liferay, Inc. All rights reserved.
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
 * Provides a way to decorate a {@link RenderResponse} as an {@link HttpServletResponse}. The methods signatures that
 * are unique to {@link HttpServletResponse} throw {@link UnsupportedOperationException} since they are never called
 * during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more
 * information, see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class RenderResponseHttpServletAdapter extends MimeResponseHttpServletAdapter implements RenderResponse {

	public RenderResponseHttpServletAdapter(RenderResponse renderResponse) {
		super(renderResponse);
	}

	@Override
	public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
		((RenderResponse) getResponse()).setNextPossiblePortletModes(portletModes);
	}

	@Override
	public void setTitle(String title) {
		((RenderResponse) getResponse()).setTitle(title);
	}
}
