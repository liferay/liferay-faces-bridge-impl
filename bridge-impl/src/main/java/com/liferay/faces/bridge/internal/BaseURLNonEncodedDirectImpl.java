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
package com.liferay.faces.bridge.internal;

import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;


/**
 * This class represents a "direct" non-encoded {@link BaseURL}, meaning an implementation that wraps a path and
 * constructs an absolute path URL based on the scheme, server/host name, and port found in the {@link PortletRequest}.
 *
 * @author  Neil Griffin
 */
public class BaseURLNonEncodedDirectImpl extends BaseURLNonEncodedImpl {

	// Private Data Members
	private String scheme;
	private String host;
	private int port;

	public BaseURLNonEncodedDirectImpl(BridgeURI bridgeURI, String scheme, String host, int port) {
		super(bridgeURI);
		this.scheme = scheme;
		this.host = host;
		this.port = port;
	}

	@Override
	public String toString() {

		StringBuilder buf = new StringBuilder();

		if (scheme != null) {
			buf.append(scheme);
			buf.append(':');
		}

		if ((host != null) || (port != -1)) {

			buf.append("//");

			if (host != null) {
				buf.append(host);
			}

			if (port != -1) {
				buf.append(':');
				buf.append(port);
			}
		}

		buf.append(super.toString());

		return buf.toString();
	}
}
