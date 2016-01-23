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
import javax.portlet.PortletResponse;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This abstract class represents a simple "encoded" {@link BaseURL}, meaning an implementation that encodes a String
 * based URL. The only methods that are meant to be called is {@link BaseURLEncodedStringImpl#toString()} and {@link
 * BaseURLEncodedStringImpl#write(Writer, boolean)}. All other methods throw an {@link UnsupportedOperationException}.
 *
 * @author  Neil Griffin
 */
public abstract class BaseURLEncodedStringImpl extends BaseURLNonEncodedStringImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BaseURLEncodedStringImpl.class);

	// Private Data Members
	private PortletResponse portletResponse;

	public BaseURLEncodedStringImpl(String url, Map<String, String[]> parameterMap, PortletResponse portletResponse) {
		super(url, parameterMap);
		this.portletResponse = portletResponse;
	}

	@Override
	public String toString() {

		String stringValue = null;

		try {
			stringValue = super.toString();
			stringValue = portletResponse.encodeURL(stringValue);
		}
		catch (Exception e) {
			logger.error(e);
		}

		return stringValue;
	}

}
