/**
 * Copyright (c) 2000-2020 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.tck.filter;

import javax.portlet.HeaderRequest;
import javax.portlet.filter.HeaderRequestWrapper;


/**
 * @author  Neil Griffin
 */
public class HeaderRequestResinImpl extends HeaderRequestWrapper {

	public HeaderRequestResinImpl(HeaderRequest request) {
		super(request);
	}

	@Override
	public Object getAttribute(String name) {

		if ("javax.servlet.include.servlet_path".equals(name)) {

			// Workaround for FACES-1629
			return null;
		}
		else {
			return super.getAttribute(name);
		}
	}
}
