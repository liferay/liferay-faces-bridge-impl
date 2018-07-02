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

import javax.portlet.ActionURL;
import javax.portlet.HeaderResponse;
import javax.portlet.RenderURL;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a way to decorate a {@link HeaderResponse} as an {@link HttpServletResponse}. The methods signatures that
 * are unique to {@link HttpServletResponse} throw {@link UnsupportedOperationException} since they are never called
 * during the RENDER_RESPONSE phase of the JSF lifecycle (the use-case for which this class was written). For more
 * information, see {@link com.liferay.faces.bridge.application.view.internal.ViewDeclarationLanguageBridgeJspImpl}.
 *
 * @author  Neil Griffin
 */
public class HeaderResponseHttpServletAdapter extends MimeResponseHttpServletAdapter implements HeaderResponse {

	public HeaderResponseHttpServletAdapter(HeaderResponse headerResponse) {
		super(headerResponse);
	}

	@Override
	public void addDependency(String name, String scope, String version) {
		((HeaderResponse) getResponse()).addDependency(name, scope, version);
	}

	@Override
	public void addDependency(String name, String scope, String version, String markup) {
		((HeaderResponse) getResponse()).addDependency(name, scope, version, markup);
	}

	@Override
	public ActionURL createActionURL(Copy option) {
		return ((HeaderResponse) getResponse()).createActionURL();
	}

	@Override
	public RenderURL createRenderURL(Copy option) {
		return ((HeaderResponse) getResponse()).createRenderURL(option);
	}

	@Override
	public void setTitle(String title) {
		((HeaderResponse) getResponse()).setTitle(title);
	}
}
