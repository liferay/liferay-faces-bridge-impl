/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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

import java.util.Locale;

import javax.portlet.MimeResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletResponse;


/**
 * @author  Neil Griffin
 */
public class BridgeWriteBehindResponseResourceImpl extends BridgeWriteBehindResponseMimeImpl
	implements ResourceResponse {

	public BridgeWriteBehindResponseResourceImpl(ResourceResponse resourceResponse, ServletResponse servletResponse) {
		super((MimeResponse) resourceResponse, servletResponse);
	}

	public void setCharacterEncoding(String charset) {
		getResponse().setCharacterEncoding(charset);

	}

	public void setContentLength(int len) {
		getResponse().setContentLength(len);

	}

	public void setLocale(Locale loc) {
		getResponse().setLocale(loc);
	}

	@Override
	public ResourceResponse getResponse() {
		return (ResourceResponse) super.getResponse();
	}

}
