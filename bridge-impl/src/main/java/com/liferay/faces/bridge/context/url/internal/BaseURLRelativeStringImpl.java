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
package com.liferay.faces.bridge.context.url.internal;

import java.io.Writer;
import java.util.Map;

import javax.portlet.BaseURL;


/**
 * This class represents a relative {@link BaseURL}, meaning an implementation that simply wraps a String based URL that
 * starts with "../" and does not require encoding. The only methods that are meant to be called is {@link
 * BaseURLRelativeStringImpl#toString()} and {@link BaseURLRelativeStringImpl#write(Writer, boolean)}. All other methods
 * throw an {@link UnsupportedOperationException}.
 *
 * @author  Neil Griffin
 */
public class BaseURLRelativeStringImpl extends BaseURLNonEncodedStringImpl {

	// Private Constants
	private static final String RELATIVE_PATH_PREFIX = "../";

	// Private Data Members
	private String contextPath;
	private String toStringValue;

	public BaseURLRelativeStringImpl(String url, Map<String, String[]> parameterMap, String contextPath) {
		super(url, parameterMap);
		this.contextPath = contextPath;
	}

	@Override
	public String toString() {

		if (toStringValue == null) {

			toStringValue = super.toString();

			if (toStringValue.startsWith(RELATIVE_PATH_PREFIX)) {
				toStringValue = contextPath.concat("/").concat(toStringValue.substring(RELATIVE_PATH_PREFIX.length()));
			}
		}

		return toStringValue;
	}

}
