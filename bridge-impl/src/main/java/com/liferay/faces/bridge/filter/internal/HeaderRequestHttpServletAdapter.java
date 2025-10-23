/**
 * Copyright (c) 2000-2025 Liferay, Inc. All rights reserved.
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

import javax.portlet.HeaderRequest;
import javax.servlet.http.HttpServletRequest;


/**
 * Provides a way to decorate a {@link HeaderRequest} as an {@link HttpServletRequest}. The methods signatures that are
 * unique to {@link HttpServletRequest} throw {@link UnsupportedOperationException} since they are never called during
 * the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more information,
 * see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class HeaderRequestHttpServletAdapter extends PortletRequestHttpServletAdapter implements HeaderRequest {

	public HeaderRequestHttpServletAdapter(HeaderRequest headerRequest, String characterEncoding) {
		super(headerRequest, characterEncoding);
	}

	@Override
	public String getETag() {
		return ((HeaderRequest) getRequest()).getETag();
	}
}
