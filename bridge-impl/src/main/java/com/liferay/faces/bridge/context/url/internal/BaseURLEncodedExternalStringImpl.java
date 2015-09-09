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
package com.liferay.faces.bridge.context.url.internal;

import java.io.Writer;
import java.util.Map;

import javax.portlet.BaseURL;
import javax.portlet.PortletResponse;


/**
 * This class represents a simple "encoded" {@link BaseURL}, meaning an implementation that encodes a String based URL
 * that is "external" to the application. Since this class doesn't override any methods in the superclass it is
 * essentially a marker-class for code readability. The only methods that are meant to be called is {@link
 * BaseURLEncodedExternalStringImpl#toString()} and {@link BaseURLEncodedExternalStringImpl#write(Writer, boolean)}. All
 * other methods throw an {@link UnsupportedOperationException}.
 *
 * @author  Neil Griffin
 */
public class BaseURLEncodedExternalStringImpl extends BaseURLEncodedStringImpl {

	/**
	 * Constructs a new instance of this class.
	 *
	 * @param  externalURL      This is a URL "external" to this application, like http://www.foo.bar/foo.png
	 * @param  parameterMap     The current map of URL parameters.
	 * @param  portletResponse  The current {@link PortletResponse}.
	 */
	public BaseURLEncodedExternalStringImpl(String externalURL, Map<String, String[]> parameterMap,
		PortletResponse portletResponse) {

		super(externalURL, parameterMap, portletResponse);
	}

}
