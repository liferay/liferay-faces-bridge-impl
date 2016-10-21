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
package com.liferay.faces.bridge.tck.tests.chapter_5.section_5_2;

import javax.portlet.HeaderResponse;
import javax.portlet.RenderResponse;
import javax.portlet.filter.RenderResponseWrapper;


/**
 * @author  Kyle Stiemann
 */
public class RenderResponseHeaderAdapterImpl extends RenderResponseWrapper implements HeaderResponse {

	public RenderResponseHeaderAdapterImpl(RenderResponse response) {
		super(response);
	}

	@Override
	public void addDependency(String name, String scope, String version) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDependency(String name, String scope, String version, String markup) {
		throw new UnsupportedOperationException();
	}
}
