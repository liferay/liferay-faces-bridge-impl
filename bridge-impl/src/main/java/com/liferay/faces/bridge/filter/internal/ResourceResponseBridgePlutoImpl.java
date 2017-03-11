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

import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ResourceResponseWrapper;


/**
 * @author  Neil Griffin
 */
public class ResourceResponseBridgePlutoImpl extends ResourceResponseWrapper {

	public ResourceResponseBridgePlutoImpl(ResourceResponse resourceResponse) {
		super(resourceResponse);
	}

	@Override
	public PortletURL createActionURL() {
		return new ActionURLBridgePlutoImpl(super.createActionURL());
	}

	@Override
	public PortletURL createRenderURL() {
		return new RenderURLBridgePlutoImpl(super.createRenderURL());
	}

	@Override
	public void setContentType(String type) {

		// If the specified contentType is "application/xhtml+xml" then use "text/html" instead. That's the only value
		// that Pluto's ResourceResponseImpl.setContentType(String) will be happy with, even though Pluto's "ACCEPT"
		// header claims it can accept "application/xhtml+xml".
		if ("application/xhtml+xml".equals(type)) {
			super.setContentType("text/html");
		}

		// Otherwise, use the specified contentType.
		else {
			super.setContentType(type);
		}
	}
}
