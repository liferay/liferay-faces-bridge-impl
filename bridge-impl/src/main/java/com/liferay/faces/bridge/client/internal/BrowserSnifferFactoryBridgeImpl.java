/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.client.internal;

import javax.faces.context.ExternalContext;

import com.liferay.faces.util.client.BrowserSniffer;
import com.liferay.faces.util.client.BrowserSnifferFactory;


/**
 * @author  Kyle Stiemann
 */
public class BrowserSnifferFactoryBridgeImpl extends BrowserSnifferFactory {

	// Private Data Memebers
	private BrowserSnifferFactory wrappedBrowserSnifferFactory;

	public BrowserSnifferFactoryBridgeImpl(BrowserSnifferFactory browserSnifferFactory) {
		this.wrappedBrowserSnifferFactory = browserSnifferFactory;
	}

	@Override
	public BrowserSniffer getBrowserSniffer(ExternalContext externalContext) {

		// Since we cannot obtain the HttpServletRequest, so we cannot obtain information about the browser, so
		// return a BrowserSniffer implementation which returns false for all booleans, 0 for all numbers, and "" for
		// all Strings.
		return new BrowserSnifferPortalImpl();
	}

	@Override
	public BrowserSnifferFactory getWrapped() {
		return wrappedBrowserSnifferFactory;
	}
}
