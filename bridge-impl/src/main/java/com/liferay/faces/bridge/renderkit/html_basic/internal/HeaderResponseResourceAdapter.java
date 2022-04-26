/**
 * Copyright (c) 2000-2022 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import javax.portlet.HeaderResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ResourceResponseWrapper;


/**
 * @author  Neil Griffin
 */
public class HeaderResponseResourceAdapter extends ResourceResponseWrapper implements HeaderResponse {

	public HeaderResponseResourceAdapter(ResourceResponse resourceResponse) {
		super(resourceResponse);
	}

	@Override
	public void addDependency(String name, String scope, String version) {
		// no-op
	}

	@Override
	public void addDependency(String name, String scope, String version, String markup) {
		// no-op
	}

	@Override
	public void setTitle(String title) {
		// no-op
	}
}