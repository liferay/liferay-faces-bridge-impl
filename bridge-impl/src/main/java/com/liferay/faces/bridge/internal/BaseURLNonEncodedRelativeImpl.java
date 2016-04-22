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

import java.io.Writer;

import javax.portlet.BaseURL;


/**
 * This class represents a "relative" non-encoded {@link BaseURL}, meaning an implementation that simply wraps a String
 * based URL that starts with "../" and does not require encoding. The only methods that are meant to be called is
 * {@link BaseURLNonEncodedRelativeImpl#toString()} and {@link BaseURLNonEncodedRelativeImpl#write(Writer, boolean)}.
 * All other methods throw an {@link UnsupportedOperationException}.
 *
 * @author  Neil Griffin
 */
public class BaseURLNonEncodedRelativeImpl extends BaseURLNonEncodedImpl {

	// Private Constants
	private static final String RELATIVE_PATH_PREFIX = "../";

	// Private Data Members
	private String contextPath;
	private String toStringValue;

	public BaseURLNonEncodedRelativeImpl(BridgeURI bridgeURI, String contextPath) {
		super(bridgeURI);
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
