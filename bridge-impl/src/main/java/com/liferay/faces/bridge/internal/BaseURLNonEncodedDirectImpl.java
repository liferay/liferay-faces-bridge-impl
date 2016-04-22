/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
