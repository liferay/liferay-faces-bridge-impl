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
package com.liferay.faces.bridge.context.flash.internal;

import java.util.Locale;

import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.bridge.context.internal.ExternalContextImpl;
import com.liferay.faces.bridge.filter.internal.HttpServletResponseAdapter;


/**
 * This class provides the ability to trick the JSF implementation into thinking that the current {@link
 * PortletResponse} is actually an {@link HttpServletResponse}. It serves as a marker class that simply extends {@link
 * HttpServletResponseAdapter}. It's only purpose is to make the code in {@link ExternalContextImpl} easier to follow by
 * providing a meaningful (self documenting) name.
 *
 * @author  Neil Griffin
 */
public class FlashHttpServletResponse extends HttpServletResponseAdapter {

	public FlashHttpServletResponse(PortletResponse portletResponse, Locale requestLocale) {
		super(portletResponse, requestLocale);
	}

}
