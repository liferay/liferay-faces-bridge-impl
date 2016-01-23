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
import javax.portlet.PortletRequest;


/**
 * This class represents a simple "direct" {@link BaseURL}, meaning an implementation that wraps a path and constructs
 * an absolute path URL based on the scheme, server name, and port found in the {@link PortletRequest}. The only methods
 * that are meant to be called is {@link BaseURLDirectStringImpl#toString()} and {@link
 * BaseURLDirectStringImpl#write(Writer, boolean)}. All other methods throw an {@link UnsupportedOperationException}.
 *
 * @author  Neil Griffin
 */
public class BaseURLDirectStringImpl extends BaseURLNonEncodedStringImpl {

	// Private Data Members
	private String path;
	private PortletRequest portletRequest;

	public BaseURLDirectStringImpl(String url, Map<String, String[]> parameterMap, String path,
		PortletRequest portletRequest) {
		super(url, parameterMap);
		this.path = path;
		this.portletRequest = portletRequest;
	}

	@Override
	public String toString() {

		StringBuilder buf = new StringBuilder();
		buf.append(portletRequest.getScheme());
		buf.append("://");
		buf.append(portletRequest.getServerName());
		buf.append(":");
		buf.append(portletRequest.getServerPort());
		buf.append(path);

		String queryString = getQuery();

		if (queryString.length() > 0) {
			buf.append("?");
			buf.append(queryString);
		}

		return buf.toString();
	}

}
