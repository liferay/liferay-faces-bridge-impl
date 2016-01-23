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
package com.liferay.faces.bridge.application.view.internal;

import java.util.Collection;

import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderResponse;
import javax.servlet.ServletResponse;


/**
 * @author  Neil Griffin
 */
public class BridgeWriteBehindResponseRenderImpl extends BridgeWriteBehindResponseMimeImpl implements RenderResponse {

	public BridgeWriteBehindResponseRenderImpl(RenderResponse renderResponse, ServletResponse servletResponse) {
		super((MimeResponse) renderResponse, servletResponse);
	}

	public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
		getResponse().setNextPossiblePortletModes(portletModes);
	}

	@Override
	public RenderResponse getResponse() {
		return (RenderResponse) super.getResponse();
	}

	public void setTitle(String title) {
		getResponse().setTitle(title);
	}

}
